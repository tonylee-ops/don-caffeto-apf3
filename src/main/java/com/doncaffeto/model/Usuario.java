package com.doncaffeto.model;

/**
 * Usuario del sistema (mesero, cajero, cocina o administrador).
 *
 * @author Equipo Don Caffeto
 */
public class Usuario {

    private int idUsuario;
    private String username;
    private String passwordHash;
    private String nombres;
    private String rol;

    /** Crea un usuario vacío. */
    public Usuario() {
    }

    /**
     * @param idUsuario código del usuario
     * @param username  nombre de acceso
     * @param nombres   nombres de la persona
     * @param rol       ADMIN, MESERO, COCINA o CAJERO
     */
    public Usuario(int idUsuario, String username, String nombres, String rol) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.nombres = nombres;
        this.rol = rol;
    }

    /** @return código del usuario */
    public int getIdUsuario() { return idUsuario; }

    /** @param idUsuario código del usuario */
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    /** @return nombre de acceso */
    public String getUsername() { return username; }

    /** @param username nombre de acceso */
    public void setUsername(String username) { this.username = username; }

    /** @return contraseña cifrada con BCrypt */
    public String getPasswordHash() { return passwordHash; }

    /** @param passwordHash contraseña cifrada con BCrypt */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /** @return nombres de la persona */
    public String getNombres() { return nombres; }

    /** @param nombres nombres de la persona */
    public void setNombres(String nombres) { this.nombres = nombres; }

    /** @return rol del usuario */
    public String getRol() { return rol; }

    /** @param rol rol del usuario */
    public void setRol(String rol) { this.rol = rol; }
}
