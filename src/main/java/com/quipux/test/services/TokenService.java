package com.quipux.test.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.quipux.test.controller.AuthenticationController;
import com.quipux.test.entities.Usuario;

@Service
public class TokenService {

    
	@Value("${api.security.secret}")
	private String apiSecret;

	public String genToken(Usuario usuario){
		try {
			Algorithm algorithm = Algorithm.HMAC256(apiSecret);
			return JWT.create()
					.withIssuer("reproduction")
					.withSubject(usuario.getLogin())
					.withClaim("id", usuario.getId())
					.withExpiresAt(genExpirationDate())
					.sign(algorithm);
		}catch (JWTCreationException e) {
			throw new RuntimeException("Error al generarl el Token", e);
		}
	}
	
	public String getSubject(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(apiSecret);
			DecodedJWT verifier = JWT.require(algorithm)
					.withIssuer("reproduction")
					.build()
					.verify(token);
			return verifier.getSubject();
		}catch (JWTVerificationException e) {
			throw new RuntimeException("Token inválido o expirado");
		}
	}
	
	private Instant genExpirationDate() {
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-05:00"));
	}
}
