package modelo; // Paquete del dominio.

import java.math.BigDecimal; // Dinero con precisión exacta.
import java.time.LocalDate;  // Fecha de la lectura.

/**
 * Lectura de consumo de agua de un usuario.
 *
 * REGLAS DE NEGOCIO (enunciado, "Consumo"):
 *  - código obligatorio; volumen en m3 mayor o igual a cero; fecha de lectura NO futura;
 *  - tarifa aplicada y tipo de lectura: REAL (tomada en sitio) o ESTIMADA (calculada por promedio).
 *
 * SUPUESTO DE DISEÑO: cada Consumo representa UNA lectura MENSUAL. Como el cargo fijo se expresa en
 * $/suscriptor/mes, cada lectura mensual incluye su cargo fijo; así una factura bimestral (dos lecturas)
 * queda con el cargo fijo multiplicado por 2, exactamente como pide el enunciado.
 */
public class Consumo {  // Declaración de la clase Consumo: Lectura de consumo de agua de un usuario

    private String codigo;          // Identificador único de la lectura.
    private double volumenM3;       // Volumen consumido en metros cúbicos (>= 0).
    private LocalDate fechaLectura; // Día en que se tomó la lectura (no futuro).
    private TipoLectura tipoLectura; // REAL o ESTIMADA.
    private Tarifa tarifaAplicada;  // Tarifa vigente con la que se valora este consumo.
    private Usuario usuario;        // Usuario al que pertenece la lectura.

    /** Constructor vacío (para la capa DAO). */
    public Consumo() {  // Constructor: Constructor vacío (para la capa DAO)
        // Sin instrucciones.
    }

    /** Constructor completo; valida cada dato mediante los setters. */
    public Consumo(String codigo, double volumenM3, LocalDate fechaLectura, TipoLectura tipoLectura,  // Constructor: Constructor completo; valida cada dato mediante los setters
                   Tarifa tarifaAplicada, Usuario usuario) {  // Continuación de la instrucción de la línea anterior.
        setCodigo(codigo);                 // Código obligatorio.
        setVolumenM3(volumenM3);           // Volumen >= 0.
        setFechaLectura(fechaLectura);     // Fecha no futura.
        setTipoLectura(tipoLectura);       // Tipo obligatorio.
        setTarifaAplicada(tarifaAplicada); // Tarifa obligatoria.
        setUsuario(usuario);               // Usuario obligatorio.
        if (!validarCoherenciaTarifa()) {  // Regla: la tarifa debe ser del mismo tipo que el usuario (una tarifa se define por tipo de usuario).
            throw new IllegalArgumentException("La tarifa aplicada debe ser del mismo tipo de usuario que el titular del consumo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
    }

    /** @return el código del consumo. */
    public String getCodigo() {  // Getter: devuelve codigo
        return codigo; // Lectura simple.
    }

    /** Asigna el código (obligatorio). */
    public void setCodigo(String codigo) {  // Método setCodigo: Asigna el código (obligatorio)
        if (codigo == null || codigo.trim().isEmpty()) { // Regla: código obligatorio.
            throw new IllegalArgumentException("El código del consumo es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.codigo = codigo.trim(); // Normaliza espacios.
    }

    /** @return el volumen en m3. */
    public double getVolumenM3() {  // Getter: devuelve volumen m3
        return volumenM3; // Lectura simple.
    }

    /** Asigna el volumen en m3 (debe ser mayor o igual a cero). */
    public void setVolumenM3(double vol) {  // Método setVolumenM3: Asigna el volumen en m3 (debe ser mayor o igual a cero)
        if (Double.isNaN(vol) || vol < 0) { // NaN ("no es un número") también es inválido; un volumen negativo es físicamente imposible.
            throw new IllegalArgumentException("El volumen debe ser mayor o igual a cero.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.volumenM3 = vol; // Asignación tras validar.
    }

    /** @return la fecha de la lectura. */
    public LocalDate getFechaLectura() {  // Getter: devuelve fecha lectura
        return fechaLectura; // LocalDate es inmutable.
    }

    /** Asigna la fecha de lectura (no puede ser futura). */
    public void setFechaLectura(LocalDate fecha) {  // Método setFechaLectura: Asigna la fecha de lectura (no puede ser futura)
        if (fecha == null || fecha.isAfter(LocalDate.now())) { // Regla: no se puede leer un medidor en el futuro.
            throw new IllegalArgumentException("La fecha de lectura es obligatoria y no puede ser futura.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.fechaLectura = fecha; // Asignación tras validar.
    }

    /** @return el tipo de lectura (REAL o ESTIMADA). */
    public TipoLectura getTipoLectura() {  // Getter: devuelve tipo lectura
        return tipoLectura; // Lectura simple.
    }

    /** Asigna el tipo de lectura (obligatorio). */
    public void setTipoLectura(TipoLectura tipo) {  // Método setTipoLectura: Asigna el tipo de lectura (obligatorio)
        if (tipo == null) { // Regla: toda lectura es real o estimada.
            throw new IllegalArgumentException("El tipo de lectura es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.tipoLectura = tipo; // Asignación tras validar.
    }

    /** @return la tarifa aplicada. */
    public Tarifa getTarifaAplicada() {  // Getter: devuelve tarifa aplicada
        return tarifaAplicada; // Lectura simple.
    }

    /** Asigna la tarifa aplicada (obligatoria). */
    public void setTarifaAplicada(Tarifa tarifa) {  // Método setTarifaAplicada: Asigna la tarifa aplicada (obligatoria)
        if (tarifa == null) { // Regla: no se puede valorar un consumo sin tarifa.
            throw new IllegalArgumentException("La tarifa aplicada es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.tarifaAplicada = tarifa; // Asignación tras validar.
    }

    /** @return el usuario dueño de la lectura. */
    public Usuario getUsuario() {  // Getter: devuelve usuario
        return usuario; // Lectura simple.
    }

    /** Asigna el usuario dueño de la lectura (obligatorio). */
    public void setUsuario(Usuario usuario) {  // Método setUsuario: Asigna el usuario dueño de la lectura (obligatorio)
        if (usuario == null) { // Regla: una lectura siempre pertenece a un suscriptor.
            throw new IllegalArgumentException("El usuario del consumo es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.usuario = usuario; // Asignación tras validar.
    }

    /**
     * Costo base del consumo: cargo fijo + (cargo variable x volumen), SIN subsidios ni recargos.
     * Ejemplo del enunciado: 10.000 + (3.000 x 15) = 55.000.
     */
    public BigDecimal calcularCosto() {  // Método calcularCosto: Costo base del consumo
        return tarifaAplicada.calcularFacturaBasica(volumenM3); // Delega en Tarifa, que conoce los cargos.
    }

    /** @return true si la lectura fue tomada en sitio (no estimada). */
    public boolean esLecturaReal() {  // Método esLecturaReal (devuelve boolean)
        return tipoLectura == TipoLectura.REAL; // Comparación de enums con == (son singletons).
    }

    /**
     * Devuelve la tarifa aplicada verificando que sea del tipo de usuario esperado. Lo usan las estrategias
     * tarifarias para no repetir cada una la misma validación (principio DRY: una regla, un solo lugar).
     *
     * @param tipo tipo de usuario para el que se calcula el cobro.
     * @throws IllegalArgumentException si no hay tarifa o si es de otro tipo.
     */
    public Tarifa obtenerTarifaDeTipo(TipoUsuario tipo) {  // Método obtenerTarifaDeTipo: Devuelve la tarifa aplicada verificando que sea del tipo de usuario esperado
        if (tarifaAplicada == null) { // Sin tarifa no existe base de cálculo.
            throw new IllegalArgumentException("El consumo no tiene tarifa aplicada.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (tarifaAplicada.getTipoUsuario() != tipo) { // Una tarifa residencial no puede cobrar a un comercio, etc.
            throw new IllegalArgumentException("La tarifa aplicada no es de tipo " + tipo.name().toLowerCase() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        return tarifaAplicada; // Tarifa verificada, lista para usarse.
    }

    /** @return true si la tarifa aplicada es del mismo tipo que el usuario dueño de la lectura. */
    public boolean validarCoherenciaTarifa() {  // Método validarCoherenciaTarifa (devuelve boolean)
        return tarifaAplicada != null && usuario != null  // Devuelve: tarifaAplicada es distinto de nulo y usuario es distinto de nulo
                && tarifaAplicada.getTipoUsuario() == usuario.getTipoUsuario(); // Mismo tipo en ambos lados.
    }

    /** @return true si el volumen cumple la regla (>= 0). */
    public boolean validarVolumen() {  // Método validarVolumen (devuelve boolean)
        return !Double.isNaN(volumenM3) && volumenM3 >= 0; // Misma regla que el setter.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "Consumo [codigo=" + codigo + ", volumenM3=" + volumenM3 + ", fechaLectura=" + fechaLectura  // Devuelve: "Consumo [codigo=" + codigo + ", volumenM3=" + volumenM3 + ", fechaLectu...
                + ", tipoLectura=" + tipoLectura + "]"; // Resumen.
    }
}
