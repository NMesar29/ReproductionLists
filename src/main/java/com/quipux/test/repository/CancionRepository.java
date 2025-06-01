package com.quipux.test.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quipux.test.entities.Cancion;

public interface CancionRepository extends JpaRepository<Cancion, Long>{

	Optional<Cancion> findByTituloAndArtista(String titulo, String artista);
	
}
