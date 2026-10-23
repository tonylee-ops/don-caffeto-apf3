package com.doncaffeto.model;

/**
 * Mesa del salón.
 *
 * @author Equipo Don Caffeto
 */
public class Mesa {

    private int idMesa;
    private int numero;
    private String estado;

    /** Crea un objeto vacío. */
    public Mesa() {
    }

    /**
     * @param idMesa código
     * @param numero número de mesa
     * @param estado LIBRE u OCUPADA
     */
    public Mesa(int idMesa, int numero, String estado) {
        this.idMesa = idMesa;
        this.numero = numero;
        this.estado = estado;
    }

    /** @return código */
    public int getIdMesa() {
        return idMesa;
    }

    /** @param idMesa código */
    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    /** @return número de mesa */
    public int getNumero() {
        return numero;
    }

    /** @param numero número de mesa */
    public void setNumero(int numero) {
        this.numero = numero;
    }

    /** @return LIBRE u OCUPADA */
    public String getEstado() {
        return estado;
    }

    /** @param estado LIBRE u OCUPADA */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /** Se muestra así en el combo de mesas. */
    @Override
    public String toString() {
        return "Mesa " + numero;
    }
}
