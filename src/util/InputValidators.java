package util;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public final class InputValidators {

    private InputValidators() {
    }

    public static void soloNumeros(JTextField campo, int maximo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                reemplazar(fb, offset, 0, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                reemplazar(fb, offset, length, text, attrs);
            }

            private void reemplazar(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) {
                    return;
                }

                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String nuevo = actual.substring(0, offset) + text + actual.substring(offset + length);

                if (nuevo.matches("\\d{0," + maximo + "}")) {
                    fb.replace(offset, length, text, attrs);
                }
            }
        });
    }

    public static void alfanumericoConEspacios(JTextField campo, int maximo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                reemplazar(fb, offset, 0, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                reemplazar(fb, offset, length, text, attrs);
            }

            private void reemplazar(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) {
                    return;
                }

                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String nuevo = actual.substring(0, offset) + text + actual.substring(offset + length);

                if (nuevo.length() <= maximo && nuevo.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 ]*")) {
                    fb.replace(offset, length, text, attrs);
                }
            }
        });
    }

    public static void soloLetrasConEspacios(JTextField campo, int maximo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                reemplazar(fb, offset, 0, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                reemplazar(fb, offset, length, text, attrs);
            }

            private void reemplazar(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) {
                    return;
                }

                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String nuevo = actual.substring(0, offset) + text + actual.substring(offset + length);

                if (nuevo.length() <= maximo && nuevo.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]*")) {
                    fb.replace(offset, length, text, attrs);
                }
            }
        });
    }

    public static void moneda(JTextField campo, int maximo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                reemplazar(fb, offset, 0, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                reemplazar(fb, offset, length, text, attrs);
            }

            private void reemplazar(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) {
                    return;
                }

                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String nuevo = actual.substring(0, offset) + text + actual.substring(offset + length);

                if (nuevo.length() <= maximo && nuevo.matches("[0-9.,]*")) {
                    fb.replace(offset, length, text, attrs);
                }
            }
        });
    }

    public static void textoLibre(JTextField campo, int maximo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                reemplazar(fb, offset, 0, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                reemplazar(fb, offset, length, text, attrs);
            }

            private void reemplazar(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) {
                    return;
                }

                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String nuevo = actual.substring(0, offset) + text + actual.substring(offset + length);

                if (nuevo.length() <= maximo && nuevo.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 .,;:()¿?¡!\\-]*")) {
                    fb.replace(offset, length, text, attrs);
                }
            }
        });
    }

    public static boolean documentoValido(String documento) {
        return documento != null && documento.trim().matches("\\d{6,10}");
    }

    public static boolean nombreClienteValido(String nombre) {
        return nombre != null && nombre.trim().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 ]{1,25}");
    }

    public static boolean celularValido(String celular) {
        return celular != null && celular.trim().matches("\\d{1,10}");
    }
}
