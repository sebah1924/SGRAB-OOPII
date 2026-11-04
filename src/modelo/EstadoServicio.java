package modelo; // Paquete "modelo": capa del dominio (reglas de negocio). No conoce Swing, SQL ni controladores.

/**
 * Estados posibles del servicio de acueducto de un usuario (suscriptor).
 *
 * Contexto técnico: un "enum" (enumeración) es un tipo con un conjunto CERRADO y fijo de valores.
 * Usarlo en lugar de un String evita errores de digitación ("Activo", "ACTIVO ", "activo")
 * y el compilador verifica que solo se use un valor válido (tipado fuerte).
 *
 * Fuente: enunciado, tabla "Estado del servicio" (sección Las personas y los usuarios del servicio).
 */
public enum EstadoServicio {  // Declaración de la enumeración EstadoServicio: Estados posibles del servicio de acueducto de un usuario (suscriptor)
    ACTIVO,      // El usuario recibe el servicio normalmente (ej.: un hogar que paga al día).
    EN_DEUDA,    // El usuario tiene facturas vencidas (ej.: un local que debe una factura).
    SUSPENDIDO,  // Servicio temporalmente detenido: usuario con dos o más facturas vencidas.
    CORTADO      // Servicio interrumpido por turno de racionamiento o por una sanción.
}
