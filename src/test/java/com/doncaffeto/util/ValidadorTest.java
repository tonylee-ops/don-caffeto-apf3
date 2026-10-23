package com.doncaffeto.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    public void validaDni() {
        assertTrue(Validador.esDni("45123678"));
        assertFalse(Validador.esDni("4512"));
    }
}
