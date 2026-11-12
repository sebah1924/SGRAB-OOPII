package modelo;
                                              // Paquete único

/**
 * Desincentivo por consumo excesivo (solo residenciales).
 * Límite: 44 m³ por bimestre (22 m³/mes).
 * Valor: $3.380,84 por m³ excedente (Bogotá, 2024).
 */
public class Desincentivo {

    // ---------- ATRIBUTOS ----------
    private String codigo;                                             // Único
    private double limiteBimestral = 44.0;                             // Regla CRA
    private double valorMetroCubico = 3380.84;                         // Valor m³ excedente

    // ---------- CONSTRUCTORES ----------
    public Desincentivo() { }

    public Desincentivo(String codigo) { setCodigo(codigo); }

    public Desincentivo(String codigo, double limite, double valor) {
        setCodigo(codigo);
        setLimiteBimestral(limite);
        setValorMetroCubico(valor);
    }

    // ---------- GETTERS / SETTERS ----------
    public String getCodigo() { return codigo; }
    public void setCodigo(String c) {
        if (c == null || c.trim().isEmpty())
            throw new IllegalArgumentException("Código del desincentivo obligatorio.");
        this.codigo = c.trim();
    }

    public double getLimiteBimestral() { return limiteBimestral; }
    public void setLimiteBimestral(double l) {
        if (l <= 0) throw new IllegalArgumentException("El límite debe ser > 0.");
        this.limiteBimestral = l;
    }

    public double getValorMetroCubico() { return valorMetroCubico; }
    public void setValorMetroCubico(double v) {
        if (v < 0) throw new IllegalArgumentException("El valor por m³ debe ser >= 0.");
        this.valorMetroCubico = v;
    }

    // ---------- MÉTODOS ----------
    /** Calcula el desincentivo sólo si el consumo total excede el límite. */
    public double calcular(double consumoTotal) {
        if (consumoTotal <= limiteBimestral) return 0.0;               // Sin excedente
        double excedente = consumoTotal - limiteBimestral;             // Diferencia
        return excedente * valorMetroCubico;                           // Regla CRA
    }

    @Override
    public String toString() {
        return "Desincentivo[" + codigo + ", límite=" + limiteBimestral +
               ", $/m³=" + valorMetroCubico + "]";
    }
}