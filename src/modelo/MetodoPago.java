package modelo;
                                   // Paquete único del modelo

/** Métodos de pago aceptados por la EAAB. */
public enum MetodoPago {
    TARJETA,        // Pago con tarjeta débito/crédito
    TRANSFERENCIA,  // Transferencia bancaria
    EFECTIVO,       // Pago en efectivo
    PSE             // Pago en línea por PSE
}