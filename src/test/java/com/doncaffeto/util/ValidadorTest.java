package com.doncaffeto.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.Test;

/**
 * Pruebas de las validaciones de datos.
 */
public class ValidadorTest {

    @Test
    public void detectaTextoVacio() {
        assertTrue(Validador.estaVacio("   "));
        assertTrue(Validador.estaVacio(null));
        assertFalse(Validador.estaVacio("Mesa 3"));
    }

    @Test
    public void validaTelefonoDeNueveDigitos() {
        assertTrue(Validador.esTelefono("964 333 444"));
        assertFalse(Validador.esTelefono("12345"));
        assertFalse(Validador.esTelefono("abc"));
    }

    @Test
    public void leeMontosConComaOPunto() {
        assertEquals(new BigDecimal("12.50"), Validador.monto("12,5"));
        assertEquals(new BigDecimal("50.00"), Validador.monto(" 50 "));
        assertNull(Validador.monto("diez"));
    }

    @Test
    public void leeFechas() {
        assertEquals(LocalDate.of(2026, 10, 23), Validador.fecha("23/10/2026"));
        assertNull(Validador.fecha("2026-10-23"));
    }
}
