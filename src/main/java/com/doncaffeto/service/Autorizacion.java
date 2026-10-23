package com.doncaffeto.service;

import com.doncaffeto.model.Usuario;
import java.util.Arrays;

/**
 * Permisos por rol. Los servicios lo usan antes de cada acción, así la regla se cumple aunque
 * alguien intente saltarse la pantalla.
 *
 * @author Equipo Don Caffeto
 */
public final class Autorizacion {

    /** Pueden registrar y anular pedidos. */
    public static final String[] PEDIDOS = {"MESERO", "ADMIN"};
    /** Pueden ver la cocina y cambiar estados. */
    public static final String[] COCINA = {"COCINA", "ADMIN"};
    /** Pueden cobrar. */
    public static final String[] CAJA = {"CAJERO", "ADMIN"};
    /** Gestionan productos, usuarios y reportes. */
    public static final String[] ADMIN = {"ADMIN"};

    private Autorizacion() {
    }

    /**
     * @param usuario usuario en sesión
     * @param roles   roles permitidos
     * @return true si el usuario tiene uno de los roles
     */
    public static boolean permite(Usuario usuario, String... roles) {
        return usuario != null && Arrays.asList(roles).contains(usuario.getRol());
    }

    /**
     * @param usuario usuario en sesión
     * @param roles   roles permitidos
     * @throws SecurityException si el usuario no tiene permiso
     */
    public static void exigir(Usuario usuario, String... roles) {
        if (!permite(usuario, roles)) {
            throw new SecurityException("Tu rol no tiene permiso para esta acción.");
        }
    }
}
