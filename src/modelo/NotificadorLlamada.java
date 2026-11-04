package modelo; // Paquete del dominio.

import java.util.logging.Logger; // Registro de eventos de la aplicación.

/**
 * Canal de notificación por LLAMADA TELEFÓNICA automatizada. Implementa INotificable.
 * Una "central telefónica" (PBX) enruta las llamadas y una voz de sistema lee el mensaje al usuario.
 * El envío se SIMULA en este modelo.
 */
public class NotificadorLlamada implements INotificable {  // Declaración de la clase NotificadorLlamada: Canal de notificación por LLAMADA TELEFÓNICA automatizada

    private static final Logger LOG = Logger.getLogger(NotificadorLlamada.class.getName()); // Logger de esta clase.

    private String centralTelefonica; // Central que realiza la llamada.
    private String vozSistema;        // Voz sintética que lee el mensaje.

    /** Constructor vacío (canal sin configurar). */
    public NotificadorLlamada() {  // Constructor: Constructor vacío (canal sin configurar)
        // Sin instrucciones.
    }

    /** Constructor con configuración inyectada. */
    public NotificadorLlamada(String central, String voz) {  // Constructor: Constructor con configuración inyectada
        setCentralTelefonica(central); // Valida central.
        setVozSistema(voz);            // Valida voz.
    }

    /** @return la central telefónica. */
    public String getCentralTelefonica() {  // Getter: devuelve central telefonica
        return centralTelefonica; // Lectura simple.
    }

    /** Asigna la central telefónica (obligatoria). */
    public void setCentralTelefonica(String central) {  // Método setCentralTelefonica: Asigna la central telefónica (obligatoria)
        if (central == null || central.trim().isEmpty()) { // Regla: sin central no hay llamada.
            throw new IllegalArgumentException("La central telefónica es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.centralTelefonica = central.trim(); // Normaliza espacios.
    }

    /** @return la voz del sistema. */
    public String getVozSistema() {  // Getter: devuelve voz sistema
        return vozSistema; // Lectura simple.
    }

    /** Asigna la voz del sistema (obligatoria). */
    public void setVozSistema(String voz) {  // Método setVozSistema: Asigna la voz del sistema (obligatoria)
        if (voz == null || voz.trim().isEmpty()) { // Regla: se debe definir quién "habla".
            throw new IllegalArgumentException("La voz del sistema es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.vozSistema = voz.trim(); // Normaliza espacios.
    }

    /** El canal de llamada exige un teléfono válido (7 a 10 dígitos). */
    @Override
    public void validarDestinatario(Usuario usuario) {  // Método validarDestinatario: El canal de llamada exige un teléfono válido (7 a 10 dígitos)
        if (usuario == null || !usuario.validarTelefono()) { // Reutiliza la regla de Persona.
            throw new IllegalArgumentException("El usuario no tiene un teléfono válido para llamadas.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
    }

    /** Realiza (simula) la llamada. */
    @Override
    public void notificar(Usuario usuario, Mensaje mensaje) {  // Método notificar: Realiza (simula) la llamada
        if (mensaje == null) { // No se envía "nada".
            throw new IllegalArgumentException("El mensaje no puede ser nulo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (centralTelefonica == null || vozSistema == null) { // El canal debe estar configurado.
            throw new IllegalStateException("El canal de llamada no está configurado.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        validarDestinatario(usuario); // Verifica el dato de contacto.
        LOG.info("[LLAMADA " + centralTelefonica + "/" + vozSistema + "] -> " + usuario.getTelefono()  // Invoca LOG.info(...)
                + " | " + mensaje.getTitulo());  // Continúa el texto de la instrucción anterior.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "NotificadorLlamada [central=" + centralTelefonica + ", voz=" + vozSistema + "]"; // Resumen.
    }
}
