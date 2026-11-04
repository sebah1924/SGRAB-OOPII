package modelo; // Paquete del dominio.

import java.math.BigDecimal; // BigDecimal: tipo de precisión exacta para dinero (double produce errores de redondeo).

/**
 * PATRÓN STRATEGY (Estrategia).
 *
 * Contexto: Strategy define una "familia de algoritmos" intercambiables detrás de una misma interfaz.
 * Aquí cada tipo de usuario se cobra con una fórmula distinta (Resolución CRA 688 de 2014).
 * En vez de un if/else gigante dentro de Usuario, cada fórmula vive en su propia clase
 * (TarifaResidencialStrategy, TarifaComercialStrategy, ...) y el Usuario DELEGA el cálculo
 * en la estrategia que se le inyectó.
 *
 * Principio SOLID aplicado: Abierto/Cerrado (OCP). Para un nuevo tipo de tarifa se crea una clase
 * nueva SIN modificar el núcleo del sistema (requerimiento no funcional de Extensibilidad).
 */
public interface IEstrategiaTarifa {  // Declaración de la interfaz IEstrategiaTarifa: PATRÓN STRATEGY (Estrategia)

    /**
     * Calcula el valor a cobrar por UN consumo (una lectura mensual) de un usuario.
     *
     * @param consumo lectura de consumo que contiene el volumen en m3 y la tarifa aplicada.
     * @param usuario suscriptor al que se le cobra (aporta estrato, carga contaminante, etc.).
     * @return valor en pesos con 2 decimales.
     */
    BigDecimal calcularTarifa(Consumo consumo, Usuario usuario); // Método abstracto: no tiene cuerpo, lo implementa cada estrategia.
}
