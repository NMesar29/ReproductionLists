package com.quipux.test.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.*;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {

    @LocalServerPort
    private int port;
	RequestSpecification req;
	
	String payloadCorrectUser = """
		    {
		        "login": "testuser",
		        "password": "123456"
		    }
		""";

	@BeforeEach
	public void createRestSpecification() {
		req = new RequestSpecBuilder().setBaseUri("http://localhost")
				.setPort(port).setContentType(ContentType.JSON).build();
		req = given().log().all().spec(req);
	}

	@Test
	@Order(1)
	public void testCreateUser() {
		Response response = req.body(payloadCorrectUser).when().post("/register");
		validateResponse(response, 200);
		assertEquals("Usuario registrado correctamente",response.getBody().asString());
	}
	
	@Test
	@Order(2)
	public void testCreateExistingUser() {
		Response response = req.body(payloadCorrectUser).when().post("/register");
		validateResponse(response, 400);
		assertEquals("El usuario ya existe",response.getBody().asString());
	}

	@Test
	@Order(3)
	public void testDoLoginSuccess() {
		Response response = req.body(payloadCorrectUser).when().post("/login");
		validateResponse(response, 200);
		String jwtToken = response.jsonPath().getString("jwtToken");
		assertNotNull(jwtToken);
	}

	@Test
	@Order(4)
	public void testDoLoginIncorrectPassword() {
		String payload = """
			    {
			        "login": "testuser",
			        "password": "wrongpassword"
			    }
			""";
		Response response = req.body(payload).when().post("/login");
		validateResponse(response, 403);
	}

	@Test
	@Order(5)
	public void testDoLoginUserDontExists() {
		String payload = """
			    {
			        "login": "nouser",
			        "password": "123456"
			    }
			""";
		Response response = req.body(payload).when().post("/login");
		validateResponse(response, 403);
	}

	private void validateResponse(Response response, int expectedStatusCode) {
		response.then().assertThat().statusCode(expectedStatusCode);
	}

}
