import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierTest {

    private String courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @After
    public void cleanUp() {
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Create courier successfully")
    public void createCourierSuccessfully() {
    //Проверяем создание курьера
        Courier courier = new Courier("testLoginMaks", "testPassword", "TestNameMaks");
        Response response = createCourier(courier);

        response.then().statusCode(201)
                .body("ok", equalTo(true));

        courierId = extractCourierId(courier);
    }

    @Test
    @DisplayName("Cannot create two identical couriers")
    public void createCourierWithDuplicateLogin() {
    //Создаем курьера
        Courier courier = new Courier("duplicateLoginMaks", "testPassword", "TestNameMaks");
        createCourier(courier);
        // Пытаемся создать курьера с теме же данными
        Response response = createCourier(courier);

        response.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется"));
    }

        @Test
    @DisplayName("Creating courier with existing login returns an error")
    public void cannotCreateCourierWithExistingLogin() {
        Courier courier = new Courier("existingLoginMaks", "existingPasswordMaks", "ExistingNameMaks");
        createCourier(courier);

        // Создание второго курьера с таким же логином и другими данными
        Courier duplicateCourier = new Courier("existingLoginMaks", "newPassword", "NewName");
        Response response = createCourier(duplicateCourier);

        response.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Cannot create courier without required field")
    public void createCourierWithoutRequiredField() {
    //Создаем клиента без обязательного поля login
        Courier courier = new Courier("", "testPassword", "TestName");
        Response response = createCourier(courier);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    // Шаги с аннотацией @Step

    @Step("Creating courier with login: {courier.login}")
    private Response createCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");
    }

    @Step("Deleting courier with ID: {courierId}")
    private void deleteCourier(String courierId) {
        given()
                .header("Content-type", "application/json")
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
    }

    @Step("Getting the courier ID from the authorization")
    private String extractCourierId(Courier courier) {
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response response = given()
                .header("Content-type", "application/json")
                .body(credentials)
                .post("/api/v1/courier/login");

        response.then()
                .statusCode(200);

        return response.then().extract().path("id").toString();
    }
}

