package modelo;

public class Torre {
    private int id;
    private String nombre;
    private String tipo;
    private int posicion;
    private int danio;
    private int rango;
    private int costo;

    public Torre(int id, String nombre, String tipo, int posicion, int danio, int rango, int costo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.danio = danio;
        this.rango = rango;
        this.costo = costo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public int getPosicion() {
        return posicion;
    }

    public int getDanio() {
        return danio;
    }

    public int getRango() {
        return rango;
    }

    public int getCosto() {
        return costo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public void setDanio(int danio) {
        this.danio = danio;
    }

    public void setRango(int rango) {
        this.rango = rango;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }

    public boolean dentroDeRango(int posicionEnemigo) {
        return Math.abs(posicionEnemigo - this.posicion) <= this.rango;
    }

    @Override
    public String toString() {
        return "Torre #" + id + " [" + nombre + " - " + tipo + "] pos=" + posicion
                + " danio=" + danio + " rango=" + rango + " costo=" + costo;
    }
}
