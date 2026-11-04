package modelo; // Paquete del dominio.

import java.io.ByteArrayOutputStream;          // Búfer de bytes en memoria para armar el PDF.
import java.io.IOException;                     // Error de lectura/escritura de archivos.
import java.math.BigDecimal;                    // Dinero con precisión exacta.
import java.math.RoundingMode;                  // Redondeo monetario.
import java.nio.charset.Charset;                // Juegos de caracteres (Windows-1252 para el PDF).
import java.nio.charset.StandardCharsets;       // UTF-8 para el CSV.
import java.nio.file.Files;                     // Escritura de archivos.
import java.nio.file.Path;                      // Ruta de archivo.
import java.nio.file.Paths;                     // Fábrica de rutas.
import java.time.LocalDateTime;                 // Fecha y hora de generación.
import java.time.format.DateTimeFormatter;      // Formato legible de fecha y hora.

/**
 * Reporte financiero de la EAAB, exportable a PDF y CSV (Necesidad 9). Implementa IReportable (ISP).
 *
 * REGLAS DE NEGOCIO (enunciado, "La EAAB también requiere reportes financieros"):
 *  - código obligatorio y único; fecha de generación NO futura; autor (funcionario de la empresa);
 *  - ingresos por facturación, cartera pendiente, subsidios otorgados y multas recaudadas.
 *
 * DEFINICIONES adoptadas para los indicadores derivados:
 *  - recaudo neto  = ingresos por facturación - cartera pendiente (lo efectivamente cobrado de lo facturado).
 *  - índice de cartera = cartera pendiente / ingresos por facturación (0 a 1; mide qué fracción no se ha cobrado).
 *
 * El PDF se genera con un escritor mínimo propio (formato PDF 1.4 con fuente estándar Helvetica) para no
 * depender de librerías externas; los archivos se guardan en la carpeta "reportes" del directorio de trabajo.
 */
public class ReporteFinanciero implements IReportable {  // Declaración de la clase ReporteFinanciero: Reporte financiero de la EAAB, exportable a PDF y CSV (Necesidad 9)

    private static final String CARPETA_SALIDA = "reportes"; // Carpeta donde se escriben los archivos exportados.
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); // Fecha legible.

    private String codigo;                    // Identificador único del reporte.
    private LocalDateTime fechaGeneracion;    // Momento de generación (no futuro).
    private String autor;                     // Funcionario que lo genera.
    private BigDecimal ingresosFacturacion = BigDecimal.ZERO; // Total facturado.
    private BigDecimal carteraPendiente = BigDecimal.ZERO;    // Total por cobrar.
    private BigDecimal subsidiosOtorgados = BigDecimal.ZERO;  // Total de subsidios concedidos.
    private BigDecimal multasRecaudadas = BigDecimal.ZERO;    // Total de multas cobradas.
    private String contenido = "";            // Texto generado del reporte.

    /** Constructor vacío (para la capa DAO). */
    public ReporteFinanciero() {  // Constructor: Constructor vacío (para la capa DAO)
        // Sin instrucciones: los montos inician en cero.
    }

    /** Constructor completo; valida cada dato mediante los setters. */
    public ReporteFinanciero(String codigo, LocalDateTime fecha, String autor, BigDecimal ingresos,  // Constructor: Constructor completo; valida cada dato mediante los setters
                             BigDecimal cartera, BigDecimal subsidios, BigDecimal multas) {  // Continuación de la instrucción de la línea anterior.
        setCodigo(codigo);                 // Código obligatorio.
        setFechaGeneracion(fecha);         // Fecha no futura.
        setAutor(autor);                   // Autor obligatorio.
        setIngresosFacturacion(ingresos);  // Monto >= 0.
        setCarteraPendiente(cartera);      // Monto >= 0.
        setSubsidiosOtorgados(subsidios);  // Monto >= 0.
        setMultasRecaudadas(multas);       // Monto >= 0.
    }

    /** Valida que un monto exista y no sea negativo. */
    private static BigDecimal validarMonto(BigDecimal valor, String nombre) {  // Método validarMonto: Valida que un monto exista y no sea negativo
        if (valor == null || valor.signum() < 0) { // Regla: no existen montos nulos ni negativos en el reporte.
            throw new IllegalArgumentException(nombre + " no puede ser nulo ni negativo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        return valor.setScale(2, RoundingMode.HALF_UP); // Se normaliza a 2 decimales.
    }

    /** @return el código del reporte. */
    public String getCodigo() {  // Getter: devuelve codigo
        return codigo; // Lectura simple.
    }

    /** Asigna el código (obligatorio). */
    public void setCodigo(String codigo) {  // Método setCodigo: Asigna el código (obligatorio)
        if (codigo == null || codigo.trim().isEmpty()) { // Regla: código obligatorio.
            throw new IllegalArgumentException("El código del reporte es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.codigo = codigo.trim(); // Normaliza espacios.
    }

    /** @return la fecha de generación. */
    public LocalDateTime getFechaGeneracion() {  // Getter: devuelve fecha generacion
        return fechaGeneracion; // Lectura simple.
    }

    /** Asigna la fecha de generación (no futura). */
    public void setFechaGeneracion(LocalDateTime fecha) {  // Método setFechaGeneracion: Asigna la fecha de generación (no futura)
        if (fecha == null || fecha.isAfter(LocalDateTime.now())) { // Regla: no se genera un reporte en el futuro.
            throw new IllegalArgumentException("La fecha de generación es obligatoria y no puede ser futura.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.fechaGeneracion = fecha; // Asignación tras validar.
    }

    /** @return el autor. */
    public String getAutor() {  // Getter: devuelve autor
        return autor; // Lectura simple.
    }

    /** Asigna el autor (funcionario de la empresa, obligatorio). */
    public void setAutor(String autor) {  // Método setAutor: Asigna el autor (funcionario de la empresa, obligatorio)
        if (autor == null || autor.trim().isEmpty()) { // Regla: todo reporte tiene responsable.
            throw new IllegalArgumentException("El autor del reporte es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.autor = autor.trim(); // Normaliza espacios.
    }

    /** @return los ingresos por facturación. */
    public BigDecimal getIngresosFacturacion() {  // Getter: devuelve ingresos facturacion
        return ingresosFacturacion; // Lectura simple.
    }

    /** Asigna los ingresos por facturación (>= 0). */
    public void setIngresosFacturacion(BigDecimal val) {  // Método setIngresosFacturacion: Asigna los ingresos por facturación (>= 0)
        this.ingresosFacturacion = validarMonto(val, "Los ingresos por facturación"); // Valida y asigna.
    }

    /** @return la cartera pendiente. */
    public BigDecimal getCarteraPendiente() {  // Getter: devuelve cartera pendiente
        return carteraPendiente; // Lectura simple.
    }

    /** Asigna la cartera pendiente (>= 0). */
    public void setCarteraPendiente(BigDecimal val) {  // Método setCarteraPendiente: Asigna la cartera pendiente (>= 0)
        this.carteraPendiente = validarMonto(val, "La cartera pendiente"); // Valida y asigna.
    }

    /** @return los subsidios otorgados. */
    public BigDecimal getSubsidiosOtorgados() {  // Getter: devuelve subsidios otorgados
        return subsidiosOtorgados; // Lectura simple.
    }

    /** Asigna los subsidios otorgados (>= 0). */
    public void setSubsidiosOtorgados(BigDecimal val) {  // Método setSubsidiosOtorgados: Asigna los subsidios otorgados (>= 0)
        this.subsidiosOtorgados = validarMonto(val, "Los subsidios otorgados"); // Valida y asigna.
    }

    /** @return las multas recaudadas. */
    public BigDecimal getMultasRecaudadas() {  // Getter: devuelve multas recaudadas
        return multasRecaudadas; // Lectura simple.
    }

    /** Asigna las multas recaudadas (>= 0). */
    public void setMultasRecaudadas(BigDecimal val) {  // Método setMultasRecaudadas: Asigna las multas recaudadas (>= 0)
        this.multasRecaudadas = validarMonto(val, "Las multas recaudadas"); // Valida y asigna.
    }

    /** @return el último contenido generado (vacío si aún no se ha llamado a generarContenido()). */
    public String getContenido() {  // Getter: devuelve contenido
        return contenido; // Lectura simple.
    }

    /** Recaudo neto = ingresos por facturación - cartera pendiente. */
    public BigDecimal calcularRecaudoNeto() {  // Método calcularRecaudoNeto: Recaudo neto = ingresos por facturación - cartera pendiente
        return ingresosFacturacion.subtract(carteraPendiente).setScale(2, RoundingMode.HALF_UP); // Resta de dos montos.
    }

    /** Índice de cartera = cartera pendiente / ingresos (0 si no hay ingresos, para evitar dividir por cero). */
    public double calcularIndiceCartera() {  // Método calcularIndiceCartera: Índice de cartera = cartera pendiente / ingresos (0 si no hay ingresos, para evitar dividir por cero)
        if (ingresosFacturacion.signum() == 0) { // Sin facturación no hay índice que calcular.
            return 0.0;  // Devuelve el valor de 0.0
        }
        return carteraPendiente.divide(ingresosFacturacion, 4, RoundingMode.HALF_UP).doubleValue(); // 4 decimales de precisión.
    }

    /** Construye el texto del reporte (una línea por dato) y lo guarda en el atributo contenido. */
    @Override
    public String generarContenido() {  // Método generarContenido: Construye el texto del reporte (una línea por dato) y lo guarda en el atributo contenido
        StringBuilder sb = new StringBuilder(); // StringBuilder concatena texto de forma eficiente.
        sb.append("REPORTE FINANCIERO - EAAB (SGRAB)\n");  // Invoca sb.append(...)
        sb.append("Código: ").append(codigo).append('\n');  // Invoca sb.append(...)
        sb.append("Fecha de generación: ").append(fechaGeneracion == null ? "N/D" : fechaGeneracion.format(FORMATO)).append('\n');  // Invoca sb.append(...)
        sb.append("Autor: ").append(autor).append('\n');  // Invoca sb.append(...)
        sb.append("Ingresos por facturación: $").append(ingresosFacturacion).append('\n');  // Invoca sb.append(...)
        sb.append("Cartera pendiente: $").append(carteraPendiente).append('\n');  // Invoca sb.append(...)
        sb.append("Subsidios otorgados: $").append(subsidiosOtorgados).append('\n');  // Invoca sb.append(...)
        sb.append("Multas recaudadas: $").append(multasRecaudadas).append('\n');  // Invoca sb.append(...)
        sb.append("Recaudo neto: $").append(calcularRecaudoNeto()).append('\n');  // Invoca sb.append(...)
        sb.append("Índice de cartera: ").append(String.format("%.2f %%", calcularIndiceCartera() * 100)).append('\n');  // Invoca sb.append(...)
        this.contenido = sb.toString(); // Se conserva para consulta posterior.
        return contenido;  // Devuelve el valor de contenido
    }

    /** Prepara la carpeta de salida y devuelve la ruta del archivo con la extensión pedida. */
    private Path rutaSalida(String extension) throws IOException {  // Método rutaSalida: Prepara la carpeta de salida y devuelve la ruta del archivo con la extensión pedida
        Path carpeta = Paths.get(CARPETA_SALIDA);     // Carpeta "reportes".
        Files.createDirectories(carpeta);             // La crea si no existe (no falla si ya existe).
        return carpeta.resolve(codigo + "." + extension); // Ej.: reportes/REP-001.csv
    }

    /** Escapa un texto para la sintaxis de cadenas de PDF: \ ( ) deben ir precedidos de barra invertida. */
    private static String escaparPdf(String texto) {  // Método escaparPdf: Escapa un texto para la sintaxis de cadenas de PDF
        return texto.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)"); // Orden importante: primero la barra.
    }

    /** Exporta el reporte a CSV (separado por comas, con BOM para que Excel reconozca las tildes). */
    @Override
    public void exportarCSV() {  // Método exportarCSV: Exporta el reporte a CSV (separado por comas, con BOM para que Excel reconozca las tildes)
        StringBuilder sb = new StringBuilder("\uFEFF"); // \uFEFF = BOM UTF-8: marca que indica la codificación a Excel.
        sb.append("campo,valor\n"); // Encabezado.
        sb.append("codigo,").append(codigo).append('\n');  // Invoca sb.append(...)
        sb.append("fecha_generacion,").append(fechaGeneracion == null ? "" : fechaGeneracion.format(FORMATO)).append('\n');  // Invoca sb.append(...)
        sb.append("autor,\"").append(autor == null ? "" : autor.replace("\"", "\"\"")).append("\"\n"); // Comillas dobles se duplican en CSV.
        sb.append("ingresos_facturacion,").append(ingresosFacturacion).append('\n');  // Invoca sb.append(...)
        sb.append("cartera_pendiente,").append(carteraPendiente).append('\n');  // Invoca sb.append(...)
        sb.append("subsidios_otorgados,").append(subsidiosOtorgados).append('\n');  // Invoca sb.append(...)
        sb.append("multas_recaudadas,").append(multasRecaudadas).append('\n');  // Invoca sb.append(...)
        sb.append("recaudo_neto,").append(calcularRecaudoNeto()).append('\n');  // Invoca sb.append(...)
        sb.append("indice_cartera,").append(calcularIndiceCartera()).append('\n');  // Invoca sb.append(...)
        try {  // Bloque protegido: si algo falla, se captura abajo
            Files.write(rutaSalida("csv"), sb.toString().getBytes(StandardCharsets.UTF_8)); // Escribe el archivo.
        } catch (IOException e) { // Error de disco o permisos.
            throw new IllegalStateException("No se pudo exportar el CSV: " + e.getMessage(), e); // Se convierte en excepción no verificada.
        }
    }

    /**
     * Exporta el reporte a PDF generando directamente la estructura del formato:
     * 1) catálogo, 2) árbol de páginas, 3) página, 4) fuente Helvetica, 5) contenido (texto),
     * seguido de la tabla "xref" con la posición en bytes de cada objeto y el "trailer".
     */
    @Override
    public void exportarPDF() {  // Método exportarPDF: Exporta el reporte a PDF generando directamente la estructura del formato
        String[] lineas = generarContenido().split("\n"); // Cada línea del reporte será una línea de texto del PDF.
        StringBuilder texto = new StringBuilder("BT /F1 11 Tf 50 780 Td 16 TL\n"); // BT: inicia texto; Tf: fuente y tamaño; Td: posición; TL: interlineado.
        for (String linea : lineas) { // Una instrucción Tj (mostrar texto) por línea.
            texto.append('(').append(escaparPdf(linea)).append(") Tj T*\n"); // T*: salta a la línea siguiente.
        }
        texto.append("ET\n"); // ET: termina el bloque de texto.
        Charset win1252 = Charset.forName("windows-1252"); // Codificación que entiende la fuente estándar (incluye tildes).
        byte[] flujo = texto.toString().getBytes(win1252); // Contenido de la página en bytes.

        String[] objetos = new String[5]; // Los cinco objetos del documento.
        objetos[0] = "<< /Type /Catalog /Pages 2 0 R >>";                                  // 1: catálogo raíz.
        objetos[1] = "<< /Type /Pages /Kids [3 0 R] /Count 1 >>";                          // 2: lista de páginas.
        objetos[2] = "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] "  // Instrucción: objetos[2] = "<< /Type /Page /Parent 2 0 R /MediaBox [0 0...
                + "/Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>";             // 3: página A4 (595 x 842 puntos).
        objetos[3] = "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>"; // 4: fuente.

        try {  // Bloque protegido: si algo falla, se captura abajo
            ByteArrayOutputStream pdf = new ByteArrayOutputStream(); // Documento en construcción.
            int[] posiciones = new int[6]; // Posición en bytes de cada objeto (índice 1 a 5).
            pdf.write("%PDF-1.4\n".getBytes(StandardCharsets.ISO_8859_1)); // Cabecera con la versión del formato.
            for (int i = 0; i < 4; i++) { // Escribe los objetos 1 a 4.
                posiciones[i + 1] = pdf.size(); // Se anota dónde empieza el objeto.
                pdf.write(((i + 1) + " 0 obj\n" + objetos[i] + "\nendobj\n").getBytes(StandardCharsets.ISO_8859_1));  // Invoca pdf.write(...)
            }
            posiciones[5] = pdf.size(); // El objeto 5 (contenido) lleva un flujo binario.
            pdf.write(("5 0 obj\n<< /Length " + flujo.length + " >>\nstream\n").getBytes(StandardCharsets.ISO_8859_1));  // Invoca pdf.write(...)
            pdf.write(flujo);                                                             // Texto de la página.
            pdf.write("endstream\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));       // Cierre del flujo.
            int inicioXref = pdf.size(); // Posición de la tabla de referencias cruzadas.
            StringBuilder xref = new StringBuilder("xref\n0 6\n0000000000 65535 f \n"); // Entrada 0: objeto libre.
            for (int i = 1; i <= 5; i++) { // Una entrada de 20 caracteres por objeto.
                xref.append(String.format("%010d 00000 n \n", posiciones[i]));  // Invoca xref.append(...)
            }
            xref.append("trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n").append(inicioXref).append("\n%%EOF\n"); // Cierre.
            pdf.write(xref.toString().getBytes(StandardCharsets.ISO_8859_1));  // Invoca pdf.write(...)
            Files.write(rutaSalida("pdf"), pdf.toByteArray()); // Escribe el archivo.
        } catch (IOException e) { // Error de disco o permisos.
            throw new IllegalStateException("No se pudo exportar el PDF: " + e.getMessage(), e); // Excepción no verificada.
        }
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "ReporteFinanciero [codigo=" + codigo + ", fecha=" + fechaGeneracion + ", autor=" + autor  // Devuelve: "ReporteFinanciero [codigo=" + codigo + ", fecha=" + fechaGeneracion + "...
                + ", ingresos=" + ingresosFacturacion + ", cartera=" + carteraPendiente + "]"; // Resumen.
    }
}
