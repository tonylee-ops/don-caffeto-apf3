package com.doncaffeto.controller;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Ventanas de mensaje con el mismo título y estilo en toda la aplicación.
 *
 * @author Equipo Don Caffeto
 */
public final class Mensajes {

    private static final String TITULO = "Don Caffeto";

    private Mensajes() {
    }

    /**
     * @param padre   ventana
     * @param mensaje texto informativo
     */
    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, TITULO, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * @param padre   ventana
     * @param mensaje texto de advertencia (dato faltante o incorrecto)
     */
    public static void aviso(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, TITULO, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * @param padre   ventana
     * @param mensaje texto del error
     */
    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, TITULO, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * @param padre    ventana
     * @param pregunta pregunta de sí o no
     * @return true si el usuario elige Sí
     */
    public static boolean confirmar(Component padre, String pregunta) {
        return JOptionPane.showConfirmDialog(padre, pregunta, TITULO, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
