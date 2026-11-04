package modelo; // Paquete del dominio.

/**
 * Canales oficiales por los que la EAAB notifica a los usuarios durante la crisis (Necesidad 3).
 * Cada usuario tiene un canal preferido.
 */
public enum CanalNotificacion {  // Declaración de la enumeración CanalNotificacion: Canales oficiales por los que la EAAB notifica a los usuarios durante la crisis (Necesidad 3)
    EMAIL,    // Correo electrónico.
    SMS,      // Mensaje de texto.
    PUSH,     // Notificación push a la aplicación móvil.
    LLAMADA   // Llamada telefónica automatizada.
}
