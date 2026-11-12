package modelo;
                                   // Paquete único del modelo

/** Eventos del negocio que disparan una notificación automática al usuario. */
public enum EventoNotificacion {
    EMISION_FACTURA,          // Se emite una factura (informativo: número y valor)
    FACTURA_VENCIDA,          // Una factura vence sin ser pagada
    SUSPENSION_INMINENTE,     // El usuario acumula 2+ facturas vencidas
    SUSPENSION_EFECTIVA,      // Se suspende el servicio (con instrucciones)
    CORTE_PROGRAMADO,         // Se programa un corte para el sector del usuario
    CAMBIO_CALENDARIO,        // Se modifica el calendario de racionamiento
    SANCION_IMPUESTA,         // Se impone una sanción
    PAGO_REGISTRADO           // Se registra un pago
}