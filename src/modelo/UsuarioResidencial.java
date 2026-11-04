package modelo; // Paquete del dominio.

import java.math.BigDecimal; // Dinero con precisión exacta.
import java.time.LocalDate;  // Fechas sin hora.

/**
 * Usuario RESIDENCIAL (hogares).
 *
 * REGLAS DE NEGOCIO (enunciado, "Los usuarios pueden ser de cuatro tipos"):
 *  - número de habitantes MAYOR O IGUAL a 1.
 *  - puede recibir beneficio de subsidio si es de estrato 1, 2 o 3.
 *  - los estratos 5 y 6 pagan contribución de solidaridad (20 %); el estrato 4 ni recibe ni paga.
 *
 * Polimorfismo: sobrescribe calcularTarifa() delegando en una TarifaResidencialStrategy.
 */
public class UsuarioResidencial extends Usuario {  // Declaración de la clase UsuarioResidencial: Usuario RESIDENCIAL (hogares)

    private int numeroHabitantes;      // Personas que viven en el inmueble (>= 1).
    private boolean beneficioSubsidio; // true si el hogar efectivamente recibe subsidio.

    /** Constructor vacío (para la capa DAO). */
    public UsuarioResidencial() {  // Constructor: Constructor vacío (para la capa DAO)
        super(); // Constructor vacío de Usuario.
        setTipoUsuario(TipoUsuario.RESIDENCIAL); // El tipo queda fijado por la propia clase.
    }

    /** Constructor completo: los datos comunes van al constructor de Usuario y los propios se validan aquí. */
    public UsuarioResidencial(String identificacion, String nombre, String apellido, LocalDate fechaNacimiento,  // Constructor: Constructor completo
                              String direccion, String telefono, String email, String codigoSuscriptor,  // Continuación de la instrucción de la línea anterior.
                              LocalDate fechaRegistro, int estrato, CanalNotificacion canalPreferido,  // Instrucción: LocalDate fechaRegistro, int estrato, CanalNotificacion c...
                              int numeroHabitantes, boolean beneficioSubsidio) {  // Abre el bloque de código: int numeroHabitantes, boolean beneficioSubsidio)
        super(identificacion, nombre, apellido, fechaNacimiento, direccion, telefono, email,  // Llama al constructor de la clase base para iniciar la parte heredada
                codigoSuscriptor, fechaRegistro, estrato, canalPreferido); // Datos de Persona y Usuario.
        setTipoUsuario(TipoUsuario.RESIDENCIAL);   // Tipo fijo.
        setNumeroHabitantes(numeroHabitantes);     // Habitantes >= 1.
        setBeneficioSubsidio(beneficioSubsidio);   // Solo si el estrato es elegible.
    }

    /** @return el número de habitantes. */
    public int getNumeroHabitantes() {  // Getter: devuelve numero habitantes
        return numeroHabitantes; // Lectura simple.
    }

    /** Asigna el número de habitantes (mayor o igual a 1). */
    public void setNumeroHabitantes(int habitantes) {  // Método setNumeroHabitantes: Asigna el número de habitantes (mayor o igual a 1)
        if (habitantes < 1) { // Regla: un hogar tiene al menos un habitante.
            throw new IllegalArgumentException("El número de habitantes debe ser mayor o igual a 1.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.numeroHabitantes = habitantes; // Asignación tras validar.
    }

    /**
     * @return true si el hogar recibe subsidio de manera EFECTIVA: el beneficio fue concedido Y el estrato actual
     * sigue siendo elegible (1, 2 o 3). Así, si el estrato cambia a 4, 5 o 6, el subsidio deja de aplicarse solo,
     * sin que el modelo pueda quedar contradictorio. (Prefijo "is" por ser boolean.)
     */
    public boolean isBeneficioSubsidio() {  // Getter booleano: indica beneficio subsidio
        return beneficioSubsidio && esElegibleSubsidio(); // Invariante derivada: nunca hay subsidio en estratos 4, 5 o 6.
    }

    /** Asigna el beneficio de subsidio. Solo se puede conceder a estratos 1, 2 y 3. */
    public void setBeneficioSubsidio(boolean beneficio) {  // Método setBeneficioSubsidio: Asigna el beneficio de subsidio
        if (beneficio && !esElegibleSubsidio()) { // Regla: subsidio únicamente para estratos 1, 2 y 3.
            throw new IllegalArgumentException("Solo los estratos 1, 2 y 3 pueden recibir subsidio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.beneficioSubsidio = beneficio; // Asignación tras validar.
    }

    /** @return true si el estrato del hogar (1, 2 o 3) lo hace elegible para subsidio. */
    public boolean esElegibleSubsidio() {  // Método esElegibleSubsidio (devuelve boolean)
        return estrato >= 1 && estrato <= 3; // Estratos 4, 5 y 6 nunca reciben subsidio.
    }

    /**
     * Obtiene la estrategia residencial inyectada. Se usa para consultar las tablas de subsidio y contribución,
     * que viven en TarifaResidencialStrategy (una sola fuente de la regla de negocio).
     */
    private TarifaResidencialStrategy estrategiaResidencial() {  // Método estrategiaResidencial: Obtiene la estrategia residencial inyectada
        if (!(estrategiaTarifa instanceof TarifaResidencialStrategy)) { // instanceof: ¿es de ese tipo concreto?
            throw new IllegalStateException("El usuario residencial no tiene una estrategia tarifaria residencial asignada.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        return (TarifaResidencialStrategy) estrategiaTarifa; // Conversión segura tras la verificación.
    }

    /**
     * @return fracción máxima de subsidio que corresponde al hogar (0,70 / 0,40 / 0,15), o 0 si no es
     * elegible o no recibe el beneficio. Ejemplo: estrato 2 con beneficio => 0,40.
     */
    public double calcularPorcentajeSubsidio() {  // Método calcularPorcentajeSubsidio (devuelve double)
        if (!isBeneficioSubsidio()) { // Sin beneficio concedido o sin estrato elegible no hay descuento.
            return 0.0;  // Devuelve el valor de 0.0
        }
        return estrategiaResidencial().obtenerSubsidio(estrato); // Tabla de subsidios por estrato.
    }

    /** @return fracción de contribución de solidaridad (0,20 para estratos 5 y 6; 0 en los demás). */
    public double calcularPorcentajeContribucion() {  // Método calcularPorcentajeContribucion (devuelve double)
        return estrategiaResidencial().obtenerContribucion(estrato); // Tabla de contribuciones por estrato.
    }

    /** POLIMORFISMO: el cobro residencial se delega en la estrategia inyectada (Strategy). */
    @Override
    public BigDecimal calcularTarifa(Consumo consumo) {  // Método calcularTarifa: POLIMORFISMO
        if (estrategiaTarifa == null) { // Sin estrategia no hay fórmula.
            throw new IllegalStateException("El usuario no tiene estrategia tarifaria asignada.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        return estrategiaTarifa.calcularTarifa(consumo, this); // Delegación: this se pasa como usuario a cobrar.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return super.toString() + " {habitantes=" + numeroHabitantes + ", subsidio=" + beneficioSubsidio + "}"; // Extiende el resumen base.
    }
}
