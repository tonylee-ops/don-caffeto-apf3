package com.doncaffeto.model;

import java.math.BigDecimal;

/**
 * Producto dentro de un pedido, con su cantidad, precio y nota opcional.
 *
 * @author Equipo Don Caffeto
 */
public class DetallePedido {

    private int idProducto;
    private String producto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private String nota;

    /** Crea un objeto vacío. */
    public DetallePedido() {
    }

    /**
     * @param idProducto código del producto
     * @param producto nombre del producto
     * @param cantidad cantidad pedida
     * @param precioUnitario precio de una unidad
     */
    public DetallePedido(int idProducto, String producto, int cantidad, BigDecimal precioUnitario) {
        this.idProducto = idProducto;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    /** @return código del producto */
    public int getIdProducto() {
        return idProducto;
    }

    /** @param idProducto código del producto */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    /** @return nombre del producto */
    public String getProducto() {
        return producto;
    }

    /** @param producto nombre del producto */
    public void setProducto(String producto) {
        this.producto = producto;
    }

    /** @return cantidad pedida */
    public int getCantidad() {
        return cantidad;
    }

    /** @param cantidad cantidad pedida */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /** @return precio de una unidad */
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    /** @param precioUnitario precio de una unidad */
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /** @return nota para cocina (ej. sin azúcar) */
    public String getNota() {
        return nota;
    }

    /** @param nota nota para cocina (ej. sin azúcar) */
    public void setNota(String nota) {
        this.nota = nota;
    }

    /** @return importe de la línea (cantidad x precio) */
    public BigDecimal getImporte() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
