package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import gui.JuegoControl.FilaEnemigo;
import modelo.Oleada;
import modelo.Torre;

/**
 * Panel lateral con las tres listas del juego en pestañas cortas: torres,
 * enemigos activos y oleadas. Solo lee de {@link JuegoControl}.
 */
public class DatosPanel extends JPanel {

    private static final String[] COLUMNAS_TORRE = { "Id", "Nombre", "Tipo", "Posición", "Daño", "Rango", "Costo" };
    private static final String[] COLUMNAS_ENEMIGO = { "Id", "Tipo", "Posición", "Vida", "Velocidad" };
    private static final String[] COLUMNAS_OLEADA = { "Id", "Tipo", "Cantidad", "Vida base", "Vel. base" };

    public static final int TAB_TORRES = 0;
    public static final int TAB_ENEMIGOS = 1;
    public static final int TAB_OLEADAS = 2;

    private final JuegoControl control;
    private final JTabbedPane pestanas = new JTabbedPane();

    private final DefaultTableModel modeloTorres = crearModelo(COLUMNAS_TORRE);
    private final DefaultTableModel modeloEnemigos = crearModelo(COLUMNAS_ENEMIGO);
    private final DefaultTableModel modeloOleadas = crearModelo(COLUMNAS_OLEADA);

    private final JTable tablaTorres = new JTable(modeloTorres);
    private final JTable tablaEnemigos = new JTable(modeloEnemigos);
    private final JTable tablaOleadas = new JTable(modeloOleadas);

    public DatosPanel(JuegoControl control) {
        this.control = control;
        setOpaque(false);
        setLayout(new BorderLayout());

        EstiloGui.configurarTabla(tablaTorres);
        EstiloGui.configurarTabla(tablaEnemigos);
        EstiloGui.configurarTabla(tablaOleadas);
        tablaTorres.getColumnModel().getColumn(0).setMaxWidth(50);
        tablaEnemigos.getColumnModel().getColumn(0).setMaxWidth(50);
        tablaOleadas.getColumnModel().getColumn(0).setMaxWidth(50);

        pestanas.setFont(EstiloGui.FUENTE_NEGRITA);
        pestanas.addTab("Torre (0)", envolver(tablaTorres, "Sin torres todavía", "Usa Registrar torre."));
        pestanas.addTab("Enemigo (0)", envolver(tablaEnemigos, "Sin enemigos activos", "Inicia una oleada."));
        pestanas.addTab("Oleada (0)", envolver(tablaOleadas, "Sin oleadas todavía", "Usa Registrar oleada."));
        add(pestanas, BorderLayout.CENTER);
    }

    private static DefaultTableModel crearModelo(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class
                        : (columnas.length > 1 && columnIndex == 1 ? String.class : Object.class);
            }
        };
    }

    private JPanel envolver(JTable tabla, String tituloVacio, String detalleVacio) {
        CardLayout tarjetas = new CardLayout();
        JPanel contenedor = new JPanel(tarjetas);
        contenedor.setOpaque(false);
        JScrollPane scroll = EstiloGui.scroll(tabla);
        contenedor.add(scroll, "tabla");
        contenedor.add(EstiloGui.vacio(tituloVacio, detalleVacio), "vacio");
        contenedor.putClientProperty("tarjetas", tarjetas);
        return contenedor;
    }

    public void mostrarPestana(int indice) {
        pestanas.setSelectedIndex(indice);
    }

    public void refrescar() {
        List<Torre> torres = control.listarTorres();
        modeloTorres.setRowCount(0);
        for (int i = 0; i < torres.size(); i++) {
            Torre t = torres.get(i);
            modeloTorres.addRow(new Object[] {
                    Integer.valueOf(t.getId()), t.getNombre(), t.getTipo(),
                    Integer.valueOf(t.getPosicion()), Integer.valueOf(t.getDanio()),
                    Integer.valueOf(t.getRango()), Integer.valueOf(t.getCosto())
            });
        }

        List<FilaEnemigo> enemigos = control.listarEnemigos();
        modeloEnemigos.setRowCount(0);
        for (int i = 0; i < enemigos.size(); i++) {
            FilaEnemigo e = enemigos.get(i);
            modeloEnemigos.addRow(new Object[] {
                    Integer.valueOf(e.id), e.tipo, Integer.valueOf(e.posicion),
                    Integer.valueOf(e.vida), Integer.valueOf(e.velocidad)
            });
        }

        List<Oleada> oleadas = control.listarOleadas();
        modeloOleadas.setRowCount(0);
        for (int i = 0; i < oleadas.size(); i++) {
            Oleada o = oleadas.get(i);
            modeloOleadas.addRow(new Object[] {
                    Integer.valueOf(o.getIdOleada()), o.getTipoEnemigo(),
                    Integer.valueOf(o.getCantidadEnemigos()), Integer.valueOf(o.getVidaBase()),
                    Integer.valueOf(o.getVelocidadBase())
            });
        }

        pestanas.setTitleAt(TAB_TORRES, "Torre (" + torres.size() + ")");
        pestanas.setTitleAt(TAB_ENEMIGOS, "Enemigo (" + enemigos.size() + ")");
        pestanas.setTitleAt(TAB_OLEADAS, "Oleada (" + oleadas.size() + ")");

        mostrarTarjeta(TAB_TORRES, torres.isEmpty());
        mostrarTarjeta(TAB_ENEMIGOS, enemigos.isEmpty());
        mostrarTarjeta(TAB_OLEADAS, oleadas.isEmpty());
    }

    private void mostrarTarjeta(int indicePestana, boolean vacio) {
        JPanel contenedor = (JPanel) pestanas.getComponentAt(indicePestana);
        CardLayout tarjetas = (CardLayout) contenedor.getClientProperty("tarjetas");
        tarjetas.show(contenedor, vacio ? "vacio" : "tabla");
    }

    /** @return el id de la torre seleccionada en la pestaña Torre, o -1 si no hay selección. */
    public int obtenerIdTorreSeleccionada() {
        return idSeleccionado(tablaTorres, modeloTorres);
    }

    private int idSeleccionado(JTable tabla, DefaultTableModel modelo) {
        int vista = tabla.getSelectedRow();
        if (vista < 0) {
            return -1;
        }
        int fila = tabla.convertRowIndexToModel(vista);
        Object valor = modelo.getValueAt(fila, 0);
        return valor instanceof Integer ? ((Integer) valor).intValue() : -1;
    }
}
