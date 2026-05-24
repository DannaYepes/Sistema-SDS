package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import servicio.ReporteService;
import servicio.SistemaData;
import util.AppStyles;

public class TopPeliculasFrame extends JFrame {

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Posición", "Película", "Género", "Formato", "Alquileres", "Ingresos"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public TopPeliculasFrame() {
        setTitle("Top de películas más alquiladas");
        AppStyles.aplicarIcono(this);
        setSize(920, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Top películas"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Identifica los títulos con más rotación e ingresos para tomar decisiones de inventario."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JTable tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);
        add(AppStyles.scrollTabla(tabla), BorderLayout.CENTER);
        cargarDatos();

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);
        var btnActualizar = AppStyles.botonNeutro("Actualizar");
        var btnCerrar = AppStyles.botonNeutro("Cerrar");
        acciones.add(btnActualizar);
        acciones.add(btnCerrar);
        add(acciones, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> cargarDatos());
        btnCerrar.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        int posicion = 1;
        for (ReporteService.ResumenPelicula item : new ReporteService().topPeliculasMasAlquiladas(20)) {
            modelo.addRow(new Object[]{
                posicion++,
                item.getTitulo(),
                item.getGenero(),
                item.getFormato(),
                item.getCantidad(),
                "$" + String.format("%,.2f", item.getIngresos())
            });
        }
    }
}
