package modelo;
                                              // Paquete único

import java.time.LocalDate;                                            // Fecha del pago

/** Pago aplicado a una factura. */
public class Pago {

    // ---------- ATRIBUTOS ----------
    private String codigo;                                             // Único
    private LocalDate fecha;                                           // No futura
    private double valor;                                              // > 0
    private MetodoPago metodoPago;                                     // Enum
    private String referencia;                                         // Comprobante

    // ---------- CONSTRUCTORES ----------
    public Pago() { }

    public Pago(String codigo, LocalDate fecha, double valor,
                MetodoPago metodo, String referencia) {
        setCodigo(codigo);
        setFecha(fecha);
        setValor(valor);
        setMetodoPago(metodo);
        setReferencia(referencia);
    }

    // ---------- GETTERS / SETTERS ----------
    public String getCodigo() { return codigo; }
    public void setCodigo(String c) {
        if (c == null || c.trim().isEmpty())
            throw new IllegalArgumentException("Código de pago obligatorio.");
        this.codigo = c.trim();
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate f) {
        if (f != null && f.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de pago no puede ser futura.");
        this.fecha = f;
    }

    public double getValor() { return valor; }
    public void setValor(double v) {
        if (v <= 0) throw new IllegalArgumentException("El valor debe ser > 0.");
        this.valor = v;
    }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago m) { this.metodoPago = m; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String r) { this.referencia = r; }

    @Override
    public String toString() {
        return "Pago[" + codigo + ", $" + valor + ", " + metodoPago + "]";
    }
}