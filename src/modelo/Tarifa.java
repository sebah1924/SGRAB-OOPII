package modelo;
                                              // Paquete único

import java.time.LocalDate;                                            // Verificación de vigencia

/**
 * Tarifa del servicio de acueducto (Resolución CRA 688 de 2014).
 * Regla: NO puede haber dos tarifas vigentes del mismo tipo al mismo tiempo.
 */
public class Tarifa {

    // ---------- ATRIBUTOS ----------
    private int id;                                                    // ID interno
    private String codigo;                                             // Único
    private double cargoFijo;                                          // $/suscriptor/mes
    private double cargoVariable;                                      // $/m³
    private double subsidio;                                           // % 0-70
    private Periodo vigencia;                                          // Rango de fechas
    private TipoUsuario tipoUsuario;                                   // Tipo al que aplica

    // ---------- CONSTRUCTORES ----------
    public Tarifa() { }

    public Tarifa(String codigo, double cargoFijo, double cargoVariable,
                  double subsidio, Periodo vigencia, TipoUsuario tipo) {
        setCodigo(codigo);
        setCargoFijo(cargoFijo);
        setCargoVariable(cargoVariable);
        setSubsidio(subsidio);
        setVigencia(vigencia);
        setTipoUsuario(tipo);
    }

    // ---------- GETTERS / SETTERS ----------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String c) {
        if (c == null || c.trim().isEmpty())
            throw new IllegalArgumentException("Código de tarifa obligatorio.");
        this.codigo = c.trim();
    }

    public double getCargoFijo() { return cargoFijo; }
    public void setCargoFijo(double v) {
        if (v < 0) throw new IllegalArgumentException("Cargo fijo >= 0.");
        this.cargoFijo = v;
    }

    public double getCargoVariable() { return cargoVariable; }
    public void setCargoVariable(double v) {
        if (v < 0) throw new IllegalArgumentException("Cargo variable >= 0.");
        this.cargoVariable = v;
    }

    public double getSubsidio() { return subsidio; }
    public void setSubsidio(double s) {
        if (s < 0 || s > 70) throw new IllegalArgumentException("Subsidio entre 0 y 70%.");
        this.subsidio = s;
    }

    public Periodo getVigencia() { return vigencia; }
    public void setVigencia(Periodo v) {
        if (v == null) throw new IllegalArgumentException("Vigencia obligatoria.");
        this.vigencia = v;
    }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario t) { this.tipoUsuario = t; }

    // ---------- MÉTODOS ----------
    /** Indica si la tarifa está vigente hoy. */
    public boolean estaVigente() {
        return vigencia != null && vigencia.contiene(LocalDate.now());
    }

    /** Indica si la tarifa estaba vigente en una fecha dada. */
    public boolean vigenteEn(LocalDate fecha) {
        return vigencia != null && vigencia.contiene(fecha);
    }

    /** Verifica si dos tarifas del mismo tipo se solapan (regla del negocio). */
    public boolean seSolapaCon(Tarifa otra) {
        return otra != null && this.tipoUsuario == otra.tipoUsuario &&
               this.vigencia.seSolapaCon(otra.vigencia);
    }

    /** Verifica si esta tarifa puede coexistir con otra (NO solaparse). */
    public boolean puedeCoexistirCon(Tarifa otra) { return !seSolapaCon(otra); }

    @Override
    public String toString() {
        return "Tarifa[" + codigo + ", fijo=" + cargoFijo + ", variable=" + cargoVariable + "]";
    }
}