package modelo;
                                              // Paquete único

/** Estrategia OFICIAL. Atributo propio: exención de recargos. */
public class EstrategiaOficial implements EstrategiaTarifa {

    // ---------- ATRIBUTOS PROPIOS ----------
    private boolean exentoDeRecargos = true;                            // Oficiales no pagan recargos

    // ---------- CONSTRUCTORES ----------
    public EstrategiaOficial() { }

    public EstrategiaOficial(boolean exentoDeRecargos) {
        this.exentoDeRecargos = exentoDeRecargos;
    }

    // ---------- GETTERS / SETTERS ----------
    public boolean isExentoDeRecargos() { return exentoDeRecargos; }
    public void setExentoDeRecargos(boolean e) { this.exentoDeRecargos = e; }

    // ---------- CÁLCULO ----------
    @Override
    public double calcularValor(Usuario u, Consumo c, Tarifa t) {
        if (!(u instanceof Oficial))                                    // Valida tipo
            throw new IllegalArgumentException("Estrategia solo para oficial.");
        double base = t.getCargoFijo() + t.getCargoVariable() * c.getVolumen();
        return base;                                                    // Sin recargos extra
    }

    @Override
    public String getNombre() { return "Tarifa Oficial"; }
}