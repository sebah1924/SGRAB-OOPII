package modelo;
                                              // Paquete único

import java.time.LocalDate;                                            // Fecha del turno
import java.time.LocalTime;                                            // Hora inicio/fin

/** Turno de racionamiento programado para un sector en una fecha. */
public class TurnoRacionamiento {

    // ---------- ATRIBUTOS ----------
    private int id;                                                    // ID interno
    private String codigo;                                             // Código único
    private LocalDate fecha;                                           // Día del turno
    private LocalTime horaInicio;                                      // Hora inicio
    private LocalTime horaFin;                                         // Hora fin
    private Sector sector;                                             // Sector afectado
    private boolean activo;                                            // ¿Vigente o modificado?
    private String motivo;                                             // Motivo del cambio

    // ---------- CONSTRUCTORES ----------
    public TurnoRacionamiento() { }

    public TurnoRacionamiento(String codigo, LocalDate fecha,
                              LocalTime inicio, LocalTime fin, Sector sector) {
        setCodigo(codigo);
        setFecha(fecha);
        setHoraInicio(inicio);
        setHoraFin(fin);
        setSector(sector);
        this.activo = true;                                            // Activo al crear
    }

    // ---------- GETTERS / SETTERS ----------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String c) {
        if (c == null || c.trim().isEmpty())
            throw new IllegalArgumentException("Código del turno obligatorio.");
        this.codigo = c.trim();
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate f) { this.fecha = f; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime h) { this.horaInicio = h; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime h) { this.horaFin = h; }

    public Sector getSector() { return sector; }
    public void setSector(Sector s) {
        if (s == null) throw new IllegalArgumentException("Sector obligatorio.");
        this.sector = s;
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    // ---------- MÉTODOS ----------
    /** Valida que horaInicio < horaFin. */
    public void validarHoras() {
        if (horaInicio == null || horaFin == null)
            throw new IllegalArgumentException("Debe indicar horas del turno.");
        if (!horaInicio.isBefore(horaFin))
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la fin.");
    }

    /** Verifica si el turno cubre una hora concreta. */
    public boolean cubreHora(LocalTime h) {
        return activo && h != null && !h.isBefore(horaInicio) && !h.isAfter(horaFin);
    }

    @Override
    public String toString() {
        return codigo + " | " + fecha + " " + horaInicio + "-" + horaFin +
               " | Sector " + (sector != null ? sector.getNombre() : "?");
    }
}