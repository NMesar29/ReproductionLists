package com.quipux.test.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.quipux.test.dto.ListaDTO;
import com.quipux.test.entities.Lista;
import com.quipux.test.services.ListaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/")
public class ListReproductionController {

	private final ListaService listaService;

	ListReproductionController(ListaService listaService) {
		this.listaService = listaService;
	}

	@GetMapping("/lists")
	public List<ListaDTO> getLists() {
		return listaService.getAllListas();
	}

	@GetMapping("/lists/{listName}")
	public ResponseEntity<?> getListaByNombre(@PathVariable String listName) {
		try {
			Lista lista = listaService.getListaBySlug(listName);
			return ResponseEntity.ok(new ListaDTO(lista));
		}catch (IllegalArgumentException e) {
			Map<String,String> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
		}

	}

	@PostMapping("/lists")
	public ResponseEntity<?> createList(@Valid @RequestBody ListaDTO listaDTO, UriComponentsBuilder uriBuilder) {
		try {
			Lista nuevaLista = listaService.createNewList(listaDTO);
			URI location = uriBuilder.path("/lists/{nombre}").buildAndExpand(nuevaLista.getSlug()).toUri();
			return ResponseEntity.created(location).body(new ListaDTO(nuevaLista));
		} catch (Exception e) {
			Map<String,String> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}
	
	@DeleteMapping("/lists/{listName}")
	public ResponseEntity<?> deleteLista(@PathVariable String listName){
		try {
			listaService.deleteListaBySlug(listName);
			return ResponseEntity.noContent().build();
		}catch (IllegalArgumentException e) {
			Map<String,String> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
		}
	}

}
