package modelo; // Paquete del dominio.

import java.time.LocalDate; // Fecha de la baja (sin hora).

/**
 * Registro de AUDITORÍA de la eliminación de un usuario.
 *
 * REGLA DE NEGOCIO (enunciado, "Los usuarios se registran... y pueden eliminarse"):
 * la eliminación debe quedar registrada con FECHA y MOTIVO, para trazabilidad y auditoría.
 * Ejemplo: un comercio se registra el día que contrata el servicio y se da de baja el día que cierra
 * su establecimiento, quedando el motivo.
 *
 * Contexto: "trazabilidad" es la capacidad de reconstruir qué pasó, cuándo y por qué;
 * por eso la baja NO borra la historia: la guarda en EmpresaServicioPublico.historialBajas.
 */
public class BajaUsuario {  // Declaración de la clase BajaUsuario: Registro de AUDITORÍA de la eliminación de un usuario

    private String codigo;      // Identificador del registro de baja.
    private LocalDate fechaBaja; // Día en que se eliminó al usuario.
    private String motivo;      // Razón de la baja (obligatoria).
    private Usuario usuario;    // Usuario que fue dado de baja.

    /** Constructor vacío (para la capa DAO). */
    public BajaUsuario() {  // Constructor: Constructor vacío (para la capa DAO)
        // Sin instrucciones.
    }

    /** Constructor completo; cada dato se valida a través de su setter. */
    public BajaUsuario(String codigo, LocalDate fechaBaja, String motivo, Usuario usuario) {  // Constructor: Constructor completo; cada dato se valida a través de su setter
        setCodigo(codigo);       // Código obligatorio.
        setFechaBaja(fechaBaja); // Fecha obligatoria.
        setMotivo(motivo);       // Motivo obligatorio.
        setUsuario(usuario);     // Usuario obligatorio.
    }

    /** @return el código de la baja. */
    public String getCodigo() {  // Getter: devuelve codigo
        return codigo; // Lectura simple.
    }

    /** Asigna el código (obligatorio). */
    public void setCodigo(String codigo) {  // Método setCodigo: Asigna el código (obligatorio)
        if (codigo == null || codigo.trim().isEmpty()) { // Regla: todo registro de auditoría se identifica.
            throw new IllegalArgumentException("El código de la baja es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.codigo = codigo.trim(); // Normaliza espacios.
    }

    /** @return la fecha de la baja. */
    public LocalDate getFechaBaja() {  // Getter: devuelve fecha baja
        return fechaBaja; // LocalDate es inmutable.
    }

    /** Asigna la fecha de la baja (obligatoria). */
    public void setFechaBaja(LocalDate fecha) {  // Método setFechaBaja: Asigna la fecha de la baja (obligatoria)
        if (fecha == null) { // Regla: la baja debe quedar registrada con fecha.
            throw new IllegalArgumentException("La fecha de la baja es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.fechaBaja = fecha; // Asignación tras validar.
    }

    /** @return el motivo de la baja. */
    public String getMotivo() {  // Getter: devuelve motivo
        return motivo; // Lectura simple.
    }

    /** Asigna el motivo (obligatorio, para auditoría). */
    public void setMotivo(String motivo) {  // Método setMotivo: Asigna el motivo (obligatorio, para auditoría)
        if (motivo == null || motivo.trim().isEmpty()) { // Regla: el motivo es obligatorio.
            throw new IllegalArgumentException("El motivo de la baja es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.motivo = motivo.trim(); // Normaliza espacios.
    }

    /** @return el usuario dado de baja. */
    public Usuario getUsuario() {  // Getter: devuelve usuario
        return usuario; // Lectura simple.
    }

    /** Asigna el usuario dado de baja (obligatorio). */
    public void setUsuario(Usuario usuario) {  // Método setUsuario: Asigna el usuario dado de baja (obligatorio)
        if (usuario == null) { // Regla: una baja siempre pertenece a un usuario.
            throw new IllegalArgumentException("El usuario de la baja es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.usuario = usuario; // Asignación tras validar.
    }

    /** @return true si el motivo está presente. */
    public boolean validarMotivo() {  // Método validarMotivo (devuelve boolean)
        return motivo != null && !motivo.trim().isEmpty(); // Texto presente.
    }

    /** @return frase descriptiva de la baja para reportes de auditoría. */
    public String getDescripcion() {  // Getter: devuelve descripcion
        return "Baja " + codigo + " del usuario "  // Devuelve: "Baja " + codigo + " del usuario "
                + (usuario != null ? usuario.getCodigoSuscriptor() : "N/D") // Operador ternario: evita NullPointerException.
                + " el " + fechaBaja + ". Motivo: " + motivo;  // Continúa el texto de la instrucción anterior.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "BajaUsuario [codigo=" + codigo + ", fechaBaja=" + fechaBaja + ", motivo=" + motivo + "]"; // Resumen.
    }
}
