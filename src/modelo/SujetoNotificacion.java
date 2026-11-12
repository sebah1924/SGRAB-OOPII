package modelo;
                                              // Paquete único

/** Contrato Subject: permite registrar/notificar observadores. */
public interface SujetoNotificacion {
    void registrarObservador(ObservadorNotificacion o);
    void eliminarObservador(ObservadorNotificacion o);
    void notificarObservadores(String mensaje);
}