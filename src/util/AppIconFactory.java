package util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;

public final class AppIconFactory {

    private AppIconFactory() {
    }

    public static Icon forText(String text, Color color) {
        return new ButtonIcon(resolveType(text), color == null ? AppStyles.TEXT : color);
    }

    private static IconType resolveType(String text) {
        String t = text == null ? "" : text.toLowerCase();
        if (t.contains("cerrar sesión") || t.contains("cerrar sesion") || t.contains("salir")) {
            return IconType.EXIT;
        }
        if (t.contains("volver") || t.contains("atrás") || t.contains("atras")) {
            return IconType.BACK;
        }
        if (t.equals("cerrar") || t.contains("cerrar ventana")) {
            return IconType.CLOSE;
        }
        if (t.contains("dashboard") || t.contains("panel") || t.contains("opciones")) {
            return IconType.DASHBOARD;
        }
        if (t.contains("catálogo") || t.contains("catalogo") || t.contains("película") || t.contains("pelicula")) {
            return IconType.FILM;
        }
        if (t.contains("precio") || t.contains("ingreso") || t.contains("pago")) {
            return IconType.MONEY;
        }
        if (t.contains("historial")) {
            return IconType.HISTORY;
        }
        if (t.contains("reporte") || t.contains("pdf") || t.contains("exportar")) {
            return IconType.PDF;
        }
        if (t.contains("imprimir")) {
            return IconType.PRINT;
        }
        if (t.contains("agregar") || t.contains("crear") || t.contains("guardar")) {
            return IconType.ADD;
        }
        if (t.contains("editar")) {
            return IconType.EDIT;
        }
        if (t.contains("eliminar")) {
            return IconType.TRASH;
        }
        if (t.contains("buscar") || t.contains("detalle") || t.contains("ver")) {
            return IconType.SEARCH;
        }
        if (t.contains("actualizar") || t.contains("refrescar")) {
            return IconType.REFRESH;
        }
        if (t.contains("alquilar") || t.contains("alquiler")) {
            return IconType.PLAY;
        }
        if (t.contains("reserv")) {
            return IconType.BOOKMARK;
        }
        if (t.contains("devol")) {
            return IconType.CHECK;
        }
        if (t.contains("factura") || t.contains("comprobante")) {
            return IconType.RECEIPT;
        }
        if (t.contains("usuario") || t.contains("cliente") || t.contains("empleado") || t.contains("administrador")) {
            return IconType.USER;
        }
        return IconType.DOT;
    }

    private enum IconType {
        ADD, EDIT, TRASH, SEARCH, REFRESH, PDF, PRINT, BACK, CLOSE, EXIT, RECEIPT, DASHBOARD, HISTORY, FILM, MONEY, USER, PLAY, BOOKMARK, CHECK, DOT
    }

    private static final class ButtonIcon implements Icon {

        private static final int SIZE = 16;
        private final IconType type;
        private final Color color;

        private ButtonIcon(IconType type, Color color) {
            this.type = type;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.translate(x, y);
            switch (type) {
                case ADD -> drawAdd(g2);
                case EDIT -> drawEdit(g2);
                case TRASH -> drawTrash(g2);
                case SEARCH -> drawSearch(g2);
                case REFRESH -> drawRefresh(g2);
                case PDF -> drawPdf(g2);
                case PRINT -> drawPrint(g2);
                case BACK -> drawBack(g2);
                case CLOSE -> drawClose(g2);
                case EXIT -> drawExit(g2);
                case RECEIPT -> drawReceipt(g2);
                case DASHBOARD -> drawDashboard(g2);
                case HISTORY -> drawHistory(g2);
                case FILM -> drawFilm(g2);
                case MONEY -> drawMoney(g2);
                case USER -> drawUser(g2);
                case PLAY -> drawPlay(g2);
                case BOOKMARK -> drawBookmark(g2);
                case CHECK -> drawCheck(g2);
                default -> g2.fillOval(6, 6, 4, 4);
            }
            g2.dispose();
        }

        private void drawAdd(Graphics2D g) {
            g.drawLine(8, 3, 8, 13);
            g.drawLine(3, 8, 13, 8);
        }

        private void drawEdit(Graphics2D g) {
            g.drawLine(4, 12, 12, 4);
            g.drawLine(10, 3, 13, 6);
            g.drawLine(3, 13, 6, 12);
        }

        private void drawTrash(Graphics2D g) {
            g.drawRect(5, 6, 7, 8);
            g.drawLine(4, 5, 13, 5);
            g.drawLine(7, 3, 10, 3);
        }

        private void drawSearch(Graphics2D g) {
            g.drawOval(3, 3, 8, 8);
            g.drawLine(10, 10, 14, 14);
        }

        private void drawRefresh(Graphics2D g) {
            g.drawArc(3, 3, 10, 10, 35, 280);
            g.drawLine(11, 2, 14, 3);
            g.drawLine(11, 2, 12, 6);
        }

        private void drawPdf(Graphics2D g) {
            g.drawRect(4, 2, 9, 12);
            g.drawLine(10, 2, 13, 5);
            g.drawLine(10, 2, 10, 5);
            g.drawLine(10, 5, 13, 5);
            g.setFont(g.getFont().deriveFont(6f));
            g.drawString("PDF", 4, 12);
        }

        private void drawPrint(Graphics2D g) {
            g.drawRect(5, 2, 7, 4);
            g.drawRect(3, 7, 11, 5);
            g.drawRect(5, 10, 7, 4);
            g.fillOval(11, 8, 1, 1);
        }

        private void drawBack(Graphics2D g) {
            g.drawLine(4, 8, 13, 8);
            g.drawLine(4, 8, 8, 4);
            g.drawLine(4, 8, 8, 12);
        }

        private void drawClose(Graphics2D g) {
            g.drawLine(4, 4, 12, 12);
            g.drawLine(12, 4, 4, 12);
        }

        private void drawExit(Graphics2D g) {
            g.drawRect(3, 3, 7, 10);
            g.drawLine(8, 8, 14, 8);
            g.drawLine(11, 5, 14, 8);
            g.drawLine(11, 11, 14, 8);
        }

        private void drawReceipt(Graphics2D g) {
            g.drawRect(4, 2, 9, 12);
            g.drawLine(6, 5, 11, 5);
            g.drawLine(6, 8, 11, 8);
            g.drawLine(6, 11, 10, 11);
        }

        private void drawDashboard(Graphics2D g) {
            g.drawRect(2, 3, 5, 4);
            g.drawRect(9, 3, 5, 4);
            g.drawRect(2, 9, 5, 4);
            g.drawRect(9, 9, 5, 4);
        }

        private void drawHistory(Graphics2D g) {
            g.drawOval(3, 3, 10, 10);
            g.drawLine(8, 8, 8, 5);
            g.drawLine(8, 8, 11, 9);
        }

        private void drawFilm(Graphics2D g) {
            g.drawRoundRect(2, 4, 12, 8, 2, 2);
            for (int i = 4; i <= 12; i += 4) {
                g.fillRect(i, 5, 1, 2);
                g.fillRect(i, 11, 1, 2);
            }
        }

        private void drawMoney(Graphics2D g) {
            g.drawRoundRect(2, 4, 12, 8, 2, 2);
            g.drawString("$", 6, 11);
        }

        private void drawUser(Graphics2D g) {
            g.drawOval(6, 2, 5, 5);
            g.drawArc(3, 8, 11, 8, 0, 180);
        }

        private void drawPlay(Graphics2D g) {
            int[] xs = {5, 5, 13};
            int[] ys = {3, 13, 8};
            g.fillPolygon(xs, ys, 3);
        }

        private void drawBookmark(Graphics2D g) {
            g.drawRect(5, 2, 7, 12);
            g.drawLine(5, 14, 8, 11);
            g.drawLine(12, 14, 8, 11);
        }

        private void drawCheck(Graphics2D g) {
            g.drawLine(3, 9, 7, 13);
            g.drawLine(7, 13, 14, 4);
        }
    }
}
