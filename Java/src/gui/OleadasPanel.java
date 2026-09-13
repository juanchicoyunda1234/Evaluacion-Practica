package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import modelo.Oleada;

public class OleadasPanel extends JPanel {

    private static final String[] COLUMNAS = {
            "Id", "Tipo", "Cantidad", "Vida base", "Vel. base"
    };
    private static final String[] TIPOS = { "goblin", "orco", "troll" };

    private final JuegoControl control;
    private final Runnable onCambio;
    private final Runnable irABatalla;

    private final JSpinner spinCantidad = EstiloGui.entero(1, 50, 3);
    private final JComboBox<String> comboTipo = EstiloGui.comboEditable(TIPOS, "goblin");
    private final JSpinner spinVida = EstiloGui.entero(1, 500, 10);
    private final JSpinner spinVel = EstiloGui.entero(1, 10, 1);
    private final JLabel mensaje = EstiloGui.muted("Define las oleadas antes de iniciarlas.");
    private final JButton btnAgregar = EstiloGui.primario("➕  Agregar oleada");
    private final JButton btnIniciar = EstiloGui.primario("▶️  Iniciar siguiente oleada");

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final CardLayout tarjetasTabla = new CardLayout();
    private final JPanel contenedorTabla = new JPanel(tarjetasTabla);

    public OleadasPanel(JuegoControl control, Runnable onCambio, Runnable irABatalla) {
        this.control = control;
        this.onCambio = onCambio;
        this.irABatalla = irABatalla;

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

        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        add(crearFormulario(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);

        btnAgregar.addActionListener(this::agregar);
        btnIniciar.addActionListener(this::iniciar);
        btnAgregar.setToolTipText("Registra una oleada en la lista circular.");
        btnIniciar.setToolTipText("Pone en curso la siguiente oleada y pasa a Batalla.");
        EstiloGui.alEnter(comboTipo, this::agregar);
    }

    public void refrescar() {
        List<Oleada> oleadas = control.listarOleadas();
        modeloTabla.setRowCount(0);
        for (int i = 0; i < oleadas.size(); i++) {
            Oleada o = oleadas.get(i);
            modeloTabla.addRow(new Object[] {
                    Integer.valueOf(o.getIdOleada()),
                    o.getTipoEnemigo(),
                    Integer.valueOf(o.getCantidadEnemigos()),
                    Integer.valueOf(o.getVidaBase()),
                    Integer.valueOf(o.getVelocidadBase())
            });
        }
        tarjetasTabla.show(contenedorTabla, oleadas.isEmpty() ? "vacio" : "tabla");
        boolean terminada = control.isPartidaTerminada();
        btnAgregar.setEnabled(!terminada);
        btnIniciar.setEnabled(!terminada && !oleadas.isEmpty());
        spinCantidad.setEnabled(!terminada);
        comboTipo.setEnabled(!terminada);
        spinVida.setEnabled(!terminada);
        spinVel.setEnabled(!terminada);
    }

    private JPanel crearFormulario() {
        JPanel card = EstiloGui.tarjeta("🌊  Oleadas");
        card.setLayout(new GridBagLayout());

        GridBagConstraints c;

        c = EstiloGui.gbc(0, 0);
        card.add(EstiloGui.etiqueta("Cantidad"), c);
        c = EstiloGui.gbc(1, 0);
        spinCantidad.setToolTipText("Cuántos enemigos saldrán en esta oleada.");
        card.add(spinCantidad, c);

        c = EstiloGui.gbc(2, 0);
        card.add(EstiloGui.etiqueta("Tipo"), c);
        c = EstiloGui.gbc(3, 0);
        comboTipo.setToolTipText("Puedes elegir una sugerencia o escribir otra.");
        card.add(comboTipo, c);

        c = EstiloGui.gbc(4, 0);
        card.add(EstiloGui.etiqueta("Vida"), c);
        c = EstiloGui.gbc(5, 0);
        card.add(spinVida, c);

        c = EstiloGui.gbc(6, 0);
        card.add(EstiloGui.etiqueta("Velocidad"), c);
        c = EstiloGui.gbc(7, 0);
        spinVel.setToolTipText("Casillas que avanza cada enemigo por turno.");
        card.add(spinVel, c);

        JLabel pista = EstiloGui.muted(
                "Define las oleadas y pulsa Iniciar. Después pasa a Batalla para avanzar turnos.");
        c = EstiloGui.gbcFill(0, 1, 8);
        card.add(pista, c);

        c = EstiloGui.gbcFill(0, 2, 8);
        card.add(EstiloGui.barraDerecha(btnAgregar), c);

        return card;
    }

    private JPanel crearTabla() {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        JScrollPane scroll = EstiloGui.scroll(tabla);
        contenedorTabla.setOpaque(false);
        contenedorTabla.add(scroll, "tabla");
        contenedorTabla.add(
                EstiloGui.vacio("Sin oleadas todavía", "Registra al menos una oleada para poder iniciarla."),
                "vacio");
        tarjetasTabla.show(contenedorTabla, "vacio");
        card.add(contenedorTabla, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout(12, 0));
        pie.setOpaque(false);
        pie.add(mensaje, BorderLayout.CENTER);
        pie.add(EstiloGui.barraDerecha(btnIniciar), BorderLayout.EAST);
        return pie;
    }

    private void agregar(ActionEvent e) {
        try {
            if (!EstiloGui.comprometerTodos(spinCantidad, spinVida, spinVel)) {
                EstiloGui.mensaje(mensaje, "Hay un número inválido en el formulario.", EstiloGui.MAL_TEXTO);
                return;
            }
            JuegoControl.Resultado r = control.agregarOleada(
                    EstiloGui.valorSpinner(spinCantidad),
                    EstiloGui.textoCombo(comboTipo),
                    EstiloGui.valorSpinner(spinVida),
                    EstiloGui.valorSpinner(spinVel));
            EstiloGui.mensaje(mensaje, r.mensaje, r.ok ? EstiloGui.OK_TEXTO : EstiloGui.MAL_TEXTO);
            onCambio.run();
        } catch (RuntimeException ex) {
            EstiloGui.mensaje(mensaje, "No se pudo registrar la oleada.", EstiloGui.MAL_TEXTO);
        }
    }

    private void iniciar(ActionEvent e) {
        try {
            JuegoControl.Resultado r = control.iniciarSiguienteOleada();
            EstiloGui.mensaje(mensaje, r.mensaje, r.ok ? EstiloGui.OK_TEXTO : EstiloGui.MAL_TEXTO);
            onCambio.run();
            if (r.ok) {
                irABatalla.run();
            }
        } catch (RuntimeException ex) {
            EstiloGui.mensaje(mensaje, "No se pudo iniciar la oleada.", EstiloGui.MAL_TEXTO);
        }
    }
}
