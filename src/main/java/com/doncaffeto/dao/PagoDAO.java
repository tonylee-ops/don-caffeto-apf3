package com.doncaffeto.dao;

import com.doncaffeto.model.Pago;
import java.math.BigDecimal;
import java.sql.SQLException;

public interface PagoDAO {
    void registrarCobro(Pago pago, BigDecimal subtotal, BigDecimal igv) throws SQLException;
}
