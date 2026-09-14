package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import javax.swing.JPanel;

import gui.JuegoControl.FilaEnemigo;
import modelo.Torre;

/**
 * Mini mapa de la ruta 0..20: torres (azul) y enemigos (naranja).
 */
public class RutaVisual extends JPanel {

    private static final int CELDAS = JuegoControl.POSICION_MAXIMA + 1;
    private static final Color FONDO = new Color(248, 250, 252);
    private static final Color VIA = new Color(203, 213, 225);
    private static final Color VIA_RELLENO = new Color(241, 245, 249);
    private static final Color INICIO = new Color(22, 163, 74);
    private static final Color BASE = new Color(185, 28, 28);
    private static final Color TORRE = new Color(14, 116, 144);
    private static final Color ENEMIGO = new Color(234, 88, 12);
    private static final Color TEXTO = new Color(51, 65, 85);

    private final int[] enemigosPorCelda = new int[CELDAS];
    private final Torre[] torrePorCelda = new Torre[CELDAS];

    public RutaVisual() {
        setOpaque(false);
        setPreferredSize(new Dimension(100, 92));
        setMinimumSize(new Dimension(100, 92));
        setToolTipText("Ruta lineal de 0 (inicio) a 20 (base). Los enemigos avanzan hacia la derecha.");
    }

    public void actualizar(List<FilaEnemigo> enemigos, List<Torre> torres) {
        for (int i = 0; i < CELDAS; i++) {
            enemigosPorCelda[i] = 0;
            torrePorCelda[i] = null;
        }
        if (enemigos != null) {
            for (int i = 0; i < enemigos.size(); i++) {
                int p = clamp(enemigos.get(i).posicion);
                enemigosPorCelda[p]++;
            }
        }
        if (torres != null) {
            for (int i = 0; i < torres.size(); i++) {
                Torre t = torres.get(i);
                torrePorCelda[clamp(t.getPosicion())] = t;
            }
        }
        repaint();
    }

    private int clamp(int posicion) {
        if (posicion < 0) {
            return 0;
        }
        if (posicion >= CELDAS) {
            return CELDAS - 1;
        }
        return posicion;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        g2.setColor(FONDO);
        g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 12, 12));
        g2.setColor(EstiloGui.LINEA);
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 2, h - 2, 12, 12));

        int margenX = 18;
        int margenSup = 28;
        int margenInf = 22;
        float anchoUtil = w - margenX * 2f;
        float paso = anchoUtil / (CELDAS - 1);
        int yVia = margenSup + (h - margenSup - margenInf) / 2;

        g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(VIA);
        g2.drawLine(margenX, yVia, w - margenX, yVia);
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(VIA_RELLENO);
        g2.drawLine(margenX, yVia, w - margenX, yVia);

        Font fuente = EstiloGui.FUENTE_PEQUENA;
        g2.setFont(fuente);
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < CELDAS; i++) {
            int x = Math.round(margenX + i * paso);

            g2.setColor(i == 0 ? INICIO : i == CELDAS - 1 ? BASE : new Color(148, 163, 184));
            g2.fillOval(x - 3, yVia - 3, 6, 6);

            if (i % 5 == 0) {
                String n = String.valueOf(i);
                g2.setColor(TEXTO);
                g2.drawString(n, x - fm.stringWidth(n) / 2, h - 8);
            }

            if (torrePorCelda[i] != null) {
                dibujarPinTorre(g2, torrePorCelda[i], x, yVia, h);
            }

            if (enemigosPorCelda[i] > 0) {
                int r = 8;
                g2.setColor(ENEMIGO);
                g2.fillOval(x - r, yVia - r + 1, r * 2, r * 2);
                g2.setColor(new Color(154, 52, 18));
                g2.drawOval(x - r, yVia - r + 1, r * 2, r * 2);
                g2.setColor(Color.WHITE);
                String c = String.valueOf(enemigosPorCelda[i]);
                g2.drawString(c, x - fm.stringWidth(c) / 2, yVia + 5);
            }
        }

        g2.setFont(EstiloGui.FUENTE_PEQUENA);
        g2.setColor(INICIO);
        g2.drawString("Inicio", margenX - 4, 16);
        String base = "Base";
        g2.setColor(BASE);
        g2.drawString(base, w - margenX - g2.getFontMetrics().stringWidth(base) + 4, 16);

        g2.dispose();
    }

    /**
     * Marcador tipo "pin de mapa": circulo con el id de la torre y su
     * nombre debajo, en vez de un simple triangulo.
     */
    private void dibujarPinTorre(Graphics2D g2, Torre torre, int x, int yVia, int alto) {
        int radio = 11;
        int alturaPunta = 9;
        int centroY = yVia - alturaPunta - radio;

        int anchoPunta = 8;
        int[] xsPunta = { x - anchoPunta / 2, x + anchoPunta / 2, x };
        int[] ysPunta = { centroY + radio - 3, centroY + radio - 3, yVia };

        g2.setColor(TORRE);
        g2.fillPolygon(xsPunta, ysPunta, 3);
        g2.fillOval(x - radio, centroY - radio, radio * 2, radio * 2);
        g2.setColor(TORRE.darker());
        g2.drawOval(x - radio, centroY - radio, radio * 2, radio * 2);

        Font fuenteNumero = EstiloGui.FUENTE_PEQUENA.deriveFont(Font.BOLD);
        g2.setFont(fuenteNumero);
        FontMetrics fmNumero = g2.getFontMetrics();
        String id = String.valueOf(torre.getId());
        g2.setColor(Color.WHITE);
        g2.drawString(id, x - fmNumero.stringWidth(id) / 2, centroY + fmNumero.getAscent() / 2 - 1);

        String nombre = torre.getNombre();
        if (nombre != null && !nombre.isEmpty()) {
            g2.setFont(EstiloGui.FUENTE_PEQUENA);
            FontMetrics fmNombre = g2.getFontMetrics();
            int anchoMax = 64;
            String etiqueta = recortar(nombre, fmNombre, anchoMax);
            int yEtiqueta = Math.min(yVia + 16, alto - 20);
            g2.setColor(TEXTO);
            g2.drawString(etiqueta, x - fmNombre.stringWidth(etiqueta) / 2, yEtiqueta);
        }
    }

    private String recortar(String texto, FontMetrics fm, int anchoMax) {
        if (fm.stringWidth(texto) <= anchoMax) {
            return texto;
        }
        String recortado = texto;
        while (recortado.length() > 1 && fm.stringWidth(recortado + "…") > anchoMax) {
            recortado = recortado.substring(0, recortado.length() - 1);
        }
        return recortado + "…";
    }
}
