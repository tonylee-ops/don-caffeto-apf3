package vista;

import modelo.Tema;

public class FrmCocina extends javax.swing.JFrame {
    public FrmCocina() {
        initComponents();
        personalizar();
    }

    private void personalizar() {
        Tema.aplicar(this);
        Tema.colorearEstado(tblCocina, 3);
        setSize(1120, 680);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        lblActualizado = new javax.swing.JLabel();
        btnActualizar = new javax.swing.JButton();
        pnlCardCola = new javax.swing.JPanel();
        lblSubCola = new javax.swing.JLabel();
        lblResumen = new javax.swing.JLabel();
        scrCocina = new javax.swing.JScrollPane();
        tblCocina = new javax.swing.JTable();
        pnlCardDetalle = new javax.swing.JPanel();
        lblSubDetalle = new javax.swing.JLabel();
        lblSeleccion = new javax.swing.JLabel();
        lblObservacion = new javax.swing.JLabel();
        scrDetalle = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        btnPreparar = new javax.swing.JButton();
        btnListo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Don Caffeto - Cocina");

        lblTitulo.setText("Cocina");

        lblActualizado.setText("Actualizado");

        btnActualizar.setText("Actualizar");

        lblSubCola.setText("Pedidos en cocina");

        lblResumen.setText(" ");

        tblCocina.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "N°", "Hora", "Destino", "Estado", "Espera"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCocina.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        scrCocina.setViewportView(tblCocina);

        javax.swing.GroupLayout pnlCardColaLayout = new javax.swing.GroupLayout(pnlCardCola);
        pnlCardCola.setLayout(pnlCardColaLayout);
        pnlCardColaLayout.setHorizontalGroup(
            pnlCardColaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardColaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblSubCola)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblResumen)
                .addGap(16, 16, 16))
            .addGroup(pnlCardColaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scrCocina, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        pnlCardColaLayout.setVerticalGroup(
            pnlCardColaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardColaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardColaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubCola)
                    .addComponent(lblResumen))
                .addGap(10, 10, 10)
                .addGroup(pnlCardColaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrCocina, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );

        lblSubDetalle.setText("Detalle");

        lblSeleccion.setText(" ");

        lblObservacion.setText(" ");

        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Producto", "Cant.", "Nota"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetalle.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        scrDetalle.setViewportView(tblDetalle);

        btnPreparar.setText("Empezar preparación");

        btnListo.setText("Marcar como listo");

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
                .addComponent(lblObservacion, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addGap(16, 16, 16))
            .addGroup(pnlCardDetalleLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scrDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                .addGap(16, 16, 16))
            .addGroup(pnlCardDetalleLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(btnPreparar)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnListo)
                .addGap(16, 16, 16))
        );
        pnlCardDetalleLayout.setVerticalGroup(
            pnlCardDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardDetalleLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubDetalle)
                    .addComponent(lblSeleccion))
                .addGap(10, 10, 10)
                .addGroup(pnlCardDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblObservacion))
                .addGap(10, 10, 10)
                .addGroup(pnlCardDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(pnlCardDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPreparar)
                    .addComponent(btnListo))
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
                .addComponent(lblActualizado)
                .addGap(16, 16, 16)
                .addComponent(btnActualizar)
                .addGap(20, 20, 20))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(pnlCardCola, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(pnlCardDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitulo)
                    .addComponent(lblActualizado)
                    .addComponent(btnActualizar))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlCardCola, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                    .addComponent(pnlCardDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public javax.swing.JButton getBtnActualizar() {
        return btnActualizar;
    }

    public javax.swing.JButton getBtnListo() {
        return btnListo;
    }

    public javax.swing.JButton getBtnPreparar() {
        return btnPreparar;
    }

    public javax.swing.JLabel getLblActualizado() {
        return lblActualizado;
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

    public javax.swing.JPanel getPnlCardCola() {
        return pnlCardCola;
    }

    public javax.swing.JPanel getPnlCardDetalle() {
        return pnlCardDetalle;
    }

    public javax.swing.JTable getTblCocina() {
        return tblCocina;
    }

    public javax.swing.JTable getTblDetalle() {
        return tblDetalle;
    }

    public static void main(String args[]) {
        Tema.instalar();
        java.awt.EventQueue.invokeLater(() -> new FrmCocina().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnListo;
    private javax.swing.JButton btnPreparar;
    private javax.swing.JLabel lblActualizado;
    private javax.swing.JLabel lblObservacion;
    private javax.swing.JLabel lblResumen;
    private javax.swing.JLabel lblSeleccion;
    private javax.swing.JLabel lblSubCola;
    private javax.swing.JLabel lblSubDetalle;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlCardCola;
    private javax.swing.JPanel pnlCardDetalle;
    private javax.swing.JScrollPane scrCocina;
    private javax.swing.JScrollPane scrDetalle;
    private javax.swing.JTable tblCocina;
    private javax.swing.JTable tblDetalle;
    // End of variables declaration//GEN-END:variables
}
