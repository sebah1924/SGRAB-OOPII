package modelo;
                                              // Paquete único

import java.time.LocalDate;                                            // Fechas

/** Cuota individual dentro de un acuerdo de pago. */
public class CuotaAcuerdo {

    // ---------- ATRIBUTOS ----------
    private int numero;                                                // Número de cuota
    private double valor;                                              // Valor de la cuota
    private LocalDate fechaVencimiento;                                // Cuándo vence
    private boolean pagada;                                            // ¿Pagada?
    private LocalDate fechaPago;                                       // Fecha real de pago

    // ---------- CONSTRUCTORES ----------
    public CuotaAcuerdo() { }

    public CuotaAcuerdo(int numero, double valor, LocalDate fechaVencimiento) {
        setNumero(numero);
        setValor(valor);
        setFechaVencimiento(fechaVencimiento);
        this.pagada = false;
    }

    // ---------- GETTERS / SETTERS ----------
    public int getNumero() { return numero; }
    public void setNumero(int n) {
        if (n < 1) throw new IllegalArgumentException("Número de cuota >= 1.");
        this.numero = n;
    }

    public double getValor() { return valor; }
    public void setValor(double v) {
        if (v <= 0) throw new IllegalArgumentException("Valor de cuota > 0.");
        this.valor = v;
    }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate f) { this.fechaVencimiento = f; }

    public boolean isPagada() { return pagada; }
    public void setPagada(boolean p) { this.pagada = p; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate f) { this.fechaPago = f; }

    // ---------- MÉTODOS ----------
    /** Marca la cuota como pagada. */
    public void registrarPago(LocalDate fecha) {
        if (fecha.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de pago no puede ser futura.");
        this.pagada = true;
        this.fechaPago = fecha;
    }

    /** Indica si la cuota está atrasada. */
    public boolean estaVencida() {
        return !pagada && fechaVencimiento != null &&
               LocalDate.now().isAfter(fechaVencimiento);
    }

    @Override
    public String toString() {
        return "Cuota[" + numero + ", $" + valor + ", " + (pagada ? "PAGADA" : "PENDIENTE") + "]";
    }
}