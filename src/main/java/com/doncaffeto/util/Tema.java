package com.doncaffeto.util;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;

/**
 * Colores naranjas de Don Caffeto. Se aplica a cada ventana después de initComponents().
 *
 * @author Equipo Don Caffeto
 */
public final class Tema {

    /** Naranja principal. */
    public static final Color NARANJA = new Color(232, 89, 12);
    /** Fondo crema de las ventanas. */
    public static final Color FONDO = new Color(255, 244, 230);
    /** Color café del texto. */
    public static final Color CAFE = new Color(43, 27, 18);

    private Tema() {
    }

    /** Activa FlatLaf (se llama una vez al iniciar). */
    public static void instalar() {
        try {
            FlatLightLaf.setup();
        } catch (Throwable e) {
            // si FlatLaf no está disponible se usa el aspecto normal de Swing
        }
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.focusColor", NARANJA);
        UIManager.put("Table.selectionBackground", new Color(255, 169, 77));
    }

    /**
     * Pinta la ventana y sus componentes con la paleta naranja.
     *
     * @param ventana ventana a pintar
     */
    public static void aplicar(JFrame ventana) {
        ventana.getContentPane().setBackground(FONDO);
        pintar(ventana.getContentPane());
    }

    /**
     * Pone un texto como título (grande y naranja).
     *
     * @param etiqueta etiqueta del título
     */
    public static void titulo(JLabel etiqueta) {
        etiqueta.setForeground(NARANJA);
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 22f));
    }

    private static void pintar(Container contenedor) {
        for (Component c : contenedor.getComponents()) {
            if (c instanceof JButton) {
                c.setBackground(NARANJA);
                c.setForeground(Color.WHITE);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            } else if (c instanceof JLabel) {
                c.setForeground(CAFE);
            } else if (c instanceof JTable) {
                JTable t = (JTable) c;
                t.setRowHeight(24);
                t.getTableHeader().setBackground(NARANJA);
                t.getTableHeader().setForeground(Color.WHITE);
            }
            if (c instanceof Container) {
                pintar((Container) c);
            }
        }
    }
}
