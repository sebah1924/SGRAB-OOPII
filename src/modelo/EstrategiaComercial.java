package modelo;
                                              // Paquete único

/** Estrategia COMERCIAL. Atributos propios: cargo por vertimiento. */
public class EstrategiaComercial implements EstrategiaTarifa {

    // ---------- ATRIBUTOS PROPIOS ----------
    private boolean aplicaCargoVertimiento = false;                     // ¿Genera aguas residuales no domésticas?
    private double tarifaVertimientoM3 = 0.0;                           // $/m³ por vertimiento

    // ---------- CONSTRUCTORES ----------
    public EstrategiaComercial() { }

    public EstrategiaComercial(boolean aplica, double tarifa) {
        this.aplicaCargoVertimiento = aplica;
        setTarifaVertimientoM3(tarifa);
    }

    // ---------- GETTERS / SETTERS ----------
    public boolean isAplicaCargoVertimiento() { return aplicaCargoVertimiento; }
    public void setAplicaCargoVertimiento(boolean a) { this.aplicaCargoVertimiento = a; }

    public double getTarifaVertimientoM3() { return tarifaVertimientoM3; }
    public void setTarifaVertimientoM3(double t) {
        if (t < 0) throw new IllegalArgumentException("Tarifa vertimiento >= 0.");
        this.tarifaVertimientoM3 = t;
    }

    // ---------- CÁLCULO ----------
    @Override
    public double calcularValor(Usuario u, Consumo c, Tarifa t) {
        if (!(u instanceof Comercial))                                  // Valida tipo
            throw new IllegalArgumentException("Estrategia solo para comercial.");
        double base = t.getCargoFijo() + t.getCargoVariable() * c.getVolumen();
        if (aplicaCargoVertimiento) base += tarifaVertimientoM3 * c.getVolumen();
        return base;
    }

    @Override
    public String getNombre() { return "Tarifa Comercial"; }
}