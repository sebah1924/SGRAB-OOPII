package modelo; // Paquete del dominio.

import java.math.BigDecimal;   // Dinero con precisión exacta.
import java.math.RoundingMode; // Redondeo monetario.

/**
 * ESTRATEGIA de cobro para usuarios RESIDENCIALES (Resolución CRA 688 de 2014).
 *
 * TABLA DE SUBSIDIOS Y CONTRIBUCIÓN (enunciado, "Cómo se cobra a cada tipo de usuario"):
 *   Estrato 1: subsidio hasta 70 %   | Estrato 2: hasta 40 %   | Estrato 3: hasta 15 % (solo si la cobertura es alta)
 *   Estrato 4: ni subsidio ni contribución (0 %)
 *   Estrato 5 y 6: contribución de solidaridad del 20 %.
 *
 * FÓRMULAS:
 *  - Con subsidio:      Factura = (Cargo fijo + Cargo variable x Consumo) x (1 - %Subsidio)
 *  - Con contribución:  Factura = (Cargo fijo + Cargo variable x Consumo) x (1 + %Contribución)
 *  - El subsidio solo aplica al consumo BÁSICO (tope de 20 m3 al mes por hogar) y nunca supera el 70 % del costo.
 *    Por eso el consumo que excede los 20 m3 se cobra a tarifa plena.
 *  Ejemplo (estrato 2, fijo 10.000, variable 3.000, 15 m3, subsidio 40 %): 55.000 x 0,60 = $33.000.
 *
 * Esta clase es la ÚNICA fuente de las tablas de subsidio y contribución.
 */
public class TarifaResidencialStrategy implements IEstrategiaTarifa {  // Declaración de la clase TarifaResidencialStrategy: ESTRATEGIA de cobro para usuarios RESIDENCIALES (Resolución CRA 688 de 2014)

    /** Consumo básico mensual (m3) al que se limita el subsidio. */
    public static final double CONSUMO_BASICO_SUBSIDIABLE_M3 = 20.0;  // Atributo «CONSUMO_BASICO_SUBSIDIABLE_M3» de tipo double

    /** Constructor sin parámetros: la estrategia residencial no necesita configuración. */
    public TarifaResidencialStrategy() {  // Constructor: Constructor sin parámetros
        // Sin instrucciones: no tiene estado propio.
    }

    /** @return fracción MÁXIMA de subsidio según el estrato (0,70 / 0,40 / 0,15; 0 en los demás). */
    public double obtenerSubsidio(int estrato) {  // Método obtenerSubsidio (devuelve double)
        switch (estrato) { // Un case por cada estrato que tiene subsidio.
            case 1:  // Caso 1
                return 0.70; // Estrato 1: hasta 70 %.
            case 2:  // Caso 2
                return 0.40; // Estrato 2: hasta 40 %.
            case 3:  // Caso 3
                return 0.15; // Estrato 3: hasta 15 % (solo si la cobertura del servicio es alta).
            default:  // Caso por defecto: cualquier otro valor
                return 0.0;  // Estratos 4, 5 y 6 no reciben subsidio.
        }
    }

    /** @return fracción de contribución de solidaridad según el estrato (0,20 para 5 y 6; 0 en los demás). */
    public double obtenerContribucion(int estrato) {  // Método obtenerContribucion (devuelve double)
        return (estrato == 5 || estrato == 6) ? 0.20 : 0.0; // Estrato 4 paga 0 % (ni subsidia ni contribuye).
    }

    /** @return la parte del consumo que puede subsidiarse: como máximo 20 m3 al mes. */
    public double calcularConsumoSubsidiable(double consumoM3) {  // Método calcularConsumoSubsidiable (devuelve double)
        return Math.min(consumoM3, CONSUMO_BASICO_SUBSIDIABLE_M3); // Math.min elige el menor de los dos valores.
    }

    /**
     * Calcula el cobro de un consumo residencial aplicando subsidio o contribución según el estrato.
     * El desincentivo por consumo excesivo NO se calcula aquí: se suma aparte en la factura porque no se subsidia.
     */
    @Override
    public BigDecimal calcularTarifa(Consumo consumo, Usuario usuario) {  // Método calcularTarifa: Calcula el cobro de un consumo residencial aplicando subsidio o contribución según el estrato
        if (consumo == null || consumo.getTarifaAplicada() == null) { // Sin consumo o tarifa no hay base de cálculo.
            throw new IllegalArgumentException("El consumo y su tarifa aplicada son obligatorios.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (!(usuario instanceof UsuarioResidencial)) { // Esta fórmula solo es válida para hogares.
            throw new IllegalArgumentException("La estrategia residencial solo aplica a usuarios residenciales.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        UsuarioResidencial hogar = (UsuarioResidencial) usuario; // Conversión segura tras instanceof.
        Tarifa tarifa = consumo.obtenerTarifaDeTipo(TipoUsuario.RESIDENCIAL); // Tarifa verificada: existe y es del tipo correcto.
        double volumen = consumo.getVolumenM3();                       // m3 consumidos.
        BigDecimal basico = tarifa.calcularFacturaBasica(volumen);     // Cargo fijo + (variable x consumo).

        double contribucion = obtenerContribucion(hogar.getEstrato()); // 0,20 para estratos 5 y 6.
        if (contribucion > 0) { // Estratos 5 y 6: se SUMA la contribución al cargo.
            return basico.multiply(BigDecimal.ONE.add(BigDecimal.valueOf(contribucion))).setScale(2, RoundingMode.HALF_UP);  // Devuelve: basico.multiply(BigDecimal.ONE.add(BigDecimal.valueOf(contribucion))).se...
        }

        if (hogar.esElegibleSubsidio() && hogar.isBeneficioSubsidio()) { // Estratos 1-3 con beneficio concedido.
            // El % efectivo es el menor entre el máximo legal del estrato y el subsidio autorizado por la tarifa.
            double porcentaje = Math.min(obtenerSubsidio(hogar.getEstrato()), tarifa.getSubsidio());  // Declara «porcentaje» (double) con el valor: Math.min(obtenerSubsidio(hogar.getEstrato()), t...
            porcentaje = Math.min(porcentaje, Tarifa.SUBSIDIO_MAXIMO); // Nunca supera el 70 % del costo.
            double subsidiable = calcularConsumoSubsidiable(volumen);  // Hasta 20 m3 reciben descuento.
            double excedente = volumen - subsidiable;                  // Lo que pasa de 20 m3 se cobra completo.
            BigDecimal parteSubsidiada = tarifa.calcularFacturaBasica(subsidiable)  // Declara «parteSubsidiada» (BigDecimal) con el valor: tarifa.calcularFacturaBasica(subsidiable)
                    .multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(porcentaje))); // Fijo + variable básico, con descuento.
            BigDecimal parteExcedente = tarifa.getCargoVariable().multiply(BigDecimal.valueOf(excedente)); // Sin descuento.
            return parteSubsidiada.add(parteExcedente).setScale(2, RoundingMode.HALF_UP);  // Devuelve: parteSubsidiada.add(parteExcedente).setScale(2, RoundingMode.HALF_UP)
        }

        return basico; // Estrato 4 (o sin beneficio): ni subsidio ni contribución.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "TarifaResidencialStrategy [consumoBasicoSubsidiable=" + CONSUMO_BASICO_SUBSIDIABLE_M3 + " m3]"; // Resumen.
    }
}
