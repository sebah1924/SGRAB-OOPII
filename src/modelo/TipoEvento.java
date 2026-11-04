package modelo; // Paquete del dominio.

/**
 * Clasifica los eventos que disparan una notificación (patrón Observer).
 * Usar un enum da tipado fuerte y evita errores de texto (ej.: "FACTURA_EMITIDA" mal escrito).
 */
public enum TipoEvento {  // Declaración de la enumeración TipoEvento: Clasifica los eventos que disparan una notificación (patrón Observer)
    FACTURA_EMITIDA,        // Se emite una factura: aviso informativo con número y valor.
    FACTURA_VENCIDA,        // Una factura vence sin pago: el usuario pasa a estado EN_DEUDA.
    SUSPENSION_INMINENTE,   // El usuario acumula dos o más facturas vencidas: aviso previo.
    SUSPENSION_EFECTIVA,    // Se suspende el servicio: instrucciones de pago y reactivación.
    CORTE_PROGRAMADO,       // Se programa un corte para el sector del usuario (racionamiento).
    CALENDARIO_MODIFICADO   // Se agrega un turno, se cambia una restricción o se suspende el racionamiento.
}
