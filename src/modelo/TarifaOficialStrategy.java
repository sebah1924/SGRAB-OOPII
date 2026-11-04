package modelo; // Paquete del dominio.

import java.math.BigDecimal;   // Dinero con precisión exacta.
import java.math.RoundingMode; // Redondeo monetario.

/**
 * ESTRATEGIA de cobro para usuarios OFICIALES.
 *
 * REGLA (enunciado): se cobran SIN recargos especiales.
 *   Factura = Cargo fijo + (Cargo variable x Consumo).
 * Mantienen su exención de cortes (eso lo garantiza UsuarioOficial.esExento()).
 */
public class TarifaOficialStrategy implements IEstrategiaTarifa {  // Declaración de la clase TarifaOficialStrategy: ESTRATEGIA de cobro para usuarios OFICIALES

    /** Constructor sin parámetros: no requiere configuración. */
    public TarifaOficialStrategy() {  // Constructor: Constructor sin parámetros
        // Sin instrucciones: la estrategia no tiene estado.
    }

    /** Cobro oficial: solo cargo fijo + cargo variable x consumo. */
    @Override
    public BigDecimal calcularTarifa(Consumo consumo, Usuario usuario) {  // Método calcularTarifa: Cobro oficial
        if (consumo == null || consumo.getTarifaAplicada() == null) { // Sin consumo o tarifa no hay base de cálculo.
            throw new IllegalArgumentException("El consumo y su tarifa aplicada son obligatorios.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (!(usuario instanceof UsuarioOficial)) { // Esta fórmula solo es válida para entidades oficiales.
            throw new IllegalArgumentException("La estrategia oficial solo aplica a usuarios oficiales.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        Tarifa tarifa = consumo.obtenerTarifaDeTipo(TipoUsuario.OFICIAL); // Tarifa verificada: existe y es del tipo correcto.
        return tarifa.calcularFacturaBasica(consumo.getVolumenM3()).setScale(2, RoundingMode.HALF_UP); // Sin recargos.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "TarifaOficialStrategy []"; // Sin atributos que mostrar.
    }
}
