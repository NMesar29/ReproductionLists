package com.quipux.test.entities;

import java.util.ArrayList;
import java.util.List;

import com.quipux.test.dto.ListaDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Table(name = "lista")
@Entity(name = "Lista")
public class Lista {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false)
	private String nombre;
	private String descripcion;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<Cancion> canciones;
	
	@Column(unique=true, nullable=false)
	private String slug;
	
	public Lista() {
		this.canciones = new ArrayList<>();
	}
	
	
	public Lista(ListaDTO listaDTO) {
		// TODO Auto-generated constructor stub
	}


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public List<Cancion> getCanciones() {
		return canciones;
	}
	public void setCanciones(List<Cancion> canciones) {
		this.canciones = canciones;
	}
	
}
