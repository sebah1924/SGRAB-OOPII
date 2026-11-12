package modelo;
                                   // Paquete único del modelo

/** Ciclo de vida de una sanción por derroche de agua. */
public enum EstadoSancion {
    IMPUESTA,    // Sanción notificada al usuario
    APELADA,     // El usuario apela la sanción
    CONFIRMADA,  // Sanción mantenida tras rechazo de apelación
    EJECUTADA,   // Sanción efectiva (multa pagada o servicio suspendido)
    REVOCADA     // Sanción anulada por apelación aceptada
}