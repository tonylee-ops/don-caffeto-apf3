package com.doncaffeto.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Validaciones de datos ingresados por el usuario (usa Apache Commons Lang).
 *
 * @author Equipo Don Caffeto
 */
public final class Validador {

    private Validador() {
    }

    /**
     * @param texto texto a revisar
     * @return true si está vacío o solo tiene espacios
     */
    public static boolean estaVacio(String texto) {
        return StringUtils.isBlank(texto);
    }

    /**
     * @param telefono teléfono ingresado
     * @return true si tiene 9 dígitos (celular peruano)
     */
    public static boolean esTelefono(String telefono) {
        String t = StringUtils.deleteWhitespace(telefono);
        return StringUtils.isNumeric(t) && t.length() == 9;
    }

    /**
     * @param dni DNI ingresado
     * @return true si tiene 8 dígitos
     */
    public static boolean esDni(String dni) {
        return StringUtils.isNumeric(dni) && dni.length() == 8;
    }
}
