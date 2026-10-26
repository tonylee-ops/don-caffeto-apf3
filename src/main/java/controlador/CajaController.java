package controlador;

import mvc.Servicios;
import modelo.DetallePedido;
import modelo.Pago;
import modelo.Pedido;
import modelo.Usuario;
import modelo.PagoService;
import modelo.Tema;
import modelo.Validador;
import vista.FrmCaja;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CajaController {
    private static final String[] METODOS = {"EFECTIVO", "YAPE", "PLIN", "TARJETA"};

    private final FrmCaja vista;
    private final Servicios servicios;
    private final Usuario usuario;
    private final Timer reloj;
    private List<Pedido> pedidos = new ArrayList<>();

    public CajaController(FrmCaja vista, Servicios servicios, Usuario usuario) {
        this.vista = vista;
        this.servicios = servicios;
        this.usuario = usuario;
        reloj = new Timer(15_000, e -> actualizar());
        vista.getBtnActualizar().addActionListener(e -> actualizar());
        vista.getBtnCobrar().addActionListener(e -> cobrar());
        vista.getTxtRecibido().addActionListener(e -> cobrar());
        vista.getCmbMetodo().addActionListener(e -> calcularVuelto());
        vista.getTxtRecibido().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { calcularVuelto(); }
            @Override public void removeUpdate(DocumentEvent e) { calcularVuelto(); }
            @Override public void changedUpdate(DocumentEvent e) { calcularVuelto(); }
        });
        vista.getTblPorCobrar().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                reloj.stop();
            }
        });
    }

    public void iniciar() {
        actualizar();
        reloj.start();
        vista.setVisible(true);
    }

    private void actualizar() {
        Pedido antes = seleccionado();
        try {
            pedidos = servicios.pagos.porCobrar(usuario);
        } catch (SQLException | SecurityException e) {
            reloj.stop();
            Mensajes.de(vista, e);
            return;
        }
        List<Object[]> filas = new ArrayList<>();
        for (Pedido p : pedidos) {
            filas.add(new Object[]{p.getIdPedido(), Tablas.destino(p), p.getMesero(), p.getTotal()});
        }
        Tablas.llenar(vista.getTblPorCobrar(), filas);
        vista.getLblResumen().setText(pedidos.isEmpty() ? "No hay pedidos por cobrar"
                : pedidos.size() + (pedidos.size() == 1 ? " pedido listo" : " pedidos listos"));
        for (int i = 0; antes != null && i < pedidos.size(); i++) {
            if (pedidos.get(i).getIdPedido() == antes.getIdPedido()) {
                vista.getTblPorCobrar().setRowSelectionInterval(i, i);
            }
        }
        mostrarDetalle();
    }

    private Pedido seleccionado() {
        int fila = vista.getTblPorCobrar().getSelectedRow();
        return fila < 0 || fila >= pedidos.size() ? null : pedidos.get(fila);
    }

    private void mostrarDetalle() {
        Pedido p = seleccionado();
        List<Object[]> filas = new ArrayList<>();
        BigDecimal total = p == null ? BigDecimal.ZERO : p.getTotal();
        if (p == null) {
            vista.getLblSeleccion().setText("Selecciona un pedido");
        } else {
            vista.getLblSeleccion().setText("Pedido N° " + p.getIdPedido() + "  ·  " + Tablas.destino(p));
            try {
                for (DetallePedido d : servicios.pedidos.detalle(p.getIdPedido())) {
                    filas.add(new Object[]{d.getProducto(), d.getCantidad(), d.getPrecioUnitario(), d.getImporte()});
                }
            } catch (SQLException e) {
                Mensajes.de(vista, e);
            }
        }
        Tablas.llenar(vista.getTblDetalle(), filas);
        BigDecimal igv = PagoService.igv(total);
        vista.getLblTotal().setText(Tema.soles(total));
        vista.getLblIgv().setText("Op. gravada " + Tema.soles(total.subtract(igv)) + "  ·  IGV 18 % " + Tema.soles(igv));
        calcularVuelto();
    }

    private String metodo() {
        return METODOS[vista.getCmbMetodo().getSelectedIndex()];
    }

    private void calcularVuelto() {
        Pedido p = seleccionado();
        boolean efectivo = "EFECTIVO".equals(metodo());
        vista.getTxtRecibido().setEnabled(efectivo && p != null);
        BigDecimal recibido = Validador.monto(vista.getTxtRecibido().getText());
        boolean completo = p != null && (!efectivo || (recibido != null && recibido.compareTo(p.getTotal()) >= 0));
        vista.getBtnCobrar().setEnabled(completo);
        if (p != null && efectivo && recibido != null) {
            vista.getLblVuelto().setText(recibido.compareTo(p.getTotal()) >= 0
                    ? Tema.soles(PagoService.vuelto(p.getTotal(), recibido)) : "Falta " + Tema.soles(p.getTotal().subtract(recibido)));
        } else {
            vista.getLblVuelto().setText(Tema.soles(BigDecimal.ZERO));
        }
    }

    private void cobrar() {
        Pedido p = seleccionado();
        if (p == null || !vista.getBtnCobrar().isEnabled()) {
            return;
        }
        if (!Mensajes.confirmar(vista, "¿Cobrar " + Tema.soles(p.getTotal()) + " del pedido N° " + p.getIdPedido()
                + " con " + vista.getCmbMetodo().getSelectedItem() + "?")) {
            return;
        }
        try {
            Pago pago = servicios.pagos.cobrar(p, metodo(), Validador.monto(vista.getTxtRecibido().getText()), usuario);
            Mensajes.info(vista, "Pedido N° " + p.getIdPedido() + " cobrado."
                    + (pago.getVuelto().signum() > 0 ? "\nVuelto: " + Tema.soles(pago.getVuelto()) : ""));
            vista.getTxtRecibido().setText("");
            vista.getCmbMetodo().setSelectedIndex(0);
            actualizar();
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
            actualizar();
        }
    }
}
