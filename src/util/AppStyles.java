package util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public final class AppStyles {

    public static final Color PRIMARY = new Color(10, 82, 132);
    public static final Color PRIMARY_DARK = new Color(7, 49, 79);
    public static final Color ACCENT = new Color(18, 130, 101);
    public static final Color DANGER = new Color(174, 49, 49);
    public static final Color WARNING = new Color(191, 105, 22);
    public static final Color BACKGROUND = new Color(238, 244, 248);
    public static final Color PANEL = new Color(255, 255, 255);
    public static final Color TEXT = new Color(16, 31, 46);
    public static final Color MUTED_TEXT = new Color(64, 80, 96);
    public static final Color FIELD_BORDER = new Color(172, 187, 199);
    public static final String APP_NAME = "Sistema SDS";
    public static final String APP_SLOGAN = "Tu cine en casa, rápido y organizado";
    private static final String LOGO_RESOURCE = "/recursos/sds_logo.png";

    private AppStyles() {
    }

    public static void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 13));
            UIManager.put("Label.foreground", TEXT);
            UIManager.put("TextField.foreground", TEXT);
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("ComboBox.foreground", TEXT);
            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("Table.foreground", TEXT);
            UIManager.put("Table.selectionForeground", TEXT);
            UIManager.put("TableHeader.foreground", Color.BLACK);
            UIManager.put("TableHeader.background", new Color(225, 233, 240));
        } catch (Exception ignored) {
        }
    }

    public static void aplicarIcono(JFrame frame) {
        if (frame == null) {
            return;
        }
        Image image = cargarImagenLogo();
        if (image != null) {
            frame.setIconImage(image);
        }
    }

    public static Image cargarImagenLogo() {
        URL url = AppStyles.class.getResource(LOGO_RESOURCE);
        if (url == null) {
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    public static ImageIcon logo(int size) {
        Image image = cargarImagenLogo();
        if (image == null) {
            return null;
        }
        return new ImageIcon(image.getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }

    public static JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        return panel;
    }

    public static JPanel crearTarjeta() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 216, 225)),
                new EmptyBorder(14, 14, 14, 14)
        ));
        return panel;
    }

    public static JPanel crearTarjetaEstado(Color colorBorde) {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, colorBorde),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(204, 216, 225)),
                        new EmptyBorder(16, 16, 16, 16)
                )
        ));
        return panel;
    }

    public static JLabel titulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(PRIMARY_DARK);
        return label;
    }

    public static JLabel subtitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(MUTED_TEXT);
        return label;
    }

    public static JLabel sesion(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(PRIMARY);
        return label;
    }

    public static JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT);
        return label;
    }

    public static JTextField campoTexto() {
        JTextField campo = new JTextField();
        aplicarEstiloCampo(campo);
        return campo;
    }

    public static JTextField campoTexto(String texto) {
        JTextField campo = new JTextField(texto);
        aplicarEstiloCampo(campo);
        return campo;
    }

    public static void aplicarEstiloCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setForeground(TEXT);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(PRIMARY_DARK);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER),
                new EmptyBorder(7, 8, 7, 8)
        ));
    }

    public static JComboBox<String> combo(String[] opciones) {
        JComboBox<String> combo = new JComboBox<>(opciones);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setForeground(TEXT);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(FIELD_BORDER));
        return combo;
    }

    public static JButton botonPrimario(String texto) {
        return crearBoton(texto, PRIMARY, Color.WHITE);
    }

    public static JButton botonSecundario(String texto) {
        return crearBoton(texto, ACCENT, Color.WHITE);
    }

    public static JButton botonPeligro(String texto) {
        return crearBoton(texto, DANGER, Color.WHITE);
    }

    public static JButton botonAdvertencia(String texto) {
        return crearBoton(texto, WARNING, Color.WHITE);
    }

    public static JButton botonNeutro(String texto) {
        return crearBoton(texto, new Color(225, 233, 240), TEXT);
    }

    private static JButton crearBoton(String texto, Color fondo, Color letra) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(letra);
        boton.setBackground(fondo);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setIcon(AppIconFactory.forText(texto, letra));
        boton.setIconTextGap(8);
        boton.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    public static JScrollPane scrollTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setForeground(TEXT);
        tabla.setBackground(Color.WHITE);
        tabla.setRowHeight(30);
        tabla.setSelectionBackground(new Color(204, 229, 221));
        tabla.setSelectionForeground(TEXT);
        tabla.setGridColor(new Color(220, 228, 235));
        tabla.setShowVerticalLines(false);
        tabla.setDefaultRenderer(Object.class, new EstadoTableCellRenderer());

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(225, 233, 240));
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);
        header.setOpaque(true);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(225, 233, 240));
        headerRenderer.setForeground(Color.BLACK);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        headerRenderer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(184, 198, 210)),
                new EmptyBorder(8, 6, 8, 6)
        ));
        for (int i = 0; i < tabla.getColumnModel().getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(204, 216, 225)));
        return scroll;
    }

    public static boolean confirmar(Component parent, String mensaje, String titulo) {
        int opcion = JOptionPane.showConfirmDialog(
                parent,
                mensaje,
                titulo == null ? "Confirmar acción" : titulo,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return opcion == JOptionPane.YES_OPTION;
    }

    public static boolean confirmarCerrarSesion(Component parent) {
        return confirmar(parent, "¿Está seguro que desea cerrar sesión?", "Cerrar sesión");
    }

    public static void mostrarExito(Component parent, String mensaje) {
        mostrarMensaje(parent, mensaje, "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostrarAdvertencia(Component parent, String mensaje) {
        mostrarMensaje(parent, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    public static void mostrarError(Component parent, String mensaje, String titulo) {
        mostrarMensaje(parent, mensaje, titulo == null ? "No se pudo completar" : titulo, JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarInfo(Component parent, String mensaje, String titulo) {
        mostrarMensaje(parent, mensaje, titulo == null ? "Información" : titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private static void mostrarMensaje(Component parent, String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(parent, mensaje, titulo, tipo);
    }

    public static void margen(JComponent component, int top, int left, int bottom, int right) {
        component.setBorder(new EmptyBorder(top, left, bottom, right));
    }

    public static GridBagConstraints gbc(int x, int y, int width) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 6, 6, 6);
        return gbc;
    }

    public static GridBagConstraints gbcBoton(int x, int y) {
        GridBagConstraints gbc = gbc(x, y, 1);
        gbc.weightx = 0;
        return gbc;
    }

    public static void setEnabledRecursivo(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof JPanel panel) {
            for (Component child : panel.getComponents()) {
                setEnabledRecursivo(child, enabled);
            }
        }
    }

    private static class EstadoTableCellRenderer extends DefaultTableCellRenderer {

        private final Color disponible = new Color(226, 246, 235);
        private final Color noDisponible = new Color(253, 232, 232);
        private final Color vencido = new Color(255, 239, 213);
        private final Color devuelto = new Color(225, 241, 255);
        private final Color reservado = new Color(241, 235, 255);
        private final Color pendiente = new Color(255, 248, 220);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(new EmptyBorder(0, 8, 0, 8));
            if (!isSelected) {
                c.setBackground(colorPorEstado(table, row));
                c.setForeground(TEXT);
            }
            return c;
        }

        private Color colorPorEstado(JTable table, int viewRow) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            String estado = detectarEstado(table, modelRow);
            if (estado.contains("vencido")) {
                return vencido;
            }
            if (estado.contains("no disponible")) {
                return noDisponible;
            }
            if (estado.contains("reservado")) {
                return reservado;
            }
            if (estado.contains("devuelto") || estado.contains("atendida")) {
                return devuelto;
            }
            if (estado.contains("disponible")) {
                return disponible;
            }
            if (estado.contains("pendiente")) {
                return pendiente;
            }
            return Color.WHITE;
        }

        private String detectarEstado(JTable table, int modelRow) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < table.getModel().getColumnCount(); i++) {
                Object valor = table.getModel().getValueAt(modelRow, i);
                if (valor != null) {
                    builder.append(' ').append(valor.toString().trim().toLowerCase());
                }
            }
            return builder.toString();
        }
    }
}
