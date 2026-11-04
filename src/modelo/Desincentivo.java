package modelo; // Paquete del dominio.

import java.math.BigDecimal;   // Dinero con precisión exacta.
import java.math.RoundingMode; // Redondeo monetario.

/**
 * Cobro adicional ("desincentivo") por consumo excesivo de agua (Necesidad 6).
 *
 * REGLAS DE NEGOCIO (enunciado, "El desincentivo por consumo excesivo"):
 *  - Se aplica ÚNICAMENTE a usuarios residenciales.
 *  - Se activa cuando el consumo supera el límite de 44 m3 por bimestre (22 m3 por mes).
 *  - El consumo excedente es la diferencia entre el consumo total y el límite (consumo básico).
 *  - Cada m3 excedente cuesta $3.380,84 (Bogotá, 2024) y se cobra en la MISMA factura del periodo.
 *  - Si el consumo total es menor o igual al límite, NO hay desincentivo.
 *  - El desincentivo NO se subsidia (por eso se suma aparte del resultado de la estrategia tarifaria).
 * Ejemplo: consumo 50 m3 en un bimestre => excedente 50 - 44 = 6 m3 => 6 x 3.380,84 = $20.285,04.
 */
public class Desincentivo {  // Declaración de la clase Desincentivo: Cobro adicional ("desincentivo") por consumo excesivo de agua (Necesidad 6)

    public static final double LIMITE_BIMESTRAL_POR_DEFECTO = 44.0;                  // m3 por bimestre (22 por mes).
    public static final BigDecimal VALOR_M3_POR_DEFECTO = new BigDecimal("3380.84"); // $ por m3 excedente (Bogotá, 2024).

    private String codigo;              // Identificador del desincentivo.
    private double limiteBimestralM3;   // Consumo máximo sin recargo por bimestre.
    private BigDecimal valorM3Excedente; // Precio de cada m3 que exceda el límite.

    /** Constructor vacío (para la capa DAO). */
    public Desincentivo() {  // Constructor: Constructor vacío (para la capa DAO)
        // Sin instrucciones.
    }

    /** Constructor completo; valida mediante los setters. */
    public Desincentivo(String codigo, double limite, BigDecimal valorM3) {  // Constructor: Constructor completo; valida mediante los setters
        setCodigo(codigo);              // Código obligatorio.
        setLimiteBimestralM3(limite);   // Límite mayor que cero.
        setValorM3Excedente(valorM3);   // Valor mayor o igual a cero.
    }

    /** @return el código. */
    public String getCodigo() {  // Getter: devuelve codigo
        return codigo; // Lectura simple.
    }

    /** Asigna el código (obligatorio). */
    public void setCodigo(String codigo) {  // Método setCodigo: Asigna el código (obligatorio)
        if (codigo == null || codigo.trim().isEmpty()) { // Regla: código obligatorio.
            throw new IllegalArgumentException("El código del desincentivo es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.codigo = codigo.trim(); // Normaliza espacios.
    }

    /** @return el límite bimestral en m3. */
    public double getLimiteBimestralM3() {  // Getter: devuelve limite bimestral m3
        return limiteBimestralM3; // Lectura simple.
    }

    /** Asigna el límite bimestral (debe ser mayor que cero). */
    public void setLimiteBimestralM3(double limite) {  // Método setLimiteBimestralM3: Asigna el límite bimestral (debe ser mayor que cero)
        if (Double.isNaN(limite) || limite <= 0) { // Un límite de cero o negativo no tendría sentido.
            throw new IllegalArgumentException("El límite bimestral debe ser mayor que cero.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.limiteBimestralM3 = limite; // Asignación tras validar.
    }

    /** @return el valor por m3 excedente. */
    public BigDecimal getValorM3Excedente() {  // Getter: devuelve valor m3 excedente
        return valorM3Excedente; // Lectura simple.
    }

    /** Asigna el valor por m3 excedente (no negativo). */
    public void setValorM3Excedente(BigDecimal valor) {  // Método setValorM3Excedente: Asigna el valor por m3 excedente (no negativo)
        if (valor == null || valor.signum() < 0) { // Regla: no hay precios negativos.
            throw new IllegalArgumentException("El valor por m3 excedente es obligatorio y no puede ser negativo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.valorM3Excedente = valor; // Asignación tras validar.
    }

    /** @return true si el usuario es RESIDENCIAL y su consumo total supera el límite. */
    public boolean aplicaDesincentivo(double consumoTotal, Usuario usuario) {  // Método aplicaDesincentivo (devuelve boolean)
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.RESIDENCIAL) { // Se excluye a los no residenciales.
            return false;  // Devuelve el valor de false
        }
        return consumoTotal > limiteBimestralM3; // Solo si supera estrictamente el límite.
    }

    /** @return los m3 que exceden el límite (0 si no lo supera). */
    public double calcularExcedente(double consumoTotal) {  // Método calcularExcedente (devuelve double)
        return Math.max(0.0, consumoTotal - limiteBimestralM3); // max evita excedentes negativos.
    }

    /** @return excedente x valor por m3, con 2 decimales. Es cero si no hay excedente. */
    public BigDecimal calcularDesincentivo(double consumoTotal) {  // Método calcularDesincentivo (devuelve BigDecimal)
        double excedente = calcularExcedente(consumoTotal); // Primero se mide cuánto se pasó.
        if (excedente <= 0) { // Sin excedente no hay cobro.
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);  // Devuelve: BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        }
        return valorM3Excedente.multiply(BigDecimal.valueOf(excedente)).setScale(2, RoundingMode.HALF_UP);  // Devuelve: valorM3Excedente.multiply(BigDecimal.valueOf(excedente)).setScale(2, Rou...
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "Desincentivo [codigo=" + codigo + ", limiteBimestralM3=" + limiteBimestralM3  // Devuelve: "Desincentivo [codigo=" + codigo + ", limiteBimestralM3=" + limiteBimest...
                + ", valorM3Excedente=" + valorM3Excedente + "]"; // Resumen.
    }
}
