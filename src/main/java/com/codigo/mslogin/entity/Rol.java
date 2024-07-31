package com.codigo.mslogin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.nio.file.LinkOption;

@Entity
@Table(name = "rol")
@Data
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    @Column(nullable = false, unique = true)
    private String nombreRol;
}
