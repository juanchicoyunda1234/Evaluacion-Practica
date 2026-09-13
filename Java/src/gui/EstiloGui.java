package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Paleta, tipografia y controles reutilizables de la GUI.
 * No contiene logica de juego.
 */
public final class EstiloGui {

    public static final Color PAGINA = new Color(241, 245, 249);
    public static final Color TARJETA = Color.WHITE;
    public static final Color BARRA = new Color(15, 23, 42);
    public static final Color BARRA_TEXTO = new Color(241, 245, 249);
    public static final Color ACENTO = new Color(56, 189, 248);
    public static final Color VERDE = new Color(22, 163, 74);
    public static final Color VERDE_HOVER = new Color(21, 128, 61);
    public static final Color ROJO = new Color(220, 38, 38);
    public static final Color ROJO_HOVER = new Color(185, 28, 28);
    public static final Color MUTED = new Color(100, 116, 139);
    public static final Color LINEA = new Color(226, 232, 240);
    public static final Color TITULO = new Color(15, 23, 42);
    public static final Color SELECCION = new Color(186, 230, 253);
    public static final Color FILA_ALT = new Color(248, 250, 252);
    public static final Color LOG_FONDO = new Color(15, 23, 42);
    public static final Color LOG_TEXTO = new Color(226, 232, 240);
    public static final Color BANNER_OK = new Color(220, 252, 231);
    public static final Color BANNER_MAL = new Color(254, 226, 226);
    public static final Color BANNER_INFO = new Color(224, 242, 254);
    public static final Color OK_TEXTO = new Color(22, 101, 52);
    public static final Color MAL_TEXTO = new Color(153, 27, 27);
    public static final Color INFO_TEXTO = new Color(3, 105, 161);

    public static final Font FUENTE = fuenteUi(Font.PLAIN, 14);
    public static final Font FUENTE_NEGRITA = fuenteUi(Font.BOLD, 14);
    public static final Font FUENTE_TITULO = fuenteUi(Font.BOLD, 16);
    public static final Font FUENTE_GRANDE = fuenteUi(Font.BOLD, 26);
    public static final Font FUENTE_PEQUENA = fuenteUi(Font.PLAIN, 12);
    public static final Font FUENTE_MONO = new Font("Consolas", Font.PLAIN, 13);

    /**
     * SansSerif encadena a una fuente emoji en Windows; Segoe UI no dibuja
     * pictogramas fuera del BMP y deja un recuadro vacio.
     */
    private static Font fuenteUi(int estilo, int tamano) {
        Font candidata = new Font("SansSerif", estilo, tamano);
        if (candidata.canDisplay(0x1F3AE)) {
            return candidata;
        }
        Font emoji = new Font("Segoe UI Emoji", estilo, tamano);
        if (emoji.canDisplay(0x1F3AE)) {
            return emoji;
        }
        return new Font("Dialog", estilo, tamano);
    }

    private EstiloGui() {
    }

    public static void aplicarNimbus() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (Exception ignorada) {
            return;
        }

        UIManager.put("nimbusBase", new Color(30, 64, 111));
        UIManager.put("nimbusBlueGrey", new Color(176, 190, 204));
        UIManager.put("control", PAGINA);
        UIManager.put("nimbusSelectionBackground", new Color(14, 116, 144));
        UIManager.put("defaultFont", FUENTE);
        UIManager.put("Button.font", FUENTE_NEGRITA);
        UIManager.put("Label.font", FUENTE);
        UIManager.put("Table.font", FUENTE);
        UIManager.put("TableHeader.font", FUENTE_NEGRITA);
        UIManager.put("TextField.font", FUENTE);
        UIManager.put("ComboBox.font", FUENTE);
        UIManager.put("Spinner.font", FUENTE);
        UIManager.put("TabbedPane.font", FUENTE_NEGRITA);
        UIManager.put("TitledBorder.font", FUENTE_NEGRITA);
        UIManager.put("TextArea.font", FUENTE_MONO);
        UIManager.put("OptionPane.messageFont", FUENTE);
        UIManager.put("OptionPane.buttonFont", FUENTE_NEGRITA);
        UIManager.put("ToolTip.font", FUENTE_PEQUENA);
        UIManager.put("TabbedPane.tabInsets", new Insets(8, 18, 8, 18));
    }

    public static JPanel pagina() {
        JPanel p = new JPanel();
        p.setOpaque(true);
        p.setBackground(PAGINA);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        return p;
    }

    public static JPanel tarjeta(String titulo) {
        JPanel p = new JPanel();
        p.setOpaque(true);
        p.setBackground(TARJETA);
        TitledBorder borde = BorderFactory.createTitledBorder(
                new CompoundBorder(
                        new LineBorder(LINEA, 1, true),
                        new EmptyBorder(10, 12, 10, 12)),
                titulo,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FUENTE_TITULO,
                TITULO);
        p.setBorder(borde);
        return p;
    }

    public static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE_NEGRITA);
        l.setForeground(TITULO);
        return l;
    }

    public static JLabel muted(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE_PEQUENA);
        l.setForeground(MUTED);
        return l;
    }

    public static JTextField campo(int columnas) {
        JTextField t = new JTextField(columnas);
        t.setFont(FUENTE);
        t.setMargin(new Insets(4, 8, 4, 8));
        return t;
    }

    public static JSpinner entero(int min, int max, int valor) {
        JSpinner s = new JSpinner(new SpinnerNumberModel(valor, min, max, 1));
        s.setFont(FUENTE);
        Component editor = s.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setColumns(4);
            ((JSpinner.DefaultEditor) editor).getTextField().setFont(FUENTE);
            ((JSpinner.DefaultEditor) editor).getTextField().setHorizontalAlignment(SwingConstants.CENTER);
        }
        return s;
    }

    public static JComboBox<String> comboEditable(String[] opciones, String inicial) {
        JComboBox<String> c = new JComboBox<String>(opciones);
        c.setEditable(true);
        c.setSelectedItem(inicial);
        c.setFont(FUENTE);
        c.setPreferredSize(new Dimension(150, c.getPreferredSize().height + 4));
        return c;
    }

    public static boolean comprometer(JSpinner spinner) {
        try {
            spinner.commitEdit();
            return true;
        } catch (java.text.ParseException ex) {
            return false;
        }
    }

    public static boolean comprometerTodos(JSpinner... spinners) {
        boolean ok = true;
        for (int i = 0; i < spinners.length; i++) {
            if (!comprometer(spinners[i])) {
                ok = false;
            }
        }
        return ok;
    }

    public static int valorSpinner(JSpinner spinner) {
        comprometer(spinner);
        Object v = spinner.getValue();
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        return 0;
    }

    public static String textoCombo(JComboBox<String> combo) {
        Object v = combo.getEditor().getItem();
        if (v == null) {
            v = combo.getSelectedItem();
        }
        return v == null ? "" : v.toString().trim();
    }

    public static void alEnter(JComboBox<String> combo, java.awt.event.ActionListener accion) {
        Component editor = combo.getEditor().getEditorComponent();
        if (editor instanceof JTextField) {
            ((JTextField) editor).addActionListener(accion);
        }
    }

    public static JButton primario(String texto) {
        return boton(texto, VERDE, VERDE_HOVER);
    }

    public static JButton peligro(String texto) {
        return boton(texto, ROJO, ROJO_HOVER);
    }

    public static JButton boton(String texto, Color fondo, Color hover) {
        JButton b = new JButton(texto);
        b.setFont(FUENTE_NEGRITA);
        b.setForeground(Color.WHITE);
        b.setBackground(fondo);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMargin(new Insets(8, 16, 8, 16));
        aplicarPintorNimbus(b, fondo, hover);
        return b;
    }

    private static void aplicarPintorNimbus(JButton boton, Color fondo, Color hover) {
        Color presionado = fondo.darker();
        Color deshabilitado = new Color(148, 163, 184);
        UIDefaults extras = new UIDefaults();
        extras.put("Button.contentMargins", new Insets(8, 18, 8, 18));
        extras.put("Button[Enabled].backgroundPainter", pintor(fondo));
        extras.put("Button[Default].backgroundPainter", pintor(fondo));
        extras.put("Button[MouseOver].backgroundPainter", pintor(hover));
        extras.put("Button[Default+MouseOver].backgroundPainter", pintor(hover));
        extras.put("Button[Pressed].backgroundPainter", pintor(presionado));
        extras.put("Button[Default+Pressed].backgroundPainter", pintor(presionado));
        extras.put("Button[Focused].backgroundPainter", pintor(fondo));
        extras.put("Button[Default+Focused].backgroundPainter", pintor(fondo));
        extras.put("Button[Focused+MouseOver].backgroundPainter", pintor(hover));
        extras.put("Button[Disabled].backgroundPainter", pintor(deshabilitado));
        extras.put("Button[Enabled].textForeground", Color.WHITE);
        extras.put("Button[Default].textForeground", Color.WHITE);
        extras.put("Button[MouseOver].textForeground", Color.WHITE);
        extras.put("Button[Pressed].textForeground", Color.WHITE);
        extras.put("Button[Focused].textForeground", Color.WHITE);
        extras.put("Button[Disabled].textForeground", Color.WHITE);
        boton.putClientProperty("Nimbus.Overrides", extras);
        boton.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.FALSE);
        boton.setForeground(Color.WHITE);
    }

    private static javax.swing.Painter<JComponent> pintor(Color color) {
        return (Graphics2D g, JComponent c, int w, int h) -> {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, w, h, 10, 10);
            g2.dispose();
        };
    }

    public static void configurarTabla(JTable tabla) {
        tabla.setFont(FUENTE);
        tabla.setRowHeight(28);
        tabla.setFillsViewportHeight(true);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(LINEA);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(SELECCION);
        tabla.setSelectionForeground(TITULO);
        tabla.setBackground(TARJETA);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowSelectionAllowed(true);
        tabla.setColumnSelectionAllowed(false);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(FUENTE_NEGRITA);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setBackground(PAGINA);
        header.setForeground(TITULO);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));

        DefaultTableCellRenderer cebra = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? TARJETA : FILA_ALT);
                    c.setForeground(TITULO);
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (value instanceof Number) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                }
                return c;
            }
        };
        tabla.setDefaultRenderer(Object.class, cebra);
        tabla.setDefaultRenderer(Integer.class, cebra);
        tabla.setDefaultRenderer(String.class, cebra);
    }

    public static JScrollPane scroll(Component contenido) {
        JScrollPane sp = new JScrollPane(contenido);
        sp.setBorder(new LineBorder(LINEA, 1, true));
        sp.getViewport().setBackground(TARJETA);
        sp.setBackground(TARJETA);
        return sp;
    }

    public static JPanel barraDerecha(JComponent... componentes) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalGlue());
        for (int i = 0; i < componentes.length; i++) {
            if (i > 0) {
                p.add(Box.createHorizontalStrut(8));
            }
            p.add(componentes[i]);
        }
        return p;
    }

    public static JPanel vacio(String titulo, String detalle) {
        JPanel p = new JPanel();
        p.setOpaque(true);
        p.setBackground(TARJETA);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new CompoundBorder(new LineBorder(LINEA, 1, true), new EmptyBorder(36, 24, 36, 24)));

        JLabel t = new JLabel(titulo, SwingConstants.CENTER);
        t.setFont(FUENTE_TITULO);
        t.setForeground(TITULO);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel d = new JLabel(detalle, SwingConstants.CENTER);
        d.setFont(FUENTE);
        d.setForeground(MUTED);
        d.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(Box.createVerticalGlue());
        p.add(t);
        p.add(Box.createVerticalStrut(8));
        p.add(d);
        p.add(Box.createVerticalGlue());
        return p;
    }

    public static GridBagConstraints gbc(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;
        return c;
    }

    public static GridBagConstraints gbcFill(int x, int y, int ancho) {
        GridBagConstraints c = gbc(x, y);
        c.gridwidth = ancho;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }

    public static void mensaje(JLabel destino, String texto, Color color) {
        destino.setText(texto);
        destino.setForeground(color);
    }
}
