package com.quipux.test.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.quipux.test.dto.ListaDTO;
import com.quipux.test.entities.Cancion;
import com.quipux.test.entities.Lista;
import com.quipux.test.repository.CancionRepository;
import com.quipux.test.repository.ListaRepository;

import jakarta.validation.constraints.NotBlank;

@Service
public class ListaService {
	
	private final ListaRepository listaRepository;
	private final CancionRepository cancionRepository;
	
	public ListaService(ListaRepository listaRepository, CancionRepository cancionRepository) {
		this.listaRepository = listaRepository;
		this.cancionRepository = cancionRepository;
	}
	
    public List<ListaDTO> getAllListas() {
        return listaRepository.findAll().stream()
            .map(ListaDTO::new)
            .toList();
    }
    
    public Lista createNewList(ListaDTO listaDTO) {
        if (existsByNombre(listaDTO.nombre())) {
            throw new IllegalArgumentException("Ya existe una lista con ese nombre");
        }
    	Lista lista = new Lista();
    	lista.setNombre(listaDTO.nombre());
    	lista.setSlug(toSlug(listaDTO.nombre()));
    	lista.setDescripcion(listaDTO.descripcion());
    	List<Cancion> canciones = listaDTO.canciones().stream()
    			.map(c -> {
    				return cancionRepository.findByTituloAndArtista(c.titulo(), c.artista())
    						.orElseGet(() -> {
    							Cancion nuevaCancion = new Cancion();
    							nuevaCancion.setTitulo(c.titulo());
    							nuevaCancion.setArtista(c.artista());
    							nuevaCancion.setAlbum(c.album());
    							nuevaCancion.setAnno(c.anno());
    							nuevaCancion.setGenero(c.genero());
    							return nuevaCancion;
    						});
    			}).toList();
    	
    	lista.setCanciones(canciones);    	
    	return listaRepository.save(lista);
    }

	public boolean existsByNombre(String nombre) {
	    return listaRepository.findByNombreIgnoreCase(nombre).isPresent();
	}
	
	public Optional<Lista> getListaByNombre(String nombre){
		return listaRepository.findByNombreIgnoreCase(nombre);
	}

	public void deleteListaBySlug(String listName) {
		Lista lista = getListaBySlug(listName);
        listaRepository.delete(lista);
	}
	
	public Lista getListaBySlug(String slug){
		Lista lista = listaRepository.findBySlugIgnoreCase(slug).orElseThrow(() -> new IllegalArgumentException("Lista no Encontrada"));
		return lista;
	}
	
	public String toSlug(String input) {
		return input == null ? null : input.toLowerCase()
				.replaceAll("[^a-z0-9\\s-]", "")
				.replaceAll("\\s+","-")
				.replaceAll("-+", "-")
				.trim();
	}
}
