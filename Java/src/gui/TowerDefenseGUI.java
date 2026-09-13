package gui;

import javax.swing.SwingUtilities;

/**
 * Arranque de la interfaz grafica Swing.
 * La consola ({@code app.TowerDefenseApp}) se conserva intacta.
 *
 * Compilar desde Java/src:
 *   javac -encoding UTF-8 -d ../out modelo/*.java negocio/*.java app/*.java gui/*.java
 * Ejecutar:
 *   java -cp ../out gui.TowerDefenseGUI
 */
public class TowerDefenseGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                EstiloGui.aplicarNimbus();
                MainFrame ventana = new MainFrame();
                ventana.setVisible(true);
            }
        });
    }
}
