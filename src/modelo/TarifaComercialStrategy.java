package modelo; // Paquete del dominio.

import java.math.BigDecimal;   // Dinero con precisión exacta.
import java.math.RoundingMode; // Redondeo monetario.

/**
 * ESTRATEGIA de cobro para usuarios COMERCIALES.
 *
 * REGLAS (enunciado): no reciben subsidio ni pagan contribución.
 *   Factura = Cargo fijo + (Cargo variable x Consumo)   [sobre el consumo TOTAL, sin topes]
 *   + cargo por vertimiento, cuando el uso genera aguas residuales no domésticas.
 * Ejemplo: restaurante con fijo 10.000, variable 3.000 y 30 m3 => 10.000 + 90.000 = $100.000.
 *
 * "Vertimiento" es la descarga de aguas residuales al alcantarillado. El cargo se recibe por
 * constructor (INYECCIÓN DE DEPENDENCIAS) para que el cálculo sea preciso sin escribir valores fijos en el código.
 */
public class TarifaComercialStrategy implements IEstrategiaTarifa {  // Declaración de la clase TarifaComercialStrategy: ESTRATEGIA de cobro para usuarios COMERCIALES

    private BigDecimal cargoVertimiento; // Cargo adicional por vertimiento (cero si no aplica).

    /** Constructor sin parámetros: no hay cargo por vertimiento. */
    public TarifaComercialStrategy() {  // Constructor: Constructor sin parámetros
        this.cargoVertimiento = BigDecimal.ZERO; // Sin vertimiento no se suma nada.
    }

    /** Constructor con el cargo por vertimiento inyectado. */
    public TarifaComercialStrategy(BigDecimal cargoVertimiento) {  // Constructor: Constructor con el cargo por vertimiento inyectado
        setCargoVertimiento(cargoVertimiento); // Valida que no sea negativo.
    }

    /** @return el cargo por vertimiento. */
    public BigDecimal getCargoVertimiento() {  // Getter: devuelve cargo vertimiento
        return cargoVertimiento; // Lectura simple.
    }

    /** Asigna el cargo por vertimiento (no negativo; null equivale a "no aplica"). */
    public void setCargoVertimiento(BigDecimal cargo) {  // Método setCargoVertimiento: Asigna el cargo por vertimiento (no negativo; null equivale a "no aplica")
        if (cargo != null && cargo.signum() < 0) { // Regla: no existen cargos negativos.
            throw new IllegalArgumentException("El cargo por vertimiento no puede ser negativo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.cargoVertimiento = (cargo == null) ? BigDecimal.ZERO : cargo; // null se normaliza a cero.
    }

    /** Cobro comercial: fijo + variable sobre el total + vertimiento (si aplica). */
    @Override
    public BigDecimal calcularTarifa(Consumo consumo, Usuario usuario) {  // Método calcularTarifa: Cobro comercial
        if (consumo == null || consumo.getTarifaAplicada() == null) { // Sin consumo o tarifa no hay base de cálculo.
            throw new IllegalArgumentException("El consumo y su tarifa aplicada son obligatorios.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (!(usuario instanceof UsuarioComercial)) { // Esta fórmula solo es válida para comercios.
            throw new IllegalArgumentException("La estrategia comercial solo aplica a usuarios comerciales.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        Tarifa tarifa = consumo.obtenerTarifaDeTipo(TipoUsuario.COMERCIAL); // Tarifa verificada: existe y es del tipo correcto.
        return tarifa.calcularFacturaBasica(consumo.getVolumenM3()) // Fijo + variable x TOTAL de m3 (sin topes).
                .add(cargoVertimiento)                             // + vertimiento si fue configurado.
                .setScale(2, RoundingMode.HALF_UP);  // Encadena la siguiente llamada de la misma instrucción: .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "TarifaComercialStrategy [cargoVertimiento=" + cargoVertimiento + "]"; // Resumen.
    }
}
