package com.doncaffeto.controller;

import com.doncaffeto.Servicios;
import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Mesa;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Producto;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.util.Tema;
import com.doncaffeto.view.FrmMesa;
import com.doncaffeto.view.componentes.TarjetaMesa;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MesaController {
    private static final int POR_PAGINA = 12;
    private static final String TODAS = "Todos";

    private final FrmMesa vista;
    private final Servicios servicios;
    private final Usuario usuario;
    private final Mesa mesa;
    private final Runnable alCambiar;
    private final Timer cronometro;
    private final Timer recarga;

    private Pedido pedido;
    private List<DetallePedido> enviados = new ArrayList<>();
    private Pedido nuevos = new Pedido();
    private String observacionNueva;
    private String cantidad = "";

    private List<Producto> carta = new ArrayList<>();
    private String categoria = TODAS;
    private int pagina;

    public MesaController(FrmMesa vista, Servicios servicios, Usuario usuario, Mesa mesa, Runnable alCambiar) {
        this.vista = vista;
        this.servicios = servicios;
        this.usuario = usuario;
        this.mesa = mesa;
        this.alCambiar = alCambiar;
        cronometro = new Timer(1000, e -> vista.getLblCronometro().setText(
                mesa.isLibre() ? "00:00" : TarjetaMesa.cronometro(mesa.getSegundosOcupada())));
        recarga = new Timer(10_000, e -> cargar());
        vista.getLblTitulo().setText("Mesa " + mesa.getNumero());
        vista.getLblMesero().setText("·  " + usuario.getNombres());
        vista.setTitle("Don Caffeto - Mesa " + mesa.getNumero());

        JButton[] numeros = {vista.getBtnNum0(), vista.getBtnNum1(), vista.getBtnNum2(), vista.getBtnNum3(),
            vista.getBtnNum4(), vista.getBtnNum5(), vista.getBtnNum6(), vista.getBtnNum7(), vista.getBtnNum8(),
            vista.getBtnNum9()};
        for (int i = 0; i < numeros.length; i++) {
            String digito = String.valueOf(i);
            numeros[i].addActionListener(e -> escribir(digito));
        }
        vista.getBtnNum00().addActionListener(e -> escribir("00"));
        vista.getBtnBorrar().addActionListener(e -> borrarDigito());
        vista.getBtnVaciar().addActionListener(e -> vaciar());
        vista.getBtnEliminar().addActionListener(e -> eliminarLinea());
        vista.getBtnMas().addActionListener(e -> sumar(1));
        vista.getBtnMenos().addActionListener(e -> sumar(-1));

        vista.getBtnAnterior().addActionListener(e -> cambiarPagina(-1));
        vista.getBtnSiguiente().addActionListener(e -> cambiarPagina(1));
        vista.getTxtBuscar().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { irAPagina(0); }
            @Override public void removeUpdate(DocumentEvent e) { irAPagina(0); }
            @Override public void changedUpdate(DocumentEvent e) { irAPagina(0); }
        });

        vista.getBtnConfirmar().addActionListener(e -> confirmar());
        vista.getBtnNota().addActionListener(e -> nota());
        vista.getBtnObservacion().addActionListener(e -> observacion());
        vista.getBtnPedirCuenta().addActionListener(e -> pedirCuenta());
        vista.getBtnAnular().addActionListener(e -> anular());
        vista.getBtnMesas().addActionListener(e -> cerrar());
        vista.getTblDetalle().getSelectionModel().addListSelectionListener(e -> actualizarBotones());
        int[] anchos = {45, 170, 80, 55};
        for (int i = 0; i < anchos.length; i++) {
            vista.getTblDetalle().getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cronometro.stop();
                recarga.stop();
            }
        });
    }

    public void iniciar() {
        if (!cargar()) {
            return;
        }
        try {
            carta = servicios.productos.carta();
        } catch (SQLException e) {
            Mensajes.de(vista, e);
        }
        mostrarCategorias();
        irAPagina(0);
        cronometro.start();
        recarga.start();
        vista.setVisible(true);
    }

    private boolean cargar() {
        boolean eraLibre = pedido == null && mesa.isLibre();
        try {
            pedido = servicios.pedidos.pedidoDeMesa(mesa, usuario);
            enviados = pedido == null ? new ArrayList<>() : servicios.pedidos.detalle(pedido.getIdPedido());
        } catch (SQLException | SecurityException e) {
            Mensajes.de(vista, e);
            return false;
        }
        if (pedido == null && !eraLibre) {
            Mensajes.info(vista, "La mesa " + mesa.getNumero() + " ya está libre.");
            alCambiar.run();
            vista.dispose();
            return false;
        }
        if (pedido != null) {
            mesa.setEstado(pedido.getEstadoMesa());
        }
        mostrarPedido();
        return true;
    }

    private boolean cuentaPedida() {
        return pedido != null && "CUENTA".equals(pedido.getEstadoMesa());
    }

    private void mostrarPedido() {
        int seleccion = vista.getTblDetalle().getSelectedRow();
        List<Object[]> filas = new ArrayList<>();
        for (DetallePedido d : enviados) {
            filas.add(new Object[]{d.getCantidad(), descripcion(d), d.getImporte(), "✓"});
        }
        for (DetallePedido d : nuevos.getDetalles()) {
            filas.add(new Object[]{d.getCantidad(), descripcion(d), d.getImporte(), "Nuevo"});
        }
        Tablas.llenar(vista.getTblDetalle(), filas);
        if (seleccion >= 0 && seleccion < filas.size()) {
            vista.getTblDetalle().setRowSelectionInterval(seleccion, seleccion);
        }
        java.math.BigDecimal total = (pedido == null ? java.math.BigDecimal.ZERO : pedido.getTotal())
                .add(servicios.pedidos.calcularTotal(nuevos));
        vista.getLblTotal().setText("Total: " + Tema.soles(total));

        if (pedido == null) {
            vista.getLblSeleccion().setText("Pedido nuevo");
            vista.getLblEstadoCocina().setText(" ");
            vista.getLblEstadoMesa().setText("Libre");
            vista.getLblEstadoMesa().setForeground(Tema.GRIS_MESA);
        } else {
            boolean listo = "LISTO".equals(pedido.getEstado());
            String estado = Tablas.ESTADO.get(pedido.getEstado());
            vista.getLblSeleccion().setText("Pedido N° " + pedido.getIdPedido() + "  ·  " + pedido.getMesero());
            vista.getLblEstadoCocina().setText(listo ? "Listo para servir" : estado);
            vista.getLblEstadoCocina().setForeground(Tema.color(estado));
            vista.getLblEstadoMesa().setText(Tema.textoMesa(pedido.getEstadoMesa()));
            vista.getLblEstadoMesa().setForeground(Tema.colorMesa(pedido.getEstadoMesa()));
        }
        vista.getLblCronometro().setForeground(Tema.colorMesa(mesa.getEstado()));
        vista.getLblCronometro().setText(mesa.isLibre() ? "00:00" : TarjetaMesa.cronometro(mesa.getSegundosOcupada()));
        String obs = pedido == null ? observacionNueva : pedido.getObservacion();
        vista.getLblObservacion().setText(obs == null ? " " : "Obs.: " + obs);
        actualizarBotones();
    }

    private static String descripcion(DetallePedido d) {
        return d.getNota() == null ? d.getProducto() : d.getProducto() + " (" + d.getNota() + ")";
    }

    private int lineaNueva() {
        int fila = vista.getTblDetalle().getSelectedRow();
        return fila < enviados.size() ? -1 : fila - enviados.size();
    }

    private void seleccionarUltima() {
        int ultima = enviados.size() + nuevos.getDetalles().size() - 1;
        if (ultima >= 0) {
            vista.getTblDetalle().setRowSelectionInterval(ultima, ultima);
        }
    }

    private void actualizarBotones() {
        boolean hayNuevos = !nuevos.getDetalles().isEmpty();
        boolean lineaNueva = lineaNueva() >= 0;
        boolean cuenta = cuentaPedida();
        vista.getBtnConfirmar().setEnabled(hayNuevos && !cuenta);
        vista.getBtnNota().setEnabled(lineaNueva);
        vista.getBtnEliminar().setEnabled(lineaNueva);
        vista.getBtnMas().setEnabled(lineaNueva);
        vista.getBtnMenos().setEnabled(lineaNueva);
        vista.getBtnVaciar().setEnabled(hayNuevos);
        vista.getBtnObservacion().setEnabled(!cuenta);
        vista.getBtnPedirCuenta().setEnabled(pedido != null && "LISTO".equals(pedido.getEstado()) && !hayNuevos && !cuenta);
        vista.getBtnPedirCuenta().setToolTipText(pedido != null && !"LISTO".equals(pedido.getEstado())
                ? "Disponible cuando la cocina marque el pedido como listo" : null);
        vista.getBtnAnular().setEnabled(pedido != null && "PENDIENTE".equals(pedido.getEstado()));

        bloquear(vista.getPnlCardCarta(), !cuenta);
        bloquear(vista.getPnlTeclado(), !cuenta);
        vista.getLblCantidad().setText(cuenta ? "Cuenta pedida" : "Cantidad: " + (cantidad.isEmpty() ? "1" : cantidad));
        if (cuenta && vista.getPnlProductos().getLayout() instanceof java.awt.GridLayout) {
            irAPagina(0);
        }
    }

    private static void bloquear(java.awt.Container panel, boolean activo) {
        if (activo) {
            return;
        }
        for (java.awt.Component c : panel.getComponents()) {
            if (c instanceof javax.swing.AbstractButton || c instanceof javax.swing.text.JTextComponent) {
                c.setEnabled(false);
            }
            if (c instanceof java.awt.Container) {
                bloquear((java.awt.Container) c, false);
            }
        }
    }

    private void escribir(String digitos) {
        if ((cantidad + digitos).replaceFirst("^0+", "").length() > 2) {
            return;
        }
        cantidad = (cantidad + digitos).replaceFirst("^0+", "");
        int linea = lineaNueva();
        if (linea >= 0 && !cantidad.isEmpty()) {
            servicios.pedidos.fijarCantidad(nuevos, linea, Integer.parseInt(cantidad));
            mostrarPedido();
        }
        mostrarCantidad();
    }

    private void borrarDigito() {
        if (!cantidad.isEmpty()) {
            cantidad = cantidad.substring(0, cantidad.length() - 1);
        }
        mostrarCantidad();
    }

    private void mostrarCantidad() {
        vista.getLblCantidad().setText("Cantidad: " + (cantidad.isEmpty() ? "1" : cantidad));
    }

    private int cantidadEscrita() {
        int c = cantidad.isEmpty() ? 1 : Integer.parseInt(cantidad);
        cantidad = "";
        mostrarCantidad();
        return c;
    }

    private void sumar(int cambio) {
        int linea = lineaNueva();
        if (linea >= 0) {
            servicios.pedidos.cambiarCantidad(nuevos, linea, cambio);
            mostrarPedido();
        }
    }

    private void eliminarLinea() {
        int linea = lineaNueva();
        if (linea >= 0) {
            nuevos.getDetalles().remove(linea);
            mostrarPedido();
        }
    }

    private void vaciar() {
        if (Mensajes.confirmar(vista, "¿Quitar todos los productos nuevos que aún no se envían?")) {
            nuevos = new Pedido();
            mostrarPedido();
        }
    }

    private void mostrarCategorias() {
        TreeSet<String> nombres = new TreeSet<>();
        carta.forEach(p -> nombres.add(p.getCategoria()));
        List<String> todas = new ArrayList<>();
        todas.add(TODAS);
        todas.addAll(nombres);
        vista.getPnlCategorias().removeAll();
        for (String c : todas) {
            JButton b = new JButton(c);
            Tema.categoria(b, c.equals(categoria));
            b.addActionListener(e -> {
                categoria = c;
                mostrarCategorias();
                irAPagina(0);
            });
            vista.getPnlCategorias().add(b);
        }
        vista.getPnlCategorias().revalidate();
        vista.getPnlCategorias().repaint();
    }

    private List<Producto> visibles() {
        String texto = vista.getTxtBuscar().getText().trim().toLowerCase();
        List<Producto> lista = new ArrayList<>();
        for (Producto p : carta) {
            boolean enCategoria = TODAS.equals(categoria) || p.getCategoria().equals(categoria);
            if (enCategoria && p.getNombre().toLowerCase().contains(texto)) {
                lista.add(p);
            }
        }
        return lista;
    }

    private void cambiarPagina(int cambio) {
        irAPagina(pagina + cambio);
    }

    private void irAPagina(int numero) {
        if (cuentaPedida()) {
            vista.getPnlProductos().removeAll();
            vista.getPnlProductos().setLayout(new java.awt.BorderLayout());
            javax.swing.JLabel aviso = new javax.swing.JLabel("<html><div style='text-align:center'><b>La cuenta ya fue "
                    + "pedida</b><br>Ya no se pueden agregar productos a esta mesa.</div></html>",
                    javax.swing.SwingConstants.CENTER);
            aviso.setForeground(Tema.VERDE);
            aviso.setFont(aviso.getFont().deriveFont(18f));
            vista.getPnlProductos().add(aviso);
            vista.getPnlProductos().revalidate();
            vista.getPnlProductos().repaint();
            return;
        }
        List<Producto> lista = visibles();
        int paginas = Math.max(1, (lista.size() + POR_PAGINA - 1) / POR_PAGINA);
        pagina = Math.max(0, Math.min(numero, paginas - 1));
        vista.getPnlProductos().removeAll();
        for (int i = pagina * POR_PAGINA; i < Math.min(lista.size(), (pagina + 1) * POR_PAGINA); i++) {
            Producto p = lista.get(i);
            JButton b = new JButton();
            Tema.producto(b, p.getNombre(), p.getPrecio());
            b.addActionListener(e -> agregar(p));
            vista.getPnlProductos().add(b);
        }
        for (int i = vista.getPnlProductos().getComponentCount(); i < POR_PAGINA; i++) {
            vista.getPnlProductos().add(new javax.swing.JLabel());
        }
        vista.getPnlProductos().revalidate();
        vista.getPnlProductos().repaint();
        vista.getLblPagina().setText((pagina + 1) + " / " + paginas);
        vista.getBtnAnterior().setEnabled(pagina > 0);
        vista.getBtnSiguiente().setEnabled(pagina < paginas - 1);
    }

    private void agregar(Producto p) {
        try {
            servicios.pedidos.agregarProducto(nuevos, p, cantidadEscrita(), null);
            mostrarPedido();
            seleccionarUltima();
        } catch (IllegalArgumentException e) {
            Mensajes.aviso(vista, e.getMessage());
        }
    }

    private void confirmar() {
        try {
            if (pedido == null) {
                nuevos.setModalidad("SALON");
                nuevos.setIdMesa(mesa.getIdMesa());
                nuevos.setObservacion(observacionNueva);
                int id = servicios.pedidos.guardar(nuevos, usuario);
                mesa.setEstado("OCUPADA");
                mesa.setSegundosOcupada(0);
                Mensajes.info(vista, "Pedido N° " + id + " enviado a cocina. La mesa " + mesa.getNumero()
                        + " ahora está en naranja.");
            } else {
                servicios.pedidos.agregarAPedido(pedido, nuevos.getDetalles(), usuario);
                Mensajes.info(vista, "Productos enviados a cocina. Aparecen con la nota «Agregado».");
            }
            nuevos = new Pedido();
            observacionNueva = null;
            alCambiar.run();
            cargar();
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
            cargar();
        }
    }

    private void nota() {
        int linea = lineaNueva();
        if (linea < 0) {
            return;
        }
        DetallePedido d = nuevos.getDetalles().get(linea);
        Object texto = JOptionPane.showInputDialog(vista, "Nota para «" + d.getProducto() + "» (ej. sin azúcar):",
                "Nota para cocina", JOptionPane.PLAIN_MESSAGE, null, null, d.getNota());
        if (texto != null) {
            String t = texto.toString().trim();
            d.setNota(t.isEmpty() ? null : t);
            mostrarPedido();
        }
    }

    private void observacion() {
        Object texto = JOptionPane.showInputDialog(vista, "Observación del pedido para cocina:",
                "Observación", JOptionPane.PLAIN_MESSAGE, null, null, pedido == null ? observacionNueva : null);
        if (texto == null || texto.toString().isBlank()) {
            return;
        }
        if (pedido == null) {
            observacionNueva = texto.toString().trim();
            mostrarPedido();
            return;
        }
        try {
            servicios.pedidos.agregarObservacion(pedido, texto.toString(), usuario);
            cargar();
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
        }
    }

    private void pedirCuenta() {
        if (!Mensajes.confirmar(vista, "¿Pedir la cuenta de la mesa " + mesa.getNumero() + " por "
                + Tema.soles(pedido.getTotal()) + "?\nEl pedido pasará a caja para el cobro.")) {
            return;
        }
        try {
            servicios.pedidos.pedirCuenta(pedido, usuario);
            alCambiar.run();
            cargar();
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
        }
    }

    private void anular() {
        if (!Mensajes.confirmar(vista, "¿Anular el pedido de la mesa " + mesa.getNumero() + "?\n"
                + "La cocina dejará de verlo y la mesa quedará libre.")) {
            return;
        }
        try {
            servicios.pedidos.anular(pedido, usuario);
            alCambiar.run();
            vista.dispose();
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
        }
    }

    private void cerrar() {
        if (!nuevos.getDetalles().isEmpty()
                && !Mensajes.confirmar(vista, "Hay productos sin confirmar. ¿Volver a mesas sin enviarlos a cocina?")) {
            return;
        }
        vista.dispose();
    }
}
