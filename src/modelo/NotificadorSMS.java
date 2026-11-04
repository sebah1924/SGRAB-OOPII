package modelo; // Paquete del dominio.

import java.util.logging.Logger; // Registro de eventos de la aplicación.

/**
 * Canal de notificación por MENSAJE DE TEXTO (SMS). Implementa INotificable.
 * El envío se SIMULA; en producción se integraría la pasarela (gateway) del operador de telefonía.
 */
public class NotificadorSMS implements INotificable {  // Declaración de la clase NotificadorSMS: Canal de notificación por MENSAJE DE TEXTO (SMS)

    private static final Logger LOG = Logger.getLogger(NotificadorSMS.class.getName()); // Logger de esta clase.

    private String operador; // Operador o pasarela de mensajería usada para el envío.

    /** Constructor vacío (canal sin configurar). */
    public NotificadorSMS() {  // Constructor: Constructor vacío (canal sin configurar)
        // Sin instrucciones.
    }

    /** Constructor con el operador inyectado. */
    public NotificadorSMS(String operador) {  // Constructor: Constructor con el operador inyectado
        setOperador(operador); // Valida operador.
    }

    /** @return el operador. */
    public String getOperador() {  // Getter: devuelve operador
        return operador; // Lectura simple.
    }

    /** Asigna el operador (obligatorio). */
    public void setOperador(String operador) {  // Método setOperador: Asigna el operador (obligatorio)
        if (operador == null || operador.trim().isEmpty()) { // Regla: sin operador no hay envío.
            throw new IllegalArgumentException("El operador es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.operador = operador.trim(); // Normaliza espacios.
    }

    /** El canal SMS exige un teléfono válido (7 a 10 dígitos). */
    @Override
    public void validarDestinatario(Usuario usuario) {  // Método validarDestinatario: El canal SMS exige un teléfono válido (7 a 10 dígitos)
        if (usuario == null || !usuario.validarTelefono()) { // Reutiliza la regla de Persona.
            throw new IllegalArgumentException("El usuario no tiene un teléfono válido para SMS.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
    }

    /** Envía (simula) el SMS. */
    @Override
    public void notificar(Usuario usuario, Mensaje mensaje) {  // Método notificar: Envía (simula) el SMS
        if (mensaje == null) { // No se envía "nada".
            throw new IllegalArgumentException("El mensaje no puede ser nulo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (operador == null) { // El canal debe estar configurado.
            throw new IllegalStateException("El canal SMS no está configurado.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        validarDestinatario(usuario); // Verifica el dato de contacto.
        LOG.info("[SMS via " + operador + "] -> " + usuario.getTelefono() + " | " + mensaje.getTitulo());  // Invoca LOG.info(...)
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "NotificadorSMS [operador=" + operador + "]"; // Resumen.
    }
}
