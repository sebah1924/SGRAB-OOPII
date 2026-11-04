package modelo; // Paquete del dominio.

import java.math.BigDecimal; // Dinero con precisión exacta.
import java.time.LocalDate;  // Fechas sin hora.

/**
 * Usuario INDUSTRIAL (fábricas).
 *
 * REGLAS DE NEGOCIO:
 *  - código industrial y proceso productivo obligatorios.
 *  - carga contaminante medida en unidades de contaminación por litro (ej.: DBO mg/L) y MAYOR O IGUAL a cero.
 *  - no recibe subsidio; paga además un cargo por carga contaminante (Decreto 1076 de 2015, tasa retributiva).
 *
 * Contexto: DBO = Demanda Bioquímica de Oxígeno, la cantidad de oxígeno que consumen los microorganismos
 * para degradar la materia orgánica del agua; a más DBO, más contaminada está el agua vertida.
 */
public class UsuarioIndustrial extends Usuario {  // Declaración de la clase UsuarioIndustrial: Usuario INDUSTRIAL (fábricas)

    private String codigoIndustrial;   // Código del establecimiento industrial.
    private String procesoProductivo;  // Proceso que realiza la fábrica (ej.: curtiembre, lácteos).
    private double cargaContaminante;  // DBO en mg/L (>= 0).

    /** Constructor vacío (para la capa DAO). */
    public UsuarioIndustrial() {  // Constructor: Constructor vacío (para la capa DAO)
        super(); // Constructor vacío de Usuario.
        setTipoUsuario(TipoUsuario.INDUSTRIAL); // Tipo fijo.
    }

    /** Constructor completo. */
    public UsuarioIndustrial(String identificacion, String nombre, String apellido, LocalDate fechaNacimiento,  // Constructor: Constructor completo
                             String direccion, String telefono, String email, String codigoSuscriptor,  // Continuación de la instrucción de la línea anterior.
                             LocalDate fechaRegistro, int estrato, CanalNotificacion canalPreferido,  // Instrucción: LocalDate fechaRegistro, int estrato, CanalNotificacion c...
                             String codigoIndustrial, String procesoProductivo, double cargaContaminante) {  // Abre el bloque de código: String codigoIndustrial, String procesoProducti...
        super(identificacion, nombre, apellido, fechaNacimiento, direccion, telefono, email,  // Llama al constructor de la clase base para iniciar la parte heredada
                codigoSuscriptor, fechaRegistro, estrato, canalPreferido); // Datos de Persona y Usuario.
        setTipoUsuario(TipoUsuario.INDUSTRIAL);          // Tipo fijo.
        setCodigoIndustrial(codigoIndustrial);           // Obligatorio.
        setProcesoProductivo(procesoProductivo);         // Obligatorio.
        setCargaContaminante(cargaContaminante);         // Mayor o igual a cero.
    }

    /** @return el código industrial. */
    public String getCodigoIndustrial() {  // Getter: devuelve codigo industrial
        return codigoIndustrial; // Lectura simple.
    }

    /** Asigna el código industrial (obligatorio). */
    public void setCodigoIndustrial(String codigo) {  // Método setCodigoIndustrial: Asigna el código industrial (obligatorio)
        if (codigo == null || codigo.trim().isEmpty()) { // Regla: código industrial obligatorio.
            throw new IllegalArgumentException("El código industrial es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.codigoIndustrial = codigo.trim(); // Normaliza espacios.
    }

    /** @return el proceso productivo. */
    public String getProcesoProductivo() {  // Getter: devuelve proceso productivo
        return procesoProductivo; // Lectura simple.
    }

    /** Asigna el proceso productivo (obligatorio). */
    public void setProcesoProductivo(String proceso) {  // Método setProcesoProductivo: Asigna el proceso productivo (obligatorio)
        if (proceso == null || proceso.trim().isEmpty()) { // Regla: proceso productivo obligatorio.
            throw new IllegalArgumentException("El proceso productivo es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.procesoProductivo = proceso.trim(); // Normaliza espacios.
    }

    /** @return la carga contaminante (DBO mg/L). */
    public double getCargaContaminante() {  // Getter: devuelve carga contaminante
        return cargaContaminante; // Lectura simple.
    }

    /** Asigna la carga contaminante (mayor o igual a cero). */
    public void setCargaContaminante(double carga) {  // Método setCargaContaminante: Asigna la carga contaminante (mayor o igual a cero)
        if (Double.isNaN(carga) || carga < 0) { // Regla: la contaminación medida no puede ser negativa.
            throw new IllegalArgumentException("La carga contaminante debe ser mayor o igual a cero.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.cargaContaminante = carga; // Asignación tras validar.
    }

    /** POLIMORFISMO: el cobro industrial se delega en la estrategia inyectada (Strategy). */
    @Override
    public BigDecimal calcularTarifa(Consumo consumo) {  // Método calcularTarifa: POLIMORFISMO
        if (estrategiaTarifa == null) { // Sin estrategia no hay fórmula.
            throw new IllegalStateException("El usuario no tiene estrategia tarifaria asignada.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        return estrategiaTarifa.calcularTarifa(consumo, this); // Delegación.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return super.toString() + " {codigoIndustrial=" + codigoIndustrial + ", proceso=" + procesoProductivo  // Devuelve: super.toString() + " {codigoIndustrial=" + codigoIndustrial + ", proceso...
                + ", cargaContaminante=" + cargaContaminante + "}"; // Extiende el resumen base.
    }
}
