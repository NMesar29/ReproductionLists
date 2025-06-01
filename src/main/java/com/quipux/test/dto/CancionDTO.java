package com.quipux.test.dto;

import com.quipux.test.entities.Cancion;
import jakarta.validation.constraints.NotBlank;

public record CancionDTO(
		@NotBlank String titulo,
		@NotBlank String artista,
		String album,
		int anno,
		String genero
		) {

	public CancionDTO(Cancion cancion) {
		this(cancion.getTitulo(), cancion.getArtista(), cancion.getAlbum(), cancion.getAnno(), cancion.getGenero());
	}
}
