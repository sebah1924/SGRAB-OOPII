package modelo;
                                              // Paquete único

/** Excepción legal que protege a ciertos usuarios de los cortes. */
public class Excepcion {

    // ---------- ATRIBUTOS ----------
    private String codigo;                                             // Único
    private String descripcion;                                        // Descripción
    private String categoriaExenta;                                    // Ej. "Hospital", "Centro penitenciario"
    private String justificacionLegal;                                 // Ej. "Protección del derecho a la salud"
    private boolean proteccionConstitucional;                          // ¿Es sujeto de especial protección?

    // ---------- CONSTRUCTORES ----------
    public Excepcion() { }

    public Excepcion(String codigo, String descripcion, String categoria,
                     String justificacion, boolean proteccionConstitucional) {
        setCodigo(codigo);
        setDescripcion(descripcion);
        setCategoriaExenta(categoria);
        setJustificacionLegal(justificacion);
        setProteccionConstitucional(proteccionConstitucional);
    }

    // ---------- GETTERS / SETTERS ----------
    public String getCodigo() { return codigo; }
    public void setCodigo(String c) {
        if (c == null || c.trim().isEmpty())
            throw new IllegalArgumentException("Código de excepción obligatorio.");
        this.codigo = c.trim();
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String d) { this.descripcion = d; }

    public String getCategoriaExenta() { return categoriaExenta; }
    public void setCategoriaExenta(String c) { this.categoriaExenta = c; }

    public String getJustificacionLegal() { return justificacionLegal; }
    public void setJustificacionLegal(String j) {
        if (j == null || j.trim().isEmpty())
            throw new IllegalArgumentException("Justificación legal obligatoria.");
        this.justificacionLegal = j.trim();
    }

    public boolean isProteccionConstitucional() { return proteccionConstitucional; }
    public void setProteccionConstitucional(boolean p) { this.proteccionConstitucional = p; }

    // ---------- MÉTODOS ----------
    /** Verifica si el usuario pertenece a esta categoría exenta. */
    public boolean aplicaA(Usuario usuario) {
        if (usuario == null) return false;
        return usuario.tieneExcepcion();                                // Delegado a la subclase
    }

    @Override
    public String toString() {
        return "Excepcion[" + codigo + ", " + categoriaExenta + "]";
    }
}