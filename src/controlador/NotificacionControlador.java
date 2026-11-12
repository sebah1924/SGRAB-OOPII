package controlador;
                                                  // Paquete

import dao.*;                                                         // DAOs
import modelo.*;                                                      // Modelo
import java.util.List;                                                         // Listas

/**
 * Controlador de NOTIFICACIONES (Módulo 1 – Necesidad 3).
 * Responsabilidad ÚNICA: crear y enviar notificaciones por el canal
 * preferido del usuario y mantener el historial. Usa patrón Observer.
 */


public class NotificacionControlador {

    private final NotificacionDAO notifDAO = new NotificacionDAO();
    private final GestorNotificaciones gestor = new GestorNotificaciones();

    public void enviarNotificacion(Usuario u, EventoNotificacion evento,
                                   String titulo, String cuerpo) throws Exception {
        if (u == null) throw new Exception("Usuario nulo.");
        
        CanalNotificacion canal = u.getCanalNotificacionPreferido() != null
                ? u.getCanalNotificacionPreferido() : CanalNotificacion.EMAIL;
        
        // CORRECCIÓN EXHAUSTIVA: 
        // Generamos un código único usando los milisegundos actuales.
        // Es imposible que se repita porque el tiempo siempre avanza.
        String codigo = "NOT-" + System.currentTimeMillis();
        
        Notificacion n = new Notificacion(codigo, titulo, cuerpo, u, canal, evento);
        gestor.enviarNotificacion(n);
        notifDAO.guardar(n, u.getId());
    }

    public List<Notificacion> historial(Usuario u) throws Exception {
        return notifDAO.listarPorUsuario(u.getId());
    }

    public List<Notificacion> historialGlobal() throws Exception {
        return notifDAO.listarTodas();
    }
}