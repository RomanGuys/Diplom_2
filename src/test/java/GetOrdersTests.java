import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class GetOrdersTests {
    OrdersClient orderClient;
    UserClient userClient;
    IngredientClient ingredientsClient;

    @Before
    public void setUp() {
        orderClient = new OrdersClient();
        userClient = new UserClient();
        ingredientsClient = new IngredientClient();
    }

    @Test
    @DisplayName("Получение заказа")
    public void getOrderWithAuthorizationCheck() {
        String accessToken = createAndLogin();
        Response responseIngredients = ingredientsClient.getIngredients();
        List<String> ingredients = responseIngredients.path("data._id");
        orderClient.createOrder(ingredients, accessToken);
        Response response = orderClient.getOrders(accessToken);
        assertEquals( 200, response.statusCode());
        assertEquals( true, response.path("success"));
        assertThat( response.path("orders"), notNullValue());
    }

    @Test
    @DisplayName("Получение заказа без авторизации")
    public void getOrderWithoutAuthorizationCheck() {
        String accessToken = createAndLogin();
        Response responseIngredients = ingredientsClient.getIngredients();
        List<String> ingredients = responseIngredients.path("data._id");
        orderClient.createOrder(ingredients, accessToken);
        Response response = orderClient.getOrders("");
        assertEquals( 401, response.statusCode());
        assertEquals( false, response.path("success"));
        assertEquals( "You should be authorised", response.path("message"));
    }

    private String createAndLogin(){
        String email = RandomStringUtils.randomAlphabetic(5) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphabetic(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        Response response = userClient.create(email, password, username);
        userClient.login(email, password);
        String accessToken = response.path("accessToken");
        return accessToken;
    }
}