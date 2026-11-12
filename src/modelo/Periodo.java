package modelo;
                                              // Paquete único

import java.time.LocalDate;                                            // Fechas
import java.util.Objects;                                              // equals/hashCode

/** Rango de fechas [inicio, fin) reutilizable en vigencias y facturas. */
public class Periodo {

    // ---------- ATRIBUTOS ----------
    private LocalDate fechaInicio;                                     // Inicio del periodo
    private LocalDate fechaFin;                                        // Fin del periodo

    // ---------- CONSTRUCTORES ----------
    public Periodo() { }

    public Periodo(LocalDate inicio, LocalDate fin) {                  // Completo
        setFechaInicio(inicio);
        setFechaFin(fin);
        validar();                                                     // Inicio < fin
    }

    // ---------- GETTERS / SETTERS ----------
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    // ---------- MÉTODOS DE NEGOCIO ----------
    /** Valida que ambas fechas existan y que inicio < fin. */
    public void validar() {
        if (fechaInicio == null || fechaFin == null)
            throw new IllegalArgumentException("Debe indicar ambas fechas del periodo.");
        if (!fechaInicio.isBefore(fechaFin))
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la de fin.");
    }

    /** Verifica si una fecha está dentro del rango (inclusive en bordes). */
    public boolean contiene(LocalDate fecha) {
        return fecha != null && !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    /** Verifica solapamiento con otro periodo (para validar tarifas). */
    public boolean seSolapaCon(Periodo otro) {
        return !this.fechaFin.isBefore(otro.fechaInicio) &&
               !otro.fechaFin.isBefore(this.fechaInicio);
    }

    /** Cantidad de días del periodo. */
    public long diasTotales() {
        return java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }

    @Override
    public String toString() { return fechaInicio + " → " + fechaFin; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Periodo)) return false;
        Periodo p = (Periodo) o;
        return Objects.equals(fechaInicio, p.fechaInicio) &&
               Objects.equals(fechaFin, p.fechaFin);
    }

    @Override
    public int hashCode() { return Objects.hash(fechaInicio, fechaFin); }
}