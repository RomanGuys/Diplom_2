import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CreateOrderTests {
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
    @DisplayName("Заказ без ингридиентов")
    public void createOrderWithoutIngredientsCheck() {
        String accessToken = login();
        Response response = orderClient.createOrder(null, accessToken);
        assertEquals(400, response.statusCode());
        assertEquals( false, response.path("success"));
        assertEquals( "Ingredient ids must be provided", response.path("message"));
    }

    @Test
    @DisplayName("Неверный ингридиент")
    public void createOrderWithIncorrectIngredientCheck() {
        String accessToken = login();
        Response response = orderClient.createOrder(Arrays.asList("1213"), accessToken);
        assertEquals( 500, response.statusCode());
    }

    @Test
    @DisplayName("Заказ с авторизацией")
    public void createOrderWithAuthorizationCheck() {
        String accessToken = login();
        Response responseIngredients = ingredientsClient.getIngredients();
        List<String> ingredients = responseIngredients.path("data._id");
        Response response = orderClient.createOrder(ingredients, accessToken);
        assertEquals( 200, response.statusCode());
        assertEquals(true, response.path("success"));
    }

    @Test
    @DisplayName("Заказ без авторизации")
    public void createOrderWithoutAuthorizationCheck() {
        Response responseIngredients = ingredientsClient.getIngredients();
        List<String> ingredients = responseIngredients.path("data._id");
        Response response = orderClient.createOrder(ingredients, "");
        assertEquals(200, response.statusCode());
        assertEquals( true, response.path("success"));
    }

    private String login(){
        String email = RandomStringUtils.randomAlphabetic(5) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphabetic(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        Response createResponse = userClient.create(email, password, username);
        String accessToken = createResponse.path("accessToken");
        userClient.login(email, password);
        return accessToken;
    }
}