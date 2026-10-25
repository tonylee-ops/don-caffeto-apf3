package com.doncaffeto.view;

import com.doncaffeto.util.Tema;

public class FrmReportes extends javax.swing.JFrame {
    public FrmReportes() {
        initComponents();
        personalizar();
    }

    private void personalizar() {
        Tema.aplicar(this);
        Tema.ayuda(txtDesde, "dd/mm/aaaa");
        Tema.ayuda(txtHasta, "dd/mm/aaaa");
        setSize(1000, 640);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        cmbTipo = new javax.swing.JComboBox<>();
        lblEtqDesde = new javax.swing.JLabel();
        txtDesde = new javax.swing.JTextField();
        lblEtqHasta = new javax.swing.JLabel();
        txtHasta = new javax.swing.JTextField();
        btnGenerar = new javax.swing.JButton();
        btnExportar = new javax.swing.JButton();
        scrReporte = new javax.swing.JScrollPane();
        tblReporte = new javax.swing.JTable();
        lblResumen = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Don Caffeto - Reportes");

        lblTitulo.setText("Reportes");

        cmbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ventas por día", "Productos más vendidos", "Ventas por método de pago" }));

        lblEtqDesde.setText("Desde");

        lblEtqHasta.setText("Hasta");

        btnGenerar.setText("Generar");

        btnExportar.setText("Exportar a Excel");

        tblReporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Fecha", "N° de ventas", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblReporte.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        scrReporte.setViewportView(tblReporte);

        lblResumen.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblTitulo)
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(20, 20, 20))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(cmbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblEtqDesde)
                .addGap(10, 10, 10)
                .addComponent(txtDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblEtqHasta)
                .addGap(10, 10, 10)
                .addComponent(txtHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnGenerar)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnExportar)
                .addGap(20, 20, 20))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(scrReporte, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
                .addGap(20, 20, 20))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblResumen)
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitulo))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEtqDesde)
                    .addComponent(txtDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEtqHasta)
                    .addComponent(txtHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerar)
                    .addComponent(btnExportar))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrReporte, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblResumen))
                .addGap(20, 20, 20))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public javax.swing.JButton getBtnExportar() {
        return btnExportar;
    }

    public javax.swing.JButton getBtnGenerar() {
        return btnGenerar;
    }

    public javax.swing.JComboBox<Object> getCmbTipo() {
        return cmbTipo;
    }

    public javax.swing.JLabel getLblResumen() {
        return lblResumen;
    }

    public javax.swing.JLabel getLblTitulo() {
        return lblTitulo;
    }

    public javax.swing.JTable getTblReporte() {
        return tblReporte;
    }

    public javax.swing.JTextField getTxtDesde() {
        return txtDesde;
    }

    public javax.swing.JTextField getTxtHasta() {
        return txtHasta;
    }

    public static void main(String args[]) {
        Tema.instalar();
        java.awt.EventQueue.invokeLater(() -> new FrmReportes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnGenerar;
    private javax.swing.JComboBox<Object> cmbTipo;
    private javax.swing.JLabel lblEtqDesde;
    private javax.swing.JLabel lblEtqHasta;
    private javax.swing.JLabel lblResumen;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JScrollPane scrReporte;
    private javax.swing.JTable tblReporte;
    private javax.swing.JTextField txtDesde;
    private javax.swing.JTextField txtHasta;
    // End of variables declaration//GEN-END:variables
}
