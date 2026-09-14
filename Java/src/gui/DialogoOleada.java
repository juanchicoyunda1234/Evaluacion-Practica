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
import javax.swing.border.EmptyBorder;

/**
 * Formulario emergente para registrar una oleada de enemigos.
 * Solo llama a {@link JuegoControl#agregarOleada}; no toca negocio ni modelo.
 */
public class DialogoOleada extends JDialog {

    private static final String[] TIPOS = { "goblin", "orco", "troll" };

    private final JSpinner spinCantidad = EstiloGui.entero(1, 50, 3);
    private final JComboBox<String> comboTipo = EstiloGui.comboEditable(TIPOS, "goblin");
    private final JSpinner spinVida = EstiloGui.entero(1, 500, 10);
    private final JSpinner spinVel = EstiloGui.entero(1, 10, 1);
    private final JLabel mensaje = EstiloGui.muted("Completa el formulario y pulsa Agregar.");

    private boolean registrada = false;

    public DialogoOleada(Frame propietario, JuegoControl control) {
        super(propietario, "Registrar oleada", true);

        JPanel contenido = EstiloGui.pagina();
        contenido.setLayout(new GridBagLayout());
        GridBagConstraints c;

        c = EstiloGui.gbc(0, 0);
        contenido.add(EstiloGui.etiqueta("Cantidad"), c);
        c = EstiloGui.gbc(1, 0);
        spinCantidad.setToolTipText("Cuántos enemigos saldrán en esta oleada.");
        contenido.add(spinCantidad, c);

        c = EstiloGui.gbc(2, 0);
        contenido.add(EstiloGui.etiqueta("Tipo"), c);
        c = EstiloGui.gbc(3, 0);
        contenido.add(comboTipo, c);

        c = EstiloGui.gbc(0, 1);
        contenido.add(EstiloGui.etiqueta("Vida"), c);
        c = EstiloGui.gbc(1, 1);
        contenido.add(spinVida, c);

        c = EstiloGui.gbc(2, 1);
        contenido.add(EstiloGui.etiqueta("Velocidad"), c);
        c = EstiloGui.gbc(3, 1);
        spinVel.setToolTipText("Casillas que avanza cada enemigo por turno.");
        contenido.add(spinVel, c);

        c = EstiloGui.gbcFill(0, 2, 4);
        contenido.add(mensaje, c);

        JButton btnAgregar = EstiloGui.primario("➕  Agregar oleada");
        JButton btnCancelar = EstiloGui.boton("Cancelar", EstiloGui.MUTED, EstiloGui.MUTED.darker());
        c = EstiloGui.gbcFill(0, 3, 4);
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
        if (!EstiloGui.comprometerTodos(spinCantidad, spinVida, spinVel)) {
            EstiloGui.mensaje(mensaje, "Hay un número inválido en el formulario.", EstiloGui.MAL_TEXTO);
            return;
        }
        JuegoControl.Resultado r = control.agregarOleada(
                EstiloGui.valorSpinner(spinCantidad),
                EstiloGui.textoCombo(comboTipo),
                EstiloGui.valorSpinner(spinVida),
                EstiloGui.valorSpinner(spinVel));
        if (r.ok) {
            registrada = true;
            dispose();
        } else {
            EstiloGui.mensaje(mensaje, r.mensaje, EstiloGui.MAL_TEXTO);
        }
    }

    /** @return true si la oleada quedó registrada antes de cerrar el diálogo. */
    public boolean fueRegistrada() {
        return registrada;
    }
}
