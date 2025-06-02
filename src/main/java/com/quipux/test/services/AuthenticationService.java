package com.quipux.test.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.quipux.test.repository.UsuarioRepository;

@Service
public class AuthenticationService implements UserDetailsService{

	private final UsuarioRepository usuarioRepository;
	
    public AuthenticationService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return usuarioRepository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
	}
	


}
