package modelo; // Paquete del dominio.

import java.util.logging.Logger; // Registro de eventos de la aplicación (alternativa profesional a System.out).

/**
 * Canal de notificación por CORREO ELECTRÓNICO. Implementa INotificable (polimorfismo).
 *
 * Contexto: el modelo no se conecta a un servidor real. El envío se SIMULA registrando el mensaje en el log;
 * en producción, en el punto marcado se integraría un cliente SMTP (protocolo estándar de envío de correo).
 */
public class NotificadorEmail implements INotificable {  // Declaración de la clase NotificadorEmail: Canal de notificación por CORREO ELECTRÓNICO

    private static final Logger LOG = Logger.getLogger(NotificadorEmail.class.getName()); // Logger de esta clase.

    private String remitente;    // Dirección desde la que se envía.
    private String servidorSMTP; // Servidor de correo saliente.

    /** Constructor vacío (canal sin configurar). */
    public NotificadorEmail() {  // Constructor: Constructor vacío (canal sin configurar)
        // Sin instrucciones: remitente y servidor quedan sin configurar.
    }

    /** Constructor con configuración inyectada. */
    public NotificadorEmail(String remitente, String servidor) {  // Constructor: Constructor con configuración inyectada
        setRemitente(remitente);   // Valida remitente.
        setServidorSMTP(servidor); // Valida servidor.
    }

    /** @return el remitente. */
    public String getRemitente() {  // Getter: devuelve remitente
        return remitente; // Lectura simple.
    }

    /** Asigna el remitente (obligatorio). */
    public void setRemitente(String remitente) {  // Método setRemitente: Asigna el remitente (obligatorio)
        if (remitente == null || remitente.trim().isEmpty()) { // Regla: sin remitente no se puede enviar.
            throw new IllegalArgumentException("El remitente es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.remitente = remitente.trim(); // Normaliza espacios.
    }

    /** @return el servidor SMTP. */
    public String getServidorSMTP() {  // Getter: devuelve servidor smtp
        return servidorSMTP; // Lectura simple.
    }

    /** Asigna el servidor SMTP (obligatorio). */
    public void setServidorSMTP(String servidor) {  // Método setServidorSMTP: Asigna el servidor SMTP (obligatorio)
        if (servidor == null || servidor.trim().isEmpty()) { // Regla: sin servidor no se puede enviar.
            throw new IllegalArgumentException("El servidor SMTP es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.servidorSMTP = servidor.trim(); // Normaliza espacios.
    }

    /** El canal de correo exige que el usuario tenga un correo electrónico con formato válido. */
    @Override
    public void validarDestinatario(Usuario usuario) {  // Método validarDestinatario: El canal de correo exige que el usuario tenga un correo electrónico con formato válido
        if (usuario == null || usuario.getEmail() == null || !usuario.getEmail().contains("@")) { // Verificación mínima.
            throw new IllegalArgumentException("El usuario no tiene un correo electrónico válido.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
    }

    /** Envía (simula) el mensaje por correo. */
    @Override
    public void notificar(Usuario usuario, Mensaje mensaje) {  // Método notificar: Envía (simula) el mensaje por correo
        if (mensaje == null) { // No se envía "nada".
            throw new IllegalArgumentException("El mensaje no puede ser nulo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (remitente == null || servidorSMTP == null) { // El canal debe estar configurado.
            throw new IllegalStateException("El canal de correo no está configurado.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        validarDestinatario(usuario); // Verifica el dato de contacto antes de enviar.
        // Punto de integración: aquí se llamaría al cliente SMTP real.
        LOG.info("[EMAIL] " + remitente + " -> " + usuario.getEmail() + " | " + mensaje.getTitulo());  // Invoca LOG.info(...)
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "NotificadorEmail [remitente=" + remitente + ", servidorSMTP=" + servidorSMTP + "]"; // Resumen.
    }
}
