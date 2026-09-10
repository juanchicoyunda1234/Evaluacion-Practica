package modelo;

public class NodoOleada {
    private Oleada dato;
    private NodoOleada siguiente;

    public NodoOleada(Oleada dato) {
        this.dato = dato;
    }

    public Oleada getDato() {
        return dato;
    }

    public NodoOleada getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoOleada siguiente) {
        this.siguiente = siguiente;
    }
}
