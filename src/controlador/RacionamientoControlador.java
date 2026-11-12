package controlador;
                                                  // Paquete

import dao.*;                                                         // DAOs
import modelo.*;                                                      // Modelo
import java.time.LocalDate;                                                    // Fechas
import java.time.LocalTime;                                                    // Horas
import java.util.List;                                                         // Listas

/**
 * Controlador de RACIONAMIENTO (Necesidades 3 y 4).
 * Responsabilidad ÚNICA: gestionar turnos, calendario, y aplicar cortes
 * respetando las excepciones legales (hospitales, oficiales, etc.).
 */
public class RacionamientoControlador {

    private final TurnoRacionamientoDAO turnoDAO = new TurnoRacionamientoDAO();
    private final CalendarioRacionamientoDAO calendarioDAO = new CalendarioRacionamientoDAO();
    private final SectorDAO sectorDAO = new SectorDAO();
    private final UsuarioControlador usuarioCtrl = new UsuarioControlador();
    private final NotificacionControlador notifCtrl = new NotificacionControlador();

    /** Programa un turno de racionamiento para un sector. */
    public void programarTurno(String codigo, Sector sector, LocalDate fecha,
                               LocalTime inicio, LocalTime fin) throws Exception {
        TurnoRacionamiento t = new TurnoRacionamiento(codigo, fecha, inicio, fin, sector);
        t.validarHoras();                                                       // Valida inicio < fin
        turnoDAO.guardar(t);                                                    // Persiste
    }

    /** Aplica cortes para todos los usuarios de un sector en una fecha/hora. */
    public int aplicarCortesPorSector(int sectorId, LocalDate fecha, LocalTime hora)
            throws Exception {
        // Verifica si hay turno activo
        if (!turnoDAO.hayTurno(sectorId, fecha, hora)) return 0;
        // Recorre usuarios y aplica corte si NO están exentos
        List<Usuario> usuarios = usuarioCtrl.listarTodos();
        int aplicados = 0;
        for (Usuario u : usuarios) {
            if (u.getSector() != null && u.getSector().equals(String.valueOf(sectorId))) {
                if (!u.tieneExcepcion()) {                                      // Exento = no se corta
                    u.cortar();
                    usuarioCtrl.cortar(u);                                      // Persiste + notifica
                    aplicados++;
                }
            }
        }
        return aplicados;
    }

    /** Lista turnos activos de un sector. */
    public List<TurnoRacionamiento> listarTurnos(int sectorId) throws Exception {
        return turnoDAO.listarPorSector(sectorId);
    }

    /** Suspende todo el racionamiento (por lluvias, por ejemplo). */
    public void suspenderCalendario(String codigo) throws Exception {
        calendarioDAO.actualizarSuspension(codigo, true);
        // Notifica a TODOS los usuarios (en producción se filtra por afectados)
        for (Usuario u : usuarioCtrl.listarTodos()) {
            notifCtrl.enviarNotificacion(u, EventoNotificacion.CAMBIO_CALENDARIO,
                    "Racionamiento suspendido",
                    "El racionamiento ha sido suspendido temporalmente.");
        }
    }

    /** Reactiva el calendario. */
    public void reactivarCalendario(String codigo) throws Exception {
        calendarioDAO.actualizarSuspension(codigo, false);
    }

    /** Lista sectores. */
    public List<Sector> listarSectores() throws Exception {
        return sectorDAO.listar();
    }
}