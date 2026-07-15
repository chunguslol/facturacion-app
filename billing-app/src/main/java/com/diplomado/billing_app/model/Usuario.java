package com.diplomado.billing_app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

    // VULNERABILIDAD A02: Guardaremos las contraseñas usando MD5 (algoritmo roto y obsoleto)
    private String passwordHash;
    private String rol;

    public Usuario() {}
    public Usuario(String username, String passwordHash, String rol) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRol() { return rol; }
}