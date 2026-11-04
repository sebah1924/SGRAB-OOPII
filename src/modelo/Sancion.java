package modelo; // Paquete del dominio.

import java.math.BigDecimal;   // Dinero con precisión exacta.
import java.math.RoundingMode; // Redondeo monetario.
import java.time.LocalDate;    // Fecha de imposición.

/**
 * Sanción por derroche de agua (Decreto 137 de 2024, Necesidad 7).
 *
 * REGLAS DE NEGOCIO:
 *  - Código único y obligatorio (la unicidad la garantiza la capa que registra/persiste).
 *  - Valor de la multa: SOLO si la sanción es ECONOMICA, mayor que cero y sin superar los 16 salarios mínimos diarios.
 *  - Multa = número de salarios mínimos diarios x salario mínimo diario vigente.
 *    Ejemplo: 10 x 43.333 = $433.330.
 *  - Ciclo de vida: Impuesta -> (Apelada -> Confirmada | Revocada) -> Ejecutada. Si no se apela, se ejecuta.
 *  - Usuarios exentos (especial protección constitucional, hospitales, etc.) pueden recibir sanción
 *    económica, pero NO suspensión.
 *  - Ejecutar una suspensión parcial deja al usuario en estado CORTADO ("servicio interrumpido por sanción").
 *
 * Contexto: se guarda el ESTADO dentro del objeto y se modifica solo con métodos (apelar, confirmar...)
 * que validan que la transición sea legal. Esto es una "máquina de estados" y es una forma de encapsulamiento.
 */
public class Sancion {  // Declaración de la clase Sancion: Sanción por derroche de agua (Decreto 137 de 2024, Necesidad 7)

    public static final int MAXIMO_SMDLV = 16;                                  // Tope legal: 16 salarios mínimos diarios.
    public static final BigDecimal SMDLV_REFERENCIA = new BigDecimal("43333");  // Salario mínimo diario del ejemplo del enunciado (2024).

    private String codigoSancion;       // Identificador único.
    private TipoSancion tipo;           // ECONOMICA, SUSPENSION_PARCIAL o CURSO_PEDAGOGICO.
    private BigDecimal valorMulta;      // Valor en pesos (cero si la sanción no es económica).
    private String descripcion;         // Conducta sancionada (ej.: "Lavado de vehículo con agua potable").
    private EstadoSancion estado;       // Estado del ciclo de vida.
    private LocalDate fechaImposicion;  // Día en que se impuso.
    private Usuario usuario;            // Usuario sancionado.

    /** Constructor vacío (para la capa DAO). Al reconstruir desde BD asigne primero el tipo y luego el valor. */
    public Sancion() {  // Constructor: Constructor vacío (para la capa DAO)
        this.valorMulta = BigDecimal.ZERO; // Valor neutro para evitar NullPointerException en sumas.
    }

    /**
     * Constructor completo. La sanción nace en estado IMPUESTA con la fecha de hoy
     * ("se detecta la conducta y se notifica al usuario; luego se impone").
     */
    public Sancion(String codigoSancion, TipoSancion tipo, BigDecimal valor, String desc, Usuario usuario) {  // Constructor: Constructor completo
        setCodigoSancion(codigoSancion); // Código obligatorio.
        setTipo(tipo);                   // Tipo obligatorio (antes que el valor, porque el valor depende del tipo).
        setUsuario(usuario);             // Usuario obligatorio.
        if (tipo == TipoSancion.SUSPENSION_PARCIAL) { // Reglas propias de la suspensión parcial (Necesidad 7).
            if (usuario.esExento()) { // Exentos y usuarios de especial protección: multa sí, suspensión no.
                throw new IllegalArgumentException("Un usuario exento no puede ser sancionado con suspensión.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
            if (usuario.getTipoUsuario() != TipoUsuario.COMERCIAL && usuario.getTipoUsuario() != TipoUsuario.INDUSTRIAL) {  // Condición: si usuario.getTipoUsuario() es distinto de TipoUsuario.COMERCIAL y usuario....
                throw new IllegalArgumentException("La suspensión parcial aplica solo a establecimientos (usuarios comerciales o industriales).");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
            if (!usuario.esReincidente()) { // Se impone a empresas REINCIDENTES que no acatan las directrices.
                throw new IllegalArgumentException("La suspensión parcial exige que el usuario sea reincidente (sanción previa no revocada).");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
        }
        setValorMulta(valor);            // Valida según el tipo.
        setDescripcion(desc);            // Descripción obligatoria.
        this.estado = EstadoSancion.IMPUESTA;      // Estado inicial del ciclo de vida.
        this.fechaImposicion = LocalDate.now();    // Se impone hoy.
    }

    /** @return el código de la sanción. */
    public String getCodigoSancion() {  // Getter: devuelve codigo sancion
        return codigoSancion; // Lectura simple.
    }

    /** Asigna el código (obligatorio). */
    public void setCodigoSancion(String codigo) {  // Método setCodigoSancion: Asigna el código (obligatorio)
        if (codigo == null || codigo.trim().isEmpty()) { // Regla: código obligatorio.
            throw new IllegalArgumentException("El código de la sanción es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.codigoSancion = codigo.trim(); // Normaliza espacios.
    }

    /** @return el tipo de sanción. */
    public TipoSancion getTipo() {  // Getter: devuelve tipo
        return tipo; // Lectura simple.
    }

    /** Asigna el tipo (obligatorio). */
    public void setTipo(TipoSancion tipo) {  // Método setTipo: Asigna el tipo (obligatorio)
        if (tipo == null) { // Regla: toda sanción tiene tipo.
            throw new IllegalArgumentException("El tipo de sanción es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.tipo = tipo; // Asignación tras validar.
    }

    /** @return el valor de la multa (cero si no es económica). */
    public BigDecimal getValorMulta() {  // Getter: devuelve valor multa
        return valorMulta; // Lectura simple.
    }

    /**
     * Asigna el valor de la multa. Reglas: solo las ECONOMICAS tienen valor, que debe ser
     * mayor que cero y no superar 16 salarios mínimos diarios.
     */
    public void setValorMulta(BigDecimal valor) {  // Método setValorMulta: Asigna el valor de la multa
        if (tipo == null) { // El tipo define qué es válido; por eso debe existir antes.
            throw new IllegalStateException("Asigne primero el tipo de sanción.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        BigDecimal v = (valor == null) ? BigDecimal.ZERO : valor; // null se interpreta como "sin multa".
        if (tipo != TipoSancion.ECONOMICA) { // Regla: valor "solo si es económica".
            if (v.signum() != 0) {  // Condición: si v (signo) es distinto de 0
                throw new IllegalArgumentException("Solo las sanciones económicas tienen valor de multa.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
            this.valorMulta = BigDecimal.ZERO; // Se normaliza a cero.
            return; // Fin: no hay más que validar.
        }
        BigDecimal maximo = SMDLV_REFERENCIA.multiply(BigDecimal.valueOf(MAXIMO_SMDLV)); // 16 x 43.333 = 693.328.
        if (v.signum() <= 0 || v.compareTo(maximo) > 0) { // Regla: > 0 y <= 16 SMDLV.
            throw new IllegalArgumentException("La multa debe ser mayor que cero y no superar 16 salarios mínimos diarios ("  // Lanza IllegalArgumentException con un mensaje claro para el usuario
                    + maximo + ").");  // Continúa el texto de la instrucción anterior.
        }
        this.valorMulta = v.setScale(2, RoundingMode.HALF_UP); // Asignación con 2 decimales.
    }

    /** @return la descripción de la conducta. */
    public String getDescripcion() {  // Getter: devuelve descripcion
        return descripcion; // Lectura simple.
    }

    /** Asigna la descripción (obligatoria). */
    public void setDescripcion(String desc) {  // Método setDescripcion: Asigna la descripción (obligatoria)
        if (desc == null || desc.trim().isEmpty()) { // Regla: la sanción se motiva.
            throw new IllegalArgumentException("La descripción de la sanción es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.descripcion = desc.trim(); // Normaliza espacios.
    }

    /** @return el estado actual. */
    public EstadoSancion getEstado() {  // Getter: devuelve estado
        return estado; // Lectura simple.
    }

    /**
     * Asigna el estado directamente. Está pensado para que la capa DAO reconstruya la sanción desde la
     * base de datos. Para cambiar el estado en la lógica de negocio use apelar/confirmar/revocar/ejecutar.
     */
    public void setEstado(EstadoSancion estado) {  // Método setEstado: Asigna el estado directamente
        if (estado == null) { // Regla: toda sanción tiene estado.
            throw new IllegalArgumentException("El estado de la sanción es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.estado = estado; // Asignación tras validar.
    }

    /** @return la fecha de imposición. */
    public LocalDate getFechaImposicion() {  // Getter: devuelve fecha imposicion
        return fechaImposicion; // Lectura simple.
    }

    /** Asigna la fecha de imposición (obligatoria y no futura). */
    public void setFechaImposicion(LocalDate fecha) {  // Método setFechaImposicion: Asigna la fecha de imposición (obligatoria y no futura)
        if (fecha == null || fecha.isAfter(LocalDate.now())) { // Regla: no se puede imponer en el futuro.
            throw new IllegalArgumentException("La fecha de imposición es obligatoria y no puede ser futura.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.fechaImposicion = fecha; // Asignación tras validar.
    }

    /** @return el usuario sancionado. */
    public Usuario getUsuario() {  // Getter: devuelve usuario
        return usuario; // Lectura simple.
    }

    /** Asigna el usuario sancionado (obligatorio). */
    public void setUsuario(Usuario usuario) {  // Método setUsuario: Asigna el usuario sancionado (obligatorio)
        if (usuario == null) { // Regla: la sanción recae sobre alguien.
            throw new IllegalArgumentException("El usuario sancionado es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.usuario = usuario; // Asignación tras validar.
    }

    /**
     * Multa = número de salarios mínimos diarios x salario mínimo diario vigente.
     * Ejemplo: 10 x 43.333 = 433.330.
     *
     * @param numeroSMDLV cantidad de salarios mínimos diarios legales vigentes (1 a 16).
     * @param salarioMinimoDiario valor de un salario mínimo diario (mayor que cero).
     */
    public BigDecimal calcularMulta(int numeroSMDLV, BigDecimal salarioMinimoDiario) {  // Método calcularMulta: Multa = número de salarios mínimos diarios x salario mínimo diario vigente
        if (numeroSMDLV < 1 || numeroSMDLV > MAXIMO_SMDLV) { // Regla: hasta 16 salarios mínimos diarios.
            throw new IllegalArgumentException("El número de salarios mínimos diarios debe estar entre 1 y " + MAXIMO_SMDLV + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (salarioMinimoDiario == null || salarioMinimoDiario.signum() <= 0) { // El salario base debe ser positivo.
            throw new IllegalArgumentException("El salario mínimo diario debe ser mayor que cero.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        return salarioMinimoDiario.multiply(BigDecimal.valueOf(numeroSMDLV)).setScale(2, RoundingMode.HALF_UP);  // Devuelve: salarioMinimoDiario.multiply(BigDecimal.valueOf(numeroSMDLV)).setScale(2...
    }

    /** @return true si el valor de la multa es coherente con el tipo (económica: >0 y <=16 SMDLV; otras: sin valor). */
    public boolean validarMulta() {  // Método validarMulta (devuelve boolean)
        if (tipo == null || valorMulta == null) { // Datos incompletos: no es válida.
            return false;  // Devuelve el valor de false
        }
        if (tipo != TipoSancion.ECONOMICA) { // Las no económicas no llevan valor.
            return valorMulta.signum() == 0;  // Devuelve: valorMulta (signo) es igual a 0
        }
        BigDecimal maximo = SMDLV_REFERENCIA.multiply(BigDecimal.valueOf(MAXIMO_SMDLV)); // Tope de la multa.
        return valorMulta.signum() > 0 && valorMulta.compareTo(maximo) <= 0;  // Devuelve: valorMulta (signo) es mayor que 0 y valorMulta.compareTo(maximo) es meno...
    }

    /** El usuario apela: solo se puede apelar una sanción recién IMPUESTA. */
    public void apelar() {  // Método apelar: El usuario apela
        if (estado != EstadoSancion.IMPUESTA) { // Transición válida: IMPUESTA -> APELADA.
            throw new IllegalStateException("Solo se puede apelar una sanción en estado IMPUESTA.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        this.estado = EstadoSancion.APELADA; // El recurso queda radicado ante la EAAB.
    }

    /** La EAAB rechaza la apelación y mantiene la sanción: APELADA -> CONFIRMADA. */
    public void confirmar() {  // Método confirmar: La EAAB rechaza la apelación y mantiene la sanción
        if (estado != EstadoSancion.APELADA) { // Solo se confirma lo que fue apelado.
            throw new IllegalStateException("Solo se puede confirmar una sanción en estado APELADA.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        this.estado = EstadoSancion.CONFIRMADA; // Apelación rechazada.
    }

    /** La EAAB acepta la apelación y anula la sanción: APELADA -> REVOCADA. */
    public void revocar() {  // Método revocar: La EAAB acepta la apelación y anula la sanción
        if (estado != EstadoSancion.APELADA) { // Solo se revoca una sanción apelada.
            throw new IllegalStateException("Solo se puede revocar una sanción en estado APELADA.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        this.estado = EstadoSancion.REVOCADA; // Apelación aceptada.
    }

    /**
     * Hace efectiva la sanción. Procede si el usuario no apeló (IMPUESTA) o si se confirmó (CONFIRMADA).
     * Si es una suspensión parcial, el servicio del usuario queda CORTADO (los exentos están protegidos).
     */
    public void ejecutar() {  // Método ejecutar: Hace efectiva la sanción
        if (estado != EstadoSancion.IMPUESTA && estado != EstadoSancion.CONFIRMADA) { // Transiciones válidas.
            throw new IllegalStateException("Solo se puede ejecutar una sanción IMPUESTA o CONFIRMADA.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        if (tipo == TipoSancion.SUSPENSION_PARCIAL) { // El efecto de la suspensión recae sobre el servicio.
            if (usuario.esExento()) { // Regla: exentos pueden recibir multa, pero no suspensión.
                throw new IllegalStateException("Un usuario exento no puede ser suspendido.");  // Lanza IllegalStateException con un mensaje claro para el usuario
            }
            usuario.cortar(); // Estado CORTADO: "servicio interrumpido por sanción".
        }
        this.estado = EstadoSancion.EJECUTADA; // La sanción ya es efectiva.
    }

    /**
     * Una sanción está "activa" si todavía puede cobrarse o aplicarse: IMPUESTA o CONFIRMADA.
     * Quedan fuera la APELADA (en discusión), la EJECUTADA (ya cumplida) y la REVOCADA (anulada).
     */
    public boolean estaActiva() {  // Método estaActiva: Una sanción está "activa" si todavía puede cobrarse o aplicarse
        return estado == EstadoSancion.IMPUESTA || estado == EstadoSancion.CONFIRMADA; // Estados "vivos".
    }

    /** @return true si esta sanción puede traducirse en suspensión: es suspensión parcial y el usuario NO es exento. */
    public boolean permiteSuspension() {  // Método permiteSuspension (devuelve boolean)
        return tipo == TipoSancion.SUSPENSION_PARCIAL && usuario != null && !usuario.esExento(); // Protección a exentos.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "Sancion [codigo=" + codigoSancion + ", tipo=" + tipo + ", valorMulta=" + valorMulta  // Devuelve: "Sancion [codigo=" + codigoSancion + ", tipo=" + tipo + ", valorMulta=" ...
                + ", estado=" + estado + ", fechaImposicion=" + fechaImposicion + "]"; // Resumen.
    }
}
