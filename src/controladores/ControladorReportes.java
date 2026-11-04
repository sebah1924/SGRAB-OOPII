package controladores; // Paquete de los controladores.

import java.util.ArrayList;  // Importa java.util.ArrayList
import java.util.List;  // Importa java.util.List
import modelo.*;  // Importa modelo.*

/** Controlador de los reportes financieros (RF-28 y RF-29). */
public class ControladorReportes extends ControladorBase {  // Declaración de la clase ControladorReportes: Controlador de los reportes financieros (RF-28 y RF-29)

    /** Inyecta el contexto. */
    public ControladorReportes(ContextoSgrab contexto) {  // Constructor: Inyecta el contexto
        super(contexto);  // Llama al constructor de la clase base para iniciar la parte heredada
    }

    /** Genera un reporte firmado por el funcionario indicado. */
    public Resultado<ReporteFinanciero> generar(String autor) {  // Método generar: Genera un reporte firmado por el funcionario indicado
        return ejecutar(() -> contexto.getEmpresa().generarReporteFinanciero(autor), "Reporte generado.");  // Devuelve: ejecutar(() - es mayor que contexto.getEmpresa().generarReporteFinancier...
    }

    /** Exporta a PDF; devuelve la ruta del archivo creado. */
    public Resultado<String> exportarPdf(ReporteFinanciero reporte) {  // Método exportarPdf: Exporta a PDF; devuelve la ruta del archivo creado
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            reporte.exportarPDF();  // Ejecuta la operación «exportar pdf» (definida en esta clase o en un objeto colaborador)
            return "reportes/" + reporte.getCodigo() + ".pdf";  // Devuelve: "reportes/" + reporte.getCodigo() + ".pdf"
        }, "Reporte exportado a PDF.");  // Ejecuta: }, "Reporte exportado a PDF.")
    }

    /** Exporta a CSV; devuelve la ruta del archivo creado. */
    public Resultado<String> exportarCsv(ReporteFinanciero reporte) {  // Método exportarCsv: Exporta a CSV; devuelve la ruta del archivo creado
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            reporte.exportarCSV();  // Ejecuta la operación «exportar csv» (definida en esta clase o en un objeto colaborador)
            return "reportes/" + reporte.getCodigo() + ".csv";  // Devuelve: "reportes/" + reporte.getCodigo() + ".csv"
        }, "Reporte exportado a CSV.");  // Ejecuta: }, "Reporte exportado a CSV.")
    }

    /** @return los reportes generados. */
    public List<ReporteFinanciero> listar() {  // Método listar (devuelve List<ReporteFinanciero>)
        return new ArrayList<>(contexto.getEmpresa().getListaReportes());  // Devuelve: new ArrayList<>(contexto.getEmpresa().getListaReportes())
    }
}
