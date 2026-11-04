package modelo; // Paquete del dominio.

import java.time.LocalDateTime; // Fecha y hora de creación del mensaje.

/**
 * Mensaje que la EAAB envía a un usuario por uno de los canales oficiales (Necesidad 3).
 *
 * REGLAS DE NEGOCIO (enunciado, "Notificaciones"):
 *  - código obligatorio y único; título CORTO y descriptivo; cuerpo con el motivo de la notificación;
 *  - destinatario, canal y fecha de creación; todos los mensajes enviados quedan en un historial.
 *
 * Criterio adoptado para "título corto": máximo 100 caracteres.
 * Criterio adoptado para "urgente": el título empieza por "URGENTE" (lo asigna GestorNotificaciones
 * según el tipo de evento, por ejemplo suspensión inminente).
 */
public class Mensaje {  // Declaración de la clase Mensaje: Mensaje que la EAAB envía a un usuario por uno de los canales oficiales (Necesidad 3)

    private static final int MAX_TITULO = 100;       // Límite de caracteres que define un título "corto".
    public static final String PREFIJO_URGENTE = "URGENTE"; // Marca textual de urgencia.

    private String codigo;                // Identificador único del mensaje.
    private String titulo;                // Asunto corto y descriptivo.
    private String cuerpo;                // Motivo de la notificación.
    private Usuario destinatario;         // Usuario que recibe el mensaje.
    private CanalNotificacion canal;      // Canal por el que se envía.
    private LocalDateTime fechaCreacion;  // Momento en que se creó el mensaje.

    /** Constructor vacío (para la capa DAO). */
    public Mensaje() {  // Constructor: Constructor vacío (para la capa DAO)
        this.fechaCreacion = LocalDateTime.now(); // Por defecto se fecha al crearse.
    }

    /** Constructor completo; valida cada dato mediante los setters. */
    public Mensaje(String codigo, String titulo, String cuerpo, Usuario destinatario, CanalNotificacion canal) {  // Constructor: Constructor completo; valida cada dato mediante los setters
        setCodigo(codigo);            // Código obligatorio.
        setTitulo(titulo);            // Título corto y obligatorio.
        setCuerpo(cuerpo);            // Cuerpo obligatorio.
        setDestinatario(destinatario); // Destinatario obligatorio.
        setCanal(canal);              // Canal obligatorio.
        this.fechaCreacion = LocalDateTime.now(); // Fecha de creación automática.
    }

    /** @return el código del mensaje. */
    public String getCodigo() {  // Getter: devuelve codigo
        return codigo; // Lectura simple.
    }

    /** Asigna el código (obligatorio). */
    public void setCodigo(String codigo) {  // Método setCodigo: Asigna el código (obligatorio)
        if (codigo == null || codigo.trim().isEmpty()) { // Regla: código obligatorio.
            throw new IllegalArgumentException("El código del mensaje es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.codigo = codigo.trim(); // Normaliza espacios.
    }

    /** @return el título. */
    public String getTitulo() {  // Getter: devuelve titulo
        return titulo; // Lectura simple.
    }

    /** Asigna el título (obligatorio y corto). */
    public void setTitulo(String titulo) {  // Método setTitulo: Asigna el título (obligatorio y corto)
        if (titulo == null || titulo.trim().isEmpty()) { // Regla: el título es obligatorio.
            throw new IllegalArgumentException("El título del mensaje es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (titulo.trim().length() > MAX_TITULO) { // Regla: el título debe ser corto.
            throw new IllegalArgumentException("El título debe ser corto (máximo " + MAX_TITULO + " caracteres).");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.titulo = titulo.trim(); // Normaliza espacios.
    }

    /** @return el cuerpo. */
    public String getCuerpo() {  // Getter: devuelve cuerpo
        return cuerpo; // Lectura simple.
    }

    /** Asigna el cuerpo (motivo de la notificación, obligatorio). */
    public void setCuerpo(String cuerpo) {  // Método setCuerpo: Asigna el cuerpo (motivo de la notificación, obligatorio)
        if (cuerpo == null || cuerpo.trim().isEmpty()) { // Regla: todo mensaje explica su motivo.
            throw new IllegalArgumentException("El cuerpo del mensaje es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.cuerpo = cuerpo.trim(); // Normaliza espacios.
    }

    /** @return el destinatario. */
    public Usuario getDestinatario() {  // Getter: devuelve destinatario
        return destinatario; // Lectura simple.
    }

    /** Asigna el destinatario (obligatorio). */
    public void setDestinatario(Usuario dest) {  // Método setDestinatario: Asigna el destinatario (obligatorio)
        if (dest == null) { // Regla: un mensaje siempre tiene destinatario.
            throw new IllegalArgumentException("El destinatario del mensaje es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.destinatario = dest; // Asignación tras validar.
    }

    /** @return el canal. */
    public CanalNotificacion getCanal() {  // Getter: devuelve canal
        return canal; // Lectura simple.
    }

    /** Asigna el canal (obligatorio). */
    public void setCanal(CanalNotificacion canal) {  // Método setCanal: Asigna el canal (obligatorio)
        if (canal == null) { // Regla: se debe saber por dónde viaja el mensaje.
            throw new IllegalArgumentException("El canal del mensaje es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.canal = canal; // Asignación tras validar.
    }

    /** @return la fecha y hora de creación. */
    public LocalDateTime getFechaCreacion() {  // Getter: devuelve fecha creacion
        return fechaCreacion; // Lectura simple.
    }

    /** Asigna la fecha de creación (útil al reconstruir desde la base de datos). */
    public void setFechaCreacion(LocalDateTime fecha) {  // Método setFechaCreacion: Asigna la fecha de creación (útil al reconstruir desde la base de datos)
        if (fecha == null) { // Regla: todo mensaje tiene fecha de creación.
            throw new IllegalArgumentException("La fecha de creación es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.fechaCreacion = fecha; // Asignación tras validar.
    }

    /** @return true si el título empieza con la marca de urgencia (sin distinguir mayúsculas). */
    public boolean esUrgente() {  // Método esUrgente (devuelve boolean)
        return titulo != null && titulo.toUpperCase().startsWith(PREFIJO_URGENTE); // toUpperCase evita depender del uso de mayúsculas.
    }

    /** @return resumen de una línea: título + inicio del cuerpo. */
    public String getResumen() {  // Getter: devuelve resumen
        String inicio = (cuerpo != null && cuerpo.length() > 60) ? cuerpo.substring(0, 60) + "..." : cuerpo; // Recorta cuerpos largos.
        return titulo + " - " + inicio; // Formato "título - cuerpo".
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "Mensaje [codigo=" + codigo + ", titulo=" + titulo + ", canal=" + canal  // Devuelve: "Mensaje [codigo=" + codigo + ", titulo=" + titulo + ", canal=" + canal
                + ", fechaCreacion=" + fechaCreacion + "]"; // Resumen.
    }
}
