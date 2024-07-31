package com.codigo.mslogin.service;

import org.springframework.security.core.userdetails.UserDetails;

//genera token , valida token,
public interface JwtService {
    String estracUserName(String token);
    String generateToken(UserDetails userDetails);//sacar datos de usuario
    boolean validateToken(String token, UserDetails userDetails);


}
