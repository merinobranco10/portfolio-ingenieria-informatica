package com.ubo.apirest.modelo;

/**
 *
 * @author branc
 */
public class Usuario {
    private int idUsuario;
    private String username;
    private String password;
    private String rol;
    private boolean activo;

    // Constructor vacío
    public Usuario() {}

    // Constructor completo
    public Usuario(int idUsuario, String username, String password, String rol, boolean activo) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
    }

    // Getters & Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
}