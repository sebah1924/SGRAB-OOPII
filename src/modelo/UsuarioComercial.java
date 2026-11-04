package modelo; // Paquete del dominio.

import java.math.BigDecimal; // Dinero con precisión exacta.
import java.time.LocalDate;  // Fechas sin hora.

/**
 * Usuario COMERCIAL (restaurantes, panaderías, supermercados...).
 *
 * REGLAS DE NEGOCIO:
 *  - nombre del establecimiento obligatorio.
 *  - actividad económica con una descripción válida (ej.: "restaurante", "panadería", "supermercado").
 *  - área operativa MAYOR que cero.
 *  - no recibe subsidio ni paga contribución; el cargo variable se aplica sobre el consumo total, sin topes.
 */
public class UsuarioComercial extends Usuario {  // Declaración de la clase UsuarioComercial: Usuario COMERCIAL (restaurantes, panaderías, supermercados...)

    private String nombreEstablecimiento; // Nombre comercial del negocio.
    private String actividadEconomica;    // Descripción de la actividad (solo letras y espacios).
    private double areaOperacionM2;       // Área operativa en metros cuadrados (> 0).

    /** Constructor vacío (para la capa DAO). */
    public UsuarioComercial() {  // Constructor: Constructor vacío (para la capa DAO)
        super(); // Constructor vacío de Usuario.
        setTipoUsuario(TipoUsuario.COMERCIAL); // Tipo fijo.
    }

    /** Constructor completo. */
    public UsuarioComercial(String identificacion, String nombre, String apellido, LocalDate fechaNacimiento,  // Constructor: Constructor completo
                            String direccion, String telefono, String email, String codigoSuscriptor,  // Continuación de la instrucción de la línea anterior.
                            LocalDate fechaRegistro, int estrato, CanalNotificacion canalPreferido,  // Instrucción: LocalDate fechaRegistro, int estrato, CanalNotificacion c...
                            String nombreEstablecimiento, String actividadEconomica, double areaOperacionM2) {  // Abre el bloque de código: String nombreEstablecimiento, String actividadE...
        super(identificacion, nombre, apellido, fechaNacimiento, direccion, telefono, email,  // Llama al constructor de la clase base para iniciar la parte heredada
                codigoSuscriptor, fechaRegistro, estrato, canalPreferido); // Datos de Persona y Usuario.
        setTipoUsuario(TipoUsuario.COMERCIAL);              // Tipo fijo.
        setNombreEstablecimiento(nombreEstablecimiento);    // Obligatorio.
        setActividadEconomica(actividadEconomica);          // Descripción válida.
        setAreaOperacionM2(areaOperacionM2);                // Mayor que cero.
    }

    /** @return el nombre del establecimiento. */
    public String getNombreEstablecimiento() {  // Getter: devuelve nombre establecimiento
        return nombreEstablecimiento; // Lectura simple.
    }

    /** Asigna el nombre del establecimiento (obligatorio). */
    public void setNombreEstablecimiento(String nombre) {  // Método setNombreEstablecimiento: Asigna el nombre del establecimiento (obligatorio)
        if (nombre == null || nombre.trim().isEmpty()) { // Regla: todo comercio tiene nombre.
            throw new IllegalArgumentException("El nombre del establecimiento es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.nombreEstablecimiento = nombre.trim(); // Normaliza espacios.
    }

    /** @return la actividad económica. */
    public String getActividadEconomica() {  // Getter: devuelve actividad economica
        return actividadEconomica; // Lectura simple.
    }

    /** Asigna la actividad económica (descripción válida). */
    public void setActividadEconomica(String actividad) {  // Método setActividadEconomica: Asigna la actividad económica (descripción válida)
        if (!esActividadValida(actividad)) { // Regla: descripción válida.
            throw new IllegalArgumentException("La actividad económica debe ser una descripción válida (ej.: restaurante).");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.actividadEconomica = actividad.trim(); // Normaliza espacios.
    }

    /** @return el área operativa en m2. */
    public double getAreaOperacionM2() {  // Getter: devuelve area operacion m2
        return areaOperacionM2; // Lectura simple.
    }

    /** Asigna el área operativa (mayor que cero). */
    public void setAreaOperacionM2(double area) {  // Método setAreaOperacionM2: Asigna el área operativa (mayor que cero)
        if (Double.isNaN(area) || area <= 0) { // Regla: el área operativa es mayor que cero.
            throw new IllegalArgumentException("El área operativa debe ser mayor que cero.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.areaOperacionM2 = area; // Asignación tras validar.
    }

    /**
     * Criterio de "descripción válida": al menos 3 caracteres, solo letras (con tildes) y espacios.
     * \p{L} es la clase Unicode de "cualquier letra", por lo que acepta "panadería" y "peluquería".
     */
    private static boolean esActividadValida(String actividad) {  // Método esActividadValida: Criterio de "descripción válida"
        return actividad != null && actividad.trim().matches("^[\\p{L} ]{3,}$"); // Regex de letras y espacios.
    }

    /** @return true si la actividad económica almacenada es una descripción válida. */
    public boolean validarActividad() {  // Método validarActividad (devuelve boolean)
        return esActividadValida(actividadEconomica); // Misma regla que el setter.
    }

    /** POLIMORFISMO: el cobro comercial se delega en la estrategia inyectada (Strategy). */
    @Override
    public BigDecimal calcularTarifa(Consumo consumo) {  // Método calcularTarifa: POLIMORFISMO
        if (estrategiaTarifa == null) { // Sin estrategia no hay fórmula.
            throw new IllegalStateException("El usuario no tiene estrategia tarifaria asignada.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        return estrategiaTarifa.calcularTarifa(consumo, this); // Delegación.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return super.toString() + " {establecimiento=" + nombreEstablecimiento + ", actividad=" + actividadEconomica  // Devuelve: super.toString() + " {establecimiento=" + nombreEstablecimiento + ", act...
                + ", areaM2=" + areaOperacionM2 + "}"; // Extiende el resumen base.
    }
}
