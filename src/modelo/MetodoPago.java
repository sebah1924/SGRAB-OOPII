package modelo; // Paquete del dominio.

/**
 * Métodos con los que un usuario puede pagar una factura (Necesidad 8).
 */
public enum MetodoPago {  // Declaración de la enumeración MetodoPago: Métodos con los que un usuario puede pagar una factura (Necesidad 8)
    TARJETA,        // Tarjeta débito o crédito.
    TRANSFERENCIA,  // Transferencia bancaria.
    EFECTIVO,       // Pago en efectivo en un punto autorizado.
    PSE             // Pagos Seguros en Línea (botón de pago de los bancos colombianos).
}
