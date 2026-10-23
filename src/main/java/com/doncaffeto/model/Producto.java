package com.doncaffeto.model;

import java.math.BigDecimal;

/**
 * Producto de la carta.
 *
 * @author Equipo Don Caffeto
 */
public class Producto {

    private int idProducto;
    private int idCategoria;
    private String nombre;
    private String categoria;
    private BigDecimal precio;
    private boolean disponible = true;

    /** Crea un objeto vacío. */
    public Producto() {
    }

    /**
     * @param idProducto código
     * @param nombre nombre del producto
     * @param categoria nombre de la categoría
     * @param precio precio de venta (incluye IGV)
     */
    public Producto(int idProducto, String nombre, String categoria, BigDecimal precio) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
    }

    /** @return código */
    public int getIdProducto() {
        return idProducto;
    }

    /** @param idProducto código */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    /** @return código de la categoría */
    public int getIdCategoria() {
        return idCategoria;
    }

    /** @param idCategoria código de la categoría */
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    /** @return nombre del producto */
    public String getNombre() {
        return nombre;
    }

    /** @param nombre nombre del producto */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** @return nombre de la categoría */
    public String getCategoria() {
        return categoria;
    }

    /** @param categoria nombre de la categoría */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /** @return precio de venta (incluye IGV) */
    public BigDecimal getPrecio() {
        return precio;
    }

    /** @param precio precio de venta (incluye IGV) */
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    /** @return true si se puede vender */
    public boolean isDisponible() {
        return disponible;
    }

    /** @param disponible true si se puede vender */
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
