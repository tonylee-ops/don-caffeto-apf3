package com.doncaffeto.model;

/**
 * Mesa del salón con su estado y el tiempo que lleva ocupada.
 *
 * @author Equipo Don Caffeto
 */
public class Mesa {

    private int idMesa;
    private int numero;
    private int capacidad;
    private String estado;
    private long segundosOcupada;
    private long cargadaEn = System.currentTimeMillis();

    /** Crea una mesa vacía. */
    public Mesa() {
    }

    /**
     * @param idMesa código
     * @param numero número de mesa
     * @param estado LIBRE, OCUPADA o CUENTA
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

    /** @return cantidad de personas */
    public int getCapacidad() {
        return capacidad;
    }

    /** @param capacidad cantidad de personas */
    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    /** @return LIBRE, OCUPADA o CUENTA */
    public String getEstado() {
        return estado;
    }

    /** @param estado LIBRE, OCUPADA o CUENTA */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Guarda cuántos segundos llevaba ocupada al leerla de la base (lo calcula MySQL, así todos
     * los equipos ven el mismo tiempo aunque sus relojes no coincidan).
     *
     * @param segundos segundos ocupada al momento de la consulta
     */
    public void setSegundosOcupada(long segundos) {
        this.segundosOcupada = segundos;
        this.cargadaEn = System.currentTimeMillis();
    }

    /** @return segundos que lleva ocupada ahora mismo (0 si está libre) */
    public long getSegundosOcupada() {
        if ("LIBRE".equals(estado)) {
            return 0;
        }
        return segundosOcupada + (System.currentTimeMillis() - cargadaEn) / 1000;
    }

    /** @return true si la mesa está libre */
    public boolean isLibre() {
        return "LIBRE".equals(estado);
    }

    /** Se muestra así en los combos. */
    @Override
    public String toString() {
        return "Mesa " + numero;
    }
}
