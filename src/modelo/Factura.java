package modelo; // Paquete del dominio.

import java.math.BigDecimal;   // Dinero con precisión exacta.
import java.math.RoundingMode; // Redondeo monetario.
import java.time.LocalDate;    // Fechas sin hora.
import java.util.ArrayList;    // Lista dinámica.
import java.util.Collections;  // Vistas de solo lectura.
import java.util.List;         // Interfaz de lista.

/**
 * Factura del servicio de acueducto de un usuario. Implementa IObservable (patrón Observer).
 *
 * COMPOSICIÓN (diagrama): la factura se compone de Pagos; un pago no existe sin su factura.
 * AGREGACIÓN: la factura agrupa consumos, sanciones y un desincentivo, que existen por sí mismos.
 *
 * REGLAS DE NEGOCIO (enunciado, "Qué contiene una factura" y Necesidades 8 y 9):
 *  - número obligatorio y único (lo verifica quien la registra); periodo (rango de fechas);
 *    fecha de emisión; fecha de vencimiento POSTERIOR a la emisión.
 *  - VALOR TOTAL = cargo fijo + cargo variable por consumo + desincentivo + sanciones económicas
 *    - subsidios (la contribución de solidaridad se suma al cargo). Cada lectura se valora con la estrategia
 *    tarifaria del usuario, que ya descuenta subsidios y suma contribuciones.
 *  - SALDO PENDIENTE = valor total - pagos realizados.
 *  - El estado de pago se actualiza automáticamente: PAGADA si los pagos cubren el total; VENCIDA si pasó la
 *    fecha de vencimiento sin pago completo; PENDIENTE si aún está dentro del plazo.
 *  - Confiabilidad y trazabilidad: una vez que la factura recibe pagos, su valor total ya no se recalcula.
 */
public class Factura implements IObservable {  // Declaración de la clase Factura: Factura del servicio de acueducto de un usuario

    private String numero;                 // Número único de la factura.
    private LocalDate periodoInicio;       // Primer día del periodo facturado.
    private LocalDate periodoFin;          // Último día del periodo facturado.
    private LocalDate fechaEmision;        // Día en que se emite.
    private LocalDate fechaVencimiento;    // Día límite de pago (posterior a la emisión).
    private BigDecimal valorTotal = BigDecimal.ZERO; // Total a pagar; se calcula con calcularValorTotal().
    private EstadoPago estadoPago = EstadoPago.PENDIENTE; // Toda factura nace pendiente de pago.
    private Usuario usuario;               // Usuario al que pertenece la factura.
    private List<Consumo> listaConsumos = new ArrayList<>();   // Consumos del periodo.
    private List<Pago> listaPagos = new ArrayList<>();         // Pagos recibidos (composición).
    private List<Sancion> listaSanciones = new ArrayList<>();  // Sanciones asociadas a esta factura.
    private Desincentivo desincentivo;                         // Desincentivo por consumo excesivo (puede ser null).
    private List<IObservador> observadores = new ArrayList<>(); // Suscriptores a los eventos de esta factura.

    // ------------------------------------------------------------------ CONSTRUCTORES
    /** Constructor vacío (para el Builder y la capa DAO). */
    public Factura() {  // Constructor: Constructor vacío (para el Builder y la capa DAO)
        // Sin instrucciones: los atributos tienen valores iniciales en su declaración.
    }

    /**
     * Constructor completo. El orden de asignación importa: primero inicio/fin del periodo y luego
     * emisión/vencimiento, porque cada setter compara contra el valor ya asignado.
     */
    public Factura(String numero, LocalDate inicio, LocalDate fin, LocalDate emision, LocalDate vencimiento,  // Constructor: Constructor completo
                   Usuario usuario) {  // Continuación de la instrucción de la línea anterior.
        setNumero(numero);                 // Número obligatorio.
        setPeriodoInicio(inicio);          // Inicio del periodo.
        setPeriodoFin(fin);                // Fin del periodo (no anterior al inicio).
        setFechaEmision(emision);          // Fecha de emisión.
        setFechaVencimiento(vencimiento);  // Vencimiento posterior a la emisión.
        setUsuario(usuario);               // Usuario obligatorio (además queda suscrito como observador).
    }

    // ------------------------------------------------------------------ GETTERS Y SETTERS
    /** @return el número de factura. */
    public String getNumero() {  // Getter: devuelve numero
        return numero; // Lectura simple.
    }

    /** Asigna el número (obligatorio). */
    public final void setNumero(String numero) {  // Método setNumero: Asigna el número (obligatorio)
        if (numero == null || numero.trim().isEmpty()) { // Regla: número obligatorio.
            throw new IllegalArgumentException("El número de la factura es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.numero = numero.trim(); // Normaliza espacios.
    }

    /** @return el inicio del periodo. */
    public LocalDate getPeriodoInicio() {  // Getter: devuelve periodo inicio
        return periodoInicio; // LocalDate es inmutable.
    }

    /** Asigna el inicio del periodo (no puede ser posterior al fin, si este ya existe). */
    public final void setPeriodoInicio(LocalDate fecha) {  // Método setPeriodoInicio: Asigna el inicio del periodo (no puede ser posterior al fin, si este ya existe)
        if (fecha == null) { // Regla: el periodo necesita fecha de inicio.
            throw new IllegalArgumentException("El inicio del periodo es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (periodoFin != null && fecha.isAfter(periodoFin)) { // El rango debe ser coherente.
            throw new IllegalArgumentException("El inicio del periodo no puede ser posterior al fin.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.periodoInicio = fecha; // Asignación tras validar.
    }

    /** @return el fin del periodo. */
    public LocalDate getPeriodoFin() {  // Getter: devuelve periodo fin
        return periodoFin; // Lectura simple.
    }

    /** Asigna el fin del periodo (no puede ser anterior al inicio). */
    public final void setPeriodoFin(LocalDate fecha) {  // Método setPeriodoFin: Asigna el fin del periodo (no puede ser anterior al inicio)
        if (fecha == null) { // Regla: el periodo necesita fecha de fin.
            throw new IllegalArgumentException("El fin del periodo es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (periodoInicio != null && fecha.isBefore(periodoInicio)) { // El rango debe ser coherente.
            throw new IllegalArgumentException("El fin del periodo no puede ser anterior al inicio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.periodoFin = fecha; // Asignación tras validar.
    }

    /** @return la fecha de emisión. */
    public LocalDate getFechaEmision() {  // Getter: devuelve fecha emision
        return fechaEmision; // Lectura simple.
    }

    /** Asigna la fecha de emisión (si ya hay vencimiento, debe ser anterior a este). */
    public final void setFechaEmision(LocalDate fecha) {  // Método setFechaEmision: Asigna la fecha de emisión (si ya hay vencimiento, debe ser anterior a este)
        if (fecha == null) { // Regla: toda factura tiene fecha de emisión.
            throw new IllegalArgumentException("La fecha de emisión es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (fechaVencimiento != null && !fecha.isBefore(fechaVencimiento)) { // El vencimiento debe ser posterior.
            throw new IllegalArgumentException("La fecha de vencimiento debe ser posterior a la de emisión.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.fechaEmision = fecha; // Asignación tras validar.
    }

    /** @return la fecha de vencimiento. */
    public LocalDate getFechaVencimiento() {  // Getter: devuelve fecha vencimiento
        return fechaVencimiento; // Lectura simple.
    }

    /** Asigna la fecha de vencimiento (debe ser POSTERIOR a la emisión). */
    public final void setFechaVencimiento(LocalDate fecha) {  // Método setFechaVencimiento: Asigna la fecha de vencimiento (debe ser POSTERIOR a la emisión)
        if (fecha == null) { // Regla: toda factura tiene fecha límite de pago.
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (fechaEmision != null && !fecha.isAfter(fechaEmision)) { // Regla: vencimiento posterior a la emisión.
            throw new IllegalArgumentException("La fecha de vencimiento debe ser posterior a la de emisión.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.fechaVencimiento = fecha; // Asignación tras validar.
    }

    /** @return el valor total almacenado (se calcula con calcularValorTotal()). */
    public BigDecimal getValorTotal() {  // Getter: devuelve valor total
        return valorTotal; // Lectura simple.
    }

    /**
     * Asigna el valor total directamente. Existe SOLO para que la capa DAO reconstruya una factura ya emitida
     * con el valor histórico guardado (trazabilidad); no se usa en la lógica de negocio.
     * (Este setter no figura en el diagrama; se añade por necesidad de persistencia.)
     */
    public void setValorTotal(BigDecimal valor) {  // Método setValorTotal: Asigna el valor total directamente
        if (valor == null || valor.signum() < 0) { // Regla: un total no puede ser nulo ni negativo.
            throw new IllegalArgumentException("El valor total no puede ser nulo ni negativo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.valorTotal = valor.setScale(2, RoundingMode.HALF_UP); // Se guarda con 2 decimales.
    }

    /** @return el estado de pago. */
    public EstadoPago getEstadoPago() {  // Getter: devuelve estado pago
        return estadoPago; // Lectura simple.
    }

    /** Asigna el estado de pago (p. ej. EN_ACUERDO_PAGO cuando el usuario pacta cuotas, o al reconstruir desde BD). */
    public void setEstadoPago(EstadoPago estado) {  // Método setEstadoPago: Asigna el estado de pago (p
        if (estado == null) { // Regla: toda factura tiene estado de pago.
            throw new IllegalArgumentException("El estado de pago es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.estadoPago = estado; // Asignación tras validar.
    }

    /** @return el usuario de la factura. */
    public Usuario getUsuario() {  // Getter: devuelve usuario
        return usuario; // Lectura simple.
    }

    /** Asigna el usuario (obligatorio) y lo suscribe como observador de los eventos de la factura. */
    public final void setUsuario(Usuario usuario) {  // Método setUsuario: Asigna el usuario (obligatorio) y lo suscribe como observador de los eventos de la factura
        if (usuario == null) { // Regla: una factura siempre pertenece a un suscriptor.
            throw new IllegalArgumentException("El usuario de la factura es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.usuario = usuario;       // Asignación tras validar.
        agregarObservador(usuario);   // El usuario (IObservador) reaccionará, p. ej., cuando la factura venza.
    }

    /** @return los consumos del periodo (solo lectura). */
    public List<Consumo> getListaConsumos() {  // Getter: devuelve lista consumos
        return Collections.unmodifiableList(listaConsumos); // Copia defensiva: solo se agrega con agregarConsumo().
    }

    /** @return los pagos recibidos (solo lectura). */
    public List<Pago> getListaPagos() {  // Getter: devuelve lista pagos
        return Collections.unmodifiableList(listaPagos); // Copia defensiva: solo se agrega con agregarPago().
    }

    /** @return las sanciones asociadas (solo lectura). */
    public List<Sancion> getListaSanciones() {  // Getter: devuelve lista sanciones
        return Collections.unmodifiableList(listaSanciones); // Copia defensiva: solo se agrega con agregarSancion().
    }

    /** @return el desincentivo aplicado (puede ser null). */
    public Desincentivo getDesincentivo() {  // Getter: devuelve desincentivo
        return desincentivo; // Lectura simple.
    }

    /** Asigna el desincentivo (puede ser null cuando no aplica, p. ej. usuarios no residenciales). */
    public void setDesincentivo(Desincentivo des) {  // Método setDesincentivo: Asigna el desincentivo (puede ser null cuando no aplica, p
        this.desincentivo = des; // Se permite null: el desincentivo es opcional.
    }

    // ------------------------------------------------------------------ AGREGAR ELEMENTOS A LA FACTURA
    /** Asocia un consumo del periodo. Debe ser del mismo usuario y no repetirse. */
    public void agregarConsumo(Consumo consumo) {  // Método agregarConsumo: Asocia un consumo del periodo
        if (consumo == null) { // No se puede agregar "nada".
            throw new IllegalArgumentException("El consumo no puede ser nulo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (usuario == null) { // Se necesita saber a quién pertenece la factura para comparar.
            throw new IllegalStateException("Asigne primero el usuario de la factura.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        if (!usuario.equals(consumo.getUsuario())) { // El consumo debe ser de ESTE usuario.
            throw new IllegalArgumentException("El consumo pertenece a otro usuario.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        for (Consumo existente : listaConsumos) { // Evita facturar dos veces la misma lectura.
            if (existente.getCodigo().equals(consumo.getCodigo())) {  // Condición: si existente.getCodigo().equals(consumo.getCodigo())
                throw new IllegalArgumentException("El consumo " + consumo.getCodigo() + " ya está en la factura.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
        }
        listaConsumos.add(consumo); // Se incorpora a la factura.
    }

    /**
     * Registra un pago y actualiza automáticamente el estado de la factura (Necesidad 8).
     * El pago debe haberse creado para ESTA factura y ser válido.
     */
    public void agregarPago(Pago pago) {  // Método agregarPago: Registra un pago y actualiza automáticamente el estado de la factura (Necesidad 8)
        if (pago == null) { // No se puede agregar "nada".
            throw new IllegalArgumentException("El pago no puede ser nulo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (pago.getFactura() != this) { // Composición: el pago pertenece a una única factura.
            throw new IllegalArgumentException("El pago pertenece a otra factura.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (!pago.validarPago()) { // Reglas del pago: valor > 0, fecha no futura, referencia, etc.
            throw new IllegalArgumentException("El pago no cumple las reglas de negocio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (estaPagada() && valorTotal.signum() > 0) { // Una factura ya saldada no admite más abonos.
            throw new IllegalArgumentException("La factura ya está pagada; no admite más pagos.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (pago.getValor().compareTo(calcularSaldoPendiente()) > 0) { // El saldo pendiente es el máximo que se puede abonar.
            throw new IllegalArgumentException("El pago (" + pago.getValor() + ") supera el saldo pendiente ("  // Lanza IllegalArgumentException con un mensaje claro para el usuario
                    + calcularSaldoPendiente() + ").");  // Continúa el texto de la instrucción anterior.
        }
        for (Pago existente : listaPagos) { // Código de pago único.
            if (existente.getCodigo().equals(pago.getCodigo())) {  // Condición: si existente.getCodigo().equals(pago.getCodigo())
                throw new IllegalArgumentException("Ya existe un pago con el código " + pago.getCodigo() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
        }
        listaPagos.add(pago);      // Se registra el pago.
        actualizarEstadoPago();    // El estado se actualiza solo (PAGADA, VENCIDA o PENDIENTE).
    }

    /** Asocia una sanción del usuario a la factura (para cobrarla si es económica y está activa). */
    public void agregarSancion(Sancion sancion) {  // Método agregarSancion: Asocia una sanción del usuario a la factura (para cobrarla si es económica y está activa)
        if (sancion == null) { // No se puede agregar "nada".
            throw new IllegalArgumentException("La sanción no puede ser nula.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (usuario == null || !usuario.equals(sancion.getUsuario())) { // La sanción debe ser del mismo usuario.
            throw new IllegalArgumentException("La sanción pertenece a otro usuario.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        for (Sancion existente : listaSanciones) { // Código de sanción único dentro de la factura.
            if (existente.getCodigoSancion().equals(sancion.getCodigoSancion())) {  // Condición: si existente.getCodigoSancion().equals(sancion.getCodigoSancion())
                throw new IllegalArgumentException("La sanción " + sancion.getCodigoSancion() + " ya está en la factura.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
        }
        listaSanciones.add(sancion); // Se incorpora a la factura.
    }

    // ------------------------------------------------------------------ CÁLCULOS
    /** @return la suma de los volúmenes (m3) de los consumos de la factura. */
    public double calcularConsumoTotalM3() {  // Método calcularConsumoTotalM3 (devuelve double)
        double total = 0.0; // Acumulador.
        for (Consumo c : listaConsumos) { // Recorre los consumos.
            total += c.getVolumenM3(); // Suma el volumen.
        }
        return total;  // Devuelve el valor de total
    }

    /**
     * Valor del desincentivo por consumo excesivo para esta factura.
     *
     * Cada Consumo es una lectura mensual y el límite del enunciado es de 44 m3 por BIMESTRE (22 por mes).
     * Para respetar ambos, el consumo se lleva a base bimestral (consumo / (meses / 2)), se calcula el
     * desincentivo y se devuelve a la escala del periodo facturado multiplicando por (meses / 2).
     * Ejemplo: 1 mes con 25 m3 => equivale a 50 m3 bimestrales => excedente 6 m3 => 20.285,04 x 0,5 = 10.142,52.
     */
    private BigDecimal calcularValorDesincentivo() {  // Método calcularValorDesincentivo: Valor del desincentivo por consumo excesivo para esta factura
        if (desincentivo == null || listaConsumos.isEmpty()) { // Sin desincentivo o sin lecturas no hay cobro.
            return BigDecimal.ZERO;  // Devuelve el valor de BigDecimal.ZERO
        }
        double factorBimestre = listaConsumos.size() / 2.0;                      // Fracción de bimestre facturada.
        double consumoBimestral = calcularConsumoTotalM3() / factorBimestre;     // Consumo llevado a base bimestral.
        if (!desincentivo.aplicaDesincentivo(consumoBimestral, usuario)) { // Solo residenciales que superen el límite.
            return BigDecimal.ZERO;  // Devuelve el valor de BigDecimal.ZERO
        }
        return desincentivo.calcularDesincentivo(consumoBimestral)  // Devuelve: desincentivo.calcularDesincentivo(consumoBimestral)
                .multiply(BigDecimal.valueOf(factorBimestre)).setScale(2, RoundingMode.HALF_UP); // Se devuelve a escala del periodo.
    }

    /**
     * Calcula y almacena el valor total: consumos (con subsidio/contribución ya incluidos por la estrategia)
     * + desincentivo + sanciones económicas activas.
     * Si la factura ya tiene pagos, el valor NO se recalcula (trazabilidad: lo cobrado no puede cambiar).
     */
    public BigDecimal calcularValorTotal() {  // Método calcularValorTotal: Calcula y almacena el valor total
        if (!listaPagos.isEmpty()) { // Con pagos registrados el total queda congelado.
            return valorTotal;  // Devuelve el valor de valorTotal
        }
        if (usuario == null) { // Sin usuario no hay estrategia tarifaria.
            throw new IllegalStateException("La factura no tiene usuario asignado.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        if (fechaEmision == null) { // La vigencia de las tarifas se verifica en la fecha de facturación (emisión).
            throw new IllegalStateException("Asigne primero la fecha de emisión de la factura.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        if (!validarTarifasVigentes()) { // Regla (Necesidad 5): las tarifas deben estar vigentes en la fecha de facturación.
            throw new IllegalStateException("Hay consumos cuya tarifa no está vigente en la fecha de facturación " + fechaEmision + ".");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        BigDecimal total = BigDecimal.ZERO; // Acumulador monetario.
        for (Consumo c : listaConsumos) { // Cada lectura se valora polimórficamente según el tipo de usuario.
            total = total.add(usuario.calcularTarifa(c)); // Cargo fijo + variable - subsidio + contribución (+ cargos propios).
        }
        total = total.add(calcularValorDesincentivo()); // El desincentivo no se subsidia: se suma aparte.
        for (Sancion s : listaSanciones) { // Sanciones que todavía pueden cobrarse.
            if (s.estaActiva() && s.getTipo() == TipoSancion.ECONOMICA) { // Solo las multas económicas vivas.
                total = total.add(s.getValorMulta());  // Actualiza «total»: total.add(s.getValorMulta())
            }
        }
        this.valorTotal = total.setScale(2, RoundingMode.HALF_UP); // Se almacena con 2 decimales.
        return this.valorTotal;  // Devuelve el valor de this.valorTotal
    }

    /** SALDO PENDIENTE = valor total - pagos realizados (Necesidad 8). */
    public BigDecimal calcularSaldoPendiente() {  // Método calcularSaldoPendiente: SALDO PENDIENTE = valor total - pagos realizados (Necesidad 8)
        BigDecimal pagado = BigDecimal.ZERO; // Acumulador de pagos.
        for (Pago p : listaPagos) { // Recorre los pagos.
            pagado = pagado.add(p.getValor()); // Suma cada pago.
        }
        return valorTotal.subtract(pagado).setScale(2, RoundingMode.HALF_UP); // Total menos lo pagado.
    }

    /** @return true si los pagos cubren el valor total (saldo menor o igual a cero). */
    public boolean estaPagada() {  // Método estaPagada (devuelve boolean)
        return calcularSaldoPendiente().signum() <= 0; // signum() <= 0: no queda nada por pagar.
    }

    /**
     * Actualiza AUTOMÁTICAMENTE el estado de pago (Necesidad 8):
     * PAGADA si los pagos cubren el total; VENCIDA si pasó la fecha de vencimiento sin pago completo;
     * PENDIENTE si aún está dentro del plazo. Una factura EN_ACUERDO_PAGO conserva ese estado hasta pagarse.
     */
    public void actualizarEstadoPago() {  // Método actualizarEstadoPago: Actualiza AUTOMÁTICAMENTE el estado de pago (Necesidad 8)
        if (estaPagada()) { // Los pagos cubren el total.
            this.estadoPago = EstadoPago.PAGADA;  // Asigna a estadoPago el valor: EstadoPago.PAGADA
            ejecutarMultasPagadas(); // "Ejecutada: multa pagada": las multas cobradas quedan cumplidas.
        } else if (estadoPago == EstadoPago.EN_ACUERDO_PAGO) { // El acuerdo de pago suspende el vencimiento.
            return; // Se conserva el estado pactado.
        } else if (fechaVencimiento != null && LocalDate.now().isAfter(fechaVencimiento)) { // Pasó el plazo sin pagar.
            marcarComoVencida();  // Ejecuta la operación «marcar como vencida» (definida en esta clase o en un objeto colaborador)
        } else { // Aún dentro del plazo.
            this.estadoPago = EstadoPago.PENDIENTE;  // Asigna a estadoPago el valor: EstadoPago.PENDIENTE
        }
    }

    /** Al quedar pagada la factura, las multas económicas cobradas en ella pasan a EJECUTADA. */
    private void ejecutarMultasPagadas() {  // Método ejecutarMultasPagadas: Al quedar pagada la factura, las multas económicas cobradas en ella pasan a EJECUTADA
        for (Sancion s : listaSanciones) { // Recorre las sanciones de la factura.
            if (s.getTipo() == TipoSancion.ECONOMICA && s.estaActiva()) { // Solo multas vivas (IMPUESTA o CONFIRMADA).
                s.ejecutar(); // La multa se cumplió con el pago.
            }
        }
    }

    /**
     * Marca la factura como VENCIDA y avisa a los observadores (Observer):
     *  - evento FACTURA_VENCIDA => el usuario pasa a EN_DEUDA y se le notifica;
     *  - si el usuario acumula 2 o más facturas vencidas => evento SUSPENSION_INMINENTE (aviso previo).
     * Es idempotente: si ya estaba vencida no vuelve a notificar.
     */
    public void marcarComoVencida() {  // Método marcarComoVencida: Marca la factura como VENCIDA y avisa a los observadores (Observer)
        if (estaPagada() || estadoPago == EstadoPago.VENCIDA) { // Una pagada no vence; una vencida no se repite.
            return;  // Termina el método sin devolver valor
        }
        this.estadoPago = EstadoPago.VENCIDA; // Cambio de estado antes de notificar (los observadores consultan el estado).
        notificarObservadores(new Evento(TipoEvento.FACTURA_VENCIDA, usuario, this)); // Aviso de vencimiento.
        int vencidas = usuario.contarFacturasVencidas(); // Facturas vencidas registradas en el historial del usuario.
        if (!usuario.getListaFacturas().contains(this)) { // Si esta factura aún no está en su historial, se cuenta aparte.
            vencidas++;  // Incrementa el contador vencidas
        }
        if (vencidas >= Usuario.UMBRAL_SUSPENSION) { // Dos o más vencidas.
            notificarObservadores(new Evento(TipoEvento.SUSPENSION_INMINENTE, usuario, this)); // Aviso de suspensión inminente.
        }
    }

    /** @return true si el periodo es coherente (inicio <= fin) y el vencimiento es posterior a la emisión. */
    public boolean validarFechas() {  // Método validarFechas (devuelve boolean)
        return periodoInicio != null && periodoFin != null && !periodoInicio.isAfter(periodoFin)  // Devuelve: periodoInicio es distinto de nulo y periodoFin es distinto de nulo y no ...
                && fechaEmision != null && fechaVencimiento != null && fechaVencimiento.isAfter(fechaEmision);  // Continúa la condición anterior con otra comprobación: y fechaEmision es distinto de nulo y fechaVencimiento es distinto de nul...
    }

    /** @return true si la tarifa de cada consumo está vigente en la fecha de emisión (fecha de facturación). */
    public boolean validarTarifasVigentes() {  // Método validarTarifasVigentes (devuelve boolean)
        if (fechaEmision == null) { // Sin fecha de facturación no se puede verificar la vigencia.
            return false;  // Devuelve el valor de false
        }
        for (Consumo c : listaConsumos) { // Recorre los consumos facturados.
            if (!c.getTarifaAplicada().estaVigenteEn(fechaEmision)) { // La tarifa debe cubrir la fecha de facturación.
                return false;  // Devuelve el valor de false
            }
        }
        return true; // Todas las tarifas están vigentes.
    }

    /** @return cantidad de consumos. */
    public int contarConsumos() {  // Método contarConsumos (devuelve int)
        return listaConsumos.size(); // Tamaño de la lista.
    }

    /** @return cantidad de pagos. */
    public int contarPagos() {  // Método contarPagos (devuelve int)
        return listaPagos.size(); // Tamaño de la lista.
    }

    /** @return cantidad de sanciones. */
    public int contarSanciones() {  // Método contarSanciones (devuelve int)
        return listaSanciones.size(); // Tamaño de la lista.
    }

    // ------------------------------------------------------------------ OBSERVER (rol Observable)
    /** Suscribe un observador (sin duplicados). */
    @Override
    public void agregarObservador(IObservador obs) {  // Método agregarObservador: Suscribe un observador (sin duplicados)
        if (obs != null && !observadores.contains(obs)) { // Ignora nulos y repetidos.
            observadores.add(obs);  // Invoca observadores.add(...)
        }
    }

    /** Cancela la suscripción de un observador. */
    @Override
    public void eliminarObservador(IObservador obs) {  // Método eliminarObservador: Cancela la suscripción de un observador
        observadores.remove(obs); // remove no falla si el elemento no está.
    }

    /** Avisa a todos los observadores. Se itera sobre una copia para tolerar altas/bajas durante la notificación. */
    @Override
    public void notificarObservadores(Evento evento) {  // Método notificarObservadores: Avisa a todos los observadores
        for (IObservador obs : new ArrayList<>(observadores)) { // Copia defensiva de la lista.
            obs.actualizar(evento); // Cada observador reacciona a su manera (polimorfismo).
        }
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "Factura [numero=" + numero + ", periodo=" + periodoInicio + " a " + periodoFin  // Devuelve: "Factura [numero=" + numero + ", periodo=" + periodoInicio + " a " + per...
                + ", emision=" + fechaEmision + ", vencimiento=" + fechaVencimiento + ", valorTotal=" + valorTotal  // Continúa el texto de la instrucción anterior.
                + ", estado=" + estadoPago + "]"; // Resumen.
    }
}
