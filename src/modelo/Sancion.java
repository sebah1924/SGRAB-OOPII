package modelo;
                                              // Paquete único

import java.time.LocalDate;                                            // Fecha de imposición

/** Sanción por derroche de agua (Decreto 137 de 2024). */
public class Sancion {

    // ---------- ATRIBUTOS ----------
    private String codigo;                                             // Único
    private TipoSancion tipo;                                          // Tipo de sanción
    private double valorMulta;                                         // Solo si ECONOMICA
    private String descripcion;                                        // Obligatoria
    private EstadoSancion estado;                                      // Ciclo de vida
    private LocalDate fechaImposicion;                                 // Fecha

    // Constantes legales
    private static final double SALARIO_MINIMO_DIARIO = 43333.0;       // SMDLV 2024
    private static final int MAX_SMDLV = 16;                           // Tope legal

    // ---------- CONSTRUCTORES ----------
    public Sancion() { }

    public Sancion(String codigo, TipoSancion tipo, String descripcion,
                   int salariosMinimos) {
        setCodigo(codigo);
        setTipo(tipo);
        setDescripcion(descripcion);
        if (tipo == TipoSancion.ECONOMICA) {
            this.valorMulta = calcularMulta(salariosMinimos);          // Calcula multa
        } else {
            this.valorMulta = 0;                                       // No aplica
        }
        this.estado = EstadoSancion.IMPUESTA;                          // Estado inicial
        this.fechaImposicion = LocalDate.now();
    }

    // ---------- GETTERS / SETTERS ----------
    public String getCodigo() { return codigo; }
    public void setCodigo(String c) {
        if (c == null || c.trim().isEmpty())
            throw new IllegalArgumentException("Código de sanción obligatorio.");
        this.codigo = c.trim();
    }

    public TipoSancion getTipo() { return tipo; }
    public void setTipo(TipoSancion tipo) { this.tipo = tipo; }

    public double getValorMulta() { return valorMulta; }
    public void setValorMulta(double v) {
        if (tipo == TipoSancion.ECONOMICA) {
            if (v <= 0) throw new IllegalArgumentException("Multa > 0.");
            if (v > MAX_SMDLV * SALARIO_MINIMO_DIARIO)
                throw new IllegalArgumentException("La multa no puede superar 16 SMDLV.");
        }
        this.valorMulta = v;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String d) {
        if (d == null || d.trim().isEmpty())
            throw new IllegalArgumentException("Descripción obligatoria.");
        this.descripcion = d.trim();
    }

    public EstadoSancion getEstado() { return estado; }
    public void setEstado(EstadoSancion e) { this.estado = e; }

    public LocalDate getFechaImposicion() { return fechaImposicion; }
    public void setFechaImposicion(LocalDate f) { this.fechaImposicion = f; }

    // ---------- MÉTODOS DE NEGOCIO ----------
    /** Calcula la multa = nº salarios × SMDLV (máx 16). */
    public double calcularMulta(int numeroSalarios) {
        if (numeroSalarios < 1 || numeroSalarios > MAX_SMDLV)
            throw new IllegalArgumentException("Los salarios deben estar entre 1 y 16.");
        return numeroSalarios * SALARIO_MINIMO_DIARIO;
    }

    /** Aplica la sanción a un usuario respetando excepciones constitucionales. */
    public void aplicarA(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("Usuario nulo.");
        if (tipo == TipoSancion.SUSPENSION && usuario.tieneExcepcion()) {
            // Exentos: no se les puede suspender aunque hayan derrochado
            throw new IllegalStateException(
                    "Usuario exento: no se le puede aplicar suspensión, solo sanción económica.");
        }
    }

    // ---------- CICLO DE VIDA ----------
    public void apelar() {
        if (estado != EstadoSancion.IMPUESTA)
            throw new IllegalStateException("Solo se apela desde IMPUESTA.");
        estado = EstadoSancion.APELADA;
    }

    public void confirmar() {
        if (estado != EstadoSancion.APELADA)
            throw new IllegalStateException("Solo se confirma desde APELADA.");
        estado = EstadoSancion.CONFIRMADA;
    }

    public void ejecutar() {
        if (estado != EstadoSancion.CONFIRMADA && estado != EstadoSancion.IMPUESTA)
            throw new IllegalStateException("No ejecutable desde " + estado);
        estado = EstadoSancion.EJECUTADA;
    }

    public void revocar() {
        estado = EstadoSancion.REVOCADA;
    }

    @Override
    public String toString() {
        return "Sancion[" + codigo + ", " + tipo + ", $" + valorMulta + ", " + estado + "]";
    }
}