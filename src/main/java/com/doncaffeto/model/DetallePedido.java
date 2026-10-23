package com.doncaffeto.model;

import java.math.BigDecimal;

/**
 * Línea de un pedido: producto, cantidad y precio.
 *
 * @author Equipo Don Caffeto
 */
public class DetallePedido {

    private int idProducto;
    private String producto;
    private int cantidad;
    private BigDecimal precioUnitario;

    /**
     * @param idProducto     código del producto
     * @param producto       nombre del producto
     * @param cantidad       cantidad pedida
     * @param precioUnitario precio de una unidad
     */
    public DetallePedido(int idProducto, String producto, int cantidad, BigDecimal precioUnitario) {
        this.idProducto = idProducto;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    /** @return importe de la línea (cantidad x precio) */
    public BigDecimal getImporte() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    /** @return código del producto */
    public int getIdProducto() { return idProducto; }

    /** @return nombre del producto */
    public String getProducto() { return producto; }

    /** @return cantidad pedida */
    public int getCantidad() { return cantidad; }

    /** @param cantidad cantidad pedida */
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    /** @return precio de una unidad */
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
}
