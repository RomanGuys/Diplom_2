import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class IngredientClient extends RestAssuredClient {

    private static final String INGREDIENTS_PATH = "api/ingredients";

    @Step
    public Response getIngredients() {
        Response response = given()
                .spec(getBaseSpec())
                .get(INGREDIENTS_PATH);
        return response;
    }
}