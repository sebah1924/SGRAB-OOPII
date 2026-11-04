package modelo; // Paquete del dominio.

import java.math.BigDecimal; // Dinero con precisión exacta.
import java.time.LocalDate;  // Fechas sin hora.

/**
 * Usuario OFICIAL (entidades públicas: "Hospital San Ignacio", "Colegio Distrital"...).
 *
 * REGLAS DE NEGOCIO:
 *  - entidad pública obligatoria y asignación presupuestal MAYOR que cero.
 *  - se cobra sin recargos especiales: cargo fijo + (cargo variable x consumo).
 *  - mantiene su EXENCIÓN de cortes (continuidad de los servicios esenciales del Estado).
 *
 * Polimorfismo: sobrescribe esExento() para que siempre sea true, independientemente de si tiene una
 * Excepcion asignada, porque la exención es inherente al tipo de usuario.
 */
public class UsuarioOficial extends Usuario {  // Declaración de la clase UsuarioOficial: Usuario OFICIAL (entidades públicas

    private String entidadPublica;            // Nombre de la entidad pública.
    private BigDecimal asignacionPresupuestal; // Presupuesto asignado (> 0).

    /** Constructor vacío (para la capa DAO). */
    public UsuarioOficial() {  // Constructor: Constructor vacío (para la capa DAO)
        super(); // Constructor vacío de Usuario.
        setTipoUsuario(TipoUsuario.OFICIAL); // Tipo fijo.
    }

    /** Constructor completo. */
    public UsuarioOficial(String identificacion, String nombre, String apellido, LocalDate fechaNacimiento,  // Constructor: Constructor completo
                          String direccion, String telefono, String email, String codigoSuscriptor,  // Continuación de la instrucción de la línea anterior.
                          LocalDate fechaRegistro, int estrato, CanalNotificacion canalPreferido,  // Instrucción: LocalDate fechaRegistro, int estrato, CanalNotificacion c...
                          String entidadPublica, BigDecimal asignacionPresupuestal) {  // Abre el bloque de código: String entidadPublica, BigDecimal asignacionPre...
        super(identificacion, nombre, apellido, fechaNacimiento, direccion, telefono, email,  // Llama al constructor de la clase base para iniciar la parte heredada
                codigoSuscriptor, fechaRegistro, estrato, canalPreferido); // Datos de Persona y Usuario.
        setTipoUsuario(TipoUsuario.OFICIAL);                    // Tipo fijo.
        setEntidadPublica(entidadPublica);                      // Obligatoria.
        setAsignacionPresupuestal(asignacionPresupuestal);      // Mayor que cero.
    }

    /** @return la entidad pública. */
    public String getEntidadPublica() {  // Getter: devuelve entidad publica
        return entidadPublica; // Lectura simple.
    }

    /** Asigna la entidad pública (obligatoria). */
    public void setEntidadPublica(String entidad) {  // Método setEntidadPublica: Asigna la entidad pública (obligatoria)
        if (entidad == null || entidad.trim().isEmpty()) { // Regla: toda entidad oficial se nombra.
            throw new IllegalArgumentException("La entidad pública es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.entidadPublica = entidad.trim(); // Normaliza espacios.
    }

    /** @return la asignación presupuestal. */
    public BigDecimal getAsignacionPresupuestal() {  // Getter: devuelve asignacion presupuestal
        return asignacionPresupuestal; // Lectura simple.
    }

    /** Asigna la asignación presupuestal (mayor que cero). */
    public void setAsignacionPresupuestal(BigDecimal asignacion) {  // Método setAsignacionPresupuestal: Asigna la asignación presupuestal (mayor que cero)
        if (asignacion == null || asignacion.signum() <= 0) { // Regla: presupuesto mayor que cero.
            throw new IllegalArgumentException("La asignación presupuestal debe ser mayor que cero.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.asignacionPresupuestal = asignacion; // Asignación tras validar.
    }

    /** POLIMORFISMO: un usuario oficial está SIEMPRE exento de cortes y suspensiones. */
    @Override
    public boolean esExento() {  // Método esExento: POLIMORFISMO
        return true; // La exención es inherente al tipo de usuario (continuidad de servicios esenciales del Estado).
    }

    /** POLIMORFISMO: el cobro oficial (sin recargos) se delega en la estrategia inyectada (Strategy). */
    @Override
    public BigDecimal calcularTarifa(Consumo consumo) {  // Método calcularTarifa: POLIMORFISMO
        if (estrategiaTarifa == null) { // Sin estrategia no hay fórmula.
            throw new IllegalStateException("El usuario no tiene estrategia tarifaria asignada.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        return estrategiaTarifa.calcularTarifa(consumo, this); // Delegación.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return super.toString() + " {entidad=" + entidadPublica + ", asignacion=" + asignacionPresupuestal + "}"; // Extiende el resumen base.
    }
}
