package modelo;
                                              // Paquete único

/** Estrategia INDUSTRIAL. Atributos propios: valor unitario contaminación y cargo por demanda. */
public class EstrategiaIndustrial implements EstrategiaTarifa {

    // ---------- ATRIBUTOS PROPIOS ----------
    private double valorUnitarioContaminacion = 1000.0;                 // $/unidad DBO
    private double cargoPorDemanda = 0.0;                               // $ caudal contratado

    // ---------- CONSTRUCTORES ----------
    public EstrategiaIndustrial() { }

    public EstrategiaIndustrial(double valorUnitario, double cargoDemanda) {
        setValorUnitarioContaminacion(valorUnitario);
        setCargoPorDemanda(cargoDemanda);
    }

    // ---------- GETTERS / SETTERS ----------
    public double getValorUnitarioContaminacion() { return valorUnitarioContaminacion; }
    public void setValorUnitarioContaminacion(double v) {
        if (v < 0) throw new IllegalArgumentException("Valor >= 0.");
        this.valorUnitarioContaminacion = v;
    }

    public double getCargoPorDemanda() { return cargoPorDemanda; }
    public void setCargoPorDemanda(double c) {
        if (c < 0) throw new IllegalArgumentException("Cargo demanda >= 0.");
        this.cargoPorDemanda = c;
    }

    // ---------- CÁLCULO ----------
    @Override
    public double calcularValor(Usuario u, Consumo c, Tarifa t) {
        if (!(u instanceof Industrial))                                 // Valida tipo
            throw new IllegalArgumentException("Estrategia solo para industrial.");
        Industrial ind = (Industrial) u;
        double base = t.getCargoFijo() + t.getCargoVariable() * c.getVolumen();
        base += ind.getCargaContaminante() * valorUnitarioContaminacion;
        base += cargoPorDemanda;
        return base;
    }

    @Override
    public String getNombre() { return "Tarifa Industrial"; }
}