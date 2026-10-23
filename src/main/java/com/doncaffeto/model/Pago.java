package com.doncaffeto.model;

import java.math.BigDecimal;

/**
 * Pago de un pedido registrado por el cajero.
 *
 * @author Equipo Don Caffeto
 */
public class Pago {

    private int idPedido;
    private int idUsuario;
    private String metodo;
    private BigDecimal monto;
    private BigDecimal recibido;
    private BigDecimal vuelto;

    /** Crea un objeto vacío. */
    public Pago() {
    }

    /** @return pedido que se cobra */
    public int getIdPedido() {
        return idPedido;
    }

    /** @param idPedido pedido que se cobra */
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    /** @return cajero que cobra */
    public int getIdUsuario() {
        return idUsuario;
    }

    /** @param idUsuario cajero que cobra */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /** @return EFECTIVO, YAPE, PLIN o TARJETA */
    public String getMetodo() {
        return metodo;
    }

    /** @param metodo EFECTIVO, YAPE, PLIN o TARJETA */
    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    /** @return monto a cobrar (total del pedido) */
    public BigDecimal getMonto() {
        return monto;
    }

    /** @param monto monto a cobrar (total del pedido) */
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    /** @return dinero recibido */
    public BigDecimal getRecibido() {
        return recibido;
    }

    /** @param recibido dinero recibido */
    public void setRecibido(BigDecimal recibido) {
        this.recibido = recibido;
    }

    /** @return vuelto entregado */
    public BigDecimal getVuelto() {
        return vuelto;
    }

    /** @param vuelto vuelto entregado */
    public void setVuelto(BigDecimal vuelto) {
        this.vuelto = vuelto;
    }
}
