package modelo; // Paquete del dominio.

/**
 * Contrato de un CANAL de notificación (correo, SMS, push, llamada).
 *
 * Contexto: cada canal implementa esta interfaz con su propia lógica (polimorfismo), y el
 * GestorNotificaciones los guarda en un Map<CanalNotificacion, INotificable> para elegir
 * dinámicamente el canal sin usar condicionales (también es una aplicación de Strategy).
 */
public interface INotificable {  // Declaración de la interfaz INotificable: Contrato de un CANAL de notificación (correo, SMS, push, llamada)

    /**
     * Envía un mensaje a un usuario por este canal.
     *
     * @param usuario destinatario del mensaje.
     * @param mensaje contenido a enviar (código, título y cuerpo).
     * @throws IllegalArgumentException si el destinatario no tiene los datos de contacto requeridos.
     */
    void notificar(Usuario usuario, Mensaje mensaje); // Contrato de envío.

    /**
     * Verifica que el usuario tenga los datos de contacto que exige el canal
     * (ej.: correo válido para EMAIL, teléfono para SMS).
     *
     * @param usuario destinatario a validar.
     * @throws IllegalArgumentException si falta o es inválido el dato de contacto.
     */
    void validarDestinatario(Usuario usuario); // Contrato de validación previa al envío.
}
