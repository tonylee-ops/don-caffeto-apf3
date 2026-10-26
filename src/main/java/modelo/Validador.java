package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.apache.commons.lang3.StringUtils;

public final class Validador {
    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Validador() {
    }

    public static boolean estaVacio(String texto) {
        return StringUtils.isBlank(texto);
    }

    public static boolean esTelefono(String telefono) {
        String t = StringUtils.deleteWhitespace(telefono);
        return StringUtils.isNumeric(t) && t.length() == 9;
    }

    public static boolean esDni(String dni) {
        return StringUtils.isNumeric(dni) && dni.length() == 8;
    }

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

    public static LocalDate fecha(String texto) {
        try {
            return LocalDate.parse(StringUtils.trimToEmpty(texto), FECHA);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
