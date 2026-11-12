package modelo;
                                              // Paquete único

import java.time.LocalDate;                                            // Fechas
import java.util.ArrayList;                                            // Lista
import java.util.List;                                                 // Interfaz
/**
 * Entidad que representa una factura emitida.
 * Contiene los consumos, pagos, sanciones y desincentivos asociados.
 */


public class Factura {
    private String numero;
    private Periodo periodo;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private double valorTotal;
    private Usuario usuario;
    private EstadoPago estadoPago;
    private final List<Consumo> consumos = new ArrayList<>();
    private final List<Pago> pagos = new ArrayList<>();
    private final List<Sancion> sanciones = new ArrayList<>();
    private Desincentivo desincentivo;

    public Factura() { }

    public Factura(String numero, Periodo periodo, LocalDate emision, LocalDate vencimiento, Usuario usuario) {
        setNumero(numero);
        setPeriodo(periodo);
        setFechaEmision(emision);
        setFechaVencimiento(vencimiento);
        setUsuario(usuario);
        this.estadoPago = EstadoPago.PENDIENTE;
    }

    // Getters y Setters (Mismos de antes...)
    public String getNumero() { return numero; }
    public void setNumero(String n) {
        if (n == null || n.trim().isEmpty()) throw new IllegalArgumentException("Número obligatorio.");
        this.numero = n;
    }
    public Periodo getPeriodo() { return periodo; }
    public void setPeriodo(Periodo p) { this.periodo = p; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate f) { this.fechaEmision = f; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate f) {
        if (fechaEmision != null && f != null && !f.isAfter(fechaEmision))
            throw new IllegalArgumentException("Vencimiento debe ser posterior a emisión.");
        this.fechaVencimiento = f;
    }
    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double v) { this.valorTotal = v; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario u) { this.usuario = u; }
    public EstadoPago getEstadoPago() { return estadoPago; }
    public void setEstadoPago(EstadoPago e) { this.estadoPago = e; }
    public List<Consumo> getConsumos() { return consumos; }
    public List<Pago> getPagos() { return pagos; }
    public List<Sancion> getSanciones() { return sanciones; }
    public Desincentivo getDesincentivo() { return desincentivo; }
    public void setDesincentivo(Desincentivo d) { this.desincentivo = d; }

    public void agregarConsumo(Consumo c) { consumos.add(c); }
    public void agregarPago(Pago p) { pagos.add(p); }
    public void agregarSancion(Sancion s) { sanciones.add(s); }

    public double getTotalPagado() {
        double t = 0;
        for (Pago p : pagos) t += p.getValor();
        return t;
    }

    public double getSaldoPendiente() {
        double saldo = valorTotal - getTotalPagado();
        return saldo < 0 ? 0 : saldo; // Nunca puede ser negativo
    }

    public double consumoTotal() {
        double t = 0;
        for (Consumo c : consumos) t += c.getVolumen();
        return t;
    }

    public double totalSancionesEconomicas() {
        double t = 0;
        for (Sancion s : sanciones) {
            if (s.getTipo() == TipoSancion.ECONOMICA) t += s.getValorMulta();
        }
        return t;
    }

    public double calcularValorTotal(Tarifa tarifa) {
        double consumoTotal = consumoTotal();
        
        // Si el consumo es 0, la base es 0
        if (consumoTotal <= 0 && sanciones.isEmpty() && desincentivo == null) {
            this.valorTotal = 0;
            return 0;
        }

        double base = 0;
        if (usuario != null && usuario.getEstrategia() != null) {
            // Creamos un consumo consolidado para pasarle a la estrategia
            Consumo consolidado = new Consumo(consumoTotal, LocalDate.now(), TipoLectura.REAL);
            consolidado.setTarifaAplicada(tarifa);
            
            // Esto aplica cargo fijo, variable y subsidio/contribución
            base = usuario.getEstrategia().calcularValor(usuario, consolidado, tarifa);
        }
        
        // Desincentivo (solo residenciales)
        double valorDes = 0;
        if (desincentivo != null && usuario instanceof Residencial) {
            valorDes = desincentivo.calcular(consumoTotal);
        }
        
        // Total final
        this.valorTotal = base + valorDes + totalSancionesEconomicas();
        return this.valorTotal;
    }

        public void actualizarEstado() {
        // Se suma 0.01 para evitar el error de precisión de los double.
        // Si el pago es igual o supera el total (con un margen de 1 centavo), se marca PAGADA.
        if (getTotalPagado() + 0.01 >= valorTotal) {
            estadoPago = EstadoPago.PAGADA;
        } else if (fechaVencimiento != null && LocalDate.now().isAfter(fechaVencimiento)) {
            estadoPago = EstadoPago.VENCIDA;
        } else {
            estadoPago = EstadoPago.PENDIENTE;
        }
    }
    
    /** Indica si la factura está vencida. */
    public boolean estaVencida() { return estadoPago == EstadoPago.VENCIDA; }

    @Override
    public String toString() {
        return "Factura[" + numero + ", $" + valorTotal + ", " + estadoPago + "]";
    }
}