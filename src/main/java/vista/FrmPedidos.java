package vista;

import modelo.Tema;

public class FrmPedidos extends javax.swing.JFrame {
    public FrmPedidos() {
        initComponents();
        personalizar();
    }

    private void personalizar() {
        Tema.aplicar(this);
        Tema.ayuda(txtFecha, "dd/mm/aaaa");
        Tema.colorearEstado(tblPedidos, 5);
        setSize(1120, 740);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        lblEtqFecha = new javax.swing.JLabel();
        txtFecha = new javax.swing.JTextField();
        btnHoy = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        scrPedidos = new javax.swing.JScrollPane();
        tblPedidos = new javax.swing.JTable();
        lblResumen = new javax.swing.JLabel();
        btnExportar = new javax.swing.JButton();
        pnlCardDetalle = new javax.swing.JPanel();
        lblSubDetalle = new javax.swing.JLabel();
        lblSeleccion = new javax.swing.JLabel();
        lblObservacion = new javax.swing.JLabel();
        scrDetalle = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        btnAnular = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Don Caffeto - Pedidos");

        lblTitulo.setText("Pedidos del día");

        lblEtqFecha.setText("Fecha");

        btnHoy.setText("Hoy");

        btnBuscar.setText("Buscar");

        tblPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "N°", "Hora", "Modalidad", "Destino", "Mesero", "Estado", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPedidos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        scrPedidos.setViewportView(tblPedidos);

        lblResumen.setText(" ");

        btnExportar.setText("Exportar a Excel");

        lblSubDetalle.setText("Detalle");

        lblSeleccion.setText("Selecciona un pedido para ver su detalle");

        lblObservacion.setText(" ");

        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Producto", "Nota", "Cant.", "Precio", "Importe"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetalle.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        scrDetalle.setViewportView(tblDetalle);

        btnAnular.setText("Anular pedido");

        javax.swing.GroupLayout pnlCardDetalleLayout = new javax.swing.GroupLayout(pnlCardDetalle);
        pnlCardDetalle.setLayout(pnlCardDetalleLayout);
        pnlCardDetalleLayout.setHorizontalGroup(
            pnlCardDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardDetalleLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblSubDetalle)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblSeleccion)
                .addGap(16, 16, 16))
            .addGroup(pnlCardDetalleLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblObservacion, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addGap(16, 16, 16))
            .addGroup(pnlCardDetalleLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scrDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
                .addGap(16, 16, 16))
            .addGroup(pnlCardDetalleLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnAnular)
                .addGap(16, 16, 16))
        );
        pnlCardDetalleLayout.setVerticalGroup(
            pnlCardDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardDetalleLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubDetalle)
                    .addComponent(lblSeleccion))
                .addGap(8, 8, 8)
                .addGroup(pnlCardDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblObservacion))
                .addGap(8, 8, 8)
                .addGroup(pnlCardDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addGroup(pnlCardDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAnular))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblTitulo)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblEtqFecha)
                .addGap(10, 10, 10)
                .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnHoy)
                .addGap(10, 10, 10)
                .addComponent(btnBuscar)
                .addGap(20, 20, 20))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(scrPedidos, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
                .addGap(20, 20, 20))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblResumen)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnExportar)
                .addGap(20, 20, 20))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(pnlCardDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitulo)
                    .addComponent(lblEtqFecha)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHoy)
                    .addComponent(btnBuscar))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrPedidos, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblResumen)
                    .addComponent(btnExportar))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlCardDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public javax.swing.JButton getBtnAnular() {
        return btnAnular;
    }

    public javax.swing.JButton getBtnBuscar() {
        return btnBuscar;
    }

    public javax.swing.JButton getBtnExportar() {
        return btnExportar;
    }

    public javax.swing.JButton getBtnHoy() {
        return btnHoy;
    }

    public javax.swing.JLabel getLblObservacion() {
        return lblObservacion;
    }

    public javax.swing.JLabel getLblResumen() {
        return lblResumen;
    }

    public javax.swing.JLabel getLblSeleccion() {
        return lblSeleccion;
    }

    public javax.swing.JLabel getLblTitulo() {
        return lblTitulo;
    }

    public javax.swing.JPanel getPnlCardDetalle() {
        return pnlCardDetalle;
    }

    public javax.swing.JTable getTblDetalle() {
        return tblDetalle;
    }

    public javax.swing.JTable getTblPedidos() {
        return tblPedidos;
    }

    public javax.swing.JTextField getTxtFecha() {
        return txtFecha;
    }

    public static void main(String args[]) {
        Tema.instalar();
        java.awt.EventQueue.invokeLater(() -> new FrmPedidos().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnular;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnHoy;
    private javax.swing.JLabel lblEtqFecha;
    private javax.swing.JLabel lblObservacion;
    private javax.swing.JLabel lblResumen;
    private javax.swing.JLabel lblSeleccion;
    private javax.swing.JLabel lblSubDetalle;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlCardDetalle;
    private javax.swing.JScrollPane scrDetalle;
    private javax.swing.JScrollPane scrPedidos;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JTable tblPedidos;
    private javax.swing.JTextField txtFecha;
    // End of variables declaration//GEN-END:variables
}
