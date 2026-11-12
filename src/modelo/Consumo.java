package modelo;
                                             

import java.time.LocalDate;                                      

/**
 * Entidad que representa una lectura de consumo de agua.
 * Ahora el ID es el consecutivo principal generado por la BD.
 */
public class Consumo {
    
    // ---------- ATRIBUTOS ----------
    private int id;                                // Identificador único (consecutivo)
    private double volumen;                        // Metros cúbicos consumidos (>= 0)
    private LocalDate fechaLectura;                // Fecha de la lectura (no futura)
    private Tarifa tarifaAplicada;                 // Tarifa usada en el cálculo
    private TipoLectura tipoLectura;               // REAL o ESTIMADA

    // ---------- CONSTRUCTORES ----------
    public Consumo() { }                           // Constructor vacío para frameworks/DAO

    // Constructor principal SIN código (ahora el ID lo pone la BD)
    public Consumo(double volumen, LocalDate fecha, TipoLectura tipo) {
        setVolumen(volumen);                       // Asigna validando
        setFechaLectura(fecha);                    // Asigna validando
        setTipoLectura(tipo);                      // Asigna tipo
    }

    // ---------- GETTERS / SETTERS ----------
    public int getId() { return id; }              // Retorna el ID consecutivo
    public void setId(int id) { this.id = id; }    // Asigna el ID (usado por el DAO)

    public double getVolumen() { return volumen; }
    public void setVolumen(double v) {
        if (v < 0) throw new IllegalArgumentException("El volumen debe ser >= 0."); // Regla del negocio
        this.volumen = v;
    }

    public LocalDate getFechaLectura() { return fechaLectura; }
    public void setFechaLectura(LocalDate f) {
        if (f != null && f.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de lectura no puede ser futura."); // Regla
        this.fechaLectura = f;
    }

    public Tarifa getTarifaAplicada() { return tarifaAplicada; }
    public void setTarifaAplicada(Tarifa t) { this.tarifaAplicada = t; } // La tarifa se carga desde el DAO

    public TipoLectura getTipoLectura() { return tipoLectura; }
    public void setTipoLectura(TipoLectura t) { this.tipoLectura = t; }

    // ---------- MÉTODOS DE NEGOCIO ----------
    // Calcula cuánto cuesta el agua consumida sin tener en cuenta cargos fijos
    public double calcularCargoVariable() {
        if (tarifaAplicada == null) return 0;
        return tarifaAplicada.getCargoVariable() * volumen;
    }

    @Override
    public String toString() {
        return "Consumo #" + id + " [" + volumen + " m³, " + fechaLectura + "]";
    }
}