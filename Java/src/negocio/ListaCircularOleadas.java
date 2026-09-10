package negocio;

import modelo.NodoOleada;
import modelo.Oleada;

public class ListaCircularOleadas {
    private NodoOleada ultimo;
    private NodoOleada actual;
    private int cantidad;

    public void registrarOleada(Oleada oleada) {
        NodoOleada nuevo = new NodoOleada(oleada);
        if (ultimo == null) {
            nuevo.setSiguiente(nuevo);
            ultimo = nuevo;
            actual = nuevo;
        } else {
            nuevo.setSiguiente(ultimo.getSiguiente());
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }
        cantidad++;
    }

    public void mostrarOleadas() {
        if (ultimo == null) {
            System.out.println("No hay oleadas registradas.");
            return;
        }
        NodoOleada primero = ultimo.getSiguiente();
        NodoOleada nodo = primero;
        do {
            System.out.println(nodo.getDato());
            nodo = nodo.getSiguiente();
        } while (nodo != primero);
    }

    public Oleada avanzarSiguienteOleada() {
        if (actual == null) {
            return null;
        }
        Oleada oleada = actual.getDato();
        actual = actual.getSiguiente();
        return oleada;
    }

    public void reiniciarCiclo() {
        if (ultimo != null) {
            actual = ultimo.getSiguiente();
        }
    }

    public boolean estaVacia() {
        return ultimo == null;
    }

    public int getCantidad() {
        return cantidad;
    }
}
