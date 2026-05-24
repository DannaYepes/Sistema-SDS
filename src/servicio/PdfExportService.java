package servicio;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;
import modelo.Alquiler;
import modelo.Reserva;
import util.AppStyles;

public final class PdfExportService {

    private static final int PAGE_WIDTH = 595;
    private static final int PAGE_HEIGHT = 842;
    private static final DecimalFormat MONEY = crearFormatoMoneda();
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private PdfExportService() {
    }

    public static File asegurarExtensionPdf(File destino) {
        if (destino == null) {
            return null;
        }
        String ruta = destino.getAbsolutePath();
        if (!ruta.toLowerCase().endsWith(".pdf")) {
            return new File(ruta + ".pdf");
        }
        return destino;
    }

    public static void exportarFactura(Alquiler alquiler, int totalCliente, File destino) throws IOException {
        if (alquiler == null) {
            throw new IllegalArgumentException("No hay información de alquiler para exportar.");
        }
        File archivo = asegurarExtensionPdf(destino);
        PdfCanvas canvas = new PdfCanvas();
        dibujarPlantilla(canvas, "FACTURA / COMPROBANTE");

        int y = 704;
        canvas.textBold("DATOS DEL CLIENTE", 54, y, 12);
        canvas.strokeLine(54, y - 8, 541, y - 8, 0.80, 0.86, 0.90);
        y -= 30;
        canvas.labelValue("Documento:", valorVisible(alquiler.getCliente().getDocumento()), 54, y);
        canvas.labelValue("Cliente:", alquiler.getCliente().getNombre(), 300, y);
        y -= 22;
        canvas.labelValue("Películas rentadas por el cliente:", String.valueOf(totalCliente), 54, y);
        canvas.labelValue("Registrado por:", alquiler.getUsuarioRegistro() + " / " + alquiler.getRolRegistro(), 300, y);

        y -= 46;
        canvas.textBold("DETALLE DE LA PELÍCULA", 54, y, 12);
        canvas.strokeLine(54, y - 8, 541, y - 8, 0.80, 0.86, 0.90);
        y -= 30;
        canvas.labelValue("Película:", alquiler.getPelicula().getTitulo(), 54, y);
        canvas.labelValue("Género:", alquiler.getPelicula().getGenero(), 300, y);
        y -= 22;
        canvas.labelValue("Idioma:", alquiler.getPelicula().getIdioma(), 54, y);
        canvas.labelValue("Año:", String.valueOf(alquiler.getPelicula().getAnio()), 300, y);
        y -= 22;
        canvas.labelValue("Duración:", alquiler.getPelicula().getDuracionTexto(), 54, y);
        canvas.labelValue("Formato:", alquiler.getPelicula().getFormato(), 300, y);
        y -= 22;
        canvas.labelValue("Descripción:", alquiler.getPelicula().getDescripcion(), 54, y);

        y -= 50;
        canvas.textBold("PERIODO Y ESTADO", 54, y, 12);
        canvas.strokeLine(54, y - 8, 541, y - 8, 0.80, 0.86, 0.90);
        y -= 30;
        canvas.labelValue("Fecha alquiler:", String.valueOf(alquiler.getFechaAlquiler()), 54, y);
        canvas.labelValue("Fecha devolución:", String.valueOf(alquiler.getFechaDevolucion()), 300, y);
        y -= 22;
        canvas.labelValue("Estado:", alquiler.getEstado(), 54, y);
        canvas.labelValue("Días de retraso:", String.valueOf(alquiler.getDiasRetrasoActual()), 300, y);

        y -= 58;
        canvas.fillRect(54, y - 92, 487, 104, 0.95, 0.98, 1.00);
        canvas.strokeRect(54, y - 92, 487, 104, 0.73, 0.82, 0.88);
        canvas.textBold("RESUMEN DE PAGO", 74, y - 12, 13);
        canvas.labelValue("Método de pago:", alquiler.getPago().getMetodo(), 74, y - 38);
        canvas.labelValue("Código comprobante:", alquiler.getPago().getId(), 300, y - 38);
        canvas.labelValue("Subtotal bruto:", "$" + MONEY.format(alquiler.getSubtotalSinDescuento()), 74, y - 64);
        canvas.labelValue("Descuento:", "$" + MONEY.format(alquiler.getDescuento()), 300, y - 64);
        canvas.labelValue("Multa actual:", "$" + MONEY.format(alquiler.getMultaActual()), 74, y - 86);
        canvas.labelValue("Beneficio:", truncar(alquiler.getBeneficioAplicado(), 34), 300, y - 86);
        canvas.textBold("TOTAL ACTUAL: $" + MONEY.format(alquiler.getTotalConMultaActual()), 300, y - 108, 16);

        canvas.text("Este comprobante fue generado automáticamente por " + AppStyles.APP_NAME + ".", 54, 78, 9);
        canvas.text("Listo para impresión o archivo digital.", 54, 64, 9);
        escribirPdf(archivo, canvas.build());
    }

    public static void exportarReporte(File destino) throws IOException {
        File archivo = asegurarExtensionPdf(destino);
        ReporteService reporte = new ReporteService();
        PdfCanvas canvas = new PdfCanvas();
        dibujarPlantilla(canvas, "REPORTE GENERAL");

        int y = 704;
        canvas.textBold("INDICADORES PRINCIPALES", 54, y, 12);
        canvas.strokeLine(54, y - 8, 541, y - 8, 0.80, 0.86, 0.90);
        y -= 36;
        metric(canvas, "Películas disponibles", String.valueOf(reporte.peliculasDisponibles()), "Títulos con copias", 54, y);
        metric(canvas, "Películas alquiladas", String.valueOf(reporte.peliculasAlquiladas()), "Pendientes de devolución", 300, y);
        y -= 90;
        metric(canvas, "Reservas pendientes", String.valueOf(reporte.reservasPendientes()), "Solicitudes activas", 54, y);
        metric(canvas, "Ingresos del mes", "$" + MONEY.format(reporte.ingresosDelMes()), "Mes actual", 300, y);

        y -= 104;
        canvas.textBold("RESUMEN FINANCIERO", 54, y, 12);
        canvas.strokeLine(54, y - 8, 541, y - 8, 0.80, 0.86, 0.90);
        y -= 30;
        canvas.labelValue("Ingresos totales:", "$" + MONEY.format(reporte.ingresosTotales()), 54, y);
        canvas.labelValue("Ingresos del día:", "$" + MONEY.format(reporte.ingresosDelDia()), 300, y);
        y -= 22;
        canvas.labelValue("Ingresos semana:", "$" + MONEY.format(reporte.ingresosDeLaSemana()), 54, y);
        canvas.labelValue("Alquileres vencidos:", String.valueOf(reporte.alquileresVencidos()), 300, y);
        y -= 22;
        canvas.labelValue("Descuentos del mes:", "$" + MONEY.format(reporte.descuentosDelMes()), 54, y);
        canvas.labelValue("Multas pendientes:", "$" + MONEY.format(reporte.multasActualesPendientes()), 300, y);

        y -= 42;
        canvas.textBold("TOP PELÍCULAS", 54, y, 12);
        canvas.strokeLine(54, y - 8, 541, y - 8, 0.80, 0.86, 0.90);
        y -= 28;
        int posTop = 1;
        for (ReporteService.ResumenPelicula item : reporte.topPeliculasMasAlquiladas(3)) {
            canvas.text(posTop++ + ". " + truncar(item.getTitulo(), 30) + " - " + item.getCantidad() + " alquileres - $" + MONEY.format(item.getIngresos()), 54, y, 9);
            y -= 16;
        }

        y -= 24;
        canvas.textBold("ÚLTIMOS MOVIMIENTOS", 54, y, 12);
        canvas.strokeLine(54, y - 8, 541, y - 8, 0.80, 0.86, 0.90);
        y -= 30;
        canvas.textBold("Cliente", 54, y, 9);
        canvas.textBold("Película", 184, y, 9);
        canvas.textBold("Estado", 360, y, 9);
        canvas.textBold("Total", 452, y, 9);
        y -= 14;
        canvas.strokeLine(54, y, 541, y, 0.86, 0.90, 0.93);
        y -= 18;

        List<Alquiler> alquileres = SistemaData.alquiler.getLista();
        int inicio = Math.max(0, alquileres.size() - 9);
        if (alquileres.isEmpty()) {
            canvas.text("No hay alquileres registrados todavía.", 54, y, 10);
        } else {
            for (int i = alquileres.size() - 1; i >= inicio && y > 92; i--) {
                Alquiler a = alquileres.get(i);
                canvas.text(truncar(a.getCliente().getNombre(), 21), 54, y, 9);
                canvas.text(truncar(a.getPelicula().getTitulo(), 28), 184, y, 9);
                canvas.text(a.getEstado(), 360, y, 9);
                canvas.text("$" + MONEY.format(a.getTotalConMultaActual()), 452, y, 9);
                y -= 18;
            }
        }

        y -= 12;
        canvas.textBold("RESERVAS PENDIENTES", 54, Math.max(y, 72), 10);
        int yr = Math.max(y - 18, 54);
        int contador = 0;
        for (Reserva r : SistemaData.reservas.obtenerPendientes()) {
            if (contador >= 3 || yr < 42) {
                break;
            }
            canvas.text("- " + truncar(r.getCliente(), 22) + " / " + truncar(r.getPelicula(), 28), 54, yr, 8);
            yr -= 14;
            contador++;
        }

        canvas.text("Reporte generado automáticamente por " + AppStyles.APP_NAME + " - " + AppStyles.APP_SLOGAN + ".", 54, 28, 8);
        escribirPdf(archivo, canvas.build());
    }

    private static void dibujarPlantilla(PdfCanvas canvas, String tituloDocumento) {
        canvas.fillRect(0, 760, PAGE_WIDTH, 82, 0.04, 0.19, 0.31);
        canvas.image(44, 774, 54, 54);
        canvas.textBold(AppStyles.APP_NAME, 112, 809, 20, 1, 1, 1);
        canvas.text(AppStyles.APP_SLOGAN, 112, 790, 10, 0.86, 0.94, 1.0);
        canvas.textBold(tituloDocumento, 370, 810, 16, 1, 1, 1);
        canvas.text("Generado: " + LocalDateTime.now().format(DATE_TIME), 370, 790, 9, 0.86, 0.94, 1.0);
        canvas.fillRect(0, 748, PAGE_WIDTH, 12, 0.07, 0.51, 0.40);
        canvas.fillRect(38, 52, 519, 690, 1.0, 1.0, 1.0);
        canvas.strokeRect(38, 52, 519, 690, 0.80, 0.86, 0.90);
    }

    private static void metric(PdfCanvas canvas, String title, String value, String note, int x, int y) {
        canvas.fillRect(x, y - 62, 220, 72, 0.95, 0.98, 1.00);
        canvas.strokeRect(x, y - 62, 220, 72, 0.73, 0.82, 0.88);
        canvas.text(title, x + 14, y - 8, 9, 0.25, 0.31, 0.38);
        canvas.textBold(value, x + 14, y - 32, 20, 0.04, 0.19, 0.31);
        canvas.text(note, x + 14, y - 50, 8, 0.25, 0.31, 0.38);
    }

    private static void escribirPdf(File destino, String content) throws IOException {
        if (destino == null) {
            throw new IllegalArgumentException("Selecciona una ruta válida para guardar el PDF.");
        }
        File parent = destino.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        byte[] contentBytes = content.getBytes(StandardCharsets.ISO_8859_1);
        byte[] logoBytes = cargarLogoComoJpeg();

        List<byte[]> objects = new ArrayList<>();
        objects.add(ascii("<< /Type /Catalog /Pages 2 0 R >>"));
        objects.add(ascii("<< /Type /Pages /Kids [3 0 R] /Count 1 >>"));
        objects.add(ascii("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + PAGE_WIDTH + " " + PAGE_HEIGHT + "] "
                + "/Resources << /Font << /F1 4 0 R /F2 5 0 R >> /XObject << /Logo 6 0 R >> >> "
                + "/Contents 7 0 R >>"));
        objects.add(ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>"));
        objects.add(ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>"));
        objects.add(stream(ascii("<< /Type /XObject /Subtype /Image /Width 512 /Height 512 /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode"), logoBytes));
        objects.add(stream(ascii("<<"), contentBytes));

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        writeAscii(pdf, "%PDF-1.4\n%\u00E2\u00E3\u00CF\u00D3\n");
        List<Integer> offsets = new ArrayList<>();
        offsets.add(0);
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(pdf.size());
            writeAscii(pdf, (i + 1) + " 0 obj\n");
            pdf.write(objects.get(i));
            writeAscii(pdf, "\nendobj\n");
        }
        int xref = pdf.size();
        writeAscii(pdf, "xref\n0 " + (objects.size() + 1) + "\n");
        writeAscii(pdf, "0000000000 65535 f \n");
        for (int i = 1; i < offsets.size(); i++) {
            writeAscii(pdf, String.format(Locale.US, "%010d 00000 n \n", offsets.get(i)));
        }
        writeAscii(pdf, "trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF");

        try (FileOutputStream fos = new FileOutputStream(destino)) {
            pdf.writeTo(fos);
        }
    }

    private static byte[] stream(byte[] dictionaryStart, byte[] body) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(dictionaryStart);
        writeAscii(out, " /Length " + body.length + " >>\nstream\n");
        out.write(body);
        writeAscii(out, "\nendstream");
        return out.toByteArray();
    }

    private static byte[] cargarLogoComoJpeg() throws IOException {
        BufferedImage src;
        try {
            src = ImageIO.read(PdfExportService.class.getResourceAsStream("/recursos/sds_logo.png"));
        } catch (Exception ex) {
            src = null;
        }
        if (src == null) {
            src = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = src.createGraphics();
            g.setColor(AppStyles.PRIMARY);
            g.fillRect(0, 0, 512, 512);
            g.setColor(Color.WHITE);
            g.fillOval(96, 96, 320, 320);
            g.setColor(AppStyles.PRIMARY_DARK);
            g.drawString("SDS", 215, 260);
            g.dispose();
        }
        BufferedImage rgb = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
        g.drawImage(src, 0, 0, null);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(rgb, "jpg", baos);
        return baos.toByteArray();
    }

    private static byte[] ascii(String text) {
        return text.getBytes(StandardCharsets.ISO_8859_1);
    }

    private static void writeAscii(ByteArrayOutputStream out, String text) throws IOException {
        out.write(ascii(text));
    }

    private static String esc(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("\r", " ")
                .replace("\n", " ");
    }

    private static String valorVisible(String valor) {
        return valor == null || valor.trim().isEmpty() ? "No registrado" : valor.trim();
    }

    private static String truncar(String valor, int max) {
        if (valor == null) {
            return "";
        }
        String limpio = valor.trim();
        if (limpio.length() <= max) {
            return limpio;
        }
        return limpio.substring(0, Math.max(0, max - 3)) + "...";
    }

    private static DecimalFormat crearFormatoMoneda() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("es", "CO"));
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        return new DecimalFormat("#,##0.00", simbolos);
    }

    private static final class PdfCanvas {

        private final StringBuilder sb = new StringBuilder();

        String build() {
            return sb.toString();
        }

        void text(String text, int x, int y, int size) {
            text(text, x, y, size, 0.06, 0.12, 0.18);
        }

        void text(String text, int x, int y, int size, double r, double g, double b) {
            colorText(r, g, b);
            sb.append("BT /F1 ").append(size).append(" Tf ").append(x).append(' ').append(y).append(" Td (")
                    .append(esc(text)).append(") Tj ET\n");
        }

        void textBold(String text, int x, int y, int size) {
            textBold(text, x, y, size, 0.04, 0.19, 0.31);
        }

        void textBold(String text, int x, int y, int size, double r, double g, double b) {
            colorText(r, g, b);
            sb.append("BT /F2 ").append(size).append(" Tf ").append(x).append(' ').append(y).append(" Td (")
                    .append(esc(text)).append(") Tj ET\n");
        }

        void labelValue(String label, String value, int x, int y) {
            textBold(label, x, y, 9, 0.25, 0.31, 0.38);
            text(value, x + 150, y, 9, 0.06, 0.12, 0.18);
        }

        void fillRect(int x, int y, int w, int h, double r, double g, double b) {
            sb.append(format(r)).append(' ').append(format(g)).append(' ').append(format(b)).append(" rg\n");
            sb.append(x).append(' ').append(y).append(' ').append(w).append(' ').append(h).append(" re f\n");
        }

        void strokeRect(int x, int y, int w, int h, double r, double g, double b) {
            sb.append(format(r)).append(' ').append(format(g)).append(' ').append(format(b)).append(" RG\n");
            sb.append(x).append(' ').append(y).append(' ').append(w).append(' ').append(h).append(" re S\n");
        }

        void strokeLine(int x1, int y1, int x2, int y2, double r, double g, double b) {
            sb.append(format(r)).append(' ').append(format(g)).append(' ').append(format(b)).append(" RG\n");
            sb.append(x1).append(' ').append(y1).append(" m ").append(x2).append(' ').append(y2).append(" l S\n");
        }

        void image(int x, int y, int w, int h) {
            sb.append("q ").append(w).append(" 0 0 ").append(h).append(' ').append(x).append(' ').append(y).append(" cm /Logo Do Q\n");
        }

        private void colorText(double r, double g, double b) {
            sb.append(format(r)).append(' ').append(format(g)).append(' ').append(format(b)).append(" rg\n");
        }

        private String format(double value) {
            return String.format(Locale.US, "%.3f", value);
        }
    }
}
