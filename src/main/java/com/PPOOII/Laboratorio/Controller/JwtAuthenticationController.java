package com.PPOOII.Laboratorio.Controller;

import com.PPOOII.Laboratorio.Config.JWTAuthtenticationConfig;
import com.PPOOII.Laboratorio.Config.Model.JwtRequest;
import com.PPOOII.Laboratorio.Config.Model.JwtResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    private final JWTAuthtenticationConfig jwtAuthtenticationConfig;
    private final UserDetailsService jwtInMemoryUserDetailsService;

    public JwtAuthenticationController(
        JWTAuthtenticationConfig jwtAuthtenticationConfig,
        @Qualifier("jwtInMemoryUserDetailsService") UserDetailsService jwtInMemoryUserDetailsService
    ) {
        this.jwtAuthtenticationConfig = jwtAuthtenticationConfig;
        this.jwtInMemoryUserDetailsService = jwtInMemoryUserDetailsService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        System.out.println("Intentando autenticar al usuario: " + authenticationRequest.getUsername());

        UserDetails userDetails = jwtInMemoryUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        if (userDetails.getPassword() == null
            || !userDetails.getPassword().equals(authenticationRequest.getPassword())) {
            System.out.println("Credenciales invalidas para el usuario: " + authenticationRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales invalidas");
        }

        System.out.println("Usuario autenticado correctamente: " + authenticationRequest.getUsername());
        String token = jwtAuthtenticationConfig.getJWTToken(authenticationRequest.getUsername());
        System.out.println("JWT generado correctamente para el usuario: " + authenticationRequest.getUsername());

        return ResponseEntity.ok(new JwtResponse(token));
    }
}
