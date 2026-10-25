package com.doncaffeto.view;

import com.doncaffeto.util.Tema;

public class FrmLogin extends javax.swing.JFrame {
    public FrmLogin() {
        initComponents();
        personalizar();
    }

    private void personalizar() {
        Tema.aplicar(this);
        Tema.logo(lblLogo, 150);
        Tema.ayuda(txtUsuario, "Ejemplo: mesero1");
        Tema.ayuda(pwdPassword, "Tu contraseña");
        getRootPane().setDefaultButton(btnIngresar);
        setResizable(false);
        setSize(820, 520);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlBarra = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblMarca = new javax.swing.JLabel();
        lblEslogan = new javax.swing.JLabel();
        pnlFormulario = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblAyuda = new javax.swing.JLabel();
        lblEtqUsuario = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        lblEtqPassword = new javax.swing.JLabel();
        pwdPassword = new javax.swing.JPasswordField();
        chkMostrar = new javax.swing.JCheckBox();
        lblMensaje = new javax.swing.JLabel();
        btnIngresar = new javax.swing.JButton();
        lblAyudaVersion = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Don Caffeto - Iniciar sesión");

        lblLogo.setText("");

        lblMarca.setText("Don Caffeto");

        lblEslogan.setText("Café de la selva central");

        javax.swing.GroupLayout pnlBarraLayout = new javax.swing.GroupLayout(pnlBarra);
        pnlBarra.setLayout(pnlBarraLayout);
        pnlBarraLayout.setHorizontalGroup(
            pnlBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBarraLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblLogo)
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(30, 30, 30))
            .addGroup(pnlBarraLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblMarca)
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(30, 30, 30))
            .addGroup(pnlBarraLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblEslogan)
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );
        pnlBarraLayout.setVerticalGroup(
            pnlBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBarraLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(pnlBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLogo))
                .addGap(8, 8, 8)
                .addGroup(pnlBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMarca))
                .addGap(8, 8, 8)
                .addGroup(pnlBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEslogan))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        lblTitulo.setText("Bienvenido");

        lblAyuda.setText("Ingresa con tu usuario para empezar");

        lblEtqUsuario.setText("Usuario");

        txtUsuario.setToolTipText("Ejemplo: mesero1");

        lblEtqPassword.setText("Contraseña");

        chkMostrar.setText("Mostrar contraseña");

        lblMensaje.setText(" ");

        btnIngresar.setText("Ingresar");

        lblAyudaVersion.setText("Don Caffeto · Sistema de ventas");

        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(lblTitulo)
                .addGap(48, 48, 48))
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(lblAyuda)
                .addGap(48, 48, 48))
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(lblEtqUsuario)
                .addGap(48, 48, 48))
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(txtUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addGap(48, 48, 48))
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(lblEtqPassword)
                .addGap(48, 48, 48))
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(pwdPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addGap(48, 48, 48))
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(chkMostrar)
                .addGap(48, 48, 48))
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(lblMensaje, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addGap(48, 48, 48))
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(btnIngresar, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addGap(48, 48, 48))
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblAyudaVersion)
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(48, 48, 48))
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitulo))
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAyuda))
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEtqUsuario))
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEtqPassword))
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pwdPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkMostrar))
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMensaje))
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnIngresar))
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAyudaVersion))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(48, 48, 48))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlBarra, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlFormulario, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlBarra, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(pnlFormulario, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public javax.swing.JButton getBtnIngresar() {
        return btnIngresar;
    }

    public javax.swing.JCheckBox getChkMostrar() {
        return chkMostrar;
    }

    public javax.swing.JLabel getLblLogo() {
        return lblLogo;
    }

    public javax.swing.JLabel getLblMensaje() {
        return lblMensaje;
    }

    public javax.swing.JLabel getLblTitulo() {
        return lblTitulo;
    }

    public javax.swing.JPanel getPnlBarra() {
        return pnlBarra;
    }

    public javax.swing.JPanel getPnlFormulario() {
        return pnlFormulario;
    }

    public javax.swing.JPasswordField getPwdPassword() {
        return pwdPassword;
    }

    public javax.swing.JTextField getTxtUsuario() {
        return txtUsuario;
    }

    public static void main(String args[]) {
        Tema.instalar();
        java.awt.EventQueue.invokeLater(() -> new FrmLogin().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIngresar;
    private javax.swing.JCheckBox chkMostrar;
    private javax.swing.JLabel lblAyuda;
    private javax.swing.JLabel lblAyudaVersion;
    private javax.swing.JLabel lblEslogan;
    private javax.swing.JLabel lblEtqPassword;
    private javax.swing.JLabel lblEtqUsuario;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlBarra;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPasswordField pwdPassword;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
