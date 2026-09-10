package modelo;

public class Oleada {
    private int idOleada;
    private int cantidadEnemigos;
    private String tipoEnemigo;
    private int vidaBase;
    private int velocidadBase;

    public Oleada(int idOleada, int cantidadEnemigos, String tipoEnemigo, int vidaBase, int velocidadBase) {
        this.idOleada = idOleada;
        this.cantidadEnemigos = cantidadEnemigos;
        this.tipoEnemigo = tipoEnemigo;
        this.vidaBase = vidaBase;
        this.velocidadBase = velocidadBase;
    }

    public int getIdOleada() {
        return idOleada;
    }

    public int getCantidadEnemigos() {
        return cantidadEnemigos;
    }

    public String getTipoEnemigo() {
        return tipoEnemigo;
    }

    public int getVidaBase() {
        return vidaBase;
    }

    public int getVelocidadBase() {
        return velocidadBase;
    }

    @Override
    public String toString() {
        return "Oleada #" + idOleada + " [" + tipoEnemigo + "] cantidad=" + cantidadEnemigos
                + " vidaBase=" + vidaBase + " velBase=" + velocidadBase;
    }
}
