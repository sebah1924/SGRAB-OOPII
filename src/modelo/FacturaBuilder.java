package modelo; // Paquete del dominio.

import java.time.LocalDate; // Fechas sin hora.

/**
 * PATRÓN BUILDER (Constructor paso a paso).
 *
 * Contexto: una Factura necesita muchos datos (número, periodo, fechas, usuario, consumos, sanciones,
 * desincentivo). En lugar de un constructor gigante, el Builder la arma con métodos encadenados
 * ("interfaz fluida"): new FacturaBuilder().conNumero("F-1").conPeriodo(a, b)....build().
 * Cada método devuelve el propio Builder (this) para poder encadenar la siguiente llamada.
 *
 * Ventajas: código legible, validación centralizada en validar() y una factura que solo se entrega
 * cuando está completa y con su valor total calculado.
 *
 * Uso recomendado: llamar primero paraUsuario(), porque los consumos y sanciones se verifican contra el usuario.
 */
public class FacturaBuilder {  // Declaración de la clase FacturaBuilder: PATRÓN BUILDER (Constructor paso a paso)

    private Factura factura; // Factura en construcción.

    /** Constructor sin parámetros: comienza con una factura vacía. */
    public FacturaBuilder() {  // Constructor: Constructor sin parámetros
        this.factura = new Factura(); // Producto vacío listo para configurarse.
    }

    /** Constructor que ya fija el usuario de la factura. */
    public FacturaBuilder(Usuario usuario) {  // Constructor: Constructor que ya fija el usuario de la factura
        this.factura = new Factura(); // Producto vacío.
        this.factura.setUsuario(usuario); // Usuario obligatorio (valida nulo).
    }

    /** Fija el número de factura. */
    public FacturaBuilder conNumero(String numero) {  // Método conNumero: Fija el número de factura
        factura.setNumero(numero); // Valida y asigna.
        return this; // Permite encadenar.
    }

    /** Fija el periodo facturado (inicio y fin). */
    public FacturaBuilder conPeriodo(LocalDate inicio, LocalDate fin) {  // Método conPeriodo: Fija el periodo facturado (inicio y fin)
        factura.setPeriodoInicio(inicio); // Valida y asigna el inicio.
        factura.setPeriodoFin(fin);       // Valida y asigna el fin.
        return this; // Permite encadenar.
    }

    /** Fija la fecha de emisión. */
    public FacturaBuilder conFechaEmision(LocalDate fecha) {  // Método conFechaEmision: Fija la fecha de emisión
        factura.setFechaEmision(fecha); // Valida y asigna.
        return this; // Permite encadenar.
    }

    /** Fija la fecha de vencimiento (posterior a la emisión). */
    public FacturaBuilder conFechaVencimiento(LocalDate fecha) {  // Método conFechaVencimiento: Fija la fecha de vencimiento (posterior a la emisión)
        factura.setFechaVencimiento(fecha); // Valida y asigna.
        return this; // Permite encadenar.
    }

    /** Fija el usuario de la factura. */
    public FacturaBuilder paraUsuario(Usuario usuario) {  // Método paraUsuario: Fija el usuario de la factura
        factura.setUsuario(usuario); // Valida y asigna.
        return this; // Permite encadenar.
    }

    /** Agrega un consumo del periodo. */
    public FacturaBuilder agregarConsumo(Consumo consumo) {  // Método agregarConsumo: Agrega un consumo del periodo
        factura.agregarConsumo(consumo); // Valida usuario y duplicados.
        return this; // Permite encadenar.
    }

    /** Agrega una sanción asociada. */
    public FacturaBuilder agregarSancion(Sancion sancion) {  // Método agregarSancion: Agrega una sanción asociada
        factura.agregarSancion(sancion); // Valida usuario y duplicados.
        return this; // Permite encadenar.
    }

    /** Asocia el desincentivo por consumo excesivo (solo tiene efecto en usuarios residenciales). */
    public FacturaBuilder conDesincentivo(Desincentivo des) {  // Método conDesincentivo: Asocia el desincentivo por consumo excesivo (solo tiene efecto en usuarios residenciales)
        factura.setDesincentivo(des); // Asigna (admite null).
        return this; // Permite encadenar.
    }

    /** @return true si la factura en construcción tiene todo lo necesario para entregarse. */
    public boolean validar() {  // Método validar (devuelve boolean)
        return factura.getNumero() != null          // Número asignado.
                && factura.getUsuario() != null     // Usuario asignado.
                && factura.validarFechas()          // Periodo y fechas coherentes.
                && factura.contarConsumos() > 0     // Una factura incluye los consumos del periodo.
                && factura.validarTarifasVigentes(); // Las tarifas deben estar vigentes en la fecha de facturación.
    }

    /**
     * Entrega la factura terminada con su valor total calculado y deja el Builder limpio (reset).
     * Reiniciar el Builder evita que se siga modificando una factura ya entregada.
     *
     * @throws IllegalStateException si faltan datos obligatorios.
     */
    public Factura build() {  // Método build: Entrega la factura terminada con su valor total calculado y deja el Builder limpio (reset)
        if (!validar()) { // No se entrega un producto incompleto.
            throw new IllegalStateException("La factura está incompleta o inválida: se requieren número, usuario, periodo, "  // Lanza IllegalStateException con un mensaje claro para el usuario
                    + "fechas válidas, al menos un consumo y tarifas vigentes en la fecha de facturación.");  // Continúa el texto de la instrucción anterior.
        }
        factura.calcularValorTotal();  // Cargo fijo + variable + desincentivo + sanciones - subsidios.
        Factura terminada = factura;   // Se guarda la referencia al producto final.
        reset();                       // El Builder queda listo para una nueva factura.
        return terminada;  // Devuelve el valor de terminada
    }

    /** Descarta lo construido y comienza una factura nueva. */
    public FacturaBuilder reset() {  // Método reset: Descarta lo construido y comienza una factura nueva
        this.factura = new Factura(); // Nuevo producto vacío.
        return this; // Permite encadenar.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "FacturaBuilder [en construcción: " + factura + "]"; // Muestra la factura parcial.
    }
}
