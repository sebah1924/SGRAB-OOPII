package modelo;
                                              // Paquete único

import java.time.LocalDate;                                            // Fechas
import java.util.ArrayList;                                            // Lista
import java.util.List;                                                 // Interfaz

/** Calendario maestro de turnos de racionamiento. */
public class CalendarioRacionamiento {

    // ---------- ATRIBUTOS ----------
    private String codigo;                                             // Único
    private Periodo vigencia;                                          // Rango del calendario
    private boolean suspendido;                                        // ¿Racionamiento suspendido?
    private final List<TurnoRacionamiento> turnos = new ArrayList<>();  // Turnos programados

    // ---------- CONSTRUCTORES ----------
    public CalendarioRacionamiento() { }

    public CalendarioRacionamiento(String codigo, Periodo vigencia) {
        setCodigo(codigo);
        setVigencia(vigencia);
        this.suspendido = false;                                       // Inicialmente activo
    }

    // ---------- GETTERS / SETTERS ----------
    public String getCodigo() { return codigo; }
    public void setCodigo(String c) {
        if (c == null || c.trim().isEmpty())
            throw new IllegalArgumentException("Código del calendario obligatorio.");
        this.codigo = c.trim();
    }

    public Periodo getVigencia() { return vigencia; }
    public void setVigencia(Periodo v) {
        if (v == null) throw new IllegalArgumentException("Vigencia obligatoria.");
        this.vigencia = v;
    }

    public boolean isSuspendido() { return suspendido; }
    public void setSuspendido(boolean s) { this.suspendido = s; }

    public List<TurnoRacionamiento> getTurnos() { return turnos; }

    // ---------- MÉTODOS DE NEGOCIO ----------
    /** Agrega un turno al calendario. */
    public void agregarTurno(TurnoRacionamiento t) {
        if (t == null) throw new IllegalArgumentException("Turno nulo.");
        t.validarHoras();                                              // Valida horas
        turnos.add(t);
    }

    /** Modifica un turno existente (por código). */
    public void modificarTurno(String codigo, TurnoRacionamiento nuevo) {
        for (int i = 0; i < turnos.size(); i++) {
            if (turnos.get(i).getCodigo().equals(codigo)) {
                nuevo.validarHoras();
                turnos.set(i, nuevo);                                  // Reemplaza
                return;
            }
        }
        throw new IllegalArgumentException("Turno no encontrado: " + codigo);
    }

    /** Suspende un turno puntual. */
    public void suspenderTurno(String codigo, String motivo) {
        for (TurnoRacionamiento t : turnos) {
            if (t.getCodigo().equals(codigo)) {
                t.setActivo(false);                                    // Desactiva
                t.setMotivo(motivo);
                return;
            }
        }
        throw new IllegalArgumentException("Turno no encontrado: " + codigo);
    }

    /** Suspende TODO el racionamiento. */
    public void suspenderRacionamiento() {
        this.suspendido = true;
    }

    /** Reactiva el racionamiento. */
    public void reactivarRacionamiento() {
        this.suspendido = false;
    }

    /** Verifica si un sector tiene turno activo en una fecha. */
    public boolean sectorEnTurno(Sector sector, LocalDate fecha) {
        if (suspendido) return false;                                  // Global suspendido
        for (TurnoRacionamiento t : turnos) {
            if (t.isActivo() &&
                t.getSector().equals(sector) &&
                fecha.equals(t.getFecha())) return true;
        }
        return false;
    }
}