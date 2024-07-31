package com.codigo.mslogin.service.impl;

import com.codigo.mslogin.aggregates.request.SignInRequest;
import com.codigo.mslogin.aggregates.request.SignUpRequest;
import com.codigo.mslogin.aggregates.response.AuthenticationResponse;
import com.codigo.mslogin.dao.RolRepository;
import com.codigo.mslogin.dao.UsuarioRepository;
import com.codigo.mslogin.entity.Rol;
import com.codigo.mslogin.entity.Role;
import com.codigo.mslogin.entity.Usuario;
import com.codigo.mslogin.service.AuthenticationService;
import com.codigo.mslogin.service.JwtService;
import com.codigo.mslogin.service.UsuarioService;
import com.codigo.mslogin.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UsuarioRepository usuarioRepository;

    private final RolRepository rolRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @Transactional
    @Override
    public Usuario signUpUser(SignUpRequest signUpRequest) {
        Usuario usuario = new Usuario();
        usuario.setNombre(signUpRequest.getNombres());
        usuario.setApellido(signUpRequest.getApellidos());
        usuario.setEmail(signUpRequest.getEmail());
        usuario.setTipoDoc(signUpRequest.getTipoDoc());
        usuario.setNumDoc(signUpRequest.getNumDoc());
        Set<Rol> assignedRols = new HashSet<>();
        Rol userRol = rolRepository.findByNombreRol(Role.USER.name()).orElseThrow(()-> new RuntimeException("El rol no exite"));
        assignedRols.add(userRol);
        usuario.setRoles(assignedRols);
        //hash al password pendiente
        usuario.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    @Override
    public Usuario signUpAdmin(SignUpRequest signUpRequest) {
        Usuario usuario = new Usuario();
        usuario.setNombre(signUpRequest.getNombres());
        usuario.setApellido(signUpRequest.getApellidos());
        usuario.setEmail(signUpRequest.getEmail());
        usuario.setTipoDoc(signUpRequest.getTipoDoc());
        usuario.setNumDoc(signUpRequest.getNumDoc());
        Set<Rol> assignedRols = new HashSet<>();
        Rol userRol = rolRepository.findByNombreRol(Role.ADMIN.name()).orElseThrow(()-> new RuntimeException("El rol no exite"));
        assignedRols.add(userRol);
        usuario.setRoles(assignedRols);
        //hash al password pendiente
        usuario.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> todos() {
        return usuarioRepository.findAll() ;
    }

    @Override
    public AuthenticationResponse signin(SignInRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));
        var user = usuarioRepository.findByEmail(signInRequest.getEmail()).orElseThrow(() -> new IllegalArgumentException("Email no valido"));
        var token = jwtService.generateToken(user);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(token);
        return authenticationResponse;
    }

    @Override
    public boolean validateToken(String token) {
        final String jwt;
        final String userEmail;

        if (AppUtil.isNotNullOrEmpty(token)){
            jwt = token.substring(7);
            userEmail = jwtService.estracUserName(jwt);
            if (AppUtil.isNotNullOrEmpty(userEmail)){
                UserDetails userDetails = usuarioService.userDetailsService().loadUserByUsername(userEmail);
                return jwtService.validateToken(jwt, userDetails);
            }
        }
        return false;
    }
}
