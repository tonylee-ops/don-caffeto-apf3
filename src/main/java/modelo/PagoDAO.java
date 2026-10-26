package modelo;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface PagoDAO {
    void registrarCobro(Pago pago, BigDecimal subtotal, BigDecimal igv) throws SQLException;
}
