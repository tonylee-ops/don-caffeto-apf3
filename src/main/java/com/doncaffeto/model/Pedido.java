package com.doncaffeto.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido de un cliente con sus líneas de detalle.
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
    private BigDecimal total = BigDecimal.ZERO;
    private LocalDateTime fecha;
    private final List<DetallePedido> detalles = new ArrayList<>();

    /** @return código del pedido */
    public int getIdPedido() { return idPedido; }

    /** @param idPedido código del pedido */
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    /** @return código del mesero que registró el pedido */
    public int getIdUsuario() { return idUsuario; }

    /** @param idUsuario código del mesero */
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    /** @return nombre del mesero */
    public String getMesero() { return mesero; }

    /** @param mesero nombre del mesero */
    public void setMesero(String mesero) { this.mesero = mesero; }

    /** @return código de la mesa (null si no es salón) */
    public Integer getIdMesa() { return idMesa; }

    /** @param idMesa código de la mesa */
    public void setIdMesa(Integer idMesa) { this.idMesa = idMesa; }

    /** @return número de la mesa (null si no es salón) */
    public Integer getNumeroMesa() { return numeroMesa; }

    /** @param numeroMesa número de la mesa */
    public void setNumeroMesa(Integer numeroMesa) { this.numeroMesa = numeroMesa; }

    /** @return SALON, PARA_LLEVAR o DELIVERY */
    public String getModalidad() { return modalidad; }

    /** @param modalidad SALON, PARA_LLEVAR o DELIVERY */
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    /** @return estado del pedido (REGISTRADO, CONFIRMADO, ...) */
    public String getEstado() { return estado; }

    /** @param estado estado del pedido */
    public void setEstado(String estado) { this.estado = estado; }

    /** @return dirección de entrega (delivery) */
    public String getDireccion() { return direccion; }

    /** @param direccion dirección de entrega */
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /** @return teléfono de contacto (delivery) */
    public String getTelefono() { return telefono; }

    /** @param telefono teléfono de contacto */
    public void setTelefono(String telefono) { this.telefono = telefono; }

    /** @return total del pedido */
    public BigDecimal getTotal() { return total; }

    /** @param total total del pedido */
    public void setTotal(BigDecimal total) { this.total = total; }

    /** @return fecha y hora de registro */
    public LocalDateTime getFecha() { return fecha; }

    /** @param fecha fecha y hora de registro */
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    /** @return líneas del pedido */
    public List<DetallePedido> getDetalles() { return detalles; }
}
