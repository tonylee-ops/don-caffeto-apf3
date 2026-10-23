package com.doncaffeto.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido de un cliente con sus productos.
 *
 * @author Equipo Don Caffeto
 */
public class Pedido {

    private int idPedido;
    private int idUsuario;
    private String mesero;
    private Integer idMesa;
    private Integer numeroMesa;
    private String modalidad;
    private String estado;
    private String direccion;
    private String telefono;
    private String observacion;
    private BigDecimal total = BigDecimal.ZERO;
    private LocalDateTime fecha;
    private final List<DetallePedido> detalles = new ArrayList<>();

    /** Crea un objeto vacío. */
    public Pedido() {
    }

    /** @return número del pedido */
    public int getIdPedido() {
        return idPedido;
    }

    /** @param idPedido número del pedido */
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    /** @return código del mesero */
    public int getIdUsuario() {
        return idUsuario;
    }

    /** @param idUsuario código del mesero */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /** @return nombre del mesero */
    public String getMesero() {
        return mesero;
    }

    /** @param mesero nombre del mesero */
    public void setMesero(String mesero) {
        this.mesero = mesero;
    }

    /** @return código de la mesa (solo salón) */
    public Integer getIdMesa() {
        return idMesa;
    }

    /** @param idMesa código de la mesa (solo salón) */
    public void setIdMesa(Integer idMesa) {
        this.idMesa = idMesa;
    }

    /** @return número de la mesa (solo salón) */
    public Integer getNumeroMesa() {
        return numeroMesa;
    }

    /** @param numeroMesa número de la mesa (solo salón) */
    public void setNumeroMesa(Integer numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    /** @return SALON, PARA_LLEVAR o DELIVERY */
    public String getModalidad() {
        return modalidad;
    }

    /** @param modalidad SALON, PARA_LLEVAR o DELIVERY */
    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    /** @return PENDIENTE, EN_PREPARACION, LISTO, PAGADO o CANCELADO */
    public String getEstado() {
        return estado;
    }

    /** @param estado PENDIENTE, EN_PREPARACION, LISTO, PAGADO o CANCELADO */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /** @return dirección de entrega (delivery) */
    public String getDireccion() {
        return direccion;
    }

    /** @param direccion dirección de entrega (delivery) */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /** @return teléfono de contacto (delivery) */
    public String getTelefono() {
        return telefono;
    }

    /** @param telefono teléfono de contacto (delivery) */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /** @return observación general del pedido */
    public String getObservacion() {
        return observacion;
    }

    /** @param observacion observación general del pedido */
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    /** @return total del pedido */
    public BigDecimal getTotal() {
        return total;
    }

    /** @param total total del pedido */
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    /** @return fecha y hora de registro */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /** @param fecha fecha y hora de registro */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /** @return productos del pedido */
    public List<DetallePedido> getDetalles() {
        return detalles;
    }
}
