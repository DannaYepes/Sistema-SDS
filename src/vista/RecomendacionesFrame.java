package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import modelo.Pelicula;
import servicio.SistemaData;
import util.AppStyles;

public class RecomendacionesFrame extends JFrame {

    private final Pelicula base;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Título", "Género", "Año", "Formato", "Duración", "Copias", "Precio", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public RecomendacionesFrame(Pelicula base) {
        this.base = base;
        setTitle("Recomendaciones similares");
        AppStyles.aplicarIcono(this);
        setSize(880, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Recomendaciones"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Películas disponibles del mismo género de: " + (base == null ? "selección" : base.getTitulo())), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        cargarDatos();
        JTable tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);
        add(AppStyles.scrollTabla(tabla), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);
        var btnCerrar = AppStyles.botonNeutro("Cerrar");
        acciones.add(btnCerrar);
        add(acciones, BorderLayout.SOUTH);
        btnCerrar.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        if (base == null) {
            return;
        }
        for (Pelicula p : SistemaData.catalogo.getLista()) {
            if (!p.getTitulo().equalsIgnoreCase(base.getTitulo())
                    && p.estaDisponible()
                    && p.getGenero().equalsIgnoreCase(base.getGenero())) {
                modelo.addRow(new Object[]{
                    p.getTitulo(),
                    p.getGenero(),
                    p.getAnio(),
                    p.getFormato(),
                    p.getDuracionTexto(),
                    p.getCantidad(),
                    "$" + String.format("%,.2f", p.getPrecio()),
                    "Disponible"
                });
            }
        }
    }
}
