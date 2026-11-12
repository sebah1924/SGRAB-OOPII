package modelo;
                                   // Paquete único del modelo

/** Enumeración con los estados posibles del servicio de acueducto. */
public enum EstadoServicio {
    ACTIVO,        // El usuario recibe el servicio normalmente
    EN_DEUDA,      // El usuario tiene facturas vencidas
    SUSPENDIDO,    // El servicio está temporalmente detenido (2+ facturas vencidas)
    CORTADO        // Servicio interrumpido por racionamiento o sanción
}