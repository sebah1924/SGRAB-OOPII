package modelo;
                                              // Paquete único

import java.time.LocalDate;                                            // Fecha de generación

/** Reporte financiero exportable a PDF y CSV. */
public class Reporte {

    // ---------- ATRIBUTOS ----------
    private String codigo;                                             // Único
    private LocalDate fechaGeneracion;                                 // No futura
    private String autor;                                              // Funcionario
    private double ingresosFacturacion;                                // Ingresos
    private double carteraPendiente;                                   // Cartera
    private double subsidiosOtorgados;                                 // Subsidios
    private double multasRecaudadas;                                   // Multas

    // ---------- CONSTRUCTORES ----------
    public Reporte() { }

    public Reporte(String codigo, String autor) {
        setCodigo(codigo);
        setAutor(autor);
        this.fechaGeneracion = LocalDate.now();
    }

    // ---------- GETTERS / SETTERS ----------
    public String getCodigo() { return codigo; }
    public void setCodigo(String c) {
        if (c == null || c.trim().isEmpty())
            throw new IllegalArgumentException("Código de reporte obligatorio.");
        this.codigo = c.trim();
    }

    public LocalDate getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDate f) {
        if (f != null && f.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Fecha de generación no puede ser futura.");
        this.fechaGeneracion = f;
    }

    public String getAutor() { return autor; }
    public void setAutor(String a) {
        if (a == null || a.trim().isEmpty())
            throw new IllegalArgumentException("Autor obligatorio.");
        this.autor = a.trim();
    }

    public double getIngresosFacturacion() { return ingresosFacturacion; }
    public void setIngresosFacturacion(double v) { this.ingresosFacturacion = v; }

    public double getCarteraPendiente() { return carteraPendiente; }
    public void setCarteraPendiente(double v) { this.carteraPendiente = v; }

    public double getSubsidiosOtorgados() { return subsidiosOtorgados; }
    public void setSubsidiosOtorgados(double v) { this.subsidiosOtorgados = v; }

    public double getMultasRecaudadas() { return multasRecaudadas; }
    public void setMultasRecaudadas(double v) { this.multasRecaudadas = v; }

    // ---------- MÉTODOS ----------
    /** Exporta el reporte a CSV (simulado). */
    public String exportarCSV() {
        return "codigo,autor,fecha,ingresos,cartera,subsidios,multas\n" +
               codigo + "," + autor + "," + fechaGeneracion + "," +
               ingresosFacturacion + "," + carteraPendiente + "," +
               subsidiosOtorgados + "," + multasRecaudadas;
    }

    /** Exporta a PDF (simulado con toString formateado). */
    public String exportarPDF() {
        return "=== REPORTE " + codigo + " ===\n" +
               "Autor: " + autor + "\n" +
               "Fecha: " + fechaGeneracion + "\n" +
               "Ingresos: $" + ingresosFacturacion + "\n" +
               "Cartera: $" + carteraPendiente + "\n" +
               "Subsidios: $" + subsidiosOtorgados + "\n" +
               "Multas: $" + multasRecaudadas + "\n";
    }

    @Override
    public String toString() {
        return "Reporte[" + codigo + ", " + fechaGeneracion + "]";
    }
}