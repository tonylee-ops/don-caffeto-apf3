package modelo;

import java.math.BigDecimal;

public class DetallePedido {
    private int idProducto;
    private String producto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private String nota;

    public DetallePedido() {
    }

    public DetallePedido(int idProducto, String producto, int cantidad, BigDecimal precioUnitario) {
        this.idProducto = idProducto;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public BigDecimal getImporte() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
