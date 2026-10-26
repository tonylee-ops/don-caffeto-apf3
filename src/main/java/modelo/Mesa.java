package modelo;

public class Mesa {
    private int idMesa;
    private int numero;
    private int capacidad;
    private String estado;
    private long segundosOcupada;
    private long cargadaEn = System.currentTimeMillis();

    public Mesa() {
    }

    public Mesa(int idMesa, int numero, String estado) {
        this.idMesa = idMesa;
        this.numero = numero;
        this.estado = estado;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setSegundosOcupada(long segundos) {
        this.segundosOcupada = segundos;
        this.cargadaEn = System.currentTimeMillis();
    }

    public long getSegundosOcupada() {
        if ("LIBRE".equals(estado)) {
            return 0;
        }
        return segundosOcupada + (System.currentTimeMillis() - cargadaEn) / 1000;
    }

    public boolean isLibre() {
        return "LIBRE".equals(estado);
    }

    @Override
    public String toString() {
        return "Mesa " + numero;
    }
}
