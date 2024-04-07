import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UpdateUserTest extends Api{

    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void updateEmailIsSuccessWhenLogin(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");
        File jsonForUpdateEmail = new File("src/test/resources/UpdateEmailUser.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);
        Response responseLogin = loginUser(jsonForCreateAndLogin);

        accessToken = responseLogin.jsonPath().getString("accessToken");
        Response responseUpdate = updateUser(jsonForUpdateEmail, accessToken);

        responseUpdate.then().assertThat().body("user.email",  equalTo("test-data-check-new@yandex.ru"))
                .and()
                .statusCode(200);

        accessToken = responseLogin.jsonPath().getString("accessToken");
    }

    @Test
    public void updateNameIsSuccessWhenLogin(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");
        File jsonForUpdateName = new File("src/test/resources/UpdateNameUser.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);
        Response responseLogin = loginUser(jsonForCreateAndLogin);

        accessToken = responseLogin.jsonPath().getString("accessToken");
        Response responseUpdate = updateUser(jsonForUpdateName, accessToken);

        responseUpdate.then().assertThat().body("user.name",  equalTo("UsernameNew"))
                .and()
                .statusCode(200);
    }

    @Test
    public void updateEmailIsErrorWhenAreNotLogin(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");
        File jsonForUpdateEmail = new File("src/test/resources/UpdateEmailUser.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);

        String accessTokenEmpty = "";
        Response responseUpdate = updateUser(jsonForUpdateEmail, accessTokenEmpty);

        responseUpdate.then().assertThat().body("message",  equalTo("You should be authorised"))
                .and()
                .statusCode(401);

        accessToken = responseCreate.jsonPath().getString("accessToken");
    }

    @Test
    public void updateNameIsErrorWhenAreNotLogin(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");
        File jsonForUpdateName = new File("src/test/resources/UpdateNameUser.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);

        String accessTokenEmpty = "";
        Response responseUpdate = updateUser(jsonForUpdateName, accessTokenEmpty);

        responseUpdate.then().assertThat().body("message",  equalTo("You should be authorised"))
                .and()
                .statusCode(401);

        accessToken = responseCreate.jsonPath().getString("accessToken");
    }

    @After
    public void userDeletion(){
        if (accessToken != null) {
            RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
            given()
                    .auth().oauth2(accessToken.substring(7))
                    .header("Content-type", "application/json")
                    .expect().statusCode(202)
                    .when()
                    .delete("api/auth/user");
        }
    }
}
