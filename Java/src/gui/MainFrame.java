package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainFrame extends JFrame {

    private final JuegoControl control;
    private final JLabel titulo = new JLabel("🎮  Tower Defense");
    private final JLabel vidas = new JLabel();
    private final JLabel turno = new JLabel();
    private final JLabel estado = new JLabel();
    private final JTabbedPane pestanas = new JTabbedPane();
    private final TorresPanel torresPanel;
    private final OleadasPanel oleadasPanel;
    private final BatallaPanel batallaPanel;
    private final EstadoPanel estadoPanel;

    public MainFrame() {
        this(new JuegoControl());
    }

    MainFrame(JuegoControl control) {
        super("Tower Defense");
        this.control = control;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 680);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        torresPanel = new TorresPanel(control, this::refrescarTodo);
        oleadasPanel = new OleadasPanel(control, this::refrescarTodo, this::irABatalla);
        batallaPanel = new BatallaPanel(control, this::refrescarTodo);
        estadoPanel = new EstadoPanel(control);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(EstiloGui.PAGINA);
        root.add(crearBarraEstado(), BorderLayout.NORTH);
        root.add(crearPestanas(), BorderLayout.CENTER);
        setContentPane(root);

        pestanas.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                refrescarPestanaActiva();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                torresPanel.enfocarNombre();
                refrescarTodo();
            }
        });
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel();
        barra.setLayout(new BoxLayout(barra, BoxLayout.X_AXIS));
        barra.setBackground(EstiloGui.BARRA);
        barra.setBorder(new EmptyBorder(12, 18, 12, 18));
        barra.setPreferredSize(new Dimension(900, 52));

        titulo.setFont(EstiloGui.FUENTE_TITULO);
        titulo.setForeground(EstiloGui.ACENTO);
        vidas.setFont(EstiloGui.FUENTE_NEGRITA);
        turno.setFont(EstiloGui.FUENTE_NEGRITA);
        estado.setFont(EstiloGui.FUENTE_NEGRITA);
        vidas.setForeground(EstiloGui.BARRA_TEXTO);
        turno.setForeground(EstiloGui.BARRA_TEXTO);
        estado.setForeground(EstiloGui.BARRA_TEXTO);

        JPanel stats = new JPanel();
        stats.setOpaque(false);
        stats.setLayout(new BoxLayout(stats, BoxLayout.X_AXIS));
        stats.add(vidas);
        stats.add(separadorTexto());
        stats.add(turno);
        stats.add(separadorTexto());
        stats.add(estado);

        barra.add(titulo);
        barra.add(Box.createHorizontalGlue());
        barra.add(stats);
        return barra;
    }

    private JLabel separadorTexto() {
        JLabel s = new JLabel("   |   ");
        s.setFont(EstiloGui.FUENTE);
        s.setForeground(new Color(71, 85, 105));
        return s;
    }

    private JTabbedPane crearPestanas() {
        pestanas.setFont(EstiloGui.FUENTE_NEGRITA);
        pestanas.setBorder(new EmptyBorder(8, 10, 10, 10));
        pestanas.setBackground(EstiloGui.PAGINA);

        JPanel pTorres = EstiloGui.pagina();
        pTorres.setLayout(new BorderLayout());
        pTorres.add(torresPanel, BorderLayout.CENTER);

        JPanel pOleadas = EstiloGui.pagina();
        pOleadas.setLayout(new BorderLayout());
        pOleadas.add(oleadasPanel, BorderLayout.CENTER);

        JPanel pBatalla = EstiloGui.pagina();
        pBatalla.setLayout(new BorderLayout());
        pBatalla.add(batallaPanel, BorderLayout.CENTER);

        JPanel pEstado = EstiloGui.pagina();
        pEstado.setLayout(new BorderLayout());
        pEstado.add(estadoPanel, BorderLayout.CENTER);

        pestanas.addTab("🏹  Torres", pTorres);
        pestanas.addTab("🌊  Oleadas", pOleadas);
        pestanas.addTab("⚔️  Batalla", pBatalla);
        pestanas.addTab("📊  Estado", pEstado);

        pestanas.setToolTipTextAt(0, "Registrar, listar y eliminar torres");
        pestanas.setToolTipTextAt(1, "Registrar oleadas e iniciar la siguiente");
        pestanas.setToolTipTextAt(2, "Avanzar turnos y ver enemigos activos");
        pestanas.setToolTipTextAt(3, "Resumen general de la partida");
        return pestanas;
    }

    void refrescarTodo() {
        control.actualizarEstado();
        actualizarBarra();
        torresPanel.refrescar();
        oleadasPanel.refrescar();
        batallaPanel.refrescar();
        estadoPanel.refrescar();
    }

    private void refrescarPestanaActiva() {
        control.actualizarEstado();
        actualizarBarra();
        int i = pestanas.getSelectedIndex();
        if (i == 0) {
            torresPanel.refrescar();
        } else if (i == 1) {
            oleadasPanel.refrescar();
        } else if (i == 2) {
            batallaPanel.refrescar();
        } else if (i == 3) {
            estadoPanel.refrescar();
        }
    }

    private void actualizarBarra() {
        JuegoControl.EstadoPartida e = control.getEstado();
        vidas.setText("❤️  Vidas: " + e.vidas);
        turno.setText("⏱️  Turno: " + e.turno);
        estado.setText("🏆  Estado: " + e.etiquetaEstado());
        if (e.terminada && e.ganada) {
            estado.setForeground(new Color(134, 239, 172));
        } else if (e.terminada) {
            estado.setForeground(new Color(252, 165, 165));
        } else {
            estado.setForeground(EstiloGui.BARRA_TEXTO);
        }
        if (e.vidas <= 1) {
            vidas.setForeground(new Color(252, 165, 165));
        } else {
            vidas.setForeground(EstiloGui.BARRA_TEXTO);
        }
    }

    private void irABatalla() {
        pestanas.setSelectedIndex(2);
    }
}
