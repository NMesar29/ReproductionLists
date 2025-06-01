package com.quipux.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quipux.test.entities.Cancion;

public interface CancionRepository extends JpaRepository<Cancion, String>{
	
}
