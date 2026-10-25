package com.doncaffeto.view;

import com.doncaffeto.util.Tema;

public class FrmUsuarios extends javax.swing.JFrame {
    public FrmUsuarios() {
        initComponents();
        personalizar();
    }

    private void personalizar() {
        Tema.aplicar(this);
        Tema.ayuda(pwdClave, "Mínimo 8 caracteres");
        setSize(1100, 640);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        scrUsuarios = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();
        pnlCardFormulario = new javax.swing.JPanel();
        lblSubFormulario = new javax.swing.JLabel();
        lblModo = new javax.swing.JLabel();
        lblEtqUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblEtqNombres = new javax.swing.JLabel();
        txtNombres = new javax.swing.JTextField();
        lblEtqRol = new javax.swing.JLabel();
        cmbRol = new javax.swing.JComboBox<>();
        lblEtqClave = new javax.swing.JLabel();
        pwdClave = new javax.swing.JPasswordField();
        lblAyudaClave = new javax.swing.JLabel();
        chkActivo = new javax.swing.JCheckBox();
        btnNuevo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Don Caffeto - Usuarios");

        lblTitulo.setText("Usuarios");

        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Usuario", "Nombres", "Rol", "Activo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUsuarios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        scrUsuarios.setViewportView(tblUsuarios);

        lblSubFormulario.setText("Datos del usuario");

        lblModo.setText("Nuevo usuario");

        lblEtqUsername.setText("Usuario");

        lblEtqNombres.setText("Nombres");

        lblEtqRol.setText("Rol");

        cmbRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMIN", "MESERO", "COCINA", "CAJERO" }));

        lblEtqClave.setText("Contraseña");

        lblAyudaClave.setText("Al editar, déjala vacía para no cambiarla.");

        chkActivo.setSelected(true);
        chkActivo.setText("Usuario activo");

        btnNuevo.setText("Nuevo");

        btnGuardar.setText("Guardar");

        javax.swing.GroupLayout pnlCardFormularioLayout = new javax.swing.GroupLayout(pnlCardFormulario);
        pnlCardFormulario.setLayout(pnlCardFormularioLayout);
        pnlCardFormularioLayout.setHorizontalGroup(
            pnlCardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardFormularioLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblSubFormulario)
                .addGap(18, 18, 18))
            .addGroup(pnlCardFormularioLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblModo)
                .addGap(18, 18, 18))
            .addGroup(pnlCardFormularioLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblEtqUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(txtUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(pnlCardFormularioLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblEtqNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(txtNombres, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(pnlCardFormularioLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblEtqRol, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(cmbRol, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(pnlCardFormularioLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblEtqClave, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(pwdClave, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(pnlCardFormularioLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblAyudaClave, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(pnlCardFormularioLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(chkActivo)
                .addGap(18, 18, 18))
            .addGroup(pnlCardFormularioLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnNuevo)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnGuardar)
                .addGap(18, 18, 18))
        );
        pnlCardFormularioLayout.setVerticalGroup(
            pnlCardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardFormularioLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlCardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubFormulario))
                .addGap(10, 10, 10)
                .addGroup(pnlCardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblModo))
                .addGap(10, 10, 10)
                .addGroup(pnlCardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEtqUsername)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlCardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEtqNombres)
                    .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlCardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEtqRol)
                    .addComponent(cmbRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlCardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEtqClave)
                    .addComponent(pwdClave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlCardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAyudaClave))
                .addGap(10, 10, 10)
                .addGroup(pnlCardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkActivo))
                .addGap(10, 10, 10)
                .addGroup(pnlCardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevo)
                    .addComponent(btnGuardar))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

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
                .addComponent(scrUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(pnlCardFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitulo))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                    .addComponent(pnlCardFormulario, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public javax.swing.JButton getBtnGuardar() {
        return btnGuardar;
    }

    public javax.swing.JButton getBtnNuevo() {
        return btnNuevo;
    }

    public javax.swing.JCheckBox getChkActivo() {
        return chkActivo;
    }

    public javax.swing.JComboBox<Object> getCmbRol() {
        return cmbRol;
    }

    public javax.swing.JLabel getLblAyudaClave() {
        return lblAyudaClave;
    }

    public javax.swing.JLabel getLblModo() {
        return lblModo;
    }

    public javax.swing.JLabel getLblTitulo() {
        return lblTitulo;
    }

    public javax.swing.JPanel getPnlCardFormulario() {
        return pnlCardFormulario;
    }

    public javax.swing.JPasswordField getPwdClave() {
        return pwdClave;
    }

    public javax.swing.JTable getTblUsuarios() {
        return tblUsuarios;
    }

    public javax.swing.JTextField getTxtNombres() {
        return txtNombres;
    }

    public javax.swing.JTextField getTxtUsername() {
        return txtUsername;
    }

    public static void main(String args[]) {
        Tema.instalar();
        java.awt.EventQueue.invokeLater(() -> new FrmUsuarios().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JCheckBox chkActivo;
    private javax.swing.JComboBox<Object> cmbRol;
    private javax.swing.JLabel lblAyudaClave;
    private javax.swing.JLabel lblEtqClave;
    private javax.swing.JLabel lblEtqNombres;
    private javax.swing.JLabel lblEtqRol;
    private javax.swing.JLabel lblEtqUsername;
    private javax.swing.JLabel lblModo;
    private javax.swing.JLabel lblSubFormulario;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlCardFormulario;
    private javax.swing.JPasswordField pwdClave;
    private javax.swing.JScrollPane scrUsuarios;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
