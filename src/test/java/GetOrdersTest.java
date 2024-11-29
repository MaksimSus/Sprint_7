import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest {

    @Before
    public void setUp() {
        // Устанавливаем базовый URL для запросов
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Get list of orders")
    public void getListOfOrders() {
        // Используем шаг для получения списка заказов
        var response = OrderSteps.getOrders();

        response.then()
                .statusCode(200)
                .body("orders", notNullValue()); // Проверяем, что в ответе есть список заказов
    }
}
