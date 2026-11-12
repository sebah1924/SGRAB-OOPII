package modelo;
                                              // Paquete único

import java.time.LocalDate;                                            // Fechas
import java.util.ArrayList;                                            // Listas
import java.util.List;                                                 // Interfaz List
import java.util.Objects;                                              // equals/hashCode
import java.util.stream.Collectors;                                    // Filtros por stream

/**
 * Clase ABSTRACTA que representa a un suscriptor de la EAAB.
 * HEREDA de Persona (porque aporta atributos propios del servicio).
 * Implementa el patrón Observer para recibir notificaciones.
 */

public abstract class Usuario extends Persona implements ObservadorNotificacion {

    // ---------- ATRIBUTOS ----------
    private int id;                                                   // ← PK del usuario en BD (autoincrement)
    private String codigoSuscriptor;
    private LocalDate fechaRegistro;
    private TipoUsuario tipoUsuario;
    private int estrato;
    private String sector;
    private CanalNotificacion canalNotificacionPreferido;
    private EstadoServicio estadoServicio;
    private double consumoPromedio;
    private final List<Consumo> historialConsumos = new ArrayList<>();
    private final List<Factura> historialFacturas = new ArrayList<>();
    private LocalDate fechaBaja;
    private String motivoBaja;
    private EstrategiaTarifa estrategia;
    private Excepcion excepcionAplicable;

    // ---------- CONSTRUCTORES ----------
    public Usuario() { super(); }

    public Usuario(String identificacion, String nombre, String apellido,
                   String codigoSuscriptor, int estrato) {
        super(identificacion, nombre, apellido);
        setCodigoSuscriptor(codigoSuscriptor);
        setEstrato(estrato);
        this.fechaRegistro = LocalDate.now();
        this.estadoServicio = EstadoServicio.ACTIVO;
    }

    public Usuario(String identificacion, String nombre, String apellido,
                   LocalDate fechaNacimiento, String direccion,
                   String telefono, String correo,
                   String codigoSuscriptor, int estrato, String sector,
                   CanalNotificacion canal, TipoUsuario tipo) {
        super(identificacion, nombre, apellido, fechaNacimiento,
              direccion, telefono, correo);
        setCodigoSuscriptor(codigoSuscriptor);
        setEstrato(estrato);
        setSector(sector);
        setCanalNotificacionPreferido(canal);
        setTipoUsuario(tipo);
        this.fechaRegistro = LocalDate.now();
        this.estadoServicio = EstadoServicio.ACTIVO;
    }

    // ---------- GETTERS / SETTERS ----------
    // ← NUEVO: id del usuario
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigoSuscriptor() { return codigoSuscriptor; }
    public void setCodigoSuscriptor(String codigoSuscriptor) {
        if (codigoSuscriptor == null || codigoSuscriptor.trim().isEmpty())
            throw new IllegalArgumentException("El código de suscriptor es obligatorio.");
        this.codigoSuscriptor = codigoSuscriptor.trim();
    }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public int getEstrato() { return estrato; }
    public void setEstrato(int estrato) {
        if (estrato < 1 || estrato > 6)
            throw new IllegalArgumentException("El estrato debe estar entre 1 y 6.");
        this.estrato = estrato;
    }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public CanalNotificacion getCanalNotificacionPreferido() { return canalNotificacionPreferido; }
    public void setCanalNotificacionPreferido(CanalNotificacion canal) { this.canalNotificacionPreferido = canal; }

    public EstadoServicio getEstadoServicio() { return estadoServicio; }
    public void setEstadoServicio(EstadoServicio estadoServicio) { this.estadoServicio = estadoServicio; }

    public double getConsumoPromedio() { return consumoPromedio; }
    public void setConsumoPromedio(double consumoPromedio) { this.consumoPromedio = consumoPromedio; }

    public List<Consumo> getHistorialConsumos() { return historialConsumos; }
    public List<Factura> getHistorialFacturas() { return historialFacturas; }

    public LocalDate getFechaBaja() { return fechaBaja; }
    public String getMotivoBaja() { return motivoBaja; }

    public EstrategiaTarifa getEstrategia() { return estrategia; }
    public void setEstrategia(EstrategiaTarifa estrategia) { this.estrategia = estrategia; }

    public Excepcion getExcepcionAplicable() { return excepcionAplicable; }
    public void setExcepcionAplicable(Excepcion excepcionAplicable) { this.excepcionAplicable = excepcionAplicable; }

    // ---------- MÉTODOS DE NEGOCIO ----------
    public void agregarConsumo(Consumo c) {
        if (c == null) throw new IllegalArgumentException("Consumo nulo.");
        historialConsumos.add(c);
        calcularConsumoPromedio();
    }

    public void agregarFactura(Factura f) {
        if (f == null) throw new IllegalArgumentException("Factura nula.");
        historialFacturas.add(f);
    }

    public double calcularConsumoPromedio() {
        if (historialConsumos.isEmpty()) { consumoPromedio = 0; return 0; }
        double suma = 0;
        for (Consumo c : historialConsumos) suma += c.getVolumen();
        consumoPromedio = suma / historialConsumos.size();
        return consumoPromedio;
    }

    public void eliminar(String motivo) {
        if (motivo == null || motivo.trim().isEmpty())
            throw new IllegalArgumentException("Debe indicar el motivo de la baja.");
        this.fechaBaja = LocalDate.now();
        this.motivoBaja = motivo.trim();
    }

    public boolean estaEliminado() { return fechaBaja != null; }

    public double consumoTotalEntre(LocalDate desde, LocalDate hasta) {
        double t = 0;
        for (Consumo c : historialConsumos) {
            LocalDate f = c.getFechaLectura();
            if (f != null && !f.isBefore(desde) && !f.isAfter(hasta)) t += c.getVolumen();
        }
        return t;
    }

    public List<Factura> facturasPorEstado(EstadoPago estado) {
        return historialFacturas.stream()
                .filter(f -> f.getEstadoPago() == estado)
                .collect(Collectors.toList());
    }

    public List<Factura> facturasEntreFechas(LocalDate desde, LocalDate hasta) {
        return historialFacturas.stream()
                .filter(f -> !f.getFechaEmision().isBefore(desde) &&
                             !f.getFechaEmision().isAfter(hasta))
                .collect(Collectors.toList());
    }

    public long contarFacturasVencidas() {
        return historialFacturas.stream()
                .filter(f -> f.getEstadoPago() == EstadoPago.VENCIDA)
                .count();
    }

    public void actualizarEstadoPorDeudas() {
        long vencidas = contarFacturasVencidas();
        if (vencidas >= 2) estadoServicio = EstadoServicio.SUSPENDIDO;
        else if (vencidas == 1) estadoServicio = EstadoServicio.EN_DEUDA;
        else if (estadoServicio == EstadoServicio.EN_DEUDA ||
                 estadoServicio == EstadoServicio.SUSPENDIDO)
            estadoServicio = EstadoServicio.ACTIVO;
    }

    public void suspender() {
        if (!puedeSerCortado())
            throw new IllegalStateException("Usuario exento: no puede suspenderse.");
        this.estadoServicio = EstadoServicio.SUSPENDIDO;
    }

    public void reactivar() {
        if (estadoServicio == EstadoServicio.SUSPENDIDO ||
            estadoServicio == EstadoServicio.CORTADO)
            this.estadoServicio = EstadoServicio.ACTIVO;
    }

    public void cortar() {
        if (!puedeSerCortado())
            throw new IllegalStateException("Usuario exento: no puede cortarse.");
        this.estadoServicio = EstadoServicio.CORTADO;
    }

    public void marcarEnDeuda() {
        if (estadoServicio == EstadoServicio.ACTIVO)
            this.estadoServicio = EstadoServicio.EN_DEUDA;
    }

    // ---------- ABSTRACTOS ----------
    public abstract boolean tieneExcepcion();
    public abstract boolean puedeSerCortado();

    // ---------- OBSERVER ----------
    @Override
    public void actualizar(String mensaje) {
        System.out.println("[NOTIF " + codigoSuscriptor + "] " + mensaje);
    }

    // ---------- SOBREESCRITURA ----------
    @Override
    public String getTipoPersona() { return "Usuario EAAB"; }

    @Override
    public String toString() {
        return super.toString() + " | Suscriptor: " + codigoSuscriptor +
               " | Estado: " + estadoServicio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        if (!super.equals(o)) return false;
        Usuario u = (Usuario) o;
        return Objects.equals(codigoSuscriptor, u.codigoSuscriptor);
    }

    @Override
    public int hashCode() { return Objects.hash(super.hashCode(), codigoSuscriptor); }
}