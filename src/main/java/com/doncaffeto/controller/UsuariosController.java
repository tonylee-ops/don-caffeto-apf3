package com.doncaffeto.controller;

import com.doncaffeto.Servicios;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.view.FrmUsuarios;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuariosController {
    private final FrmUsuarios vista;
    private final Servicios servicios;
    private final Usuario admin;
    private List<Usuario> usuarios = new ArrayList<>();
    private Usuario editando;

    public UsuariosController(FrmUsuarios vista, Servicios servicios, Usuario admin) {
        this.vista = vista;
        this.servicios = servicios;
        this.admin = admin;
        vista.getBtnNuevo().addActionListener(e -> nuevo());
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getTblUsuarios().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                editar();
            }
        });
    }

    public void iniciar() {
        try {
            cargar();
        } catch (SQLException | SecurityException e) {
            Mensajes.de(vista, e);
        }
        nuevo();
        vista.setVisible(true);
    }

    private void cargar() throws SQLException {
        usuarios = servicios.usuarios.listar(admin);
        List<Object[]> filas = new ArrayList<>();
        for (Usuario u : usuarios) {
            filas.add(new Object[]{u.getUsername(), u.getNombres(), u.getRol(), u.isActivo() ? "Sí" : "No"});
        }
        Tablas.llenar(vista.getTblUsuarios(), filas);
    }

    private void nuevo() {
        editando = new Usuario();
        vista.getTblUsuarios().clearSelection();
        vista.getLblModo().setText("Nuevo usuario");
        vista.getLblAyudaClave().setText("Mínimo 8 caracteres.");
        vista.getTxtUsername().setText("");
        vista.getTxtNombres().setText("");
        vista.getPwdClave().setText("");
        vista.getCmbRol().setSelectedItem("MESERO");
        vista.getChkActivo().setSelected(true);
        vista.getTxtUsername().requestFocusInWindow();
    }

    private void editar() {
        int fila = vista.getTblUsuarios().getSelectedRow();
        if (fila < 0) {
            return;
        }
        editando = usuarios.get(fila);
        vista.getLblModo().setText("Editando: " + editando.getUsername());
        vista.getLblAyudaClave().setText("Déjala vacía para no cambiarla.");
        vista.getTxtUsername().setText(editando.getUsername());
        vista.getTxtNombres().setText(editando.getNombres());
        vista.getPwdClave().setText("");
        vista.getCmbRol().setSelectedItem(editando.getRol());
        vista.getChkActivo().setSelected(editando.isActivo());
    }

    private void guardar() {
        Usuario u = new Usuario(editando.getIdUsuario(), vista.getTxtUsername().getText(),
                vista.getTxtNombres().getText(), String.valueOf(vista.getCmbRol().getSelectedItem()));
        u.setActivo(vista.getChkActivo().isSelected());
        String clave = new String(vista.getPwdClave().getPassword());
        vista.getPwdClave().setText("");
        try {
            servicios.usuarios.guardar(u, clave, admin);
            Mensajes.info(vista, "Usuario «" + u.getUsername() + "» guardado.");
            cargar();
            nuevo();
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
        }
    }
}
