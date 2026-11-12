package modelo;
                                              // Paquete único

import java.util.ArrayList;                                            // Lista
import java.util.List;                                                 // Interfaz List

/** Gestor central de notificaciones. Implementa Subject del Observer. */
public class GestorNotificaciones implements SujetoNotificacion {

    // ---------- ATRIBUTOS ----------
    private final List<ObservadorNotificacion> observadores = new ArrayList<>(); // Observadores
    private final List<Notificacion> historial = new ArrayList<>();              // Histórico

    // ---------- OBSERVER ----------
    @Override
    public void registrarObservador(ObservadorNotificacion o) {
        if (o != null && !observadores.contains(o)) observadores.add(o);
    }

    @Override
    public void eliminarObservador(ObservadorNotificacion o) {
        observadores.remove(o);
    }

    @Override
    public void notificarObservadores(String mensaje) {
        for (ObservadorNotificacion o : observadores) o.actualizar(mensaje);
    }

    // ---------- MÉTODOS ----------
    /** Envía una notificación, la guarda y avisa a los observadores. */
    public boolean enviarNotificacion(Notificacion n) {
        if (n == null) throw new IllegalArgumentException("Notificación nula.");
        boolean ok = n.enviar();                                        // Canal decide
        if (ok) {
            historial.add(n);                                           // Guarda en historial
            notificarObservadores(n.getTitulo());                       // Notifica observadores
        }
        return ok;
    }

    /** Envía una notificación automática por evento al usuario. */
    public void notificarPorEvento(Usuario usuario, EventoNotificacion evento,
                                   String codigo, String titulo, String cuerpo) {
        CanalNotificacion canal = usuario.getCanalNotificacionPreferido() != null
                ? usuario.getCanalNotificacionPreferido()
                : CanalNotificacion.EMAIL;                              // Fallback por defecto
        Notificacion n = new Notificacion(codigo, titulo, cuerpo, usuario, canal, evento);
        enviarNotificacion(n);                                          // Envía y guarda
    }

    public List<Notificacion> getHistorial() { return historial; }
    public List<ObservadorNotificacion> getObservadores() { return observadores; }

    @Override
    public String toString() {
        return "GestorNotificaciones[historial=" + historial.size() + "]";
    }
}