package gui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modelo.Oleada;
import modelo.Torre;
import negocio.Juego;

/**
 * Adaptador de la GUI hacia {@link Juego}.
 * No modifica modelo ni negocio: solo invoca metodos publicos
 * y, cuando hace falta, captura System.out para el registro y el estado.
 */
public class JuegoControl {

    public static final int VIDAS_INICIALES = 3;
    public static final int POSICION_MAXIMA = 20;
    public static final int MAX_TORRES = 20;

    private static final Pattern ENEMIGO = Pattern.compile(
            "Enemigo #(\\d+) \\[(.+)\\] pos=(-?\\d+) vida=(-?\\d+) vel=(-?\\d+) recompensa=(-?\\d+)");

    private final Juego juego;
    private final List<Torre> torres = new ArrayList<Torre>();
    private final List<Oleada> oleadas = new ArrayList<Oleada>();
    private final StringBuilder registro = new StringBuilder();

    private int siguienteIdTorre = 1;
    private int siguienteIdOleada = 1;
    private EstadoPartida estado = new EstadoPartida();

    public JuegoControl() {
        this.juego = new Juego(VIDAS_INICIALES);
        actualizarEstado();
    }

    public synchronized Resultado agregarTorre(String nombre, String tipo, int posicion, int danio, int rango,
            int costo) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                return Resultado.error("Escribe un nombre para la torre.");
            }
            if (tipo == null || tipo.trim().isEmpty()) {
                return Resultado.error("Escribe un tipo de torre.");
            }
            if (posicion < 0 || posicion > POSICION_MAXIMA) {
                return Resultado.error("La posicion debe estar entre 0 y " + POSICION_MAXIMA + ".");
            }
            if (danio <= 0) {
                return Resultado.error("El dano debe ser mayor a 0.");
            }
            if (rango < 0) {
                return Resultado.error("El rango no puede ser negativo.");
            }
            if (costo < 0) {
                return Resultado.error("El costo no puede ser negativo.");
            }
            if (torres.size() >= MAX_TORRES) {
                return Resultado.error("No se pudo registrar: capacidad maxima alcanzada (" + MAX_TORRES + ").");
            }
            if (hayTorreEnPosicion(posicion)) {
                return Resultado.error("Ya hay una torre en la posicion " + posicion + ". Elige otra casilla.");
            }

            Torre torre = new Torre(siguienteIdTorre, nombre.trim(), tipo.trim(), posicion, danio, rango, costo);
            boolean insertada = juego.registrarTorre(torre);
            if (!insertada) {
                actualizarEstado();
                return Resultado.error("No se pudo registrar la torre. Revisa: posicion entre 0 y 20, "
                        + "dano mayor a 0, rango y costo no negativos, sin otra torre en esa posicion, "
                        + "y capacidad maxima no alcanzada.");
            }

            torres.add(torre);
            int id = siguienteIdTorre;
            siguienteIdTorre++;
            actualizarEstado();
            return Resultado.ok("Torre registrada con id " + id + ".");
        } catch (RuntimeException ex) {
            System.err.println("agregarTorre: " + ex);
            return Resultado.error("No se pudo registrar la torre.");
        }
    }

    public synchronized Resultado eliminarTorre(int id) {
        try {
            boolean eliminada = juego.eliminarTorre(id);
            if (!eliminada) {
                actualizarEstado();
                return Resultado.error("No se encontro una torre con ese id.");
            }
            for (int i = 0; i < torres.size(); i++) {
                if (torres.get(i).getId() == id) {
                    torres.remove(i);
                    break;
                }
            }
            actualizarEstado();
            return Resultado.ok("Torre eliminada.");
        } catch (RuntimeException ex) {
            System.err.println("eliminarTorre: " + ex);
            return Resultado.error("No se pudo eliminar la torre.");
        }
    }

    public synchronized Resultado agregarOleada(int cantidad, String tipo, int vidaBase, int velocidadBase) {
        try {
            if (tipo == null || tipo.trim().isEmpty()) {
                return Resultado.error("Escribe un tipo de enemigo.");
            }
            if (cantidad <= 0 || vidaBase <= 0 || velocidadBase <= 0) {
                return Resultado.error(
                        "Cantidad, vida base y velocidad base deben ser mayores a 0.");
            }

            Oleada oleada = new Oleada(siguienteIdOleada, cantidad, tipo.trim(), vidaBase, velocidadBase);
            boolean registrada = juego.registrarOleada(oleada);
            if (!registrada) {
                actualizarEstado();
                return Resultado.error(
                        "No se pudo registrar la oleada: cantidad, vida base y velocidad base deben ser mayores a 0.");
            }

            oleadas.add(oleada);
            int id = siguienteIdOleada;
            siguienteIdOleada++;
            actualizarEstado();
            return Resultado.ok("Oleada registrada con id " + id + ".");
        } catch (RuntimeException ex) {
            System.err.println("agregarOleada: " + ex);
            return Resultado.error("No se pudo registrar la oleada.");
        }
    }

    public synchronized Resultado iniciarSiguienteOleada() {
        try {
            final boolean[] ok = new boolean[] { false };
            String salida = capturar(new Runnable() {
                @Override
                public void run() {
                    ok[0] = juego.iniciarSiguienteOleada();
                }
            });
            actualizarEstado();
            if (salida != null && !salida.isEmpty()) {
                anexarRegistro(salida);
            }
            if (ok[0]) {
                return Resultado.ok(salida);
            }
            if (salida == null || salida.isEmpty()) {
                return Resultado.error("No se pudo iniciar la oleada.");
            }
            return Resultado.error(salida);
        } catch (RuntimeException ex) {
            System.err.println("iniciarSiguienteOleada: " + ex);
            return Resultado.error("No se pudo iniciar la oleada.");
        }
    }

    public synchronized Resultado avanzarTurno() {
        try {
            if (juego.isPartidaTerminada()) {
                return Resultado.error("La partida ya termino.");
            }
            String salida = capturar(new Runnable() {
                @Override
                public void run() {
                    juego.avanzarTurno();
                }
            });
            anexarRegistro(salida);
            actualizarEstado();
            return Resultado.ok(salida);
        } catch (RuntimeException ex) {
            System.err.println("avanzarTurno: " + ex);
            return Resultado.error("No se pudo avanzar el turno.");
        }
    }

    public synchronized List<Torre> listarTorres() {
        return Collections.unmodifiableList(torres);
    }

    public synchronized List<Oleada> listarOleadas() {
        return Collections.unmodifiableList(oleadas);
    }

    public synchronized List<FilaEnemigo> listarEnemigos() {
        List<FilaEnemigo> filas = new ArrayList<FilaEnemigo>();
        String salida;
        try {
            salida = capturar(new Runnable() {
                @Override
                public void run() {
                    juego.mostrarEnemigosActivos();
                }
            });
        } catch (RuntimeException ex) {
            System.err.println("listarEnemigos: " + ex);
            return filas;
        }
        if (salida == null || salida.isEmpty() || salida.contains("No hay enemigos activos")) {
            return filas;
        }
        String[] lineas = salida.split("\\R");
        for (int i = 0; i < lineas.length; i++) {
            Matcher m = ENEMIGO.matcher(lineas[i].trim());
            if (m.find()) {
                try {
                    filas.add(new FilaEnemigo(
                            Integer.parseInt(m.group(1)),
                            m.group(2),
                            Integer.parseInt(m.group(3)),
                            Integer.parseInt(m.group(4)),
                            Integer.parseInt(m.group(5))));
                } catch (NumberFormatException ignorada) {
                    // linea mal formada: se omite
                }
            }
        }
        return filas;
    }

    public synchronized EstadoPartida getEstado() {
        return estado.copia();
    }

    public synchronized String getRegistro() {
        return registro.toString();
    }

    public synchronized boolean isPartidaTerminada() {
        return juego.isPartidaTerminada();
    }

    public synchronized void actualizarEstado() {
        try {
            String salida = capturar(new Runnable() {
                @Override
                public void run() {
                    juego.mostrarEstadoGeneral();
                }
            });
            estado = parsearEstado(salida);
            estado.torres = torres.size();
            estado.oleadas = oleadas.size();
            if (estado.vidas < 0) {
                estado.vidas = 0;
            }
        } catch (RuntimeException ex) {
            System.err.println("actualizarEstado: " + ex);
        }
    }

    private boolean hayTorreEnPosicion(int posicion) {
        for (int i = 0; i < torres.size(); i++) {
            if (torres.get(i).getPosicion() == posicion) {
                return true;
            }
        }
        return false;
    }

    private void anexarRegistro(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return;
        }
        if (registro.length() > 0) {
            registro.append("\n");
        }
        registro.append(texto.trim());
        if (registro.length() > 20000) {
            registro.delete(0, registro.length() - 15000);
        }
    }

    private String capturar(Runnable accion) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream original = System.out;
        PrintStream extra = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        System.setOut(extra);
        try {
            accion.run();
            extra.flush();
            return new String(buffer.toByteArray(), StandardCharsets.UTF_8).trim();
        } catch (RuntimeException ex) {
            extra.flush();
            throw ex;
        } finally {
            System.setOut(original);
            extra.close();
        }
    }

    private EstadoPartida parsearEstado(String texto) {
        EstadoPartida e = new EstadoPartida();
        if (texto == null || texto.isEmpty()) {
            return e;
        }
        String[] lineas = texto.split("\\R");
        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i].trim();
            if (linea.startsWith("Turno actual:")) {
                e.turno = enteroDespuesDeDosPuntos(linea);
            } else if (linea.startsWith("Vidas del jugador:")) {
                e.vidas = enteroDespuesDeDosPuntos(linea);
            } else if (linea.startsWith("Torres registradas:")) {
                e.torres = enteroDespuesDeDosPuntos(linea);
            } else if (linea.startsWith("Enemigos activos:")) {
                e.enemigos = enteroDespuesDeDosPuntos(linea);
            } else if (linea.startsWith("Oleadas registradas:")) {
                e.oleadas = enteroDespuesDeDosPuntos(linea);
            } else if (linea.startsWith("Oleadas iniciadas:")) {
                e.oleadasIniciadas = enteroDespuesDeDosPuntos(linea);
            } else if (linea.startsWith("Partida terminada:")) {
                String valor = linea.substring(linea.indexOf(':') + 1).trim();
                e.terminada = valor.startsWith("true");
                e.ganada = valor.contains("ganada");
            }
        }
        return e;
    }

    private static int enteroDespuesDeDosPuntos(String linea) {
        try {
            return Integer.parseInt(linea.substring(linea.indexOf(':') + 1).trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static class EstadoPartida {
        public int turno;
        public int vidas = VIDAS_INICIALES;
        public int torres;
        public int enemigos;
        public int oleadas;
        public int oleadasIniciadas;
        public boolean terminada;
        public boolean ganada;

        public String etiquetaEstado() {
            if (!terminada) {
                return "En curso";
            }
            return ganada ? "Victoria" : "Derrota";
        }

        public EstadoPartida copia() {
            EstadoPartida c = new EstadoPartida();
            c.turno = turno;
            c.vidas = vidas;
            c.torres = torres;
            c.oleadas = oleadas;
            c.enemigos = enemigos;
            c.oleadasIniciadas = oleadasIniciadas;
            c.terminada = terminada;
            c.ganada = ganada;
            return c;
        }
    }

    public static class FilaEnemigo {
        public final int id;
        public final String tipo;
        public final int posicion;
        public final int vida;
        public final int velocidad;

        public FilaEnemigo(int id, String tipo, int posicion, int vida, int velocidad) {
            this.id = id;
            this.tipo = tipo;
            this.posicion = posicion;
            this.vida = vida;
            this.velocidad = velocidad;
        }
    }

    public static class Resultado {
        public final boolean ok;
        public final String mensaje;

        private Resultado(boolean ok, String mensaje) {
            this.ok = ok;
            this.mensaje = mensaje;
        }

        public static Resultado ok(String mensaje) {
            return new Resultado(true, mensaje);
        }

        public static Resultado error(String mensaje) {
            return new Resultado(false, mensaje);
        }
    }
}
