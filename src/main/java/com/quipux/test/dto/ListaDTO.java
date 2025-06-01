package com.quipux.test.dto;

import java.util.List;

public record ListaDTO(
		String nombre,
		String descripcion,
		List<CancionDTO> canciones
		) {}
