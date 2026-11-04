package modelo; // Paquete del dominio.

import java.math.BigDecimal;   // Dinero con precisión exacta.
import java.math.RoundingMode; // Redondeo monetario.

/**
 * ESTRATEGIA de cobro para usuarios INDUSTRIALES.
 *
 * REGLAS (enunciado): no reciben subsidio.
 *   Factura = Cargo fijo + (Cargo variable x Consumo) + Cargo por carga contaminante (si aplica)
 *   - La carga contaminante se mide según el Decreto 1076 de 2015.
 *   - El cargo adicional se calcula con la TASA RETRIBUTIVA por vertimientos (valor unitario de contaminación).
 *   - Puede incluir cargo por demanda si el usuario tiene un caudal contratado.
 * Ejemplo: fijo 10.000, variable 3.000, 100 m3 y carga de 5 unidades => 10.000 + 300.000 + (5 x valor unitario).
 */
public class TarifaIndustrialStrategy implements IEstrategiaTarifa {  // Declaración de la clase TarifaIndustrialStrategy: ESTRATEGIA de cobro para usuarios INDUSTRIALES

    private BigDecimal valorUnitarioContaminacion; // Valor de cada unidad de carga contaminante (tasa retributiva).
    private BigDecimal cargoDemanda;               // Cargo por caudal contratado (cero si no aplica).

    /** Constructor sin parámetros: sin cargo por contaminación ni por demanda. */
    public TarifaIndustrialStrategy() {  // Constructor: Constructor sin parámetros
        this.valorUnitarioContaminacion = BigDecimal.ZERO; // Sin valor unitario no se cobra contaminación.
        this.cargoDemanda = BigDecimal.ZERO;               // Sin caudal contratado.
    }

    /** Constructor con valores inyectados. */
    public TarifaIndustrialStrategy(BigDecimal valorContaminacion, BigDecimal cargoDemanda) {  // Constructor: Constructor con valores inyectados
        setValorUnitarioContaminacion(valorContaminacion); // Valida no negativo.
        setCargoDemanda(cargoDemanda);                     // Valida no negativo.
    }

    /** @return el valor unitario de contaminación. */
    public BigDecimal getValorUnitarioContaminacion() {  // Getter: devuelve valor unitario contaminacion
        return valorUnitarioContaminacion; // Lectura simple.
    }

    /** Asigna el valor unitario de contaminación (no negativo; null equivale a cero). */
    public void setValorUnitarioContaminacion(BigDecimal valor) {  // Método setValorUnitarioContaminacion: Asigna el valor unitario de contaminación (no negativo; null equivale a cero)
        if (valor != null && valor.signum() < 0) { // Regla: no hay valores negativos.
            throw new IllegalArgumentException("El valor unitario de contaminación no puede ser negativo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.valorUnitarioContaminacion = (valor == null) ? BigDecimal.ZERO : valor; // null se normaliza a cero.
    }

    /** @return el cargo por demanda. */
    public BigDecimal getCargoDemanda() {  // Getter: devuelve cargo demanda
        return cargoDemanda; // Lectura simple.
    }

    /** Asigna el cargo por demanda (no negativo; null equivale a cero). */
    public void setCargoDemanda(BigDecimal cargo) {  // Método setCargoDemanda: Asigna el cargo por demanda (no negativo; null equivale a cero)
        if (cargo != null && cargo.signum() < 0) { // Regla: no hay cargos negativos.
            throw new IllegalArgumentException("El cargo por demanda no puede ser negativo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.cargoDemanda = (cargo == null) ? BigDecimal.ZERO : cargo; // null se normaliza a cero.
    }

    /** Cobro industrial: fijo + variable + (carga contaminante x valor unitario) + demanda. */
    @Override
    public BigDecimal calcularTarifa(Consumo consumo, Usuario usuario) {  // Método calcularTarifa: Cobro industrial
        if (consumo == null || consumo.getTarifaAplicada() == null) { // Sin consumo o tarifa no hay base de cálculo.
            throw new IllegalArgumentException("El consumo y su tarifa aplicada son obligatorios.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (!(usuario instanceof UsuarioIndustrial)) { // Esta fórmula solo es válida para industrias.
            throw new IllegalArgumentException("La estrategia industrial solo aplica a usuarios industriales.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        UsuarioIndustrial fabrica = (UsuarioIndustrial) usuario; // Conversión segura tras instanceof.
        Tarifa tarifa = consumo.obtenerTarifaDeTipo(TipoUsuario.INDUSTRIAL); // Tarifa verificada: existe y es del tipo correcto.
        BigDecimal cargoContaminacion = valorUnitarioContaminacion  // Declara «cargoContaminacion» (BigDecimal) con el valor: valorUnitarioContaminacion
                .multiply(BigDecimal.valueOf(fabrica.getCargaContaminante())); // Unidades de carga x valor unitario.
        return tarifa.calcularFacturaBasica(consumo.getVolumenM3()) // Fijo + variable x consumo.
                .add(cargoContaminacion)                           // + cargo por carga contaminante.
                .add(cargoDemanda)                                 // + cargo por demanda (caudal contratado).
                .setScale(2, RoundingMode.HALF_UP);  // Encadena la siguiente llamada de la misma instrucción: .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "TarifaIndustrialStrategy [valorUnitarioContaminacion=" + valorUnitarioContaminacion  // Devuelve: "TarifaIndustrialStrategy [valorUnitarioContaminacion=" + valorUnitarioC...
                + ", cargoDemanda=" + cargoDemanda + "]"; // Resumen.
    }
}
