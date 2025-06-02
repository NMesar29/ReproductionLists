package com.quipux.test.controller;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.quipux.test.dto.CancionDTO;
import com.quipux.test.dto.ListaDTO;
import com.quipux.test.entities.Usuario;
import com.quipux.test.repository.UsuarioRepository;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ListReproductionControllerTest {

	@LocalServerPort
	private int port;
	RequestSpecification req;
	private String jwtToken;
	private String createdSlug;
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	

	@BeforeAll
	public void setupUser() {
		if (usuarioRepository.findByLogin("testuser").isEmpty()) {
			Usuario user = new Usuario();
			user.setLogin("testuser");
			user.setPassword(passwordEncoder.encode("123456"));
			usuarioRepository.save(user);
		}
	}
	
	@BeforeEach
	public void doLogin() {
		req = new RequestSpecBuilder().setBaseUri("http://localhost").setPort(port).setContentType(ContentType.JSON)
				.build();
		req = given().log().all().spec(req);
		
		if(jwtToken == null) {
			String payload = """
		            {
		                "login": "testuser",
		                "password": "123456"
		            }
		        """;

	        Response response = given().spec(req).body(payload).when().post("/login");

	        response.then().statusCode(200);
	        jwtToken = response.jsonPath().getString("jwtToken");
		}
	    
	}

	@Test
	@Order(1)
	public void testCreateNewListaSuccess() {
		List<CancionDTO> canciones = new ArrayList<>();
		canciones.add(new CancionDTO("Cancion 1","Artista 1", "Album Artista 1", 2001, "Rock"));
		canciones.add(new CancionDTO("Cancion 2","Artista 2", "Album Artista 2", 2005, "Jazz"));
		canciones.add(new CancionDTO("Cancion 3","Artista 1", "Album Artista", 2020, "R&B"));
		ListaDTO newLista = new ListaDTO("Lista de Prueba",
				"Esto es una Lista de Prueba",canciones);
		
		Response response = req.header("Authorization", "Bearer " + jwtToken)
				.body(newLista).when().post("/lists");
		validateResponse(response, 201);
		String nombreLista = response.jsonPath().getString("nombre");
		assertEquals("Lista de Prueba", nombreLista);
		
		String location = response.getHeader("Location");
		String slug = location.substring(location.lastIndexOf("/") + 1);
		createdSlug = slug;
	}

	@Test
	@Order(2)
	public void testVerListasSuccess() {
		Response response = req.header("Authorization", "Bearer " + jwtToken)
				.when().get("/lists");
		validateResponse(response, 200);
		
		List<?> listas = response.jsonPath().getList("$");
		assertNotNull(listas);
		assertTrue(listas.size() > 0, "La lista de Listas no debe estar vacía");
	}

	@Test
	@Order(3)
	public void testFindListaSuccess() {
		Response response = req.header("Authorization", "Bearer " + jwtToken)
				.when().get("/lists/{listName}", createdSlug);
		validateResponse(response, 200);
		
		String listaNombre = response.jsonPath().getString("nombre");
		assertNotNull(listaNombre);
		assertEquals("Lista de Prueba", listaNombre);
	}

	@Test
	@Order(4)
	public void testDeleteListaSuccess() {
		Response response = req.header("Authorization", "Bearer " + jwtToken)
				.when().delete("/lists/{listName}", createdSlug);
		validateResponse(response, 204);
	}

	@Test
	@Order(5)
	public void testCreateListaNombreEmpty() {
		List<CancionDTO> canciones = new ArrayList<>();
		canciones.add(new CancionDTO("Cancion 1","Artista 1", "Album Artista 1", 2001, "Rock"));
		canciones.add(new CancionDTO("Cancion 2","Artista 2", "Album Artista 2", 2005, "Jazz"));
		canciones.add(new CancionDTO("Cancion 3","Artista 1", "Album Artista", 2020, "R&B"));
		ListaDTO newLista = new ListaDTO("",
				"Esto es una Lista de Prueba",canciones);
		
		Response response = req.header("Authorization", "Bearer " + jwtToken)
				.body(newLista).when().post("/lists");
		validateResponse(response, 400);
		
	}

	@Test
	@Order(6)
	public void testFindListaDontExists() {
		String slug = "lista-inexistente";
		Response response = req.header("Authorization", "Bearer " + jwtToken)
				.when().get("/lists/{listName}", slug);
		validateResponse(response, 404);
		String error = response.jsonPath().getString("error");
		assertNotNull(error);
		assertEquals("Lista no Encontrada", error);
	}

	@Test
	@Order(7)
	public void testDeleteListaDontExists() {
		String slug = "lista-inexistente";
		Response response = req.header("Authorization", "Bearer " + jwtToken)
				.when().delete("/lists/{listName}", slug);
		validateResponse(response, 404);
		String error = response.jsonPath().getString("error");
		assertNotNull(error);
		assertEquals("Lista no Encontrada", error);
	}
	
	private void validateResponse(Response response, int expectedStatusCode) {
		response.then().assertThat().statusCode(expectedStatusCode);
	}

}
