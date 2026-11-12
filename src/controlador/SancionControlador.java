package controlador;
                                                  // Paquete

import dao.*;                                                         // DAOs
import modelo.*;                                                      // Modelo
import java.util.List;                                                         // Listas

/**
 * Controlador de SANCIONES (Módulo 2 – Necesidad 7).
 * Responsabilidad ÚNICA: imponer sanciones, gestionar su ciclo de vida
 * y verificar excepciones antes de aplicar suspensión.
 */
public class SancionControlador {

    private final SancionDAO sancionDAO = new SancionDAO();                    // CRUD sanción
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();                    // Actualizar estado
    private final NotificacionControlador notifCtrl = new NotificacionControlador();

    /** Impone una multa económica (1 a 16 SMDLV). */
    public Sancion imponerMulta(Usuario u, String descripcion, int salariosMinimos)
            throws Exception {
        String codigo = "SAN-" + System.currentTimeMillis();                   // Código único
        Sancion s = new Sancion(codigo, TipoSancion.ECONOMICA, descripcion, salariosMinimos);
        sancionDAO.guardar(s, u.getId());                                       // Persiste
        notifCtrl.enviarNotificacion(u, EventoNotificacion.SANCION_IMPUESTA,
                "Sanción impuesta", "Multa de $" + s.getValorMulta() + " – " + descripcion);
        return s;
    }

    /** Impone una suspensión parcial (falla si el usuario está exento). */
    public Sancion imponerSuspension(Usuario u, String descripcion) throws Exception {
        if (u.tieneExcepcion())                                                // Regla: exentos NO se suspenden
            throw new Exception("Usuario exento: no se puede suspender, solo multar.");
        String codigo = "SAN-" + System.currentTimeMillis();
        Sancion s = new Sancion(codigo, TipoSancion.SUSPENSION, descripcion, 0);
        sancionDAO.guardar(s, u.getId());
        // Aplica corte al servicio
        u.suspender();
        usuarioDAO.actualizarEstado(u.getId(), u.getEstadoServicio());
        notifCtrl.enviarNotificacion(u, EventoNotificacion.SUSPENSION_EFECTIVA,
                "Suspensión aplicada", descripcion);
        return s;
    }

    /** Impone un curso pedagógico. */
    public Sancion imponerCurso(Usuario u, String descripcion) throws Exception {
        String codigo = "SAN-" + System.currentTimeMillis();
        Sancion s = new Sancion(codigo, TipoSancion.PEDAGOGICA, descripcion, 0);
        sancionDAO.guardar(s, u.getId());
        return s;
    }

    /** Cambia el estado de una sanción (ciclo de vida). */
    public void cambiarEstado(String codigo, EstadoSancion nuevo) throws Exception {
        sancionDAO.actualizarEstado(codigo, nuevo);                             // Persiste
    }

    /** Lista sanciones de un usuario. */
    public List<Sancion> listar(Usuario u) throws Exception {
        return sancionDAO.listarPorUsuario(u.getId());
    }
}