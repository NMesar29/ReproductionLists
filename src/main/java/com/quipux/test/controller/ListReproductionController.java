package com.quipux.test.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import com.quipux.test.repository.ListaRepository;
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
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}

	}

	@PostMapping("/lists")
	public ResponseEntity<?> createList(@Valid @RequestBody ListaDTO listaDTO, UriComponentsBuilder uriBuilder) {
		try {
			Lista nuevaLista = listaService.createNewList(listaDTO);
			URI location = uriBuilder.path("/lists/{nombre}").buildAndExpand(nuevaLista.getSlug()).toUri();
			return ResponseEntity.created(location).body(new ListaDTO(nuevaLista));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
	
	@DeleteMapping("/lists/{listName}")
	public ResponseEntity<?> deleteLista(@PathVariable String listName){
		try {
			listaService.deleteListaBySlug(listName);
			return ResponseEntity.noContent().build();
		}catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

}
