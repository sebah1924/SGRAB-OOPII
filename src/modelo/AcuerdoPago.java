package modelo;
                                              // Paquete único

import java.time.LocalDate;                                            // Fechas
import java.util.ArrayList;                                            // Lista
import java.util.List;                                                 // Interfaz

/** Acuerdo de pago en cuotas para una factura vencida. */
public class AcuerdoPago {

    // ---------- ATRIBUTOS ----------
    private String codigo;                                             // Único
    private Factura factura;                                           // Factura asociada
    private Usuario usuario;                                           // Usuario deudor
    private LocalDate fechaAcuerdo;                                    // Fecha de creación
    private int numeroCuotas;                                          // Cantidad de cuotas
    private double valorTotal;                                         // Valor total del acuerdo
    private final List<CuotaAcuerdo> cuotas = new ArrayList<>();       // Cuotas generadas

    // ---------- CONSTRUCTORES ----------
    public AcuerdoPago() { }

    public AcuerdoPago(String codigo, Factura factura, int numeroCuotas) {
        setCodigo(codigo);
        setFactura(factura);
        setNumeroCuotas(numeroCuotas);
        setUsuario(factura != null ? factura.getUsuario() : null);
        this.fechaAcuerdo = LocalDate.now();
        this.valorTotal = factura != null ? factura.getSaldoPendiente() : 0;
        generarCuotas();                                               // Calcula cuotas
    }

    // ---------- GETTERS / SETTERS ----------
    public String getCodigo() { return codigo; }
    public void setCodigo(String c) {
        if (c == null || c.trim().isEmpty())
            throw new IllegalArgumentException("Código de acuerdo obligatorio.");
        this.codigo = c.trim();
    }

    public Factura getFactura() { return factura; }
    public void setFactura(Factura f) {
        if (f == null) throw new IllegalArgumentException("Factura obligatoria.");
        this.factura = f;
    }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario u) { this.usuario = u; }

    public LocalDate getFechaAcuerdo() { return fechaAcuerdo; }
    public void setFechaAcuerdo(LocalDate f) { this.fechaAcuerdo = f; }

    public int getNumeroCuotas() { return numeroCuotas; }
    public void setNumeroCuotas(int n) {
        if (n < 1) throw new IllegalArgumentException("El número de cuotas debe ser >= 1.");
        this.numeroCuotas = n;
    }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double v) { this.valorTotal = v; }

    public List<CuotaAcuerdo> getCuotas() { return cuotas; }

    // ---------- MÉTODOS ----------
    /** Genera las cuotas dividiendo el valor total entre las cuotas. */
    public void generarCuotas() {
        if (valorTotal <= 0 || numeroCuotas <= 0) return;
        cuotas.clear();                                                // Reinicia lista
        double valorCuota = valorTotal / numeroCuotas;                 // Valor por cuota
        LocalDate hoy = LocalDate.now();
        for (int i = 1; i <= numeroCuotas; i++) {                      // Crea cada cuota
            CuotaAcuerdo cuota = new CuotaAcuerdo(i, valorCuota, hoy.plusMonths(i));
            cuotas.add(cuota);
        }
    }

    /** Suma lo pagado en cuotas. */
    public double totalPagado() {
        double t = 0;
        for (CuotaAcuerdo c : cuotas) if (c.isPagada()) t += c.getValor();
        return t;
    }

    /** Verifica si el acuerdo está completo. */
    public boolean estaCompleto() { return totalPagado() >= valorTotal; }
}