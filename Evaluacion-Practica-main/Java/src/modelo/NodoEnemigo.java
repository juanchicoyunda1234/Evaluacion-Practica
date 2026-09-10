package modelo;

public class NodoEnemigo {
    private Enemigo dato;
    private NodoEnemigo anterior;
    private NodoEnemigo siguiente;

    public NodoEnemigo(Enemigo dato) {
        this.dato = dato;
    }

    public Enemigo getDato() {
        return dato;
    }

    public NodoEnemigo getAnterior() {
        return anterior;
    }

    public NodoEnemigo getSiguiente() {
        return siguiente;
    }

    public void setDato(Enemigo dato) {
        this.dato = dato;
    }

    public void setAnterior(NodoEnemigo anterior) {
        this.anterior = anterior;
    }

    public void setSiguiente(NodoEnemigo siguiente) {
        this.siguiente = siguiente;
    }
}
