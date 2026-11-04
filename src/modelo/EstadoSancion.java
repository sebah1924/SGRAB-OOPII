package modelo; // Paquete del dominio.

/**
 * Ciclo de vida de una sanción por derroche de agua (Decreto 137 de 2024).
 *
 * Flujo permitido (lo hace cumplir la clase Sancion):
 *   IMPUESTA -> APELADA -> CONFIRMADA -> EJECUTADA
 *   IMPUESTA -> EJECUTADA        (si el usuario no apela)
 *   APELADA  -> REVOCADA         (si la apelación es aceptada)
 */
public enum EstadoSancion {  // Declaración de la enumeración EstadoSancion: Ciclo de vida de una sanción por derroche de agua (Decreto 137 de 2024)
    IMPUESTA,    // Sanción notificada al usuario (ej.: multa notificada).
    APELADA,     // El usuario interpuso un recurso ante la EAAB.
    CONFIRMADA,  // La apelación fue rechazada y la sanción se mantiene.
    EJECUTADA,   // La sanción se hizo efectiva (multa pagada o servicio suspendido).
    REVOCADA     // La sanción fue anulada porque la apelación fue aceptada.
}
