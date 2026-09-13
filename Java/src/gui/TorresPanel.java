package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import modelo.Torre;

public class TorresPanel extends JPanel {

    private static final String[] COLUMNAS = {
            "Id", "Nombre", "Tipo", "Pos", "Daño", "Rango", "Costo"
    };
    private static final String[] TIPOS = { "arquero", "canon", "mago" };

    private final JuegoControl control;
    private final Runnable onCambio;

    private final JTextField campoNombre = EstiloGui.campo(16);
    private final JComboBox<String> comboTipo = EstiloGui.comboEditable(TIPOS, "arquero");
    private final JSpinner spinPos = EstiloGui.entero(0, JuegoControl.POSICION_MAXIMA, 5);
    private final JSpinner spinDanio = EstiloGui.entero(1, 100, 5);
    private final JSpinner spinRango = EstiloGui.entero(0, JuegoControl.POSICION_MAXIMA, 3);
    private final JSpinner spinCosto = EstiloGui.entero(0, 999, 10);
    private final JLabel cupo = EstiloGui.muted("Torres: 0 / " + JuegoControl.MAX_TORRES);
    private final JLabel mensaje = EstiloGui.muted("Completa el formulario y pulsa Agregar.");
    private final JButton btnAgregar = EstiloGui.primario("➕  Agregar torre");
    private final JButton btnEliminar = EstiloGui.peligro("✕  Eliminar seleccionada");

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final CardLayout tarjetasTabla = new CardLayout();
    private final JPanel contenedorTabla = new JPanel(tarjetasTabla);

    public TorresPanel(JuegoControl control, Runnable onCambio) {
        this.control = control;
        this.onCambio = onCambio;

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 || columnIndex == 2 ? String.class : Integer.class;
            }
        };
        tabla = new JTable(modeloTabla);
        EstiloGui.configurarTabla(tabla);
        tabla.getColumnModel().getColumn(0).setMaxWidth(60);
        tabla.getColumnModel().getColumn(3).setMaxWidth(70);
        tabla.getColumnModel().getColumn(4).setMaxWidth(70);
        tabla.getColumnModel().getColumn(5).setMaxWidth(80);
        tabla.getColumnModel().getColumn(6).setMaxWidth(80);

        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        add(crearFormulario(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);

        btnAgregar.addActionListener(this::agregar);
        btnEliminar.addActionListener(this::eliminar);
        btnEliminar.setEnabled(false);
        btnAgregar.setToolTipText("Registra una torre en la lista secuencial.");
        btnEliminar.setToolTipText("Elimina la torre de la fila seleccionada.");
        campoNombre.addActionListener(this::agregar);
        EstiloGui.alEnter(comboTipo, this::agregar);

        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    btnEliminar.setEnabled(tabla.getSelectedRow() >= 0 && !control.isPartidaTerminada());
                }
            }
        });
        tabla.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE && btnEliminar.isEnabled()) {
                    eliminar(null);
                }
            }
        });
    }

    public void enfocarNombre() {
        campoNombre.requestFocusInWindow();
    }

    public void refrescar() {
        List<Torre> torres = control.listarTorres();
        modeloTabla.setRowCount(0);
        for (int i = 0; i < torres.size(); i++) {
            Torre t = torres.get(i);
            modeloTabla.addRow(new Object[] {
                    Integer.valueOf(t.getId()),
                    t.getNombre(),
                    t.getTipo(),
                    Integer.valueOf(t.getPosicion()),
                    Integer.valueOf(t.getDanio()),
                    Integer.valueOf(t.getRango()),
                    Integer.valueOf(t.getCosto())
            });
        }
        cupo.setText("Torres: " + torres.size() + " / " + JuegoControl.MAX_TORRES);
        tarjetasTabla.show(contenedorTabla, torres.isEmpty() ? "vacio" : "tabla");
        boolean terminada = control.isPartidaTerminada();
        btnAgregar.setEnabled(!terminada && torres.size() < JuegoControl.MAX_TORRES);
        btnEliminar.setEnabled(!terminada && tabla.getSelectedRow() >= 0);
        campoNombre.setEnabled(!terminada);
        comboTipo.setEnabled(!terminada);
        spinPos.setEnabled(!terminada);
        spinDanio.setEnabled(!terminada);
        spinRango.setEnabled(!terminada);
        spinCosto.setEnabled(!terminada);
    }

    private JPanel crearFormulario() {
        JPanel card = EstiloGui.tarjeta("🏹  Torres defensivas");
        card.setLayout(new GridBagLayout());

        GridBagConstraints c;

        c = EstiloGui.gbc(0, 0);
        card.add(EstiloGui.etiqueta("Nombre"), c);
        c = EstiloGui.gbcFill(1, 0, 3);
        campoNombre.setToolTipText("Ejemplo: Arquero Norte");
        card.add(campoNombre, c);

        c = EstiloGui.gbc(4, 0);
        card.add(EstiloGui.etiqueta("Tipo"), c);
        c = EstiloGui.gbc(5, 0);
        comboTipo.setToolTipText("Puedes elegir una sugerencia o escribir otra.");
        card.add(comboTipo, c);

        JPanel numeros = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        numeros.setOpaque(false);
        spinPos.setToolTipText("Casilla única en la ruta (0 a 20). No pueden coincidir dos torres.");
        spinRango.setToolTipText("Distancia a la que la torre alcanza enemigos (0 o más).");
        numeros.add(EstiloGui.etiqueta("Posición"));
        numeros.add(spinPos);
        numeros.add(EstiloGui.etiqueta("Daño"));
        numeros.add(spinDanio);
        numeros.add(EstiloGui.etiqueta("Rango"));
        numeros.add(spinRango);
        numeros.add(EstiloGui.etiqueta("Costo"));
        numeros.add(spinCosto);
        c = EstiloGui.gbcFill(0, 1, 6);
        card.add(numeros, c);

        JLabel pista = EstiloGui.muted(
                "Cada torre ocupa una casilla distinta (0 a 20) y ataca sola a los enemigos en su rango.");
        c = EstiloGui.gbcFill(0, 2, 4);
        card.add(pista, c);
        c = EstiloGui.gbc(4, 2);
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.EAST;
        card.add(cupo, c);

        c = EstiloGui.gbcFill(0, 3, 6);
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
                EstiloGui.vacio("Sin torres todavía", "Completa el formulario y pulsa ➕ Agregar torre."),
                "vacio");
        tarjetasTabla.show(contenedorTabla, "vacio");
        card.add(contenedorTabla, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout(12, 0));
        pie.setOpaque(false);
        pie.add(mensaje, BorderLayout.CENTER);
        pie.add(EstiloGui.barraDerecha(btnEliminar), BorderLayout.EAST);
        return pie;
    }

    private void agregar(ActionEvent e) {
        try {
            if (!EstiloGui.comprometerTodos(spinPos, spinDanio, spinRango, spinCosto)) {
                EstiloGui.mensaje(mensaje, "Hay un número inválido en el formulario.", EstiloGui.MAL_TEXTO);
                return;
            }
            JuegoControl.Resultado r = control.agregarTorre(
                    campoNombre.getText(),
                    EstiloGui.textoCombo(comboTipo),
                    EstiloGui.valorSpinner(spinPos),
                    EstiloGui.valorSpinner(spinDanio),
                    EstiloGui.valorSpinner(spinRango),
                    EstiloGui.valorSpinner(spinCosto));
            if (r.ok) {
                campoNombre.setText("");
                campoNombre.requestFocusInWindow();
                EstiloGui.mensaje(mensaje, r.mensaje, EstiloGui.OK_TEXTO);
            } else {
                EstiloGui.mensaje(mensaje, r.mensaje, EstiloGui.MAL_TEXTO);
            }
            onCambio.run();
        } catch (RuntimeException ex) {
            EstiloGui.mensaje(mensaje, "No se pudo registrar la torre.", EstiloGui.MAL_TEXTO);
        }
    }

    private void eliminar(ActionEvent e) {
        try {
            int vista = tabla.getSelectedRow();
            if (vista < 0) {
                EstiloGui.mensaje(mensaje, "Selecciona una torre en la tabla.", EstiloGui.MAL_TEXTO);
                return;
            }
            int modelo = tabla.convertRowIndexToModel(vista);
            Object idValor = modeloTabla.getValueAt(modelo, 0);
            if (!(idValor instanceof Integer)) {
                EstiloGui.mensaje(mensaje, "No se pudo leer el id de la torre.", EstiloGui.MAL_TEXTO);
                return;
            }
            int id = ((Integer) idValor).intValue();
            String nombre = String.valueOf(modeloTabla.getValueAt(modelo, 1));
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Eliminar la torre \"" + nombre + "\" (id " + id + ")?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
            JuegoControl.Resultado r = control.eliminarTorre(id);
            EstiloGui.mensaje(mensaje, r.mensaje, r.ok ? EstiloGui.OK_TEXTO : EstiloGui.MAL_TEXTO);
            onCambio.run();
        } catch (RuntimeException ex) {
            EstiloGui.mensaje(mensaje, "No se pudo eliminar la torre.", EstiloGui.MAL_TEXTO);
        }
    }
}
