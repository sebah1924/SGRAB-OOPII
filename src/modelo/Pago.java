package modelo; // Paquete del dominio.

import java.math.BigDecimal; // Dinero con precisión exacta.
import java.time.LocalDate;  // Fecha del pago.

/**
 * Pago realizado sobre una factura (Necesidad 8).
 *
 * REGLAS DE NEGOCIO (enunciado, "Qué contiene una factura"):
 *  - código obligatorio y único; fecha NO futura; valor MAYOR que cero;
 *  - método de pago: tarjeta, transferencia, efectivo o PSE;
 *  - referencia: número de comprobante o transacción.
 *
 * Relación del diagrama: una Factura se COMPONE de pagos (composición 1 a 0..*): un pago no tiene
 * sentido sin la factura que paga. Se registra con Factura.agregarPago(), que actualiza el estado de la factura.
 */
public class Pago {  // Declaración de la clase Pago: Pago realizado sobre una factura (Necesidad 8)

    private String codigo;        // Identificador único del pago.
    private LocalDate fecha;      // Día en que se pagó (no futuro).
    private BigDecimal valor;     // Monto pagado (> 0).
    private MetodoPago metodoPago; // TARJETA, TRANSFERENCIA, EFECTIVO o PSE.
    private String referencia;    // Número de comprobante o transacción.
    private Factura factura;      // Factura que se está pagando.

    /** Constructor vacío (para la capa DAO). */
    public Pago() {  // Constructor: Constructor vacío (para la capa DAO)
        // Sin instrucciones.
    }

    /** Constructor completo; valida mediante los setters. */
    public Pago(String codigo, LocalDate fecha, BigDecimal valor, MetodoPago metodo, String ref, Factura factura) {  // Constructor: Constructor completo; valida mediante los setters
        setCodigo(codigo);      // Código obligatorio.
        setFecha(fecha);        // Fecha no futura.
        setValor(valor);        // Valor > 0.
        setMetodoPago(metodo);  // Método obligatorio.
        setReferencia(ref);     // Referencia obligatoria.
        setFactura(factura);    // Factura obligatoria.
    }

    /** @return el código del pago. */
    public String getCodigo() {  // Getter: devuelve codigo
        return codigo; // Lectura simple.
    }

    /** Asigna el código (obligatorio). */
    public void setCodigo(String codigo) {  // Método setCodigo: Asigna el código (obligatorio)
        if (codigo == null || codigo.trim().isEmpty()) { // Regla: código obligatorio.
            throw new IllegalArgumentException("El código del pago es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.codigo = codigo.trim(); // Normaliza espacios.
    }

    /** @return la fecha del pago. */
    public LocalDate getFecha() {  // Getter: devuelve fecha
        return fecha; // LocalDate es inmutable.
    }

    /** Asigna la fecha (no futura). */
    public void setFecha(LocalDate fecha) {  // Método setFecha: Asigna la fecha (no futura)
        if (fecha == null || fecha.isAfter(LocalDate.now())) { // Regla: no existen pagos del futuro.
            throw new IllegalArgumentException("La fecha del pago es obligatoria y no puede ser futura.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.fecha = fecha; // Asignación tras validar.
    }

    /** @return el valor pagado. */
    public BigDecimal getValor() {  // Getter: devuelve valor
        return valor; // Lectura simple.
    }

    /** Asigna el valor (mayor que cero). */
    public void setValor(BigDecimal valor) {  // Método setValor: Asigna el valor (mayor que cero)
        if (valor == null || valor.signum() <= 0) { // Regla: valor mayor que cero.
            throw new IllegalArgumentException("El valor del pago debe ser mayor que cero.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.valor = valor; // Asignación tras validar.
    }

    /** @return el método de pago. */
    public MetodoPago getMetodoPago() {  // Getter: devuelve metodo pago
        return metodoPago; // Lectura simple.
    }

    /** Asigna el método de pago (obligatorio). */
    public void setMetodoPago(MetodoPago metodo) {  // Método setMetodoPago: Asigna el método de pago (obligatorio)
        if (metodo == null) { // Regla: todo pago declara cómo se hizo.
            throw new IllegalArgumentException("El método de pago es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.metodoPago = metodo; // Asignación tras validar.
    }

    /** @return la referencia del comprobante o transacción. */
    public String getReferencia() {  // Getter: devuelve referencia
        return referencia; // Lectura simple.
    }

    /** Asigna la referencia (obligatoria). */
    public void setReferencia(String ref) {  // Método setReferencia: Asigna la referencia (obligatoria)
        if (ref == null || ref.trim().isEmpty()) { // Regla: el pago se respalda con un comprobante.
            throw new IllegalArgumentException("La referencia del pago es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.referencia = ref.trim(); // Normaliza espacios.
    }

    /** @return la factura pagada. */
    public Factura getFactura() {  // Getter: devuelve factura
        return factura; // Lectura simple.
    }

    /** Asigna la factura pagada (obligatoria). */
    public void setFactura(Factura factura) {  // Método setFactura: Asigna la factura pagada (obligatoria)
        if (factura == null) { // Regla: un pago siempre abona a una factura.
            throw new IllegalArgumentException("La factura del pago es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.factura = factura; // Asignación tras validar.
    }

    /** @return true si todos los datos del pago cumplen las reglas de negocio. */
    public boolean validarPago() {  // Método validarPago (devuelve boolean)
        return codigo != null && fecha != null && !fecha.isAfter(LocalDate.now())  // Devuelve: codigo es distinto de nulo y fecha es distinto de nulo y no fecha.isAfte...
                && valor != null && valor.signum() > 0  // Continúa la condición anterior con otra comprobación: y valor es distinto de nulo y valor (signo) es mayor que 0
                && metodoPago != null && referencia != null && !referencia.trim().isEmpty()  // Continúa la condición anterior con otra comprobación: y metodoPago es distinto de nulo y referencia es distinto de nulo y no r...
                && factura != null; // Todas las condiciones deben cumplirse a la vez.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "Pago [codigo=" + codigo + ", fecha=" + fecha + ", valor=" + valor + ", metodo=" + metodoPago  // Devuelve: "Pago [codigo=" + codigo + ", fecha=" + fecha + ", valor=" + valor + ", ...
                + ", referencia=" + referencia + "]"; // Resumen.
    }
}
