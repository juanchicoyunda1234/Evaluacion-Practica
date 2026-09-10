package negocio;

import modelo.Torre;

public class ListaSecuencialTorres {
    private static final int MAX_TORRES = 20;

    private Torre[] torres;
    private int tope;

    public ListaSecuencialTorres() {
        this.torres = new Torre[MAX_TORRES];
        this.tope = 0;
    }

    public boolean insertarTorre(Torre torre) {
        if (tope >= MAX_TORRES) {
            return false;
        }
        torres[tope] = torre;
        tope++;
        return true;
    }

    public boolean eliminarTorrePorId(int id) {
        int posicion = buscarPosicionPorId(id);
        if (posicion == -1) {
            return false;
        }
        for (int i = posicion; i < tope - 1; i++) {
            torres[i] = torres[i + 1];
        }
        torres[tope - 1] = null;
        tope--;
        return true;
    }

    public Torre buscarTorrePorId(int id) {
        int posicion = buscarPosicionPorId(id);
        if (posicion == -1) {
            return null;
        }
        return torres[posicion];
    }

    private int buscarPosicionPorId(int id) {
        for (int i = 0; i < tope; i++) {
            if (torres[i].getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public void mostrarTodas() {
        if (tope == 0) {
            System.out.println("No hay torres registradas.");
            return;
        }
        for (int i = 0; i < tope; i++) {
            System.out.println(torres[i]);
        }
    }

    public int contarActivas() {
        return tope;
    }

    public Torre[] getTorres() {
        return torres;
    }

    public int getTope() {
        return tope;
    }
}
