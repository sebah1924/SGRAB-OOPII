package modelo; // Paquete del dominio.

/**
 * PATRÓN OBSERVER - rol "Observador" (suscriptor).
 *
 * Contexto: Observer define una dependencia uno-a-muchos: cuando un objeto "observable" cambia
 * de estado, avisa automáticamente a todos sus observadores. Así Factura y EmpresaServicioPublico
 * no necesitan conocer a quién le interesa lo que ocurre (bajo acoplamiento).
 * Implementan IObservador: Usuario (reacciona al vencimiento de una factura) y
 * GestorNotificaciones (envía los mensajes por el canal correspondiente).
 */
public interface IObservador {  // Declaración de la interfaz IObservador: PATRÓN OBSERVER - rol "Observador" (suscriptor)

    /**
     * Método que el observable invoca cuando ocurre un evento relevante.
     *
     * @param evento descripción del suceso (tipo, usuario afectado, datos y fecha/hora).
     */
    void actualizar(Evento evento); // Callback ("llamada de vuelta") disparado por el observable.
}
