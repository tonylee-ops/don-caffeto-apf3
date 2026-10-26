package controlador;

import java.awt.Component;
import javax.swing.JOptionPane;

public final class Mensajes {
    private static final String TITULO = "Don Caffeto";

    private Mensajes() {
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, TITULO, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aviso(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, TITULO, JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, TITULO, JOptionPane.ERROR_MESSAGE);
    }

    public static void de(Component padre, Exception e) {
        if (e instanceof IllegalArgumentException || e instanceof SecurityException) {
            aviso(padre, e.getMessage());
        } else {
            error(padre, "No se pudo completar la operación.\n" + e.getMessage());
        }
    }

    public static boolean confirmar(Component padre, String pregunta) {
        return JOptionPane.showConfirmDialog(padre, pregunta, TITULO, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
