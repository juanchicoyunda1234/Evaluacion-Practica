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
    private final int[] torresPorCelda = new int[CELDAS];

    public RutaVisual() {
        setOpaque(false);
        setPreferredSize(new Dimension(100, 92));
        setMinimumSize(new Dimension(100, 92));
        setToolTipText("Ruta lineal de 0 (inicio) a 20 (base). Los enemigos avanzan hacia la derecha.");
    }

    public void actualizar(List<FilaEnemigo> enemigos, List<Torre> torres) {
        for (int i = 0; i < CELDAS; i++) {
            enemigosPorCelda[i] = 0;
            torresPorCelda[i] = 0;
        }
        if (enemigos != null) {
            for (int i = 0; i < enemigos.size(); i++) {
                int p = clamp(enemigos.get(i).posicion);
                enemigosPorCelda[p]++;
            }
        }
        if (torres != null) {
            for (int i = 0; i < torres.size(); i++) {
                int p = clamp(torres.get(i).getPosicion());
                torresPorCelda[p]++;
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

            if (torresPorCelda[i] > 0) {
                int tw = 10;
                int[] xs = { x, x - tw / 2, x + tw / 2 };
                int[] ys = { yVia - 22, yVia - 10, yVia - 10 };
                g2.setColor(TORRE);
                g2.fillPolygon(xs, ys, 3);
                if (torresPorCelda[i] > 1) {
                    g2.setColor(Color.WHITE);
                    String c = String.valueOf(torresPorCelda[i]);
                    g2.drawString(c, x - fm.stringWidth(c) / 2, yVia - 12);
                }
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
}
