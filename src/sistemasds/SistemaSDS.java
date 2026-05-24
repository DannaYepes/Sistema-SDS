package sistemasds;

import javax.swing.SwingUtilities;
import servicio.ArchivoService;
import util.AppStyles;
import vista.BienvenidaFrame;

public class SistemaSDS {

    public static void main(String[] args) {
        AppStyles.aplicarLookAndFeel();
        ArchivoService.cargarTodo();

        Runtime.getRuntime().addShutdownHook(new Thread(ArchivoService::guardarTodo));

        SwingUtilities.invokeLater(BienvenidaFrame::new);
    }
}
