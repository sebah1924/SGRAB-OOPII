package controlador;
                                                  // Paquete

import dao.*;                                                         // DAOs
import modelo.*;                                                      // Modelo
import java.time.LocalDate;                                                    // Fechas
import java.util.List;                                                         // Listas

/**
 * Controlador de REPORTES (Necesidad 9).
 * Responsabilidad ÚNICA: consolidar cifras financieras y exportarlas.
 */
public class ReporteControlador {

    private final ReporteDAO reporteDAO = new ReporteDAO();                    // CRUD
    private final FacturaDAO facturaDAO = new FacturaDAO();                    // Facturas
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();                    // Usuarios

    /** Genera un reporte consolidado del periodo. */
    public Reporte generar(String codigo, String autor, LocalDate desde, LocalDate hasta)
            throws Exception {
        Reporte r = new Reporte(codigo, autor);                                // Crea reporte
        double ingresos = 0, cartera = 0, subsidios = 0, multas = 0;
        for (Usuario u : usuarioDAO.listar()) {
            List<Factura> fs = facturaDAO.listarPorUsuario(u.getId());
            for (Factura f : fs) {
                if (f.getFechaEmision().isBefore(desde) ||
                    f.getFechaEmision().isAfter(hasta)) continue;              // Filtra rango
                ingresos += f.getTotalPagado();                                // Suma ingresos
                cartera += Math.max(0, f.getSaldoPendiente());                 // Suma cartera
                // Estima subsidio: si es residencial y recibe subsidio
                if (u instanceof Residencial) {
                    Residencial rr = (Residencial) u;
                    if (rr.isRecibeSubsidio()) subsidios += f.getValorTotal() * 0.3; // Est.
                }
            }
        }
        r.setIngresosFacturacion(ingresos);
        r.setCarteraPendiente(cartera);
        r.setSubsidiosOtorgados(subsidios);
        r.setMultasRecaudadas(multas);
        reporteDAO.guardar(r);                                                  // Persiste
        return r;
    }

    /** Lista reportes históricos. */
    public List<Reporte> listar() throws Exception {
        return reporteDAO.listar();
    }

    /** Exporta a CSV. */
    public String exportarCSV(Reporte r) { return r.exportarCSV(); }

    /** Exporta a PDF (simulado). */
    public String exportarPDF(Reporte r) { return r.exportarPDF(); }
}