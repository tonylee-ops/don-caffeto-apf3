package com.doncaffeto.controller;

import com.doncaffeto.Servicios;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.service.ReporteService;
import com.doncaffeto.util.Tema;
import com.doncaffeto.util.Validador;
import com.doncaffeto.view.FrmReportes;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Controla los reportes de ventas (administrador).
 *
 * @author Equipo Don Caffeto
 */
public class ReportesController {

    private final FrmReportes vista;
    private final Servicios servicios;
    private final Usuario admin;
    private List<Object[]> filas = new ArrayList<>();

    /**
     * @param vista     pantalla de reportes
     * @param servicios servicios de la aplicación
     * @param admin     administrador en sesión
     */
    public ReportesController(FrmReportes vista, Servicios servicios, Usuario admin) {
        this.vista = vista;
        this.servicios = servicios;
        this.admin = admin;
        LocalDate hoy = LocalDate.now();
        vista.getTxtDesde().setText(hoy.withDayOfMonth(1).format(Validador.FECHA));
        vista.getTxtHasta().setText(hoy.format(Validador.FECHA));
        vista.getBtnGenerar().addActionListener(e -> generar());
        vista.getCmbTipo().addActionListener(e -> generar());
        vista.getBtnExportar().addActionListener(e -> Tablas.exportar(vista, "Reporte",
                ReporteService.COLUMNAS[vista.getCmbTipo().getSelectedIndex()], filas,
                "reporte_" + String.valueOf(vista.getCmbTipo().getSelectedItem()).toLowerCase().replace(' ', '_')));
    }

    /** Genera el reporte del mes y muestra la pantalla. */
    public void iniciar() {
        generar();
        vista.setVisible(true);
    }

    private void generar() {
        LocalDate desde = Validador.fecha(vista.getTxtDesde().getText());
        LocalDate hasta = Validador.fecha(vista.getTxtHasta().getText());
        if (desde == null || hasta == null) {
            Mensajes.aviso(vista, "Escribe las fechas como dd/mm/aaaa.");
            return;
        }
        int tipo = vista.getCmbTipo().getSelectedIndex();
        try {
            filas = servicios.reportes.generar(tipo, desde, hasta, admin);
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
            return;
        }
        ((DefaultTableModel) vista.getTblReporte().getModel()).setColumnIdentifiers(ReporteService.COLUMNAS[tipo]);
        Tablas.llenar(vista.getTblReporte(), filas);
        BigDecimal total = BigDecimal.ZERO;
        for (Object[] f : filas) {
            total = total.add((BigDecimal) f[2]);
        }
        vista.getLblResumen().setText(filas.isEmpty() ? "No hay ventas en ese rango de fechas"
                : filas.size() + " filas  ·  Total " + Tema.soles(total));
        vista.getBtnExportar().setEnabled(!filas.isEmpty());
    }
}
