package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class EstadoPanel extends JPanel {

    private final JuegoControl control;
    private final BannerPartida banner = new BannerPartida();
    private final TarjetaEstado turno = new TarjetaEstado("⏱️", "Turno actual");
    private final TarjetaEstado vidas = new TarjetaEstado("❤️", "Vidas del jugador");
    private final TarjetaEstado torres = new TarjetaEstado("🏹", "Torres registradas");
    private final TarjetaEstado enemigos = new TarjetaEstado("👾", "Enemigos activos");
    private final TarjetaEstado oleadas = new TarjetaEstado("🌊", "Oleadas registradas");
    private final TarjetaEstado iniciadas = new TarjetaEstado("▶️", "Oleadas iniciadas");
    private final TarjetaEstado estado = new TarjetaEstado("🏆", "Estado partida");

    public EstadoPanel(JuegoControl control) {
        this.control = control;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));

        JPanel card = EstiloGui.tarjeta("📊  Estado general");
        card.setLayout(new BorderLayout(0, 14));
        card.add(banner, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 12));
        grid.setOpaque(false);
        grid.add(turno);
        grid.add(vidas);
        grid.add(torres);
        grid.add(enemigos);
        grid.add(oleadas);
        grid.add(iniciadas);
        grid.add(estado);
        grid.add(new TarjetaEstadoFija("🛤️", "Ruta lineal", "0  →  20  (base)"));
        card.add(grid, BorderLayout.CENTER);
        JLabel ayuda = EstiloGui.muted(
                "Victoria: completar todas las oleadas sin perder las 3 vidas.  Derrota: un enemigo llega a la base.");
        ayuda.setBorder(new EmptyBorder(4, 4, 0, 4));
        card.add(ayuda, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    public void refrescar() {
        JuegoControl.EstadoPartida e = control.getEstado();
        turno.setValor(String.valueOf(e.turno));
        vidas.setValor(textoVidas(e.vidas));
        torres.setValor(e.torres + " / " + JuegoControl.MAX_TORRES);
        enemigos.setValor(String.valueOf(e.enemigos));
        oleadas.setValor(String.valueOf(e.oleadas));
        iniciadas.setValor(String.valueOf(e.oleadasIniciadas));
        estado.setValor(e.etiquetaEstado());

        vidas.resaltar(e.vidas <= 1 ? EstiloGui.ROJO : EstiloGui.TITULO);
        if (e.terminada && e.ganada) {
            estado.resaltar(EstiloGui.VERDE);
        } else if (e.terminada) {
            estado.resaltar(EstiloGui.ROJO);
        } else {
            estado.resaltar(EstiloGui.INFO_TEXTO);
        }
        banner.actualizar(e);
    }

    private static String textoVidas(int n) {
        if (n < 0) {
            n = 0;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(n).append("   ");
        for (int i = 0; i < JuegoControl.VIDAS_INICIALES; i++) {
            sb.append(i < n ? "♥ " : "♡ ");
        }
        return sb.toString().trim();
    }

    private static class BannerPartida extends JPanel {
        private final JLabel titulo = new JLabel("Partida en curso", SwingConstants.CENTER);
        private final JLabel detalle = new JLabel(
                "Registra torres y oleadas, inicia una oleada y avanza turnos en Batalla.",
                SwingConstants.CENTER);

        BannerPartida() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(16, 16, 16, 16));
            setBackground(EstiloGui.BANNER_INFO);
            setOpaque(true);
            titulo.setFont(EstiloGui.FUENTE_GRANDE);
            titulo.setForeground(EstiloGui.INFO_TEXTO);
            titulo.setAlignmentX(CENTER_ALIGNMENT);
            detalle.setFont(EstiloGui.FUENTE);
            detalle.setForeground(EstiloGui.MUTED);
            detalle.setAlignmentX(CENTER_ALIGNMENT);
            add(titulo);
            add(Box.createVerticalStrut(6));
            add(detalle);
        }

        void actualizar(JuegoControl.EstadoPartida e) {
            if (!e.terminada) {
                aplicar("🎮  Partida en curso",
                        "Usa Torres, Oleadas y Batalla. El resumen se actualiza solo.",
                        EstiloGui.BANNER_INFO, EstiloGui.INFO_TEXTO);
            } else if (e.ganada) {
                aplicar("🏆  ¡Victoria!",
                        "Se completaron todas las oleadas definidas.",
                        EstiloGui.BANNER_OK, EstiloGui.OK_TEXTO);
            } else {
                aplicar("💀  Derrota",
                        "El jugador perdió todas sus vidas.",
                        EstiloGui.BANNER_MAL, EstiloGui.MAL_TEXTO);
            }
        }

        private void aplicar(String t, String d, Color fondo, Color colorTitulo) {
            titulo.setText(t);
            detalle.setText(d);
            titulo.setForeground(colorTitulo);
            setBackground(fondo);
        }
    }

    private static class TarjetaEstado extends JPanel {
        private final JLabel valor = new JLabel("0");

        TarjetaEstado(String emoji, String titulo) {
            setOpaque(true);
            setBackground(EstiloGui.TARJETA);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(EstiloGui.LINEA, 1, true),
                    new EmptyBorder(14, 16, 14, 16)));
            setLayout(new BorderLayout(8, 4));

            JLabel cabeza = new JLabel(emoji + "   " + titulo);
            cabeza.setFont(EstiloGui.FUENTE);
            cabeza.setForeground(EstiloGui.MUTED);
            valor.setFont(EstiloGui.FUENTE_GRANDE);
            valor.setForeground(EstiloGui.TITULO);
            add(cabeza, BorderLayout.NORTH);
            add(valor, BorderLayout.CENTER);
        }

        void setValor(String texto) {
            valor.setText(texto);
        }

        void resaltar(Color color) {
            valor.setForeground(color);
        }
    }

    private static class TarjetaEstadoFija extends TarjetaEstado {
        TarjetaEstadoFija(String emoji, String titulo, String valorInicial) {
            super(emoji, titulo);
            setValor(valorInicial);
        }
    }
}
