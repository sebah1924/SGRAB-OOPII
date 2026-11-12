package modelo;
                                   // Paquete único del modelo

/** Enumeración con los estados posibles del pago de una factura. */
public enum EstadoPago {
    PENDIENTE,        // Aún dentro del plazo, no pagada
    PAGADA,           // Los pagos cubren el total
    VENCIDA,          // No pagada a tiempo (fecha de vencimiento superada)
    EN_ACUERDO_PAGO   // El usuario pactó pagar en cuotas
}