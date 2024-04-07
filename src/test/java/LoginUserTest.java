import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest extends Api{

    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void loginUserIsSuccess() {
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);
        Response responseLogin = loginUser(jsonForCreateAndLogin);

        responseLogin.then().assertThat().body("success",  equalTo(true))
                .and()
                .statusCode(200);

        accessToken = responseLogin.jsonPath().getString("accessToken");
    }

    @Test
    public void loginUserWithIncorrectPassIsError(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");
        File jsonForLoginWithIncorrectPass = new File("src/test/resources/LoginUserWithIncorrectPass.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);
        Response responseLogin = loginUser(jsonForLoginWithIncorrectPass);

        responseLogin.then().assertThat().body("message",  equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);

        accessToken = responseLogin.jsonPath().getString("accessToken");
    }

    @Test
    public void loginUserWithIncorrectEmailIsError(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");
        File jsonForLoginWithIncorrectEmail = new File("src/test/resources/LoginUserWithIncorrectEmail.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);
        Response responseLogin = loginUser(jsonForLoginWithIncorrectEmail);

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
