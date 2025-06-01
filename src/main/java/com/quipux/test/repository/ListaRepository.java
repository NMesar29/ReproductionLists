package com.quipux.test.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quipux.test.entities.Lista;

public interface ListaRepository extends JpaRepository<Lista, Long>{

	Optional<Lista> findByNombreIgnoreCase(String nombre);
	
	Optional<Lista> findBySlugIgnoreCase(String slug);

}
