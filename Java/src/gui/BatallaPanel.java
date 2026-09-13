package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import gui.JuegoControl.FilaEnemigo;
import modelo.Torre;

public class BatallaPanel extends JPanel {

    private static final String[] COLUMNAS = {
            "Id", "Tipo", "Posición", "Vida", "Velocidad"
    };

    private final JuegoControl control;
    private final Runnable onCambio;

    private final JButton btnAvanzar = EstiloGui.primario("▶️  Avanzar turno");
    private final JLabel pista = EstiloGui.muted(
            "Inicia una oleada en la pestaña Oleadas. Cada turno genera un enemigo, mueve a los activos y aplica el daño de las torres.");
    private final RutaVisual ruta = new RutaVisual();
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JTextArea registro = new JTextArea();
    private JSplitPane split;

    public BatallaPanel(JuegoControl control, Runnable onCambio) {
        this.control = control;
        this.onCambio = onCambio;

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? String.class : Integer.class;
            }
        };
        tabla = new JTable(modeloTabla);
        EstiloGui.configurarTabla(tabla);
        tabla.getColumnModel().getColumn(0).setMaxWidth(60);

        registro.setEditable(false);
        registro.setLineWrap(true);
        registro.setWrapStyleWord(true);
        registro.setBackground(EstiloGui.LOG_FONDO);
        registro.setForeground(EstiloGui.LOG_TEXTO);
        registro.setCaretColor(EstiloGui.LOG_TEXTO);
        registro.setFont(EstiloGui.FUENTE_MONO);
        registro.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        registro.setText("El registro de cada turno aparecerá aquí.");

        setOpaque(false);
        setLayout(new BorderLayout(0, 10));
        add(crearCabecera(), BorderLayout.NORTH);
        add(crearCuerpo(), BorderLayout.CENTER);

        btnAvanzar.addActionListener(this::avanzar);
        btnAvanzar.setToolTipText("Ejecuta un turno completo de la partida.");
    }

    public void refrescar() {
        List<FilaEnemigo> enemigos = control.listarEnemigos();
        List<Torre> torres = control.listarTorres();
        modeloTabla.setRowCount(0);
        for (int i = 0; i < enemigos.size(); i++) {
            FilaEnemigo e = enemigos.get(i);
            modeloTabla.addRow(new Object[] {
                    Integer.valueOf(e.id),
                    e.tipo,
                    Integer.valueOf(e.posicion),
                    Integer.valueOf(e.vida),
                    Integer.valueOf(e.velocidad)
            });
        }
        ruta.actualizar(enemigos, torres);

        String log = control.getRegistro();
        if (log != null && !log.isEmpty()) {
            registro.setText(log);
            registro.setCaretPosition(registro.getDocument().getLength());
        }

        btnAvanzar.setEnabled(!control.isPartidaTerminada());
        if (control.isPartidaTerminada()) {
            JuegoControl.EstadoPartida est = control.getEstado();
            pista.setText(est.ganada
                    ? "La partida terminó: victoria. Revisa la pestaña Estado."
                    : "La partida terminó: derrota. Revisa la pestaña Estado.");
        }
    }

    private JPanel crearCabecera() {
        JPanel card = EstiloGui.tarjeta("⚔️  Batalla");
        card.setLayout(new BorderLayout(0, 10));

        JPanel arriba = new JPanel(new BorderLayout(8, 6));
        arriba.setOpaque(false);
        JPanel botonera = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        botonera.setOpaque(false);
        botonera.add(btnAvanzar);
        arriba.add(botonera, BorderLayout.NORTH);
        arriba.add(pista, BorderLayout.CENTER);

        card.add(arriba, BorderLayout.NORTH);
        card.add(ruta, BorderLayout.CENTER);

        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        leyenda.setOpaque(false);
        leyenda.add(EstiloGui.muted("▲ Torre"));
        leyenda.add(EstiloGui.muted("● Enemigo"));
        leyenda.add(EstiloGui.muted("Ruta 0 → 20"));
        card.add(leyenda, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearCuerpo() {
        JPanel enemigos = EstiloGui.tarjeta("👾  Enemigos activos");
        enemigos.setLayout(new BorderLayout());
        JScrollPane scrollTabla = EstiloGui.scroll(tabla);
        scrollTabla.setPreferredSize(new java.awt.Dimension(100, 160));
        enemigos.add(scrollTabla, BorderLayout.CENTER);

        JPanel logCard = new JPanel(new BorderLayout(0, 8));
        logCard.setOpaque(true);
        logCard.setBackground(EstiloGui.TARJETA);
        logCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloGui.LINEA, 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        JLabel logTitulo = new JLabel("📜  Registro del turno");
        logTitulo.setFont(EstiloGui.FUENTE_TITULO);
        logTitulo.setForeground(EstiloGui.TITULO);
        JScrollPane scrollLog = EstiloGui.scroll(registro);
        scrollLog.getViewport().setBackground(EstiloGui.LOG_FONDO);
        logCard.add(logTitulo, BorderLayout.NORTH);
        logCard.add(scrollLog, BorderLayout.CENTER);

        split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, enemigos, logCard);
        split.setResizeWeight(0.48);
        split.setOpaque(false);
        split.setBorder(null);
        split.setDividerSize(8);

        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setOpaque(false);
        cuerpo.add(split, BorderLayout.CENTER);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (split.getHeight() > 0) {
                    split.setDividerLocation(0.48);
                }
            }
        });
        return cuerpo;
    }

    private void avanzar(ActionEvent e) {
        try {
            JuegoControl.Resultado r = control.avanzarTurno();
            if (!r.ok) {
                pista.setText(r.mensaje);
            }
            onCambio.run();
        } catch (RuntimeException ex) {
            pista.setText("No se pudo avanzar el turno.");
        }
    }
}
