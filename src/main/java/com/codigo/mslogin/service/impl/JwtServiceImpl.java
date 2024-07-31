package com.codigo.mslogin.service.impl;

import com.codigo.mslogin.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.hibernate.service.spi.Stoppable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {



    @Value("${key.signature}")//clave para mi token
    private String keySignature;
    @Override
    public String estracUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {

        return Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+120000))
                .signWith(getSegnKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = estracUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));


    }

    //Devuelve clave operativa para firmar el token
    private Key getSegnKey(){
        byte[] key = Decoders.BASE64.decode(keySignature);
        return Keys.hmacShaKeyFor(key);
    }

    //Extrae el Pyload del tocken, requiere formarse para acceder al contenido
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getSegnKey()).build().parseClaimsJws(token).getBody();
    }

     //metodo que te devuelve un objeto del body o tambien denominado Claim

    private <T> T extractClaims(String token, Function<Claims,T> claimResult){
        final Claims claims = extractAllClaims(token);
        return claimResult.apply(claims);
    }

    //metodo para validar el token
    private boolean isTokenExpired(String token){
        return  extractClaims(token,Claims::getExpiration).before(new Date());
    }


}
