package modelo; // Paquete del dominio.

import java.math.BigDecimal;   // Dinero con precisión exacta.
import java.math.RoundingMode; // Modo de redondeo (HALF_UP = el "redondeo escolar": 0,5 sube).
import java.time.LocalDate;    // Fechas de vigencia.

/**
 * Tarifa del servicio de acueducto según la Resolución CRA 688 de 2014.
 *
 * La tarifa tiene DOS partes:
 *  1) CARGO FIJO ($/suscriptor/mes): se paga por tener el servicio disponible, sin importar el consumo.
 *  2) CARGO VARIABLE ($/m3): depende de cuánta agua se consuma.
 * Ejemplo: cargo fijo 10.000, variable 3.000, consumo 15 m3 => 10.000 + (3.000 x 15) = $55.000.
 *
 * REGLAS DE NEGOCIO:
 *  - La vigencia es un rango de fechas donde la fecha de inicio debe ser ANTERIOR a la de fin.
 *  - No puede haber dos tarifas vigentes al mismo tiempo para el mismo tipo de usuario
 *    (se detecta con seSolapaCon(); el Regulador lo hace cumplir al registrar).
 *  - El subsidio nunca puede superar el 70 % del costo del suministro.
 *
 * Convención: "subsidio" se guarda como FRACCIÓN decimal (0,40 significa 40 %).
 */
public class Tarifa {  // Declaración de la clase Tarifa: Tarifa del servicio de acueducto según la Resolución CRA 688 de 2014

    private static final int ESCALA = 2;                          // Decimales de los valores monetarios.
    private static final RoundingMode REDONDEO = RoundingMode.HALF_UP; // Redondeo estándar para dinero.
    public static final double SUBSIDIO_MAXIMO = 0.70;            // Tope legal: el subsidio nunca supera el 70 %.

    private String codigo;            // Identificador único de la tarifa.
    private TipoUsuario tipoUsuario;  // Tipo de usuario al que se aplica.
    private BigDecimal cargoFijo;     // $/suscriptor/mes.
    private BigDecimal cargoVariable; // $/m3.
    private double subsidio;          // Fracción de subsidio autorizada por esta tarifa (0 a 0,70).
    private LocalDate vigenciaInicio; // Primer día de vigencia (inclusive).
    private LocalDate vigenciaFin;    // Último día de vigencia (inclusive).

    /** Constructor vacío (para la capa DAO). */
    public Tarifa() {  // Constructor: Constructor vacío (para la capa DAO)
        // Sin instrucciones.
    }

    /**
     * Constructor completo. Se asigna primero el inicio y luego el fin, porque el setter del fin
     * compara contra el inicio ya asignado.
     */
    public Tarifa(String codigo, TipoUsuario tipoUsuario, BigDecimal cargoFijo, BigDecimal cargoVariable,  // Constructor: Constructor completo
                  double subsidio, LocalDate inicio, LocalDate fin) {  // Continuación de la instrucción de la línea anterior.
        setCodigo(codigo);               // Código obligatorio.
        setTipoUsuario(tipoUsuario);     // Tipo obligatorio.
        setCargoFijo(cargoFijo);         // Cargo fijo >= 0.
        setCargoVariable(cargoVariable); // Cargo variable >= 0.
        setSubsidio(subsidio);           // Subsidio entre 0 y 70 %.
        setVigenciaInicio(inicio);       // Inicio obligatorio.
        setVigenciaFin(fin);             // Fin obligatorio y posterior al inicio.
    }

    /** @return el código de la tarifa. */
    public String getCodigo() {  // Getter: devuelve codigo
        return codigo; // Lectura simple.
    }

    /** Asigna el código (obligatorio). */
    public void setCodigo(String codigo) {  // Método setCodigo: Asigna el código (obligatorio)
        if (codigo == null || codigo.trim().isEmpty()) { // Regla: código obligatorio.
            throw new IllegalArgumentException("El código de la tarifa es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.codigo = codigo.trim(); // Normaliza espacios.
    }

    /** @return el tipo de usuario de la tarifa. */
    public TipoUsuario getTipoUsuario() {  // Getter: devuelve tipo usuario
        return tipoUsuario; // Lectura simple.
    }

    /** Asigna el tipo de usuario (obligatorio). */
    public void setTipoUsuario(TipoUsuario tipo) {  // Método setTipoUsuario: Asigna el tipo de usuario (obligatorio)
        if (tipo == null) { // Regla: cada tarifa es para un tipo de usuario.
            throw new IllegalArgumentException("El tipo de usuario de la tarifa es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.tipoUsuario = tipo; // Asignación tras validar.
    }

    /** @return el cargo fijo. */
    public BigDecimal getCargoFijo() {  // Getter: devuelve cargo fijo
        return cargoFijo; // BigDecimal es inmutable: seguro de exponer.
    }

    /** Asigna el cargo fijo (>= 0). */
    public void setCargoFijo(BigDecimal cargo) {  // Método setCargoFijo: Asigna el cargo fijo (>= 0)
        if (cargo == null || cargo.signum() < 0) { // signum() devuelve -1, 0 o 1 según el signo.
            throw new IllegalArgumentException("El cargo fijo es obligatorio y no puede ser negativo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.cargoFijo = cargo; // Asignación tras validar.
    }

    /** @return el cargo variable. */
    public BigDecimal getCargoVariable() {  // Getter: devuelve cargo variable
        return cargoVariable; // Lectura simple.
    }

    /** Asigna el cargo variable (>= 0). */
    public void setCargoVariable(BigDecimal cargo) {  // Método setCargoVariable: Asigna el cargo variable (>= 0)
        if (cargo == null || cargo.signum() < 0) { // Regla: no existen cargos negativos.
            throw new IllegalArgumentException("El cargo variable es obligatorio y no puede ser negativo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.cargoVariable = cargo; // Asignación tras validar.
    }

    /** @return la fracción de subsidio (0,40 = 40 %). */
    public double getSubsidio() {  // Getter: devuelve subsidio
        return subsidio; // Lectura simple.
    }

    /** Asigna la fracción de subsidio (entre 0 y 0,70). */
    public void setSubsidio(double sub) {  // Método setSubsidio: Asigna la fracción de subsidio (entre 0 y 0,70)
        if (Double.isNaN(sub) || sub < 0 || sub > SUBSIDIO_MAXIMO) { // Regla: nunca supera el 70 %.
            throw new IllegalArgumentException("El subsidio debe estar entre 0 y 0,70 (70 %).");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.subsidio = sub; // Asignación tras validar.
    }

    /** @return el inicio de la vigencia. */
    public LocalDate getVigenciaInicio() {  // Getter: devuelve vigencia inicio
        return vigenciaInicio; // Lectura simple.
    }

    /** Asigna el inicio de la vigencia (si ya hay fin, debe ser anterior a este). */
    public void setVigenciaInicio(LocalDate fecha) {  // Método setVigenciaInicio: Asigna el inicio de la vigencia (si ya hay fin, debe ser anterior a este)
        if (fecha == null) { // Regla: la vigencia necesita fecha de inicio.
            throw new IllegalArgumentException("La fecha de inicio de vigencia es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (vigenciaFin != null && !fecha.isBefore(vigenciaFin)) { // Regla: inicio ANTERIOR al fin.
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.vigenciaInicio = fecha; // Asignación tras validar.
    }

    /** @return el fin de la vigencia. */
    public LocalDate getVigenciaFin() {  // Getter: devuelve vigencia fin
        return vigenciaFin; // Lectura simple.
    }

    /** Asigna el fin de la vigencia (debe ser posterior al inicio). */
    public void setVigenciaFin(LocalDate fecha) {  // Método setVigenciaFin: Asigna el fin de la vigencia (debe ser posterior al inicio)
        if (fecha == null) { // Regla: la vigencia necesita fecha de fin.
            throw new IllegalArgumentException("La fecha de fin de vigencia es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (vigenciaInicio != null && !vigenciaInicio.isBefore(fecha)) { // Regla: inicio ANTERIOR al fin.
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.vigenciaFin = fecha; // Asignación tras validar.
    }

    /** @return true si la tarifa está vigente HOY. */
    public boolean estaVigente() {  // Método estaVigente (devuelve boolean)
        return estaVigenteEn(LocalDate.now()); // Reutiliza estaVigenteEn con la fecha actual.
    }

    /** @return true si la fecha cae dentro del rango de vigencia (extremos incluidos). */
    public boolean estaVigenteEn(LocalDate fecha) {  // Método estaVigenteEn (devuelve boolean)
        if (fecha == null || vigenciaInicio == null || vigenciaFin == null) { // Sin datos no se puede afirmar vigencia.
            return false;  // Devuelve el valor de false
        }
        return !fecha.isBefore(vigenciaInicio) && !fecha.isAfter(vigenciaFin); // inicio <= fecha <= fin.
    }

    /**
     * Regla "no puede haber dos tarifas vigentes al mismo tiempo para el mismo tipo de usuario".
     * Dos rangos [a1,a2] y [b1,b2] se solapan si a1 <= b2 Y b1 <= a2.
     */
    public boolean seSolapaCon(Tarifa otra) {  // Método seSolapaCon: Regla "no puede haber dos tarifas vigentes al mismo tiempo para el mismo tipo de usuario"
        if (otra == null || this.tipoUsuario != otra.tipoUsuario) { // Tarifas de tipos distintos nunca chocan.
            return false;  // Devuelve el valor de false
        }
        return !this.vigenciaInicio.isAfter(otra.vigenciaFin) && !otra.vigenciaInicio.isAfter(this.vigenciaFin);  // Devuelve: no this.vigenciaInicio.isAfter(otra.vigenciaFin) y no otra.vigenciaInici...
    }

    /** Cargo fijo + (cargo variable x consumo). Es la fórmula base sin subsidios ni recargos. */
    public BigDecimal calcularFacturaBasica(double consumoM3) {  // Método calcularFacturaBasica: Cargo fijo + (cargo variable x consumo)
        BigDecimal variable = cargoVariable.multiply(BigDecimal.valueOf(consumoM3)); // $/m3 x m3.
        return cargoFijo.add(variable).setScale(ESCALA, REDONDEO); // Se suma el fijo y se fija a 2 decimales.
    }

    /** Fórmula base x (1 - subsidio de ESTA tarifa). Ejemplo: 55.000 x 0,60 = 33.000. */
    public BigDecimal calcularFacturaConSubsidio(double consumoM3) {  // Método calcularFacturaConSubsidio: Fórmula base x (1 - subsidio de ESTA tarifa)
        BigDecimal factor = BigDecimal.ONE.subtract(BigDecimal.valueOf(subsidio)); // 1 - subsidio.
        return calcularFacturaBasica(consumoM3).multiply(factor).setScale(ESCALA, REDONDEO);  // Devuelve: calcularFacturaBasica(consumoM3).multiply(factor).setScale(ESCALA, REDON...
    }

    /** Fórmula base x (1 + contribución). La contribución de solidaridad se SUMA al cargo. */
    public BigDecimal calcularFacturaConContribucion(double consumoM3, double contribucion) {  // Método calcularFacturaConContribucion: Fórmula base x (1 + contribución)
        BigDecimal factor = BigDecimal.ONE.add(BigDecimal.valueOf(contribucion)); // 1 + contribución.
        return calcularFacturaBasica(consumoM3).multiply(factor).setScale(ESCALA, REDONDEO);  // Devuelve: calcularFacturaBasica(consumoM3).multiply(factor).setScale(ESCALA, REDON...
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "Tarifa [codigo=" + codigo + ", tipo=" + tipoUsuario + ", cargoFijo=" + cargoFijo  // Devuelve: "Tarifa [codigo=" + codigo + ", tipo=" + tipoUsuario + ", cargoFijo=" + ...
                + ", cargoVariable=" + cargoVariable + ", subsidio=" + subsidio  // Continúa el texto de la instrucción anterior.
                + ", vigencia=" + vigenciaInicio + " a " + vigenciaFin + "]"; // Resumen.
    }
}
