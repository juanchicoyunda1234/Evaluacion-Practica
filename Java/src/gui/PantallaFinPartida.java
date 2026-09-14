package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Overlay de pantalla completa (glass pane de {@link MainFrame}) que anuncia
 * victoria o derrota con un efecto de aparicion. Se cierra con un clic.
 */
public class PantallaFinPartida extends JComponent {

    private static final Color FONDO = new Color(15, 23, 42);
    private static final Color VERDE = new Color(74, 222, 128);
    private static final Color ROJO = new Color(248, 113, 113);

    private boolean ganada;
    private String titulo = "";
    private String detalle = "";
    private float progreso = 0f;
    private Timer animador;

    public PantallaFinPartida() {
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ocultar();
            }
        });
    }

    public void mostrar(boolean ganada, String detalle) {
        this.ganada = ganada;
        this.titulo = ganada ? "🏆  ¡VICTORIA!" : "💀  DERROTA";
        this.detalle = detalle;
        this.progreso = 0f;
        setVisible(true);

        if (animador != null && animador.isRunning()) {
            animador.stop();
        }
        animador = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progreso = Math.min(1f, progreso + 0.045f);
                repaint();
                if (progreso >= 1f) {
                    animador.stop();
                }
            }
        });
        animador.start();
    }

    public void ocultar() {
        if (animador != null) {
            animador.stop();
        }
        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        float alpha = suavizar(progreso);
        float escala = 0.75f + 0.25f * alpha;

        g2.setColor(new Color(FONDO.getRed(), FONDO.getGreen(), FONDO.getBlue(), Math.round(215 * alpha)));
        g2.fillRect(0, 0, w, h);

        Color colorTitulo = ganada ? VERDE : ROJO;
        Font fuenteTitulo = EstiloGui.FUENTE_GRANDE.deriveFont(Font.BOLD, 54f * escala);
        g2.setFont(fuenteTitulo);
        g2.setColor(conAlpha(colorTitulo, alpha));
        FontMetrics fmT = g2.getFontMetrics();
        int ty = h / 2 - 6;
        g2.drawString(titulo, (w - fmT.stringWidth(titulo)) / 2, ty);

        if (detalle != null && !detalle.isEmpty()) {
            g2.setFont(EstiloGui.FUENTE_TITULO);
            g2.setColor(conAlpha(new Color(226, 232, 240), alpha));
            FontMetrics fmD = g2.getFontMetrics();
            g2.drawString(detalle, (w - fmD.stringWidth(detalle)) / 2, ty + 42);
        }

        String pista = "Haz clic en cualquier lugar para continuar";
        g2.setFont(EstiloGui.FUENTE_PEQUENA);
        g2.setColor(conAlpha(new Color(148, 163, 184), alpha));
        FontMetrics fmP = g2.getFontMetrics();
        g2.drawString(pista, (w - fmP.stringWidth(pista)) / 2, h - 40);

        g2.dispose();
    }

    private static float suavizar(float t) {
        return 1f - (1f - t) * (1f - t);
    }

    private static Color conAlpha(Color base, float alpha) {
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), Math.round(255 * alpha));
    }
}
