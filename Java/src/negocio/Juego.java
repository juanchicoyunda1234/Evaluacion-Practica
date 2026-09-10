package negocio;

import modelo.Enemigo;
import modelo.NodoEnemigo;
import modelo.Oleada;
import modelo.Torre;

public class Juego {
    private static final int LONGITUD_RUTA = 20;

    private ListaSecuencialTorres torres;
    private ListaDobleEnemigos enemigosActivos;
    private ListaCircularOleadas oleadas;

    private int vidasJugador;
    private int turnoActual;
    private int contadorIdEnemigo;

    private boolean oleadaEnCurso;
    private Oleada oleadaActual;
    private int enemigosPorGenerar;
    private int oleadasIniciadas;

    private boolean partidaTerminada;
    private boolean partidaGanada;

    public Juego(int vidasIniciales) {
        this.torres = new ListaSecuencialTorres();
        this.enemigosActivos = new ListaDobleEnemigos();
        this.oleadas = new ListaCircularOleadas();
        this.vidasJugador = vidasIniciales;
        this.turnoActual = 0;
        this.contadorIdEnemigo = 1;
        this.oleadaEnCurso = false;
        this.oleadasIniciadas = 0;
        this.partidaTerminada = false;
        this.partidaGanada = false;
    }

    public boolean registrarTorre(Torre torre) {
        return torres.insertarTorre(torre);
    }

    public boolean eliminarTorre(int id) {
        return torres.eliminarTorrePorId(id);
    }

    public void mostrarTorres() {
        torres.mostrarTodas();
    }

    public void registrarOleada(Oleada oleada) {
        oleadas.registrarOleada(oleada);
    }

    public void mostrarOleadas() {
        oleadas.mostrarOleadas();
    }

    public boolean iniciarSiguienteOleada() {
        if (partidaTerminada) {
            System.out.println("La partida ya termino.");
            return false;
        }
        if (oleadaEnCurso) {
            System.out.println("Ya hay una oleada en curso.");
            return false;
        }
        if (oleadas.estaVacia()) {
            System.out.println("No hay oleadas registradas.");
            return false;
        }

        Oleada oleada = oleadas.avanzarSiguienteOleada();
        this.oleadaActual = oleada;
        this.enemigosPorGenerar = oleada.getCantidadEnemigos();
        this.oleadaEnCurso = true;
        this.oleadasIniciadas++;

        System.out.println("Inicia la oleada #" + oleada.getIdOleada() + " (" + oleada.getTipoEnemigo() + ")");
        return true;
    }

    public void avanzarTurno() {
        if (partidaTerminada) {
            System.out.println("La partida ya termino.");
            return;
        }

        turnoActual++;
        System.out.println("--- Turno " + turnoActual + " ---");

        generarEnemigoDeOleada();
        enemigosActivos.actualizarPosiciones();
        aplicarAtaquesDeTorres();
        eliminarEnemigosDestruidos();
        descontarVidasPorEnemigosQueLlegaron();
        mostrarResumenDeTurno();
        verificarFinDePartida();
    }

    private void generarEnemigoDeOleada() {
        if (!oleadaEnCurso) {
            return;
        }
        if (enemigosPorGenerar <= 0) {
            oleadaEnCurso = false;
            return;
        }

        Enemigo nuevo = new Enemigo(
                contadorIdEnemigo,
                oleadaActual.getTipoEnemigo(),
                oleadaActual.getVidaBase(),
                oleadaActual.getVelocidadBase(),
                0,
                oleadaActual.getVidaBase() / 2);
        contadorIdEnemigo++;
        enemigosActivos.insertarFinal(nuevo);
        enemigosPorGenerar--;
    }

    private void aplicarAtaquesDeTorres() {
        Torre[] listaTorres = torres.getTorres();
        int totalTorres = torres.getTope();

        NodoEnemigo actual = enemigosActivos.getPrimero();
        while (actual != null) {
            Enemigo enemigo = actual.getDato();
            for (int i = 0; i < totalTorres && !enemigo.estaDestruido(); i++) {
                Torre torre = listaTorres[i];
                if (torre.dentroDeRango(enemigo.getPosicion())) {
                    enemigo.recibirDanio(torre.getDanio());
                }
            }
            actual = actual.getSiguiente();
        }
    }

    private void eliminarEnemigosDestruidos() {
        NodoEnemigo actual = enemigosActivos.getPrimero();
        while (actual != null) {
            NodoEnemigo siguiente = actual.getSiguiente();
            if (actual.getDato().estaDestruido()) {
                System.out.println("Enemigo #" + actual.getDato().getId() + " destruido.");
                enemigosActivos.eliminarDestruido(actual.getDato().getId());
            }
            actual = siguiente;
        }
    }

    private void descontarVidasPorEnemigosQueLlegaron() {
        NodoEnemigo actual = enemigosActivos.getPrimero();
        while (actual != null) {
            NodoEnemigo siguiente = actual.getSiguiente();
            if (actual.getDato().getPosicion() >= LONGITUD_RUTA) {
                vidasJugador--;
                System.out.println("Un enemigo llego a la base. Vidas restantes: " + vidasJugador);
                enemigosActivos.eliminarDestruido(actual.getDato().getId());
            }
            actual = siguiente;
        }
    }

    private void mostrarResumenDeTurno() {
        System.out.println("Vidas del jugador: " + vidasJugador);
        System.out.println("Enemigos activos: " + enemigosActivos.getCantidad());
    }

    private void verificarFinDePartida() {
        if (vidasJugador <= 0) {
            partidaTerminada = true;
            System.out.println("Fin de la partida: el jugador perdio todas sus vidas.");
            return;
        }

        boolean todasLasOleadasCompletadas = oleadas.getCantidad() > 0
                && oleadasIniciadas >= oleadas.getCantidad()
                && !oleadaEnCurso
                && enemigosPorGenerar <= 0
                && enemigosActivos.estaVacia();

        if (todasLasOleadasCompletadas) {
            partidaTerminada = true;
            partidaGanada = true;
            System.out.println("Fin de la partida: se completaron todas las oleadas definidas.");
        }
    }

    public void mostrarEnemigosActivos() {
        enemigosActivos.recorrerAdelante();
    }

    public void mostrarEstadoGeneral() {
        System.out.println("Turno actual: " + turnoActual);
        System.out.println("Vidas del jugador: " + vidasJugador);
        System.out.println("Torres registradas: " + torres.getTope());
        System.out.println("Enemigos activos: " + enemigosActivos.getCantidad());
        System.out.println("Oleadas registradas: " + oleadas.getCantidad());
        System.out.println("Oleadas iniciadas: " + oleadasIniciadas);
        System.out.println("Partida terminada: " + partidaTerminada + (partidaGanada ? " (ganada)" : ""));
    }

    public boolean isPartidaTerminada() {
        return partidaTerminada;
    }
}
