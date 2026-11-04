package modelo; // Paquete del dominio.

import java.util.logging.Logger; // Registro de eventos de la aplicación.

/**
 * Canal de notificación PUSH (aviso emergente en la app móvil). Implementa INotificable.
 *
 * Contexto: una "server key" es la credencial secreta con la que el servidor se identifica ante el servicio
 * de mensajería push (por ejemplo Firebase Cloud Messaging). El envío se SIMULA en este modelo.
 */
public class NotificadorPush implements INotificable {  // Declaración de la clase NotificadorPush: Canal de notificación PUSH (aviso emergente en la app móvil)

    private static final Logger LOG = Logger.getLogger(NotificadorPush.class.getName()); // Logger de esta clase.

    private String serverKey; // Credencial del servicio push.
    private String appName;   // Nombre de la aplicación móvil destino.

    /** Constructor vacío (canal sin configurar). */
    public NotificadorPush() {  // Constructor: Constructor vacío (canal sin configurar)
        // Sin instrucciones.
    }

    /** Constructor con configuración inyectada. */
    public NotificadorPush(String serverKey, String appName) {  // Constructor: Constructor con configuración inyectada
        setServerKey(serverKey); // Valida credencial.
        setAppName(appName);     // Valida nombre de app.
    }

    /** @return la server key. */
    public String getServerKey() {  // Getter: devuelve server key
        return serverKey; // Lectura simple.
    }

    /** Asigna la server key (obligatoria). */
    public void setServerKey(String serverKey) {  // Método setServerKey: Asigna la server key (obligatoria)
        if (serverKey == null || serverKey.trim().isEmpty()) { // Regla: sin credencial no hay envío.
            throw new IllegalArgumentException("La server key es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.serverKey = serverKey.trim(); // Normaliza espacios.
    }

    /** @return el nombre de la app. */
    public String getAppName() {  // Getter: devuelve app name
        return appName; // Lectura simple.
    }

    /** Asigna el nombre de la app (obligatorio). */
    public void setAppName(String appName) {  // Método setAppName: Asigna el nombre de la app (obligatorio)
        if (appName == null || appName.trim().isEmpty()) { // Regla: se debe saber a qué app enviar.
            throw new IllegalArgumentException("El nombre de la aplicación es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.appName = appName.trim(); // Normaliza espacios.
    }

    /** El canal push solo exige un usuario existente (el dispositivo se identifica en el servicio push). */
    @Override
    public void validarDestinatario(Usuario usuario) {  // Método validarDestinatario: El canal push solo exige un usuario existente (el dispositivo se identifica en el servicio push)
        if (usuario == null) { // Sin destinatario no hay a quién avisar.
            throw new IllegalArgumentException("El destinatario no puede ser nulo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
    }

    /** Envía (simula) la notificación push. */
    @Override
    public void notificar(Usuario usuario, Mensaje mensaje) {  // Método notificar: Envía (simula) la notificación push
        if (mensaje == null) { // No se envía "nada".
            throw new IllegalArgumentException("El mensaje no puede ser nulo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (serverKey == null || appName == null) { // El canal debe estar configurado.
            throw new IllegalStateException("El canal push no está configurado.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        validarDestinatario(usuario); // Verifica el destinatario.
        LOG.info("[PUSH " + appName + "] -> " + usuario.getCodigoSuscriptor() + " | " + mensaje.getTitulo());  // Invoca LOG.info(...)
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "NotificadorPush [appName=" + appName + "]"; // Resumen (la server key es secreta y NO se imprime).
    }
}
