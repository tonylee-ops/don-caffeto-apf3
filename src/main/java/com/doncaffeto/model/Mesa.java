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

    /**
     * @param idMesa código
     * @param numero número visible de la mesa
     * @param estado LIBRE, OCUPADA o INACTIVA
     */
    public Mesa(int idMesa, int numero, String estado) {
        this.idMesa = idMesa;
        this.numero = numero;
        this.estado = estado;
    }

    /** @return código */
    public int getIdMesa() { return idMesa; }

    /** @return número de mesa */
    public int getNumero() { return numero; }

    /** @return estado de la mesa */
    public String getEstado() { return estado; }

    /** Se muestra así en el combo de mesas. */
    @Override
    public String toString() {
        return "Mesa " + numero;
    }
}
