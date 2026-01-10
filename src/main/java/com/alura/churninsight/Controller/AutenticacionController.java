package com.alura.churninsight.Controller;

import com.alura.churninsight.Security.TokenService;
import com.alura.churninsight.domain.Usuario.DatosAutenticacion;
import com.alura.churninsight.domain.Usuario.DatosTokenJWT;
import com.alura.churninsight.domain.Usuario.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AutenticacionController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<DatosTokenJWT> login(
            @RequestBody @Valid DatosAutenticacion datos) {

        var authToken = new UsernamePasswordAuthenticationToken(
                datos.correoElectronico(),
                datos.contrasena());

        var auth = authenticationManager.authenticate(authToken);
        var token = tokenService.generarToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new DatosTokenJWT(token));
    }

}
