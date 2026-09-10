package negocio;

import modelo.Enemigo;
import modelo.NodoEnemigo;

public class ListaDobleEnemigos {
    private NodoEnemigo primero;
    private NodoEnemigo ultimo;
    private int cantidad;

    public void insertarFinal(Enemigo enemigo) {
        NodoEnemigo nuevo = new NodoEnemigo(enemigo);
        if (primero == null) {
            primero = nuevo;
            ultimo = nuevo;
        } else {
            nuevo.setAnterior(ultimo);
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }
        cantidad++;
    }

    public boolean eliminarDestruido(int id) {
        NodoEnemigo actual = primero;
        while (actual != null) {
            if (actual.getDato().getId() == id) {
                desconectar(actual);
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    private void desconectar(NodoEnemigo nodo) {
        NodoEnemigo anterior = nodo.getAnterior();
        NodoEnemigo siguiente = nodo.getSiguiente();

        if (anterior != null) {
            anterior.setSiguiente(siguiente);
        } else {
            primero = siguiente;
        }

        if (siguiente != null) {
            siguiente.setAnterior(anterior);
        } else {
            ultimo = anterior;
        }

        cantidad--;
    }

    public Enemigo buscarPorId(int id) {
        NodoEnemigo actual = primero;
        while (actual != null) {
            if (actual.getDato().getId() == id) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public void recorrerAdelante() {
        if (primero == null) {
            System.out.println("No hay enemigos activos.");
            return;
        }
        NodoEnemigo actual = primero;
        while (actual != null) {
            System.out.println(actual.getDato());
            actual = actual.getSiguiente();
        }
    }

    public void recorrerAtras() {
        if (ultimo == null) {
            System.out.println("No hay enemigos activos.");
            return;
        }
        NodoEnemigo actual = ultimo;
        while (actual != null) {
            System.out.println(actual.getDato());
            actual = actual.getAnterior();
        }
    }

    public void actualizarPosiciones() {
        NodoEnemigo actual = primero;
        while (actual != null) {
            actual.getDato().avanzar();
            actual = actual.getSiguiente();
        }
    }

    public NodoEnemigo getPrimero() {
        return primero;
    }

    public NodoEnemigo getUltimo() {
        return ultimo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public boolean estaVacia() {
        return primero == null;
    }
}
