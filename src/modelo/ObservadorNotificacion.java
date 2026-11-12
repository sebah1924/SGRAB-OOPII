package modelo;
                                              // Paquete único

/** Contrato Observer: cualquier observador debe implementar `actualizar`. */
public interface ObservadorNotificacion {
    void actualizar(String mensaje);                                    // Recibe el mensaje
}