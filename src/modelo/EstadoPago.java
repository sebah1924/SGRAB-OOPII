package modelo; // Paquete del dominio.

/**
 * Estados de pago de una factura.
 * Se actualizan automáticamente según los pagos recibidos y la fecha de vencimiento
 * (Necesidad 8 y 9 del enunciado).
 */
public enum EstadoPago {  // Declaración de la enumeración EstadoPago: Estados de pago de una factura
    PENDIENTE,        // Aún dentro del plazo y sin pagar completamente (ej.: factura recién emitida).
    PAGADA,           // Los pagos registrados cubren el valor total (ej.: pagada antes del vencimiento).
    VENCIDA,          // Pasó la fecha de vencimiento sin que los pagos cubran el total.
    EN_ACUERDO_PAGO   // El usuario pactó con la EAAB pagar en cuotas.
}
