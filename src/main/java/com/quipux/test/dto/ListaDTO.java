package com.quipux.test.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import com.quipux.test.entities.Lista;

public record ListaDTO(
		@NotBlank String nombre,
		String descripcion,
		@Valid List<CancionDTO> canciones
		) {
	
	public ListaDTO(Lista lista) {
		this(
				lista.getNombre(),
				lista.getDescripcion(),
				lista.getCanciones().stream().map(CancionDTO::new).toList()
			);
	}

}
