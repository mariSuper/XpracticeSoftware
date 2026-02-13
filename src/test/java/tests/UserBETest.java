package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.AddressModel;
import models.RequestUserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserBETest {

    //test TestNG (poate fi rulat din IDE / pipeline)
    @Test
    public void userTest(){
        //timeout explicit (dacă API nu răspunde → test FAIL, NU blocaj)
        RestAssured.config = RestAssured.config()
                .httpClient(
                        io.restassured.config.HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", 5000)
                                .setParam("http.socket.timeout", 5000)
                );

        //URL-ul de bază al API-ului (host-ul)
        RestAssured.baseURI ="https://api.practicesoftwaretesting.com";
        //creează request-ul HTTP (echivalentul unui Postman request)
        RequestSpecification request = RestAssured.given();
        //îi spune serverului: „trimit JSON”
        request.header("Content-Type", "application/json");
        //îi spune serverului: „vreau răspuns JSON”
        request.header("Accept", "application/json");

        //Construim Request body
        AddressModel addressModel = new AddressModel("Street 3",
                "City", "State", "Country", "1234AB");
        RequestUserModel requestBody = new RequestUserModel("Mari", "Ana", addressModel, "0987654322",
                "1970-01-01", "Super!Secure@123", "mari@yahoo.com");
        //atașează body-ul la request
        request.body(requestBody);
        //trimite POST către endpoint
        Response response = request.post("/users/register");
        //afișează statusul HTTP (ex: 201 Created)
        System.out.println(response.getStatusLine());
        //afișează răspunsul serverului frumos formatat
        response.body().prettyPrint();
        //Validare pe Back-End
        Assert.assertEquals(response.getStatusCode(), 201);

        WebDriver driver = new ChromeDriver();
        driver.get("https://practicesoftwaretesting.com/auth/login");
        driver.manage().window().maximize();

        WebElement emailElement = driver.findElement(By.id("email"));
        emailElement.sendKeys(requestBody.getEmail());

        WebElement passwordElement = driver.findElement(By.id("password"));
        passwordElement.sendKeys(requestBody.getPassword());

        WebElement loginButtonElement = driver.findElement(By.className("btnSubmit"));
        loginButtonElement.click();
    }
}
