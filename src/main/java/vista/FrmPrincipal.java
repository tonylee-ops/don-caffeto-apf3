package vista;

import modelo.Tema;

public class FrmPrincipal extends javax.swing.JFrame {
    public FrmPrincipal() {
        initComponents();
        personalizar();
    }

    private void personalizar() {
        Tema.aplicar(this);
        Tema.logo(lblLogo, 40);
        setSize(1180, 680);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlBarra = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblMarca = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        pnlMenu = new javax.swing.JPanel();
        lblEtqMenu = new javax.swing.JLabel();
        btnMenuMesas = new javax.swing.JButton();
        btnMenuPedidos = new javax.swing.JButton();
        btnMenuCocina = new javax.swing.JButton();
        btnMenuCaja = new javax.swing.JButton();
        btnMenuProductos = new javax.swing.JButton();
        btnMenuUsuarios = new javax.swing.JButton();
        btnMenuReportes = new javax.swing.JButton();
        pnlInicio = new javax.swing.JPanel();
        lblSaludo = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        pnlCardMesas = new javax.swing.JPanel();
        lblSubMesas = new javax.swing.JLabel();
        lblAyudaMesas = new javax.swing.JLabel();
        btnMesas = new javax.swing.JButton();
        pnlCardPedidos = new javax.swing.JPanel();
        lblSubPedidos = new javax.swing.JLabel();
        lblAyudaPedidos = new javax.swing.JLabel();
        btnPedidos = new javax.swing.JButton();
        pnlCardCocina = new javax.swing.JPanel();
        lblSubCocina = new javax.swing.JLabel();
        lblAyudaCocina = new javax.swing.JLabel();
        btnCocina = new javax.swing.JButton();
        pnlCardCaja = new javax.swing.JPanel();
        lblSubCaja = new javax.swing.JLabel();
        lblAyudaCaja = new javax.swing.JLabel();
        btnCaja = new javax.swing.JButton();
        pnlCardProductos = new javax.swing.JPanel();
        lblSubProductos = new javax.swing.JLabel();
        lblAyudaProductos = new javax.swing.JLabel();
        btnProductos = new javax.swing.JButton();
        pnlCardUsuarios = new javax.swing.JPanel();
        lblSubUsuarios = new javax.swing.JLabel();
        lblAyudaUsuarios = new javax.swing.JLabel();
        btnUsuarios = new javax.swing.JButton();
        pnlCardReportes = new javax.swing.JPanel();
        lblSubReportes = new javax.swing.JLabel();
        lblAyudaReportes = new javax.swing.JLabel();
        btnReportes = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Don Caffeto - Ventas");

        lblLogo.setText("");

        lblMarca.setText("Don Caffeto");

        lblUsuario.setText("Usuario");

        btnCerrarSesion.setText("Cerrar sesión");

        javax.swing.GroupLayout pnlBarraLayout = new javax.swing.GroupLayout(pnlBarra);
        pnlBarra.setLayout(pnlBarraLayout);
        pnlBarraLayout.setHorizontalGroup(
            pnlBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBarraLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblLogo)
                .addGap(8, 8, 8)
                .addComponent(lblMarca)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblUsuario)
                .addGap(8, 8, 8)
                .addComponent(btnCerrarSesion)
                .addGap(12, 12, 12))
        );
        pnlBarraLayout.setVerticalGroup(
            pnlBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBarraLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLogo)
                    .addComponent(lblMarca)
                    .addComponent(lblUsuario)
                    .addComponent(btnCerrarSesion))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        lblEtqMenu.setText("MENÚ");

        btnMenuMesas.setText("Mesas");

        btnMenuPedidos.setText("Pedidos del día");

        btnMenuCocina.setText("Cocina");

        btnMenuCaja.setText("Caja");

        btnMenuProductos.setText("Productos");

        btnMenuUsuarios.setText("Usuarios");

        btnMenuReportes.setText("Reportes");

        javax.swing.GroupLayout pnlMenuLayout = new javax.swing.GroupLayout(pnlMenu);
        pnlMenu.setLayout(pnlMenuLayout);
        pnlMenuLayout.setHorizontalGroup(
            pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblEtqMenu)
                .addGap(16, 16, 16))
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(btnMenuMesas, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(16, 16, 16))
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(btnMenuPedidos, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(16, 16, 16))
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(btnMenuCocina, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(16, 16, 16))
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(btnMenuCaja, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(16, 16, 16))
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(btnMenuProductos, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(16, 16, 16))
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(btnMenuUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(16, 16, 16))
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(btnMenuReportes, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        pnlMenuLayout.setVerticalGroup(
            pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEtqMenu))
                .addGap(4, 4, 4)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMenuMesas))
                .addGap(4, 4, 4)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMenuPedidos))
                .addGap(4, 4, 4)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMenuCocina))
                .addGap(4, 4, 4)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMenuCaja))
                .addGap(4, 4, 4)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMenuProductos))
                .addGap(4, 4, 4)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMenuUsuarios))
                .addGap(4, 4, 4)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMenuReportes))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        lblSaludo.setText("Hola");

        lblFecha.setText("Fecha");

        lblSubMesas.setText("Mesas");

        lblAyudaMesas.setText("Toma pedidos y pide la cuenta.");

        btnMesas.setText("Ver mesas");

        javax.swing.GroupLayout pnlCardMesasLayout = new javax.swing.GroupLayout(pnlCardMesas);
        pnlCardMesas.setLayout(pnlCardMesasLayout);
        pnlCardMesasLayout.setHorizontalGroup(
            pnlCardMesasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardMesasLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblSubMesas)
                .addGap(18, 18, 18))
            .addGroup(pnlCardMesasLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblAyudaMesas, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(pnlCardMesasLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnMesas)
                .addGap(18, 18, 18))
        );
        pnlCardMesasLayout.setVerticalGroup(
            pnlCardMesasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardMesasLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlCardMesasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubMesas))
                .addGap(8, 8, 8)
                .addGroup(pnlCardMesasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAyudaMesas))
                .addGap(8, 8, 8)
                .addGroup(pnlCardMesasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMesas))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        lblSubPedidos.setText("Pedidos del día");

        lblAyudaPedidos.setText("Todos los pedidos del día.");

        btnPedidos.setText("Ver pedidos");

        javax.swing.GroupLayout pnlCardPedidosLayout = new javax.swing.GroupLayout(pnlCardPedidos);
        pnlCardPedidos.setLayout(pnlCardPedidosLayout);
        pnlCardPedidosLayout.setHorizontalGroup(
            pnlCardPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardPedidosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblSubPedidos)
                .addGap(18, 18, 18))
            .addGroup(pnlCardPedidosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblAyudaPedidos, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(pnlCardPedidosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnPedidos)
                .addGap(18, 18, 18))
        );
        pnlCardPedidosLayout.setVerticalGroup(
            pnlCardPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardPedidosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlCardPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubPedidos))
                .addGap(8, 8, 8)
                .addGroup(pnlCardPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAyudaPedidos))
                .addGap(8, 8, 8)
                .addGroup(pnlCardPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPedidos))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        lblSubCocina.setText("Cocina");

        lblAyudaCocina.setText("Prepara y marca pedidos listos.");

        btnCocina.setText("Abrir cocina");

        javax.swing.GroupLayout pnlCardCocinaLayout = new javax.swing.GroupLayout(pnlCardCocina);
        pnlCardCocina.setLayout(pnlCardCocinaLayout);
        pnlCardCocinaLayout.setHorizontalGroup(
            pnlCardCocinaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardCocinaLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblSubCocina)
                .addGap(18, 18, 18))
            .addGroup(pnlCardCocinaLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblAyudaCocina, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(pnlCardCocinaLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnCocina)
                .addGap(18, 18, 18))
        );
        pnlCardCocinaLayout.setVerticalGroup(
            pnlCardCocinaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardCocinaLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlCardCocinaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubCocina))
                .addGap(8, 8, 8)
                .addGroup(pnlCardCocinaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAyudaCocina))
                .addGap(8, 8, 8)
                .addGroup(pnlCardCocinaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCocina))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        lblSubCaja.setText("Caja");

        lblAyudaCaja.setText("Cobra los pedidos listos.");

        btnCaja.setText("Abrir caja");

        javax.swing.GroupLayout pnlCardCajaLayout = new javax.swing.GroupLayout(pnlCardCaja);
        pnlCardCaja.setLayout(pnlCardCajaLayout);
        pnlCardCajaLayout.setHorizontalGroup(
            pnlCardCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardCajaLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblSubCaja)
                .addGap(18, 18, 18))
            .addGroup(pnlCardCajaLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblAyudaCaja, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(pnlCardCajaLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnCaja)
                .addGap(18, 18, 18))
        );
        pnlCardCajaLayout.setVerticalGroup(
            pnlCardCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardCajaLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlCardCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubCaja))
                .addGap(8, 8, 8)
                .addGroup(pnlCardCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAyudaCaja))
                .addGap(8, 8, 8)
                .addGroup(pnlCardCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCaja))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        lblSubProductos.setText("Productos");

        lblAyudaProductos.setText("Carta, precios y disponibilidad.");

        btnProductos.setText("Gestionar");

        javax.swing.GroupLayout pnlCardProductosLayout = new javax.swing.GroupLayout(pnlCardProductos);
        pnlCardProductos.setLayout(pnlCardProductosLayout);
        pnlCardProductosLayout.setHorizontalGroup(
            pnlCardProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardProductosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblSubProductos)
                .addGap(18, 18, 18))
            .addGroup(pnlCardProductosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblAyudaProductos, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(pnlCardProductosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnProductos)
                .addGap(18, 18, 18))
        );
        pnlCardProductosLayout.setVerticalGroup(
            pnlCardProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardProductosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlCardProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubProductos))
                .addGap(8, 8, 8)
                .addGroup(pnlCardProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAyudaProductos))
                .addGap(8, 8, 8)
                .addGroup(pnlCardProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProductos))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        lblSubUsuarios.setText("Usuarios");

        lblAyudaUsuarios.setText("Personal, roles y contraseñas.");

        btnUsuarios.setText("Gestionar");

        javax.swing.GroupLayout pnlCardUsuariosLayout = new javax.swing.GroupLayout(pnlCardUsuarios);
        pnlCardUsuarios.setLayout(pnlCardUsuariosLayout);
        pnlCardUsuariosLayout.setHorizontalGroup(
            pnlCardUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardUsuariosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblSubUsuarios)
                .addGap(18, 18, 18))
            .addGroup(pnlCardUsuariosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblAyudaUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(pnlCardUsuariosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnUsuarios)
                .addGap(18, 18, 18))
        );
        pnlCardUsuariosLayout.setVerticalGroup(
            pnlCardUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardUsuariosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlCardUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubUsuarios))
                .addGap(8, 8, 8)
                .addGroup(pnlCardUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAyudaUsuarios))
                .addGap(8, 8, 8)
                .addGroup(pnlCardUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUsuarios))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        lblSubReportes.setText("Reportes");

        lblAyudaReportes.setText("Ventas por día, producto y pago.");

        btnReportes.setText("Ver reportes");

        javax.swing.GroupLayout pnlCardReportesLayout = new javax.swing.GroupLayout(pnlCardReportes);
        pnlCardReportes.setLayout(pnlCardReportesLayout);
        pnlCardReportesLayout.setHorizontalGroup(
            pnlCardReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardReportesLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblSubReportes)
                .addGap(18, 18, 18))
            .addGroup(pnlCardReportesLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblAyudaReportes, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(pnlCardReportesLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnReportes)
                .addGap(18, 18, 18))
        );
        pnlCardReportesLayout.setVerticalGroup(
            pnlCardReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardReportesLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlCardReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubReportes))
                .addGap(8, 8, 8)
                .addGroup(pnlCardReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAyudaReportes))
                .addGap(8, 8, 8)
                .addGroup(pnlCardReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReportes))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout pnlInicioLayout = new javax.swing.GroupLayout(pnlInicio);
        pnlInicio.setLayout(pnlInicioLayout);
        pnlInicioLayout.setHorizontalGroup(
            pnlInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInicioLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(lblSaludo)
                .addGap(32, 32, 32))
            .addGroup(pnlInicioLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(lblFecha)
                .addGap(32, 32, 32))
            .addGroup(pnlInicioLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(pnlCardMesas, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(pnlCardPedidos, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(pnlCardCocina, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(pnlCardCaja, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(32, 32, 32))
            .addGroup(pnlInicioLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(pnlCardProductos, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(pnlCardUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(pnlCardReportes, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(32, 32, 32))
        );
        pnlInicioLayout.setVerticalGroup(
            pnlInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInicioLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(pnlInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSaludo))
                .addGap(14, 14, 14)
                .addGroup(pnlInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFecha))
                .addGap(14, 14, 14)
                .addGroup(pnlInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlCardMesas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardPedidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardCocina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardCaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(pnlInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlCardProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardReportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlBarra, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlInicio, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlBarra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addComponent(pnlInicio, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public javax.swing.JButton getBtnCaja() {
        return btnCaja;
    }

    public javax.swing.JButton getBtnCerrarSesion() {
        return btnCerrarSesion;
    }

    public javax.swing.JButton getBtnCocina() {
        return btnCocina;
    }

    public javax.swing.JButton getBtnMenuCaja() {
        return btnMenuCaja;
    }

    public javax.swing.JButton getBtnMenuCocina() {
        return btnMenuCocina;
    }

    public javax.swing.JButton getBtnMenuMesas() {
        return btnMenuMesas;
    }

    public javax.swing.JButton getBtnMenuPedidos() {
        return btnMenuPedidos;
    }

    public javax.swing.JButton getBtnMenuProductos() {
        return btnMenuProductos;
    }

    public javax.swing.JButton getBtnMenuReportes() {
        return btnMenuReportes;
    }

    public javax.swing.JButton getBtnMenuUsuarios() {
        return btnMenuUsuarios;
    }

    public javax.swing.JButton getBtnMesas() {
        return btnMesas;
    }

    public javax.swing.JButton getBtnPedidos() {
        return btnPedidos;
    }

    public javax.swing.JButton getBtnProductos() {
        return btnProductos;
    }

    public javax.swing.JButton getBtnReportes() {
        return btnReportes;
    }

    public javax.swing.JButton getBtnUsuarios() {
        return btnUsuarios;
    }

    public javax.swing.JLabel getLblFecha() {
        return lblFecha;
    }

    public javax.swing.JLabel getLblLogo() {
        return lblLogo;
    }

    public javax.swing.JLabel getLblSaludo() {
        return lblSaludo;
    }

    public javax.swing.JLabel getLblUsuario() {
        return lblUsuario;
    }

    public javax.swing.JPanel getPnlBarra() {
        return pnlBarra;
    }

    public javax.swing.JPanel getPnlCardCaja() {
        return pnlCardCaja;
    }

    public javax.swing.JPanel getPnlCardCocina() {
        return pnlCardCocina;
    }

    public javax.swing.JPanel getPnlCardMesas() {
        return pnlCardMesas;
    }

    public javax.swing.JPanel getPnlCardPedidos() {
        return pnlCardPedidos;
    }

    public javax.swing.JPanel getPnlCardProductos() {
        return pnlCardProductos;
    }

    public javax.swing.JPanel getPnlCardReportes() {
        return pnlCardReportes;
    }

    public javax.swing.JPanel getPnlCardUsuarios() {
        return pnlCardUsuarios;
    }

    public javax.swing.JPanel getPnlInicio() {
        return pnlInicio;
    }

    public javax.swing.JPanel getPnlMenu() {
        return pnlMenu;
    }

    public static void main(String args[]) {
        Tema.instalar();
        java.awt.EventQueue.invokeLater(() -> new FrmPrincipal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCaja;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnCocina;
    private javax.swing.JButton btnMenuCaja;
    private javax.swing.JButton btnMenuCocina;
    private javax.swing.JButton btnMenuMesas;
    private javax.swing.JButton btnMenuPedidos;
    private javax.swing.JButton btnMenuProductos;
    private javax.swing.JButton btnMenuReportes;
    private javax.swing.JButton btnMenuUsuarios;
    private javax.swing.JButton btnMesas;
    private javax.swing.JButton btnPedidos;
    private javax.swing.JButton btnProductos;
    private javax.swing.JButton btnReportes;
    private javax.swing.JButton btnUsuarios;
    private javax.swing.JLabel lblAyudaCaja;
    private javax.swing.JLabel lblAyudaCocina;
    private javax.swing.JLabel lblAyudaMesas;
    private javax.swing.JLabel lblAyudaPedidos;
    private javax.swing.JLabel lblAyudaProductos;
    private javax.swing.JLabel lblAyudaReportes;
    private javax.swing.JLabel lblAyudaUsuarios;
    private javax.swing.JLabel lblEtqMenu;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblSaludo;
    private javax.swing.JLabel lblSubCaja;
    private javax.swing.JLabel lblSubCocina;
    private javax.swing.JLabel lblSubMesas;
    private javax.swing.JLabel lblSubPedidos;
    private javax.swing.JLabel lblSubProductos;
    private javax.swing.JLabel lblSubReportes;
    private javax.swing.JLabel lblSubUsuarios;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel pnlBarra;
    private javax.swing.JPanel pnlCardCaja;
    private javax.swing.JPanel pnlCardCocina;
    private javax.swing.JPanel pnlCardMesas;
    private javax.swing.JPanel pnlCardPedidos;
    private javax.swing.JPanel pnlCardProductos;
    private javax.swing.JPanel pnlCardReportes;
    private javax.swing.JPanel pnlCardUsuarios;
    private javax.swing.JPanel pnlInicio;
    private javax.swing.JPanel pnlMenu;
    // End of variables declaration//GEN-END:variables
}
