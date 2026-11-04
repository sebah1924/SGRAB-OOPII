package modelo; // Paquete del dominio.

/**
 * PATRÓN OBSERVER - rol "Observable" (sujeto).
 *
 * Contexto: la clase que implementa esta interfaz mantiene una lista de observadores y los
 * notifica cuando algo importante sucede. La implementan Factura (emisión y vencimiento) y
 * EmpresaServicioPublico (suspensión, cortes y cambios del calendario de racionamiento).
 */
public interface IObservable {  // Declaración de la interfaz IObservable: PATRÓN OBSERVER - rol "Observable" (sujeto)

    /** Suscribe un observador para que reciba los eventos futuros. */
    void agregarObservador(IObservador obs); // Alta en la lista de suscriptores.

    /** Cancela la suscripción de un observador. */
    void eliminarObservador(IObservador obs); // Baja de la lista de suscriptores.

    /** Avisa a TODOS los observadores suscritos que ocurrió un evento. */
    void notificarObservadores(Evento evento); // Recorre la lista y llama a actualizar() de cada uno.
}
