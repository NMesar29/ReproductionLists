package com.quipux.test.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.quipux.test.repository.UsuarioRepository;
import com.quipux.test.services.TokenService;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public SecurityConfigurations(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.cors().and()
				.csrf(csrf -> csrf.disable())
		        .headers(headers -> headers.frameOptions().disable())
		        .authorizeHttpRequests(auth -> auth
		                .requestMatchers("/h2-console/**").permitAll()
		                .requestMatchers("/login", "/register").permitAll()
		                .anyRequest().authenticated()
		        		)
		        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		        .addFilterBefore(new SecurityFilter(tokenService, usuarioRepository), UsernamePasswordAuthenticationFilter.class)
		        .build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
