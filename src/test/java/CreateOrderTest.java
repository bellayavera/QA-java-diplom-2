import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest extends Api{

    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void createOrderIsSuccessWhenLogin(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);
        Response responseLogin = loginUser(jsonForCreateAndLogin);
        Response responseGetIngredients = getIngredients();
        List<String> ingredients = responseGetIngredients.jsonPath().getList("data._id");
        String json = "{\"ingredients\": \"" + ingredients.get(1) + "\"}";
        accessToken = responseLogin.jsonPath().getString("accessToken");

        Response responseCreateOrder = createOrder(json, accessToken);

        responseCreateOrder.then().assertThat().body("order.number",  notNullValue())
                .and()
                .statusCode(200);

        var orderNumber = responseCreateOrder.jsonPath().getInt("order.number");

        Response responseGetOrder = getOrder(accessToken);
        List<Integer> orders = responseGetOrder.jsonPath().getList("orders.number");
        Assert.assertTrue(orders.contains(orderNumber));
    }

    @Test
    public void createOrderIsErrorWhenAreNotLogin(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);
        Response responseGetIngredients = getIngredients();
        List<String> ingredients = responseGetIngredients.jsonPath().getList("data._id");
        String json = "{\"ingredients\": \"" + ingredients.get(1) + "\"}";

        String accessTokenEmpty = "";
        Response responseCreateOrder = createOrder(json, accessTokenEmpty);

        var orderNumber = responseCreateOrder.jsonPath().getInt("order.number");

        Response responseLogin = loginUser(jsonForCreateAndLogin);
        accessToken = responseLogin.jsonPath().getString("accessToken");

        Response responseGetOrder = getOrder(accessToken);
        List<Integer> orders = responseGetOrder.jsonPath().getList("orders.number");
        Assert.assertFalse(orders.contains(orderNumber));
    }

    @Test
    public void createOrderIsErrorWhenOrderEmpty(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);
        Response responseLogin = loginUser(jsonForCreateAndLogin);
        accessToken = responseLogin.jsonPath().getString("accessToken");

        String json = "{\"ingredients\": []}";

        Response responseCreateOrder = createOrder(json, accessToken);

        responseCreateOrder.then().assertThat().body("message",  equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
    }

    @Test
    public void createOrderIsErrorWhenIncorrectIngredients(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);
        Response responseLogin = loginUser(jsonForCreateAndLogin);
        accessToken = responseLogin.jsonPath().getString("accessToken");

        String json = "{\"ingredients\": [\"incorrect_id\"]}";

        Response responseCreateOrder = createOrder(json, accessToken);

        responseCreateOrder.then().assertThat().body("message",  equalTo("One or more ids provided are incorrect"))
                .and()
                .statusCode(400);
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
