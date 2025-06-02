package com.quipux.test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quipux.test.dto.DatosJWTToken;
import com.quipux.test.dto.UsuarioDTO;
import com.quipux.test.entities.Usuario;
import com.quipux.test.repository.UsuarioRepository;
import com.quipux.test.services.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }
	
	@PostMapping("/login")
	public ResponseEntity<DatosJWTToken> userAuth(@RequestBody @Valid UsuarioDTO usuarioDTO){
		Authentication authToken = new UsernamePasswordAuthenticationToken(usuarioDTO.login(), usuarioDTO.password());
		var authUser = authenticationManager.authenticate(authToken);
		var usuario = (Usuario) authUser.getPrincipal();
		var jwtToken = tokenService.genToken(usuario);
		return ResponseEntity.ok(new DatosJWTToken(jwtToken));
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody @Valid UsuarioDTO usuarioDTO){
		if(usuarioRepository.findByLogin(usuarioDTO.login()).isPresent()) {
			return ResponseEntity.badRequest().body("El usuario ya existe");
		}
		
		Usuario usuario = new Usuario();
		usuario.setLogin(usuarioDTO.login());
		usuario.setPassword(passwordEncoder.encode(usuarioDTO.password()));
		usuarioRepository.save(usuario);
		
		return ResponseEntity.ok("Usuario registrado correctamente");
	}
}
