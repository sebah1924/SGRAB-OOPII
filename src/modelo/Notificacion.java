package modelo;
                                              // Paquete único

import java.time.LocalDate;                                            // Fecha de creación

/**
 * Notificación enviada al usuario.
 * CORRECCIÓN: ya NO hay subclases Email/SMS/Push/Llamada porque no aportaban
 * atributos propios. Ahora `enviar()` decide por el canal.
 */
public class Notificacion {

    // ---------- ATRIBUTOS ----------
    private String codigo;                                             // Único
    private String titulo;                                             // Corto y descriptivo
    private String cuerpo;                                             // Motivo
    private Usuario destinatario;                                      // Quién la recibe
    private CanalNotificacion canal;                                   // Canal de envío
    private EventoNotificacion evento;                                 // Qué evento la disparó
    private LocalDate fechaCreacion;                                   // Fecha de creación
    private boolean enviada;                                           // ¿Enviada con éxito?

    // ---------- CONSTRUCTORES ----------
    public Notificacion() { }

    public Notificacion(String codigo, String titulo, String cuerpo,
                        Usuario destinatario, CanalNotificacion canal,
                        EventoNotificacion evento) {
        setCodigo(codigo);
        setTitulo(titulo);
        setCuerpo(cuerpo);
        setDestinatario(destinatario);
        setCanal(canal);
        setEvento(evento);
        this.fechaCreacion = LocalDate.now();
        this.enviada = false;
    }

    // ---------- GETTERS / SETTERS ----------
    public String getCodigo() { return codigo; }
    public void setCodigo(String c) {
        if (c == null || c.trim().isEmpty())
            throw new IllegalArgumentException("Código de notificación obligatorio.");
        this.codigo = c.trim();
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String t) {
        if (t == null || t.trim().isEmpty())
            throw new IllegalArgumentException("Título obligatorio.");
        this.titulo = t.trim();
    }

    public String getCuerpo() { return cuerpo; }
    public void setCuerpo(String c) { this.cuerpo = c; }

    public Usuario getDestinatario() { return destinatario; }
    public void setDestinatario(Usuario u) {
        if (u == null) throw new IllegalArgumentException("Destinatario obligatorio.");
        this.destinatario = u;
    }

    public CanalNotificacion getCanal() { return canal; }
    public void setCanal(CanalNotificacion c) {
        if (c == null) throw new IllegalArgumentException("Canal obligatorio.");
        this.canal = c;
    }

    public EventoNotificacion getEvento() { return evento; }
    public void setEvento(EventoNotificacion e) { this.evento = e; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate f) { this.fechaCreacion = f; }

    public boolean isEnviada() { return enviada; }
    public void setEnviada(boolean e) { this.enviada = e; }

    // ---------- ENVÍO POLIMÓRFICO ----------
    /** Envía por el canal correspondiente (switch — sin subclases). */
    public boolean enviar() {
        switch (canal) {
            case EMAIL:
                System.out.println("[EMAIL → " + destinatario.getCorreo() + "] " + titulo);
                break;
            case SMS:
                System.out.println("[SMS → " + destinatario.getTelefono() + "] " + titulo);
                break;
            case PUSH:
                System.out.println("[PUSH → " + destinatario.getNombre() + "] " + titulo);
                break;
            case LLAMADA:
                System.out.println("[LLAMADA → " + destinatario.getTelefono() + "] " + titulo);
                break;
            default:
                return false;
        }
        this.enviada = true;
        return true;
    }

    @Override
    public String toString() {
        return "Notificacion[" + codigo + ", " + canal + ", " + titulo + "]";
    }
}