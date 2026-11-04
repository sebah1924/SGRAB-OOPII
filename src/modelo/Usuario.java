package modelo; // Paquete del dominio.

import java.math.BigDecimal;     // Dinero con precisión exacta.
import java.time.LocalDate;      // Fechas sin hora.
import java.util.ArrayList;      // Implementación de lista dinámica.
import java.util.Collections;    // Utilidades; aquí se usa para devolver listas de solo lectura.
import java.util.List;           // Interfaz de lista (programar contra la interfaz = bajo acoplamiento).

/**
 * Usuario del servicio de acueducto (suscriptor de la EAAB). Clase ABSTRACTA.
 *
 * PILARES DE LA POO
 *  - Herencia: Usuario "es una" Persona (reutiliza sus datos) y es la superclase de los cuatro tipos de usuario.
 *  - Polimorfismo: calcularTarifa() es abstracto; cada subclase lo sobrescribe y se invoca siempre igual
 *    desde Factura, sin importar el tipo real del usuario.
 *  - Encapsulamiento: el estado del servicio solo cambia con métodos que validan la regla de negocio
 *    (suspender, reactivar, cortar...). Cohesión: "el objeto gobierna su propio estado".
 *  - Abstracción: modela al suscriptor; no se instancia directamente (lo crea UsuarioFactory).
 *
 * PATRONES: Strategy (delega el cálculo tarifario en estrategiaTarifa) y Observer (implementa IObservador
 * para reaccionar, por ejemplo, cuando una factura suya vence).
 *
 * REGLAS DE NEGOCIO (enunciado):
 *  - código de suscriptor obligatorio y único; estrato entre 1 y 6.
 *  - consumo promedio = suma de consumos / cantidad de consumos (ej.: 10, 12 y 14 => 12 m3).
 *  - estados: ACTIVO, EN_DEUDA, SUSPENDIDO (2 o más facturas vencidas), CORTADO (racionamiento o sanción).
 *  - un usuario EXENTO (excepción legal) no puede ser cortado ni suspendido.
 */
public abstract class Usuario extends Persona implements IObservador {  // Declaración de la clase Usuario: Usuario del servicio de acueducto (suscriptor de la EAAB)

    /** Cantidad de facturas vencidas a partir de la cual el servicio puede suspenderse (enunciado, estado Suspendido). */
    public static final int UMBRAL_SUSPENSION = 2;  // Atributo «UMBRAL_SUSPENSION» de tipo int

    // ------------------------------------------------------------------ ATRIBUTOS (protected, según el diagrama)
    protected String codigoSuscriptor;            // Identificador único del suscriptor.
    protected LocalDate fechaRegistro;            // Día en que se suscribió al servicio.
    protected TipoUsuario tipoUsuario;            // RESIDENCIAL, COMERCIAL, INDUSTRIAL u OFICIAL.
    protected int estrato;                        // Estrato socioeconómico de 1 a 6.
    protected EstadoServicio estadoServicio = EstadoServicio.ACTIVO; // Todo usuario nace con el servicio activo.
    protected CanalNotificacion canalPreferido;   // Canal por el que prefiere ser notificado.
    protected double consumoPromedioM3;           // Promedio calculado automáticamente (no tiene setter).
    protected List<Consumo> listaConsumos = new ArrayList<>(); // Historial de consumos.
    protected List<Factura> listaFacturas = new ArrayList<>(); // Historial de facturas.
    protected List<Sancion> listaSanciones = new ArrayList<>(); // Sanciones recibidas (relación "recibe" del modelo de datos).
    protected IEstrategiaTarifa estrategiaTarifa; // Strategy inyectada: fórmula de cobro de este usuario.
    protected Excepcion excepcion;                // Excepción legal a cortes (0..1); null si no tiene.

    // ------------------------------------------------------------------ CONSTRUCTORES
    /** Constructor vacío (para subclases y para la capa DAO). */
    public Usuario() {  // Constructor: Constructor vacío (para subclases y para la capa DAO)
        super(); // Llama al constructor vacío de Persona.
    }

    /**
     * Constructor completo con los 7 datos de Persona y los 4 propios del suscriptor.
     * El tipo de usuario NO se recibe aquí: lo fija cada subclase concreta.
     */
    public Usuario(String identificacion, String nombre, String apellido, LocalDate fechaNacimiento,  // Constructor: Constructor completo con los 7 datos de Persona y los 4 propios del suscriptor
                   String direccion, String telefono, String email, String codigoSuscriptor,  // Continuación de la instrucción de la línea anterior.
                   LocalDate fechaRegistro, int estrato, CanalNotificacion canalPreferido) {  // Abre el bloque de código: LocalDate fechaRegistro, int estrato, CanalNoti...
        super(identificacion, nombre, apellido, fechaNacimiento, direccion, telefono, email); // Datos y validaciones de Persona.
        setCodigoSuscriptor(codigoSuscriptor); // Código obligatorio.
        setFechaRegistro(fechaRegistro);       // Fecha de registro obligatoria.
        setEstrato(estrato);                   // Estrato 1 a 6.
        setCanalPreferido(canalPreferido);     // Canal obligatorio.
    }

    // ------------------------------------------------------------------ GETTERS Y SETTERS
    /** @return el código de suscriptor. */
    public String getCodigoSuscriptor() {  // Getter: devuelve codigo suscriptor
        return codigoSuscriptor; // Lectura simple.
    }

    /** Asigna el código de suscriptor (obligatorio; su unicidad la verifica la empresa al registrar). */
    public final void setCodigoSuscriptor(String codigo) {  // Método setCodigoSuscriptor: Asigna el código de suscriptor (obligatorio; su unicidad la verifica la empresa al registrar)
        if (codigo == null || codigo.trim().isEmpty()) { // Regla: código de suscriptor obligatorio.
            throw new IllegalArgumentException("El código de suscriptor es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.codigoSuscriptor = codigo.trim(); // Normaliza espacios.
    }

    /** @return la fecha de registro. */
    public LocalDate getFechaRegistro() {  // Getter: devuelve fecha registro
        return fechaRegistro; // LocalDate es inmutable.
    }

    /** Asigna la fecha de registro (obligatoria). */
    public final void setFechaRegistro(LocalDate fecha) {  // Método setFechaRegistro: Asigna la fecha de registro (obligatoria)
        if (fecha == null) { // Regla: se registra cuándo se suscribió el usuario.
            throw new IllegalArgumentException("La fecha de registro es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.fechaRegistro = fecha; // Asignación tras validar.
    }

    /** @return el tipo de usuario. */
    public TipoUsuario getTipoUsuario() {  // Getter: devuelve tipo usuario
        return tipoUsuario; // Lectura simple.
    }

    /** Asigna el tipo de usuario (obligatorio). Lo invocan los constructores de las subclases. */
    public void setTipoUsuario(TipoUsuario tipo) {  // Método setTipoUsuario: Asigna el tipo de usuario (obligatorio)
        if (tipo == null) { // Regla: todo usuario tiene un tipo.
            throw new IllegalArgumentException("El tipo de usuario es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.tipoUsuario = tipo; // Asignación tras validar.
    }

    /** @return el estrato. */
    public int getEstrato() {  // Getter: devuelve estrato
        return estrato; // Lectura simple.
    }

    /** Asigna el estrato (debe estar entre 1 y 6). El enunciado permite editarlo (Necesidad 2). */
    public final void setEstrato(int estrato) {  // Método setEstrato: Asigna el estrato (debe estar entre 1 y 6)
        if (estrato < 1 || estrato > 6) { // Regla: estrato entre 1 y 6.
            throw new IllegalArgumentException("El estrato debe estar entre 1 y 6.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.estrato = estrato; // Asignación tras validar.
    }

    /** @return el estado del servicio. */
    public EstadoServicio getEstadoServicio() {  // Getter: devuelve estado servicio
        return estadoServicio; // Lectura simple.
    }

    /**
     * Asigna el estado del servicio directamente (edición de datos, Necesidad 2, y reconstrucción desde BD).
     * Para cambios con reglas de negocio use suspender(), reactivar(), cortar() o marcarEnDeuda().
     */
    public void setEstadoServicio(EstadoServicio estado) {  // Método setEstadoServicio: Asigna el estado del servicio directamente (edición de datos, Necesidad 2, y reconstrucción desde BD)
        if (estado == null) { // Regla: el servicio siempre tiene un estado.
            throw new IllegalArgumentException("El estado del servicio es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.estadoServicio = estado; // Asignación tras validar.
    }

    /** @return el canal de notificación preferido. */
    public CanalNotificacion getCanalPreferido() {  // Getter: devuelve canal preferido
        return canalPreferido; // Lectura simple.
    }

    /** Asigna el canal preferido (obligatorio). */
    public final void setCanalPreferido(CanalNotificacion canal) {  // Método setCanalPreferido: Asigna el canal preferido (obligatorio)
        if (canal == null) { // Regla: cada usuario tiene un canal preferido.
            throw new IllegalArgumentException("El canal de notificación preferido es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.canalPreferido = canal; // Asignación tras validar.
    }

    /** @return el consumo promedio en m3 (calculado automáticamente). */
    public double getConsumoPromedioM3() {  // Getter: devuelve consumo promedio m3
        return consumoPromedioM3; // Lectura simple; no existe setter porque el valor siempre se deriva del historial.
    }

    /**
     * @return el historial de consumos en modo SOLO LECTURA.
     * Buena práctica ("copia defensiva"): si se devolviera la lista real, cualquiera podría agregar
     * consumos sin pasar por agregarConsumo() y el promedio quedaría desactualizado.
     */
    public List<Consumo> getListaConsumos() {  // Getter: devuelve lista consumos
        return Collections.unmodifiableList(listaConsumos); // Vista que lanza excepción si se intenta modificar.
    }

    /** @return las sanciones del usuario en modo SOLO LECTURA (misma razón que getListaConsumos). */
    public List<Sancion> getListaSanciones() {  // Getter: devuelve lista sanciones
        return Collections.unmodifiableList(listaSanciones); // Vista de solo lectura: se agrega solo con agregarSancion().
    }

    /** @return el historial de facturas en modo SOLO LECTURA (misma razón que getListaConsumos). */
    public List<Factura> getListaFacturas() {  // Getter: devuelve lista facturas
        return Collections.unmodifiableList(listaFacturas); // Vista de solo lectura.
    }

    /** @return la estrategia tarifaria inyectada. */
    public IEstrategiaTarifa getEstrategiaTarifa() {  // Getter: devuelve estrategia tarifa
        return estrategiaTarifa; // Lectura simple.
    }

    /** Inyecta la estrategia tarifaria (obligatoria). Es "inyección de dependencias" por setter. */
    public void setEstrategiaTarifa(IEstrategiaTarifa estrategia) {  // Método setEstrategiaTarifa: Inyecta la estrategia tarifaria (obligatoria)
        if (estrategia == null) { // Regla: sin estrategia no se puede calcular el cobro.
            throw new IllegalArgumentException("La estrategia tarifaria es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.estrategiaTarifa = estrategia; // Asignación tras validar.
    }

    /** @return la excepción legal del usuario, o null si no goza de ninguna. */
    public Excepcion getExcepcion() {  // Getter: devuelve excepcion
        return excepcion; // Lectura simple.
    }

    /** Asigna la excepción legal (puede ser null para quitarla). Relación "goza de" 0..1 del diagrama. */
    public void setExcepcion(Excepcion excepcion) {  // Método setExcepcion: Asigna la excepción legal (puede ser null para quitarla)
        this.excepcion = excepcion; // Se permite null: la mayoría de usuarios no tiene excepción.
    }

    // ------------------------------------------------------------------ HISTORIAL Y CONSUMO PROMEDIO
    /**
     * Agrega un consumo al historial y recalcula el promedio automáticamente (Necesidad 2).
     * Reglas: no se aceptan nulos, lecturas de otro usuario ni códigos de consumo repetidos.
     */
    public void agregarConsumo(Consumo consumo) {  // Método agregarConsumo: Agrega un consumo al historial y recalcula el promedio automáticamente (Necesidad 2)
        if (consumo == null) { // No se puede agregar "nada" al historial.
            throw new IllegalArgumentException("El consumo no puede ser nulo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (consumo.getUsuario() != null && !this.equals(consumo.getUsuario())) { // La lectura debe ser de ESTE usuario.
            throw new IllegalArgumentException("El consumo pertenece a otro usuario.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (consumo.getTarifaAplicada() == null || consumo.getTarifaAplicada().getTipoUsuario() != tipoUsuario) { // La tarifa debe ser del tipo del usuario.
            throw new IllegalArgumentException("La tarifa del consumo no corresponde al tipo de usuario.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        for (Consumo existente : listaConsumos) { // Recorre el historial para evitar duplicados.
            if (existente.getCodigo().equals(consumo.getCodigo())) { // Código de consumo único.
                throw new IllegalArgumentException("Ya existe un consumo con el código " + consumo.getCodigo() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
        }
        listaConsumos.add(consumo);   // Se registra en el historial.
        recalcularConsumoPromedio();  // El promedio se mantiene siempre al día.
    }

    /** Consumo promedio = (suma de los consumos del historial) / (cantidad de consumos). Sin consumos => 0. */
    public void recalcularConsumoPromedio() {  // Método recalcularConsumoPromedio: Consumo promedio = (suma de los consumos del historial) / (cantidad de consumos)
        if (listaConsumos.isEmpty()) { // Evita la división por cero.
            this.consumoPromedioM3 = 0.0;  // Asigna a consumoPromedioM3 el valor: 0.0
            return;  // Termina el método sin devolver valor
        }
        double suma = 0.0; // Acumulador de volúmenes.
        for (Consumo c : listaConsumos) { // Recorre todo el historial.
            suma += c.getVolumenM3(); // Suma el volumen de cada lectura.
        }
        this.consumoPromedioM3 = suma / listaConsumos.size(); // Ejemplo: (10 + 12 + 14) / 3 = 12.
    }

    /** Agrega una factura al historial del usuario (sin duplicar el número de factura). */
    public void agregarFactura(Factura factura) {  // Método agregarFactura: Agrega una factura al historial del usuario (sin duplicar el número de factura)
        if (factura == null) { // No se puede agregar "nada".
            throw new IllegalArgumentException("La factura no puede ser nula.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (!this.equals(factura.getUsuario())) { // Una factura solo puede estar en el historial de su titular.
            throw new IllegalArgumentException("La factura pertenece a otro usuario.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        for (Factura existente : listaFacturas) { // Recorre el historial.
            if (existente.getNumero().equals(factura.getNumero())) { // Número de factura único.
                throw new IllegalArgumentException("Ya existe una factura con el número " + factura.getNumero() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
        }
        listaFacturas.add(factura); // Se registra en el historial.
    }

    /** Agrega una sanción al historial del usuario (sin repetir el código). */
    public void agregarSancion(Sancion sancion) {  // Método agregarSancion: Agrega una sanción al historial del usuario (sin repetir el código)
        if (sancion == null) { // No se puede agregar "nada".
            throw new IllegalArgumentException("La sanción no puede ser nula.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (!this.equals(sancion.getUsuario())) { // La sanción debe recaer sobre ESTE usuario.
            throw new IllegalArgumentException("La sanción pertenece a otro usuario.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        for (Sancion existente : listaSanciones) { // Recorre las sanciones para evitar duplicados.
            if (existente.getCodigoSancion().equals(sancion.getCodigoSancion())) { // Código de sanción único.
                throw new IllegalArgumentException("Ya existe una sanción con el código " + sancion.getCodigoSancion() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
        }
        listaSanciones.add(sancion); // Se registra en el historial.
    }

    /** @return cantidad de sanciones que no fueron revocadas (las que cuentan para la reincidencia). */
    public int contarSancionesNoRevocadas() {  // Método contarSancionesNoRevocadas (devuelve int)
        int cuenta = 0; // Contador.
        for (Sancion s : listaSanciones) { // Recorre las sanciones.
            if (s.getEstado() != EstadoSancion.REVOCADA) { // Una sanción anulada no cuenta como antecedente.
                cuenta++;  // Incrementa el contador cuenta
            }
        }
        return cuenta;  // Devuelve el valor de cuenta
    }

    /** @return true si el usuario ya tiene al menos una sanción previa no revocada (reincidente). */
    public boolean esReincidente() {  // Método esReincidente (devuelve boolean)
        return contarSancionesNoRevocadas() >= 1; // Reincidir es volver a incurrir habiendo sido sancionado antes.
    }

    /**
     * Consulta el historial de facturas con filtros por FECHA (de emisión) y por ESTADO de pago (Necesidad 9).
     * Cualquier filtro en null se ignora.
     *
     * @param desde fecha inicial de emisión (inclusive) o null.
     * @param hasta fecha final de emisión (inclusive) o null.
     * @param estado estado de pago o null.
     * @return lista nueva con las facturas que cumplen todos los filtros.
     */
    public List<Factura> consultarFacturas(LocalDate desde, LocalDate hasta, EstadoPago estado) {  // Método consultarFacturas: Consulta el historial de facturas con filtros por FECHA (de emisión) y por ESTADO de pago (Necesidad 9)
        if (desde != null && hasta != null && desde.isAfter(hasta)) { // El rango de fechas debe ser coherente.
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        List<Factura> resultado = new ArrayList<>(); // Facturas que cumplen los filtros.
        for (Factura f : listaFacturas) { // Recorre el historial completo.
            boolean cumpleDesde = desde == null || !f.getFechaEmision().isBefore(desde); // Emitida desde la fecha inicial.
            boolean cumpleHasta = hasta == null || !f.getFechaEmision().isAfter(hasta);  // Emitida hasta la fecha final.
            boolean cumpleEstado = estado == null || f.getEstadoPago() == estado;        // Estado de pago pedido.
            if (cumpleDesde && cumpleHasta && cumpleEstado) { // Debe cumplir los tres criterios a la vez.
                resultado.add(f);  // Invoca resultado.add(...)
            }
        }
        return resultado;  // Devuelve el valor de resultado
    }

    /** @return cantidad de facturas del historial. */
    public int contarFacturas() {  // Método contarFacturas (devuelve int)
        return listaFacturas.size(); // Tamaño de la lista.
    }

    /** @return cantidad de facturas en estado VENCIDA (base de la suspensión inminente: 2 o más). */
    public int contarFacturasVencidas() {  // Método contarFacturasVencidas (devuelve int)
        int cuenta = 0; // Contador.
        for (Factura f : listaFacturas) { // Recorre el historial.
            if (f.getEstadoPago() == EstadoPago.VENCIDA) { // Solo cuentan las vencidas.
                cuenta++;  // Incrementa el contador cuenta
            }
        }
        return cuenta;  // Devuelve el valor de cuenta
    }

    // ------------------------------------------------------------------ ESTADO DEL SERVICIO (reglas de negocio)
    /** Una factura vencida pasa al usuario de ACTIVO a EN_DEUDA. Si ya está suspendido o cortado, no cambia. */
    public void marcarEnDeuda() {  // Método marcarEnDeuda: Una factura vencida pasa al usuario de ACTIVO a EN_DEUDA
        if (estadoServicio == EstadoServicio.ACTIVO) { // Solo un usuario al día puede "caer" en deuda.
            this.estadoServicio = EstadoServicio.EN_DEUDA;  // Asigna a estadoServicio el valor: EstadoServicio.EN_DEUDA
        }
    }

    /**
     * Suspende el servicio por deudas. Reglas: los exentos NUNCA se suspenden, y se exigen
     * dos o más facturas vencidas (enunciado: "Suspendido: usuario con dos o más facturas vencidas").
     */
    public void suspender() {  // Método suspender: Suspende el servicio por deudas
        if (esExento()) { // Protección legal: exentos no se suspenden.
            throw new IllegalStateException("El usuario goza de una excepción legal y no puede ser suspendido.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        if (contarFacturasVencidas() < UMBRAL_SUSPENSION) { // Sin deuda suficiente no procede.
            throw new IllegalStateException("Se requieren al menos " + UMBRAL_SUSPENSION + " facturas vencidas para suspender.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        this.estadoServicio = EstadoServicio.SUSPENDIDO; // Servicio temporalmente detenido.
    }

    /**
     * Reactiva el servicio cuando el usuario regulariza su situación (Necesidad 2).
     * Si sigue acumulando el umbral de facturas vencidas no puede reactivarse; si le queda alguna vencida
     * queda EN_DEUDA y si no le queda ninguna queda ACTIVO.
     */
    public void reactivar() {  // Método reactivar: Reactiva el servicio cuando el usuario regulariza su situación (Necesidad 2)
        int vencidas = contarFacturasVencidas(); // Deuda pendiente actual.
        if (estadoServicio == EstadoServicio.SUSPENDIDO && vencidas >= UMBRAL_SUSPENSION) { // Aún no regulariza.
            throw new IllegalStateException("El usuario aún no regulariza su deuda; no se puede reactivar.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        this.estadoServicio = (vencidas == 0) ? EstadoServicio.ACTIVO : EstadoServicio.EN_DEUDA; // Estado según deuda restante.
    }

    /**
     * Corta el servicio por turno de racionamiento o sanción. REGLA CLAVE: antes de actuar consulta
     * esExento(); un usuario exento NO se corta aunque esté en zona de racionamiento (Necesidad 4).
     */
    public void cortar() {  // Método cortar: Corta el servicio por turno de racionamiento o sanción
        if (esExento()) { // Protección legal: hospitales, colegios, oficiales, etc.
            throw new IllegalStateException("El usuario goza de una excepción legal y no puede ser cortado.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        this.estadoServicio = EstadoServicio.CORTADO; // Servicio interrumpido.
    }

    /**
     * @return true si el usuario está protegido de cortes y suspensiones: tiene una excepción asignada
     * que le es aplicable. UsuarioOficial sobrescribe este método (siempre exento).
     */
    public boolean esExento() {  // Método esExento (devuelve boolean)
        return excepcion != null && excepcion.esAplicableA(this); // Ambas condiciones deben cumplirse.
    }

    /** @return true si el estrato está entre 1 y 6. */
    public boolean validarEstrato() {  // Método validarEstrato (devuelve boolean)
        return estrato >= 1 && estrato <= 6; // Misma regla que el setter.
    }

    // ------------------------------------------------------------------ POLIMORFISMO
    /**
     * Contrato polimórfico: cada tipo de usuario calcula su cobro de forma distinta (Resolución CRA 688).
     * Las subclases lo sobrescriben delegando en la estrategia inyectada (patrón Strategy).
     */
    public abstract BigDecimal calcularTarifa(Consumo consumo);  // Método calcularTarifa: Contrato polimórfico

    /** Implementación común del contrato heredado de Persona. */
    @Override
    public String obtenerTipoPersona() {  // Método obtenerTipoPersona: Implementación común del contrato heredado de Persona
        return "Usuario " + (tipoUsuario == null ? "sin tipo" : tipoUsuario.name().toLowerCase()); // Ej.: "Usuario residencial".
    }

    // ------------------------------------------------------------------ OBSERVER
    /**
     * El usuario es OBSERVADOR: cuando su factura vence (FACTURA_VENCIDA) pasa a estado EN_DEUDA
     * (enunciado, "Una factura vence sin ser pagada (el usuario pasa a estado En deuda)").
     * Los demás eventos son informativos para el usuario y los atiende el GestorNotificaciones.
     */
    @Override
    public void actualizar(Evento evento) {  // Método actualizar: El usuario es OBSERVADOR
        if (evento == null || !this.equals(evento.getUsuario())) { // Solo reacciona a eventos propios.
            return;  // Termina el método sin devolver valor
        }
        if (evento.esTipo(TipoEvento.FACTURA_VENCIDA)) { // Único evento que cambia el estado del usuario.
            marcarEnDeuda();  // Ejecuta la operación «marcar en deuda» (definida en esta clase o en un objeto colaborador)
        }
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return obtenerTipoPersona() + " [codigoSuscriptor=" + codigoSuscriptor + ", identificacion=" + identificacion  // Devuelve: obtenerTipoPersona() + " [codigoSuscriptor=" + codigoSuscriptor + ", ide...
                + ", estrato=" + estrato + ", estado=" + estadoServicio + "]"; // Resumen del suscriptor.
    }
}
