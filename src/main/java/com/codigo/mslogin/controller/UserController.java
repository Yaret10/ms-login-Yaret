package com.codigo.mslogin.controller;

import com.codigo.mslogin.entity.Usuario;
import com.codigo.mslogin.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UsuarioService usuarioService;

    @GetMapping("/hola")
    public ResponseEntity<String> saludoAdmin(){
        return ResponseEntity.ok("Hola usuario");
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Usuario>> todos(){
        return ResponseEntity.ok(usuarioService.getUsuarios());
    }

}
