package modelo;
                                              // Paquete único

/** Interfaz STRATEGY: contrato de cálculo por tipo de usuario. */
public interface EstrategiaTarifa {
    double calcularValor(Usuario usuario, Consumo consumo, Tarifa tarifa); // Cálculo base
    String getNombre();                                                    // Nombre auditable
}