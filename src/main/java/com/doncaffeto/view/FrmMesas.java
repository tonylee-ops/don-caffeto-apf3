package com.doncaffeto.view;

import com.doncaffeto.util.Tema;

public class FrmMesas extends javax.swing.JFrame {
    public FrmMesas() {
        initComponents();
        personalizar();
    }

    private void personalizar() {
        Tema.aplicar(this);
        pnlMesas.setLayout(new java.awt.GridLayout(0, 4, 18, 18));
        Tema.leyenda(lblLeyendaLibre, lblLeyendaOcupada, lblLeyendaCuenta);
        setSize(1000, 640);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        lblLeyendaLibre = new javax.swing.JLabel();
        lblLeyendaOcupada = new javax.swing.JLabel();
        lblLeyendaCuenta = new javax.swing.JLabel();
        lblResumen = new javax.swing.JLabel();
        btnParaLlevar = new javax.swing.JButton();
        btnDelivery = new javax.swing.JButton();
        pnlMesas = new javax.swing.JPanel();
        lblAyudaMesas = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Don Caffeto - Mesas");

        lblTitulo.setText("Mesas");

        lblLeyendaLibre.setText("Libre");

        lblLeyendaOcupada.setText("En cocina");

        lblLeyendaCuenta.setText("Cuenta pedida");

        lblResumen.setText(" ");

        btnParaLlevar.setText("Pedido para llevar");

        btnDelivery.setText("Pedido delivery");

        pnlMesas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 12));

        lblAyudaMesas.setText("Toca una mesa gris para tomar el pedido, o una naranja o verde para ver su pedido.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblTitulo)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblLeyendaLibre)
                .addGap(10, 10, 10)
                .addComponent(lblLeyendaOcupada)
                .addGap(10, 10, 10)
                .addComponent(lblLeyendaCuenta)
                .addGap(24, 24, 24))
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblResumen)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnParaLlevar)
                .addGap(10, 10, 10)
                .addComponent(btnDelivery)
                .addGap(24, 24, 24))
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(pnlMesas, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
                .addGap(24, 24, 24))
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblAyudaMesas)
                .addGap(24, 24, 24))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitulo)
                    .addComponent(lblLeyendaLibre)
                    .addComponent(lblLeyendaOcupada)
                    .addComponent(lblLeyendaCuenta))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblResumen)
                    .addComponent(btnParaLlevar)
                    .addComponent(btnDelivery))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlMesas, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAyudaMesas))
                .addGap(24, 24, 24))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public javax.swing.JButton getBtnDelivery() {
        return btnDelivery;
    }

    public javax.swing.JButton getBtnParaLlevar() {
        return btnParaLlevar;
    }

    public javax.swing.JLabel getLblLeyendaCuenta() {
        return lblLeyendaCuenta;
    }

    public javax.swing.JLabel getLblLeyendaLibre() {
        return lblLeyendaLibre;
    }

    public javax.swing.JLabel getLblLeyendaOcupada() {
        return lblLeyendaOcupada;
    }

    public javax.swing.JLabel getLblResumen() {
        return lblResumen;
    }

    public javax.swing.JLabel getLblTitulo() {
        return lblTitulo;
    }

    public javax.swing.JPanel getPnlMesas() {
        return pnlMesas;
    }

    public static void main(String args[]) {
        Tema.instalar();
        java.awt.EventQueue.invokeLater(() -> new FrmMesas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelivery;
    private javax.swing.JButton btnParaLlevar;
    private javax.swing.JLabel lblAyudaMesas;
    private javax.swing.JLabel lblLeyendaCuenta;
    private javax.swing.JLabel lblLeyendaLibre;
    private javax.swing.JLabel lblLeyendaOcupada;
    private javax.swing.JLabel lblResumen;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlMesas;
    // End of variables declaration//GEN-END:variables
}
