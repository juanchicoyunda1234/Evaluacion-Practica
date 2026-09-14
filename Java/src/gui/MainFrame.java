package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Panel único (estilo tablero) con el campo de batalla siempre visible, las
 * listas de torres/enemigos/oleadas al lado y todas las acciones abajo.
 */
public class MainFrame extends JFrame {

    private final JuegoControl control;

    private final JLabel titulo = new JLabel("🎮  Tower Defense");
    private final JLabel vidas = new JLabel();
    private final JLabel torresCantidad = new JLabel();
    private final JLabel oleadaActual = new JLabel();
    private final JLabel turno = new JLabel();
    private final JLabel mensaje = EstiloGui.muted(" ");

    private final RutaVisual ruta = new RutaVisual();
    private final DatosPanel datosPanel;
    private final PantallaFinPartida pantallaFin = new PantallaFinPartida();
    private boolean ultimaTerminada = false;

    private final JButton btnRegistrarTorre = EstiloGui.primario("➕  Registrar torre");
    private final JButton btnEliminarTorre = EstiloGui.peligro("✕  Eliminar torre");
    private final JButton btnRegistrarOleada = EstiloGui.boton("➕  Registrar oleada", new Color(37, 99, 235),
            new Color(29, 78, 216));
    private final JButton btnIniciarOleada = EstiloGui.boton("▶  Iniciar siguiente oleada", new Color(234, 88, 12),
            new Color(194, 65, 12));
    private final JButton btnAvanzarTurno = EstiloGui.primario("▶▶  Avanzar turno");
    private final JButton btnEstado = EstiloGui.boton("📊  Estado general", EstiloGui.BARRA, new Color(30, 41, 59));
    private final JButton btnReiniciar = EstiloGui.boton("🔄  Reiniciar partida", EstiloGui.BARRA,
            new Color(30, 41, 59));
    private final JButton btnSalir = EstiloGui.boton("🚪  Salir", EstiloGui.BARRA, new Color(30, 41, 59));

    public MainFrame() {
        this(new JuegoControl());
    }

    MainFrame(JuegoControl control) {
        super("Tower Defense");
        this.control = control;
        this.datosPanel = new DatosPanel(control);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(1020, 660));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(EstiloGui.PAGINA);
        root.add(crearBarraEstado(), BorderLayout.NORTH);
        root.add(crearCentro(), BorderLayout.CENTER);
        root.add(crearBarraAcciones(), BorderLayout.SOUTH);
        setContentPane(root);

        pantallaFin.setVisible(false);
        setGlassPane(pantallaFin);

        cablearAcciones();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                refrescarTodo();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                salir();
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
        torresCantidad.setFont(EstiloGui.FUENTE_NEGRITA);
        oleadaActual.setFont(EstiloGui.FUENTE_NEGRITA);
        turno.setFont(EstiloGui.FUENTE_NEGRITA);
        vidas.setForeground(EstiloGui.BARRA_TEXTO);
        torresCantidad.setForeground(EstiloGui.BARRA_TEXTO);
        oleadaActual.setForeground(EstiloGui.BARRA_TEXTO);
        turno.setForeground(EstiloGui.BARRA_TEXTO);

        JPanel stats = new JPanel();
        stats.setOpaque(false);
        stats.setLayout(new BoxLayout(stats, BoxLayout.X_AXIS));
        stats.add(vidas);
        stats.add(separadorTexto());
        stats.add(torresCantidad);
        stats.add(separadorTexto());
        stats.add(oleadaActual);
        stats.add(separadorTexto());
        stats.add(turno);

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

    private JPanel crearCentro() {
        JPanel pagina = EstiloGui.pagina();
        pagina.setLayout(new BorderLayout(14, 0));

        JPanel campoBatalla = EstiloGui.tarjeta("Campo de batalla");
        campoBatalla.setLayout(new BorderLayout(0, 8));
        campoBatalla.add(ruta, BorderLayout.CENTER);
        JPanel leyenda = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 16, 0));
        leyenda.setOpaque(false);
        leyenda.add(EstiloGui.muted("▲ Torre"));
        leyenda.add(EstiloGui.muted("● Enemigo"));
        leyenda.add(EstiloGui.muted("Ruta 0 → 20"));
        campoBatalla.add(leyenda, BorderLayout.SOUTH);
        pagina.add(campoBatalla, BorderLayout.CENTER);

        JPanel panelDatos = EstiloGui.tarjeta("Datos");
        panelDatos.setLayout(new BorderLayout());
        panelDatos.setPreferredSize(new Dimension(360, 100));
        panelDatos.add(datosPanel, BorderLayout.CENTER);
        pagina.add(panelDatos, BorderLayout.EAST);

        return pagina;
    }

    private JPanel crearBarraAcciones() {
        JPanel externo = new JPanel(new BorderLayout(0, 6));
        externo.setOpaque(true);
        externo.setBackground(EstiloGui.PAGINA);
        externo.setBorder(new EmptyBorder(0, 16, 16, 16));

        mensaje.setBorder(new EmptyBorder(0, 4, 4, 4));
        externo.add(mensaje, BorderLayout.NORTH);

        JPanel barra = new JPanel(new BorderLayout(14, 0));
        barra.setOpaque(false);

        JPanel registro = new JPanel(new GridLayout(2, 2, 10, 8));
        registro.setOpaque(false);
        registro.add(btnRegistrarTorre);
        registro.add(btnEliminarTorre);
        registro.add(btnRegistrarOleada);
        registro.add(btnIniciarOleada);
        barra.add(registro, BorderLayout.WEST);

        JPanel avanzarContenedor = new JPanel(new GridLayout(1, 1));
        avanzarContenedor.setOpaque(false);
        avanzarContenedor.setPreferredSize(new Dimension(170, 10));
        btnAvanzarTurno.setFont(EstiloGui.FUENTE_TITULO);
        avanzarContenedor.add(btnAvanzarTurno);
        barra.add(avanzarContenedor, BorderLayout.CENTER);

        JPanel generales = new JPanel();
        generales.setOpaque(false);
        generales.setLayout(new BoxLayout(generales, BoxLayout.Y_AXIS));
        generales.add(anchoFijo(btnEstado));
        generales.add(Box.createVerticalStrut(8));
        generales.add(anchoFijo(btnReiniciar));
        generales.add(Box.createVerticalStrut(8));
        generales.add(anchoFijo(btnSalir));
        barra.add(generales, BorderLayout.EAST);

        externo.add(barra, BorderLayout.CENTER);
        return externo;
    }

    private JButton anchoFijo(JButton boton) {
        boton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(180, boton.getPreferredSize().height));
        boton.setPreferredSize(new Dimension(180, boton.getPreferredSize().height));
        return boton;
    }

    private void cablearAcciones() {
        btnRegistrarTorre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DialogoTorre dlg = new DialogoTorre(MainFrame.this, control);
                dlg.setVisible(true);
                if (dlg.fueRegistrada()) {
                    datosPanel.mostrarPestana(DatosPanel.TAB_TORRES);
                    mostrarMensaje("Torre registrada.", EstiloGui.OK_TEXTO);
                }
                refrescarTodo();
            }
        });

        btnEliminarTorre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarTorre();
            }
        });

        btnRegistrarOleada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DialogoOleada dlg = new DialogoOleada(MainFrame.this, control);
                dlg.setVisible(true);
                if (dlg.fueRegistrada()) {
                    datosPanel.mostrarPestana(DatosPanel.TAB_OLEADAS);
                    mostrarMensaje("Oleada registrada.", EstiloGui.OK_TEXTO);
                }
                refrescarTodo();
            }
        });

        btnIniciarOleada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JuegoControl.Resultado r = control.iniciarSiguienteOleada();
                mostrarMensaje(r.mensaje, r.ok ? EstiloGui.OK_TEXTO : EstiloGui.MAL_TEXTO);
                refrescarTodo();
                if (r.ok) {
                    datosPanel.mostrarPestana(DatosPanel.TAB_ENEMIGOS);
                }
            }
        });

        btnAvanzarTurno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JuegoControl.Resultado r = control.avanzarTurno();
                mostrarMensaje(r.ok ? "Turno avanzado." : r.mensaje, r.ok ? EstiloGui.OK_TEXTO : EstiloGui.MAL_TEXTO);
                refrescarTodo();
                datosPanel.mostrarPestana(DatosPanel.TAB_ENEMIGOS);
            }
        });

        btnEstado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarEstadoGeneral();
            }
        });

        btnReiniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciar();
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salir();
            }
        });
    }

    private void eliminarTorre() {
        int id = datosPanel.obtenerIdTorreSeleccionada();
        if (id < 0) {
            datosPanel.mostrarPestana(DatosPanel.TAB_TORRES);
            mostrarMensaje("Selecciona una torre en la pestaña Torre.", EstiloGui.MAL_TEXTO);
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la torre seleccionada (id " + id + ")?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        JuegoControl.Resultado r = control.eliminarTorre(id);
        mostrarMensaje(r.mensaje, r.ok ? EstiloGui.OK_TEXTO : EstiloGui.MAL_TEXTO);
        refrescarTodo();
    }

    private void mostrarEstadoGeneral() {
        control.actualizarEstado();
        EstadoPanel panel = new EstadoPanel(control);
        panel.refrescar();

        JDialog dlg = new JDialog(this, "Estado general", true);
        JPanel envoltura = new JPanel(new BorderLayout(0, 12));
        envoltura.setBackground(EstiloGui.PAGINA);
        envoltura.setBorder(new EmptyBorder(16, 16, 16, 16));
        envoltura.add(panel, BorderLayout.CENTER);

        JButton cerrar = EstiloGui.boton("Cerrar", EstiloGui.BARRA, new Color(30, 41, 59));
        cerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dlg.dispose();
            }
        });
        envoltura.add(EstiloGui.barraDerecha(cerrar), BorderLayout.SOUTH);

        dlg.setContentPane(envoltura);
        dlg.setMinimumSize(new Dimension(560, 460));
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void reiniciar() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Reiniciar la partida? Se perderán las torres, oleadas y el progreso actual.",
                "Confirmar reinicio",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        control.reiniciar();
        datosPanel.mostrarPestana(DatosPanel.TAB_TORRES);
        mostrarMensaje("Partida reiniciada.", EstiloGui.OK_TEXTO);
        refrescarTodo();
    }

    private void salir() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Salir de Tower Defense?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void mostrarMensaje(String texto, Color color) {
        EstiloGui.mensaje(mensaje, texto == null || texto.isEmpty() ? " " : texto, color);
    }

    void refrescarTodo() {
        control.actualizarEstado();
        actualizarBarra();
        datosPanel.refrescar();
        ruta.actualizar(control.listarEnemigos(), control.listarTorres());

        boolean terminada = control.isPartidaTerminada();
        btnRegistrarTorre.setEnabled(!terminada);
        btnEliminarTorre.setEnabled(!terminada);
        btnRegistrarOleada.setEnabled(!terminada);
        btnIniciarOleada.setEnabled(!terminada && !control.listarOleadas().isEmpty());
        btnAvanzarTurno.setEnabled(!terminada);

        if (terminada && !ultimaTerminada) {
            JuegoControl.EstadoPartida e = control.getEstado();
            String detalle = e.ganada
                    ? "Completaste todas las oleadas sin perder tus vidas."
                    : "Un enemigo llegó a la base y perdiste tus vidas.";
            pantallaFin.mostrar(e.ganada, detalle);
        }
        ultimaTerminada = terminada;
    }

    private void actualizarBarra() {
        JuegoControl.EstadoPartida e = control.getEstado();
        vidas.setText("Vida: " + textoVidas(e.vidas));
        torresCantidad.setText("Cantidad de torres: " + e.torres);
        oleadaActual.setText("Oleada: " + e.oleadasIniciadas + " / " + e.oleadas);
        turno.setText("Turno: " + e.turno);

        vidas.setForeground(e.vidas <= 1 ? new Color(252, 165, 165) : EstiloGui.BARRA_TEXTO);
        if (e.terminada && e.ganada) {
            oleadaActual.setForeground(new Color(134, 239, 172));
        } else if (e.terminada) {
            oleadaActual.setForeground(new Color(252, 165, 165));
        } else {
            oleadaActual.setForeground(EstiloGui.BARRA_TEXTO);
        }
    }

    private static String textoVidas(int n) {
        if (n < 0) {
            n = 0;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < JuegoControl.VIDAS_INICIALES; i++) {
            sb.append(i < n ? "♥ " : "♡ ");
        }
        return sb.toString().trim();
    }
}
