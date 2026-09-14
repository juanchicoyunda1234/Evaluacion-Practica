package gui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Formulario emergente para registrar una torre defensiva.
 * Solo llama a {@link JuegoControl#agregarTorre}; no toca negocio ni modelo.
 */
public class DialogoTorre extends JDialog {

    private static final String[] TIPOS = { "arquero", "canon", "mago" };

    private final JTextField campoNombre = EstiloGui.campo(16);
    private final JComboBox<String> comboTipo = EstiloGui.comboEditable(TIPOS, "arquero");
    private final JSpinner spinPos = EstiloGui.entero(0, JuegoControl.POSICION_MAXIMA, 5);
    private final JSpinner spinDanio = EstiloGui.entero(1, 100, 5);
    private final JSpinner spinRango = EstiloGui.entero(0, JuegoControl.POSICION_MAXIMA, 3);
    private final JSpinner spinCosto = EstiloGui.entero(0, 999, 10);
    private final JLabel mensaje = EstiloGui.muted("Completa el formulario y pulsa Agregar.");

    private boolean registrada = false;

    public DialogoTorre(Frame propietario, JuegoControl control) {
        super(propietario, "Registrar torre", true);

        JPanel contenido = EstiloGui.pagina();
        contenido.setLayout(new GridBagLayout());
        GridBagConstraints c;

        c = EstiloGui.gbc(0, 0);
        contenido.add(EstiloGui.etiqueta("Nombre"), c);
        c = EstiloGui.gbcFill(1, 0, 3);
        campoNombre.setToolTipText("Ejemplo: Arquero Norte");
        contenido.add(campoNombre, c);

        c = EstiloGui.gbc(0, 1);
        contenido.add(EstiloGui.etiqueta("Tipo"), c);
        c = EstiloGui.gbc(1, 1);
        contenido.add(comboTipo, c);

        c = EstiloGui.gbc(2, 1);
        contenido.add(EstiloGui.etiqueta("Posición"), c);
        c = EstiloGui.gbc(3, 1);
        spinPos.setToolTipText("Casilla única en la ruta (0 a 20). No pueden coincidir dos torres.");
        contenido.add(spinPos, c);

        c = EstiloGui.gbc(0, 2);
        contenido.add(EstiloGui.etiqueta("Daño"), c);
        c = EstiloGui.gbc(1, 2);
        contenido.add(spinDanio, c);

        c = EstiloGui.gbc(2, 2);
        contenido.add(EstiloGui.etiqueta("Rango"), c);
        c = EstiloGui.gbc(3, 2);
        spinRango.setToolTipText("Distancia a la que la torre alcanza enemigos (0 o más).");
        contenido.add(spinRango, c);

        c = EstiloGui.gbc(0, 3);
        contenido.add(EstiloGui.etiqueta("Costo"), c);
        c = EstiloGui.gbc(1, 3);
        contenido.add(spinCosto, c);

        c = EstiloGui.gbcFill(0, 4, 4);
        contenido.add(mensaje, c);

        JButton btnAgregar = EstiloGui.primario("➕  Agregar torre");
        JButton btnCancelar = EstiloGui.boton("Cancelar", EstiloGui.MUTED, EstiloGui.MUTED.darker());
        c = EstiloGui.gbcFill(0, 5, 4);
        contenido.add(EstiloGui.barraDerecha(btnCancelar, btnAgregar), c);

        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregar(control);
            }
        });
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        contenido.setBorder(new EmptyBorder(18, 20, 14, 20));
        setContentPane(contenido);
        getRootPane().setDefaultButton(btnAgregar);
        pack();
        setResizable(false);
        setLocationRelativeTo(propietario);
    }

    private void agregar(JuegoControl control) {
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
            registrada = true;
            dispose();
        } else {
            EstiloGui.mensaje(mensaje, r.mensaje, EstiloGui.MAL_TEXTO);
        }
    }

    /** @return true si la torre quedó registrada antes de cerrar el diálogo. */
    public boolean fueRegistrada() {
        return registrada;
    }
}
