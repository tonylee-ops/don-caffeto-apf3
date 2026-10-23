package com.doncaffeto.dao;

import com.doncaffeto.model.Pago;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Registro de cobros (tablas pagos y ventas).
 *
 * @author Equipo Don Caffeto
 */
public interface PagoDAO {

    /**
     * Registra el pago y la venta, marca el pedido como PAGADO y libera la mesa,
     * todo en una sola transacción: si algo falla no se guarda nada.
     *
     * @param pago     datos del pago
     * @param subtotal valor de venta sin IGV
     * @param igv      IGV de la venta
     * @throws SQLException si falla o si el pedido ya no está LISTO
     */
    void registrarCobro(Pago pago, BigDecimal subtotal, BigDecimal igv) throws SQLException;
}
