import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final String[] colors;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    public CreateOrderTest(String[] colors) {
        this.colors = colors;
    }

// Параметризация для заполнения поля color
    @Parameterized.Parameters(name = "Colors: {0}")
    public static Object[][] getTestData() {
        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}}
        };
    }

    @Test
    @DisplayName("Create order with different color options")
    public void createOrderWithColors() {
        Order order = new Order("TestFirstName", "TestLastName", "TestAddress", "4", "+7 800 567 89 12", 5, "2024-12-01", "Comment", colors);

        // Вызов шага для создания заказа
        var response = OrderSteps.createOrder(order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}
