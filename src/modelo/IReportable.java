package modelo; // Paquete del dominio.

/**
 * Contrato de todo documento que pueda exportarse como reporte.
 *
 * Principio SOLID aplicado: Segregación de Interfaces (ISP). Se separa en una interfaz pequeña y
 * específica lo que significa "ser exportable", en lugar de obligar a las clases a depender de
 * una interfaz grande con métodos que no usan. La EAAB requiere reportes en PDF y CSV (Necesidad 9).
 */
public interface IReportable {  // Declaración de la interfaz IReportable: Contrato de todo documento que pueda exportarse como reporte

    /** Construye el texto del reporte (la base común para PDF y CSV). */
    String generarContenido(); // Devuelve el contenido en texto plano.

    /** Exporta el reporte a un archivo PDF. */
    void exportarPDF(); // Formato legible para impresión.

    /** Exporta el reporte a un archivo CSV (valores separados por coma, abrible en Excel). */
    void exportarCSV(); // Formato tabular para análisis de datos.
}
