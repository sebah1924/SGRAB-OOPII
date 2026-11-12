package modelo;
                                              // Paquete único

/**
 * Estrategia RESIDENCIAL.
 * Aporta atributos propios que justifican su existencia (no es solo herencia vacía):
 *  - consumoBasicoMensualM3: tope mensual del subsidio
 *  - topeDescuento: porcentaje máximo del costo del suministro que puede cubrir el subsidio
 */
public class EstrategiaResidencial implements EstrategiaTarifa {

    // ---------- ATRIBUTOS PROPIOS ----------
    private double consumoBasicoMensualM3 = 20.0;                      // 20 m³/mes
    private double topeDescuento = 0.70;                                // 70% máx

    // ---------- CONSTRUCTORES ----------
    public EstrategiaResidencial() { }

    public EstrategiaResidencial(double consumoBasicoMensualM3, double topeDescuento) {
        setConsumoBasicoMensualM3(consumoBasicoMensualM3);
        setTopeDescuento(topeDescuento);
    }

    // ---------- GETTERS / SETTERS ----------
    public double getConsumoBasicoMensualM3() { return consumoBasicoMensualM3; }
    public void setConsumoBasicoMensualM3(double v) {
        if (v <= 0) throw new IllegalArgumentException("Consumo básico > 0.");
        this.consumoBasicoMensualM3 = v;
    }

    public double getTopeDescuento() { return topeDescuento; }
    public void setTopeDescuento(double t) {
        if (t < 0 || t > 0.70)
            throw new IllegalArgumentException("Tope de descuento <= 0.70.");
        this.topeDescuento = t;
    }

    // ---------- CÁLCULO ----------
    @Override
    public double calcularValor(Usuario u, Consumo c, Tarifa t) {
        if (!(u instanceof Residencial))                                // Valida tipo
            throw new IllegalArgumentException("Estrategia solo para residencial.");
        Residencial r = (Residencial) u;
        double base = t.getCargoFijo() + t.getCargoVariable() * c.getVolumen();
        if (r.isRecibeSubsidio() && r.getPorcentajeSubsidio() > 0) {
            double baseVariable = t.getCargoVariable() * c.getVolumen();
            double descuento = baseVariable * (r.getPorcentajeSubsidio() / 100.0);
            double topeValor = base * topeDescuento;                    // Tope monetario
            descuento = Math.min(descuento, topeValor);                 // Aplica tope
            base -= descuento;
        } else if (r.isPagaContribucion()) {
            base += base * (r.getPorcentajeContribucion() / 100.0);      // +20%
        }
        return base;
    }

    @Override
    public String getNombre() { return "Tarifa Residencial"; }
}