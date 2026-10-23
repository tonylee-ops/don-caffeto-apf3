package com.doncaffeto.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.apache.commons.lang3.StringUtils;

/**
 * Validaciones de datos ingresados por el usuario (usa Apache Commons Lang).
 *
 * @author Equipo Don Caffeto
 */
public final class Validador {

    /** Formato de fecha que se muestra y se escribe en las pantallas. */
    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    /**
     * @param texto monto escrito (acepta coma o punto decimal)
     * @return el monto, o null si no es un número válido
     */
    public static BigDecimal monto(String texto) {
        if (estaVacio(texto)) {
            return null;
        }
        try {
            return new BigDecimal(StringUtils.replace(texto.trim(), ",", ".")).setScale(2, java.math.RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * @param texto fecha escrita como dd/mm/aaaa
     * @return la fecha, o null si el formato no es válido
     */
    public static LocalDate fecha(String texto) {
        try {
            return LocalDate.parse(StringUtils.trimToEmpty(texto), FECHA);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
