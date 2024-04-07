import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.io.File;

import static io.restassured.RestAssured.given;

public class Api {

    @Step("Send POST request to /api/auth/register")
    public Response createUser(File json){
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/auth/register");
    }
    @Step("Send POST request to /api/auth/login")
    public Response loginUser(File json){
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/auth/login");
    }
    @Step("Send PATCH request to /api/auth/user")
    public Response updateUser(File json, String accessToken){
        if (accessToken.length() > 7){
            accessToken = accessToken.substring(7);
        }
        return given()
                .auth().oauth2(accessToken)
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user");
    }
    @Step("Send GET request to /api/ingredients")
    public Response getIngredients(){
        return given()
                .get("/api/ingredients");
    }
    @Step("Send POST request to /api/orders")
    public Response createOrder(String json, String accessToken){
        if (accessToken.length() > 7){
            accessToken = accessToken.substring(7);
        }
        return given()
                .auth().oauth2(accessToken)
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/orders");
    }
    @Step("Send GET request to /api/orders")
    public Response getOrder(String accessToken){
        if (accessToken.length() > 7){
            accessToken = accessToken.substring(7);
        }
        return given()
                .auth().oauth2(accessToken)
                .get("/api/orders");
    }

}
