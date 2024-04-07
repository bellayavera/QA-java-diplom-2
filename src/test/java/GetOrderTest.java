import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GetOrderTest extends Api{

    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void getOrderIsSuccessWhenLogin(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);
        Response responseLogin = loginUser(jsonForCreateAndLogin);
        Response responseGetIngredients = getIngredients();
        List<String> ingredients = responseGetIngredients.jsonPath().getList("data._id");
        String jsonFirstOrder = "{\"ingredients\": \"" + ingredients.get(1) + "\"}";
        String jsonSecondOrder = "{\"ingredients\": \"" + ingredients.get(2) + "\"}";
        accessToken = responseLogin.jsonPath().getString("accessToken");

        Response responseCreateFirstOrder = createOrder(jsonFirstOrder, accessToken);
        Response responseCreateSecondOrder = createOrder(jsonFirstOrder, accessToken);

        var firstOrderNumber = responseCreateFirstOrder.jsonPath().getInt("order.number");
        var secondOrderNumber = responseCreateSecondOrder.jsonPath().getInt("order.number");
        List<Integer> ordersExpected = new ArrayList<>();
        ordersExpected.add(firstOrderNumber);
        ordersExpected.add(secondOrderNumber);

        Response responseGetOrder = getOrder(accessToken);

        responseGetOrder.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);

        List<Integer> ordersActual = responseGetOrder.jsonPath().getList("orders.number");
        Assert.assertTrue(ordersExpected.equals(ordersActual));
    }


    @Test
    public void getOrderIsErrorWhenAreNotLogin(){
        File jsonForCreateAndLogin = new File("src/test/resources/CreateLoginUser.json");

        Response responseCreate = createUser(jsonForCreateAndLogin);
        Response responseLogin = loginUser(jsonForCreateAndLogin);
        accessToken = responseLogin.jsonPath().getString("accessToken");

        Response responseGetIngredients = getIngredients();
        List<String> ingredients = responseGetIngredients.jsonPath().getList("data._id");
        String jsonOrder = "{\"ingredients\": \"" + ingredients.get(1) + "\"}";
        String accessTokenEmpty = "";

        Response responseCreateOrder = createOrder(jsonOrder, accessToken);

        Response responseGetOrder = getOrder(accessTokenEmpty);

        responseGetOrder.then().assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
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
