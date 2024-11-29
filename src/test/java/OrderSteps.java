import io.qameta.allure.Step;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Отправляем запрос на создание заказа")
    public static Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/v1/orders");
    }

    @Step("Отправляем запрос на получение списка заказов")
    public static Response getOrders() {
        return given()
                .header("Content-type", "application/json")
                .get("/api/v1/orders");
    }
}
