package com.doncaffeto.controller;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Ventanas de mensaje con el mismo título en toda la aplicación.
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
     * @param mensaje dato faltante o incorrecto
     */
    public static void aviso(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, TITULO, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * @param padre   ventana
     * @param mensaje error
     */
    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, TITULO, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra el mensaje adecuado según el tipo de error.
     *
     * @param padre ventana
     * @param e     error ocurrido
     */
    public static void de(Component padre, Exception e) {
        if (e instanceof IllegalArgumentException || e instanceof SecurityException) {
            aviso(padre, e.getMessage());
        } else {
            error(padre, "No se pudo completar la operación.\n" + e.getMessage());
        }
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
