package modelo; // Paquete del dominio.

import java.time.LocalDateTime; // Fecha y hora exactas del suceso.

/**
 * Evento que dispara una notificación (mensaje del patrón OBSERVER).
 *
 * Contexto: cuando algo relevante ocurre (una factura se emite, vence, se suspende el servicio...),
 * el observable (Factura o EmpresaServicioPublico) crea un Evento y se lo entrega a sus observadores.
 * El Evento es un "objeto de transporte": solo lleva información, no tiene lógica de negocio.
 * Es de solo lectura en la práctica: se crea una vez con su fecha y hora.
 */
public class Evento {  // Declaración de la clase Evento: Evento que dispara una notificación (mensaje del patrón OBSERVER)

    private TipoEvento tipoEvento;    // Qué ocurrió (FACTURA_EMITIDA, CORTE_PROGRAMADO...).
    private Usuario usuario;          // Usuario afectado por el evento.
    private Object datos;             // Información adicional (ej.: la Factura emitida). Es Object para admitir cualquier dato.
    private LocalDateTime fechaHora;  // Momento exacto en que se generó el evento.

    /** Constructor vacío (para la capa DAO o pruebas). */
    public Evento() {  // Constructor: Constructor vacío (para la capa DAO o pruebas)
        this.fechaHora = LocalDateTime.now(); // Todo evento queda fechado.
    }

    /** Constructor completo: el evento nace con la fecha y hora actuales. */
    public Evento(TipoEvento tipoEvento, Usuario usuario, Object datos) {  // Constructor: Constructor completo
        setTipoEvento(tipoEvento); // Tipo obligatorio.
        setUsuario(usuario);       // Usuario obligatorio.
        setDatos(datos);           // Datos opcionales (pueden ser null).
        this.fechaHora = LocalDateTime.now(); // Marca de tiempo del suceso.
    }

    /** @return el tipo de evento. */
    public TipoEvento getTipoEvento() {  // Getter: devuelve tipo evento
        return tipoEvento; // Lectura simple.
    }

    /** Asigna el tipo de evento (obligatorio). */
    public void setTipoEvento(TipoEvento tipo) {  // Método setTipoEvento: Asigna el tipo de evento (obligatorio)
        if (tipo == null) { // Regla: un evento siempre se clasifica (tipado fuerte).
            throw new IllegalArgumentException("El tipo de evento es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.tipoEvento = tipo; // Asignación tras validar.
    }

    /** @return el usuario afectado. */
    public Usuario getUsuario() {  // Getter: devuelve usuario
        return usuario; // Lectura simple.
    }

    /** Asigna el usuario afectado (obligatorio: las notificaciones siempre tienen destinatario). */
    public void setUsuario(Usuario usuario) {  // Método setUsuario: Asigna el usuario afectado (obligatorio
        if (usuario == null) { // Regla: sin usuario no hay a quién notificar.
            throw new IllegalArgumentException("El usuario del evento es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.usuario = usuario; // Asignación tras validar.
    }

    /** @return los datos adicionales (puede ser null). */
    public Object getDatos() {  // Getter: devuelve datos
        return datos; // Lectura simple.
    }

    /** Asigna los datos adicionales (opcionales). */
    public void setDatos(Object datos) {  // Método setDatos: Asigna los datos adicionales (opcionales)
        this.datos = datos; // Se permite null: no todos los eventos llevan datos.
    }

    /** @return la fecha y hora del evento. */
    public LocalDateTime getFechaHora() {  // Getter: devuelve fecha hora
        return fechaHora; // LocalDateTime es inmutable.
    }

    /** Asigna la fecha y hora (obligatoria; útil al reconstruir desde la base de datos). */
    public void setFechaHora(LocalDateTime fecha) {  // Método setFechaHora: Asigna la fecha y hora (obligatoria; útil al reconstruir desde la base de datos)
        if (fecha == null) { // Regla: todo evento tiene marca de tiempo.
            throw new IllegalArgumentException("La fecha y hora del evento es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.fechaHora = fecha; // Asignación tras validar.
    }

    /** @return true si este evento es del tipo indicado. */
    public boolean esTipo(TipoEvento tipo) {  // Método esTipo (devuelve boolean)
        return this.tipoEvento == tipo; // Comparación de enums con ==.
    }

    /** @return true si el evento trae datos adicionales. */
    public boolean tieneDatos() {  // Método tieneDatos (devuelve boolean)
        return datos != null; // null significa "sin datos".
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "Evento [tipo=" + tipoEvento + ", usuario=" + (usuario != null ? usuario.getCodigoSuscriptor() : "N/D")  // Devuelve: "Evento [tipo=" + tipoEvento + ", usuario=" + (usuario es distinto de nu...
                + ", fechaHora=" + fechaHora + "]"; // Resumen sin volcar objetos grandes.
    }
}
