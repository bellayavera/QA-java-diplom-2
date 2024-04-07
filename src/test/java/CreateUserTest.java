import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest extends Api{

    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void createNewUserIsSuccess(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);

        responseCreate.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);

        Response responseLogin = loginUser(jsonForCreateAndLogin);

        responseLogin.then().assertThat().body("success",  equalTo(true))
                .and()
                .statusCode(200);

        accessToken = responseLogin.jsonPath().getString("accessToken");
    }

    @Test
    public void createExistsUserIsError(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);

        responseCreate.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);

        Response responseLogin = loginUser(jsonForCreateAndLogin);

        responseLogin.then().assertThat().body("success",  equalTo(true))
                .and()
                .statusCode(200);

        Response responseCreateExistsUser = createUser(jsonForCreateAndLogin);

        responseCreateExistsUser.then().assertThat().body("message", equalTo("User already exists"))
                .and()
                .statusCode(403);

        accessToken = responseLogin.jsonPath().getString("accessToken");
    }

    @Test
    public void createUserWithoutPassIsError(){
        File jsonForCreateAndLoginWithoutPass = new File("src/test/resources/CreateLoginUserWithoutPass.json");

        Response responseCreate = createUser(jsonForCreateAndLoginWithoutPass);

        responseCreate.then().assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);

        Response responseLogin = loginUser(jsonForCreateAndLoginWithoutPass);

        responseLogin.then().assertThat().body("message",  equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);

        accessToken = responseLogin.jsonPath().getString("accessToken");
    }

    @Test
    public void createUserWithoutNameIsError(){
        File jsonForCreateAndLoginWithoutName = new File("src/test/resources/CreateLoginUserWithoutName.json");

        Response responseCreate = createUser(jsonForCreateAndLoginWithoutName);

        responseCreate.then().assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);

        Response responseLogin = loginUser(jsonForCreateAndLoginWithoutName);

        responseLogin.then().assertThat().body("message",  equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);

        accessToken = responseLogin.jsonPath().getString("accessToken");
    }

    @Test
    public void createUserWithoutEmailIsError(){
        File jsonForCreateAndLoginWithoutEmail = new File("src/test/resources/CreateLoginUserWithoutEmail.json");

        Response responseCreate = createUser(jsonForCreateAndLoginWithoutEmail);

        responseCreate.then().assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);

        Response responseLogin = loginUser(jsonForCreateAndLoginWithoutEmail);

        responseLogin.then().assertThat().body("message",  equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);

        accessToken = responseLogin.jsonPath().getString("accessToken");
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
