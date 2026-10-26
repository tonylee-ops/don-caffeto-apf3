package modelo;

import java.util.Arrays;

public final class Autorizacion {
    public static final String[] PEDIDOS = {"MESERO", "ADMIN"};

    public static final String[] COCINA = {"COCINA", "ADMIN"};

    public static final String[] CAJA = {"CAJERO", "ADMIN"};

    public static final String[] ADMIN = {"ADMIN"};

    private Autorizacion() {
    }

    public static boolean permite(Usuario usuario, String... roles) {
        return usuario != null && Arrays.asList(roles).contains(usuario.getRol());
    }

    public static void exigir(Usuario usuario, String... roles) {
        if (!permite(usuario, roles)) {
            throw new SecurityException("Tu rol no tiene permiso para esta acción.");
        }
    }
}
