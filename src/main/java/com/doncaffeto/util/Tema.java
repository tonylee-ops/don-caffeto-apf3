package com.doncaffeto.util;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.commons.lang3.StringUtils;

/**
 * Estilo visual de Don Caffeto (paleta naranja).
 * <p>
 * Cada vista llama a {@link #aplicar(JFrame)} después de initComponents(). El estilo se elige por el
 * prefijo del nombre del componente: {@code btn} botón naranja, {@code btnMenu} botón del menú lateral,
 * {@code pnlCard} tarjeta blanca, {@code lblTitulo} título, etc. Así un componente nuevo agregado en el
 * diseñador de NetBeans toma el estilo sin escribir código.
 * </p>
 *
 * @author Equipo Don Caffeto
 */
public final class Tema {

    /** Naranja principal. */
    public static final Color NARANJA = new Color(0xE8590C);
    /** Naranja oscuro (botón con el mouse encima). */
    public static final Color NARANJA_OSCURO = new Color(0xC2410C);
    /** Naranja claro (selección de filas). */
    public static final Color NARANJA_CLARO = new Color(0xFFE8D6);
    /** Fondo crema de las ventanas. */
    public static final Color FONDO = new Color(0xFFF8F1);
    /** Café oscuro del menú lateral y del texto. */
    public static final Color CAFE = new Color(0x2B1B12);
    /** Texto secundario. */
    public static final Color GRIS = new Color(0x8A6A55);
    /** Bordes suaves. */
    public static final Color BORDE = new Color(0xF1DCC6);
    /** Verde de confirmación. */
    public static final Color VERDE = new Color(0x2F9E44);
    /** Gris de una mesa libre. */
    public static final Color GRIS_MESA = new Color(0x8E9196);
    /** Rojo de error. */
    public static final Color ROJO = new Color(0xE03131);

    private static final String LETRA = "SansSerif";

    private Tema() {
    }

    /** Activa FlatLaf con bordes redondeados. Se llama una vez al iniciar la aplicación. */
    public static void instalar() {
        try {
            FlatLightLaf.setup();
        } catch (Throwable e) {
            // si FlatLaf no está disponible se usa el aspecto normal de Swing
        }
        UIManager.put("defaultFont", new Font(LETRA, Font.PLAIN, 14));
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Component.focusColor", NARANJA);
        UIManager.put("Table.selectionBackground", NARANJA_CLARO);
        UIManager.put("Table.selectionForeground", CAFE);
        UIManager.put("OptionPane.background", Color.WHITE);
    }

    /**
     * Da estilo a toda la ventana según el nombre de cada componente.
     *
     * @param ventana JFrame Form ya inicializado
     */
    public static void aplicar(JFrame ventana) {
        ventana.getContentPane().setBackground(FONDO);
        ventana.setIconImage(imagen("/img/icono.png", 64));
        for (Field campo : ventana.getClass().getDeclaredFields()) {
            if (!JComponent.class.isAssignableFrom(campo.getType())) {
                continue;
            }
            try {
                campo.setAccessible(true);
                JComponent c = (JComponent) campo.get(ventana);
                if (c != null) {
                    estilo(c, campo.getName());
                }
            } catch (IllegalAccessException e) {
                // el campo no se pudo leer: se deja con el estilo por defecto
            }
        }
    }

    private static void estilo(JComponent c, String nombre) {
        if (c instanceof JButton) {
            boton((JButton) c, nombre);
        } else if (c instanceof JCheckBox) {
            c.setOpaque(false);
            c.setForeground(GRIS);
        } else if (c instanceof JLabel) {
            etiqueta((JLabel) c, nombre);
        } else if (c instanceof JTable) {
            tabla((JTable) c);
        } else if (c instanceof JScrollPane) {
            ((JScrollPane) c).getViewport().setBackground(Color.WHITE);
            c.setBorder(BorderFactory.createLineBorder(BORDE));
        } else if (c instanceof JPanel) {
            panel((JPanel) c, nombre);
        } else {
            // cajas de texto, combos y spinner
            c.setFont(new Font(LETRA, Font.PLAIN, 14));
            c.putClientProperty("JComponent.minimumHeight", 34);
            if (c instanceof JComboBox) {
                c.setBackground(Color.WHITE);
            }
        }
    }

    private static void panel(JPanel p, String nombre) {
        if (nombre.equals("pnlBarra")) {
            p.setBackground(NARANJA);
            blanco(p);
        } else if (nombre.equals("pnlMenu")) {
            p.setBackground(CAFE);
            blanco(p);
        } else if (nombre.startsWith("pnlCard") || nombre.equals("pnlFormulario")) {
            p.setBackground(Color.WHITE);
            p.setBorder(BorderFactory.createLineBorder(BORDE, 1, true));
        } else {
            p.setBackground(FONDO);
        }
    }

    /** Pone en blanco el texto de las etiquetas dentro de un panel oscuro. */
    private static void blanco(JPanel p) {
        for (Component c : p.getComponents()) {
            if (c instanceof JLabel) {
                c.setForeground(Color.WHITE);
            }
        }
    }

    private static void etiqueta(JLabel l, String nombre) {
        l.setForeground(CAFE);
        if (nombre.startsWith("lblTitulo") || nombre.equals("lblSaludo")) {
            l.setForeground(NARANJA);
            l.setFont(new Font(LETRA, Font.BOLD, 26));
        } else if (nombre.equals("lblMarca")) {
            l.setFont(new Font(LETRA, Font.BOLD, 22));
        } else if (nombre.startsWith("lblSub")) {
            l.setFont(new Font(LETRA, Font.BOLD, 17));
        } else if (nombre.equals("lblCronometro")) {
            l.setForeground(NARANJA);
            l.setFont(new Font(Font.MONOSPACED, Font.BOLD, 28));
        } else if (StringUtils.equalsAny(nombre, "lblEstadoMesa", "lblEstadoCocina")) {
            l.setFont(new Font(LETRA, Font.BOLD, 15));
        } else if (nombre.equals("lblVuelto")) {
            l.setForeground(VERDE);
            l.setFont(new Font(LETRA, Font.BOLD, 20));
        } else if (StringUtils.equalsAny(nombre, "lblSeleccion", "lblModo", "lblResumen", "lblItems")) {
            l.setFont(new Font(LETRA, Font.BOLD, 13));
        } else if (nombre.equals("lblObservacion")) {
            l.setForeground(NARANJA_OSCURO);
            l.setFont(new Font(LETRA, Font.ITALIC, 13));
        } else if (nombre.equals("lblTotalNuevos")) {
            l.setForeground(NARANJA);
            l.setFont(new Font(LETRA, Font.BOLD, 18));
        } else if (nombre.equals("lblTotal")) {
            l.setForeground(NARANJA);
            l.setFont(new Font(LETRA, Font.BOLD, 26));
        } else if (nombre.equals("lblMensaje")) {
            l.setForeground(ROJO);
        } else if (nombre.startsWith("lblEtq")) {
            l.setForeground(GRIS);
            l.setFont(new Font(LETRA, Font.BOLD, 13));
        } else if (nombre.startsWith("lblAyuda") || nombre.equals("lblFecha") || nombre.equals("lblEslogan")
                || nombre.equals("lblSeccion")) {
            l.setForeground(GRIS);
            l.setFont(new Font(LETRA, Font.PLAIN, 13));
        }
    }

    private static void boton(JButton b, String nombre) {
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(new Font(LETRA, Font.BOLD, 14));
        if (nombre.startsWith("btnMenu")) {
            colores(b, CAFE, new Color(0x4A2E1E), Color.WHITE, BorderFactory.createEmptyBorder(4, 2, 4, 2));
            b.setHorizontalAlignment(SwingConstants.LEFT);
        } else if (nombre.equals("btnCerrarSesion")) {
            colores(b, NARANJA, NARANJA_OSCURO, Color.WHITE, BorderFactory.createLineBorder(Color.WHITE, 1, true));
        } else if (StringUtils.equalsAny(nombre, "btnQuitar", "btnLimpiar", "btnAnular", "btnQuitarNuevo")) {
            colores(b, Color.WHITE, new Color(0xFFF0F0), ROJO, BorderFactory.createLineBorder(ROJO, 1, true));
        } else if (StringUtils.equalsAny(nombre, "btnHoy", "btnExportar", "btnMas", "btnMenos", "btnActualizar",
                "btnNuevo")) {
            colores(b, Color.WHITE, NARANJA_CLARO, NARANJA, BorderFactory.createLineBorder(NARANJA, 1, true));
        } else if (nombre.equals("btnCerrar")) {
            colores(b, Color.WHITE, NARANJA_CLARO, NARANJA, BorderFactory.createLineBorder(NARANJA, 1, true));
        } else if (StringUtils.equalsAny(nombre, "btnGuardar", "btnCobrar", "btnListo", "btnPedirCuenta", "btnEnviar")) {
            colores(b, VERDE, new Color(0x237A35), Color.WHITE, BorderFactory.createEmptyBorder(1, 6, 1, 6));
        } else {
            colores(b, NARANJA, NARANJA_OSCURO, Color.WHITE, BorderFactory.createEmptyBorder(1, 6, 1, 6));
        }
    }

    /** Colores normal y con el mouse encima. */
    private static void colores(JButton b, Color fondo, Color encima, Color texto, Border borde) {
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setBackground(fondo);
        b.setForeground(texto);
        b.setBorder(BorderFactory.createCompoundBorder(borde, BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (b.isEnabled()) {
                    b.setBackground(encima);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(fondo);
            }
        });
    }

    private static void tabla(JTable t) {
        t.setRowHeight(32);
        t.setShowVerticalLines(false);
        t.setGridColor(BORDE);
        t.setFillsViewportHeight(true);
        t.setSelectionBackground(NARANJA_CLARO);
        t.setSelectionForeground(CAFE);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setBackground(NARANJA);
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(new Font(LETRA, Font.BOLD, 13));
        t.setDefaultRenderer(Object.class, new Celda());
    }

    /**
     * Muestra un texto de ayuda dentro de una caja de texto vacía (FlatLaf).
     *
     * @param campo caja de texto
     * @param texto texto de ayuda
     */
    public static void ayuda(JComponent campo, String texto) {
        campo.putClientProperty("JTextField.placeholderText", texto);
    }

    /**
     * Pone el logo de Don Caffeto en una etiqueta.
     *
     * @param etiqueta etiqueta del logo
     * @param lado     tamaño en píxeles
     */
    public static void logo(JLabel etiqueta, int lado) {
        etiqueta.setIcon(new ImageIcon(imagen("/img/logo.png", lado)));
    }

    /**
     * Pinta la columna de estado con un color por estado.
     *
     * @param t       tabla de pedidos
     * @param columna índice de la columna de estado
     */
    public static void colorearEstado(JTable t, int columna) {
        t.getColumnModel().getColumn(columna).setCellRenderer(new Celda() {
            @Override
            public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean sel, boolean foco,
                                                           int fila, int col) {
                super.getTableCellRendererComponent(tabla, valor, sel, foco, fila, col);
                String estado = String.valueOf(valor);
                setForeground(color(estado));
                setFont(getFont().deriveFont(Font.BOLD));
                return this;
            }
        });
    }

    /**
     * @param estado estado del pedido como se muestra (Pendiente, En preparación...)
     * @return color con el que se muestra
     */
    public static Color color(String estado) {
        switch (estado) {
            case "Pendiente":
                return NARANJA_OSCURO;
            case "En preparación":
                return new Color(0x1971C2);
            case "Cancelado":
                return ROJO;
            default:
                return VERDE;
        }
    }

    /**
     * @param estado estado de la mesa (LIBRE, OCUPADA o CUENTA)
     * @return gris, naranja o verde
     */
    public static Color colorMesa(String estado) {
        if ("OCUPADA".equals(estado)) {
            return NARANJA;
        }
        return "CUENTA".equals(estado) ? VERDE : GRIS_MESA;
    }

    /**
     * @param estado estado de la mesa
     * @return texto que se muestra en la tarjeta de la mesa
     */
    public static String textoMesa(String estado) {
        if ("OCUPADA".equals(estado)) {
            return "En cocina";
        }
        return "CUENTA".equals(estado) ? "Cuenta pedida" : "Libre";
    }

    /**
     * Pinta las etiquetas de la leyenda del mapa de mesas con su color.
     *
     * @param libre   leyenda de mesa libre
     * @param ocupada leyenda de mesa con pedido en cocina
     * @param cuenta  leyenda de mesa con cuenta pedida
     */
    public static void leyenda(JLabel libre, JLabel ocupada, JLabel cuenta) {
        String[] estados = {"LIBRE", "OCUPADA", "CUENTA"};
        JLabel[] etiquetas = {libre, ocupada, cuenta};
        for (int i = 0; i < etiquetas.length; i++) {
            etiquetas[i].setOpaque(true);
            etiquetas[i].setBackground(colorMesa(estados[i]));
            etiquetas[i].setForeground(Color.WHITE);
            etiquetas[i].setFont(new Font(LETRA, Font.BOLD, 12));
            etiquetas[i].setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        }
    }

    /**
     * @param ruta  ruta del recurso
     * @param lado  tamaño en píxeles
     * @return imagen escalada (o una imagen vacía si no existe)
     */
    public static Image imagen(String ruta, int lado) {
        java.net.URL url = Tema.class.getResource(ruta);
        if (url == null) {
            return new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        }
        return new ImageIcon(url).getImage().getScaledInstance(lado, lado, Image.SCALE_SMOOTH);
    }

    /**
     * @param monto monto en soles
     * @return texto con formato "S/ 12.50"
     */
    public static String soles(BigDecimal monto) {
        return "S/ " + monto.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /** Celda con filas alternas y montos alineados a la derecha. */
    private static class Celda extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean sel, boolean foco,
                                                       int fila, int col) {
            Object texto = valor instanceof BigDecimal ? soles((BigDecimal) valor) : valor;
            super.getTableCellRendererComponent(tabla, texto, sel, false, fila, col);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            setHorizontalAlignment(valor instanceof Number ? RIGHT : LEFT);
            setForeground(CAFE);
            setFont(tabla.getFont());
            if (!sel) {
                setBackground(fila % 2 == 0 ? Color.WHITE : FONDO);
            }
            return this;
        }
    }
}
