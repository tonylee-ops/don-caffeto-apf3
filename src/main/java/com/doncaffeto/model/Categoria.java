package com.doncaffeto.model;

/**
 * Categoría de la carta.
 *
 * @author Equipo Don Caffeto
 */
public class Categoria {

    private int idCategoria;
    private String nombre;

    /** Crea un objeto vacío. */
    public Categoria() {
    }

    /**
     * @param idCategoria código
     * @param nombre nombre de la categoría
     */
    public Categoria(int idCategoria, String nombre) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
    }

    /** @return código */
    public int getIdCategoria() {
        return idCategoria;
    }

    /** @param idCategoria código */
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    /** @return nombre de la categoría */
    public String getNombre() {
        return nombre;
    }

    /** @param nombre nombre de la categoría */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** Se muestra así en los combos. */
    @Override
    public String toString() {
        return nombre;
    }
}
