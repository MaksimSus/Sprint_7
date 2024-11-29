import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierLoginTest {
    private String courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @After
    public void cleanUp() {
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Login successfully with valid credentials")
    public void loginSuccessfully() {
        Courier courier = new Courier("validLoginMaks", "validPassword", "ValidName");
        createCourier(courier);

        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response response = loginCourier(credentials);

        response.then()
                .statusCode(200)
                .body("id", equalTo(Integer.parseInt(extractCourierId(courier))));
    }

    @Test
    @DisplayName("Login fails with incorrect login")
    public void loginFailsWithIncorrectLogin() {
        Courier courier = new Courier("correctLoginMaks", "correctPassword", "CorrectName");
        createCourier(courier);

        CourierCredentials incorrectLogin = new CourierCredentials("wrongLogin", courier.getPassword());
        Response response = loginCourier(incorrectLogin);

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Login fails with incorrect password")
    public void loginFailsWithIncorrectPassword() {
        Courier courier = new Courier("anotherLoginMaks", "anotherPassword", "AnotherName");
        createCourier(courier);

        CourierCredentials incorrectPassword = new CourierCredentials(courier.getLogin(), "wrongPassword");
        Response response = loginCourier(incorrectPassword);

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Login fails when required fields are missing")
    public void loginFailsWithoutRequiredFields() {
        Courier courier = new Courier("missingFieldsLoginMaks", "missingFieldsPassword", "MissingFieldsName");
        createCourier(courier);

        // Пробуем авторизоваться без логина
        CourierCredentials noLogin = new CourierCredentials("", courier.getPassword());
        Response responseNoLogin = loginCourier(noLogin);

        responseNoLogin.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));

        // Пробуем авторизоваться без пароля
        CourierCredentials noPassword = new CourierCredentials(courier.getLogin(), "");
        Response responseNoPassword = loginCourier(noPassword);

        responseNoPassword.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    // Шаги с аннотацией @Step

    @Step("Creating courier with login: {courier.login}")
    private Response createCourier(Courier courier) {
        //Создание курьера
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");

        if (response.statusCode() == 201) {
            courierId = extractCourierId(courier);
        }
        return response;
    }

    @Step("Deleting courier with ID: {courierId}")
    private void deleteCourier(String courierId) {
        //Удаление курьера
        given()
                .header("Content-type", "application/json")
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
    }

    @Step("Logging courier with login: {credentials.login}")
    private Response loginCourier(CourierCredentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
                .post("/api/v1/courier/login");
    }

    @Step("Extracting courier ID after login")
    private String extractCourierId(Courier courier) {
        //Получаем ID курьера после авторизации
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response response = loginCourier(credentials);

        response.then()
                .statusCode(200);


        Integer id = response.then().extract().path("id");
        return String.valueOf(id);
    }

    @Step("Getting courier ID for comparison")
    private String getCourierId(Courier courier) {
        //Передаем id для сравнения
        return extractCourierId(courier);
    }
}