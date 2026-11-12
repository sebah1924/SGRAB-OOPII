package modelo;
                                              // Paquete único

/**
 * Usuario INDUSTRIAL. HEREDA porque aporta atributos propios.
 */
public class Industrial extends Usuario {

    // ---------- ATRIBUTOS PROPIOS ----------
    private String codigoIndustrial;                                   // Obligatorio
    private String procesoProductivo;                                  // Descripción del proceso
    private double cargaContaminante;                                  // DBO mg/L >= 0

    // ---------- CONSTRUCTORES ----------
    public Industrial() { super(); }

    public Industrial(String identificacion, String nombre, String apellido,
                      String codigoSuscriptor, int estrato,
                      String codigoIndustrial, String proceso, double carga) {
        super(identificacion, nombre, apellido, codigoSuscriptor, estrato);
        setCodigoIndustrial(codigoIndustrial);
        setProcesoProductivo(proceso);
        setCargaContaminante(carga);
        setTipoUsuario(TipoUsuario.INDUSTRIAL);
    }

    public Industrial(String identificacion, String nombre, String apellido,
                      java.time.LocalDate fechaNacimiento, String direccion,
                      String telefono, String correo, String codigoSuscriptor,
                      int estrato, String sector, CanalNotificacion canal,
                      String codigoIndustrial, String proceso, double carga) {
        super(identificacion, nombre, apellido, fechaNacimiento, direccion,
              telefono, correo, codigoSuscriptor, estrato, sector, canal,
              TipoUsuario.INDUSTRIAL);
        setCodigoIndustrial(codigoIndustrial);
        setProcesoProductivo(proceso);
        setCargaContaminante(carga);
    }

    // ---------- GETTERS / SETTERS ----------
    public String getCodigoIndustrial() { return codigoIndustrial; }
    public void setCodigoIndustrial(String c) {
        if (c == null || c.trim().isEmpty())                           // Obligatorio
            throw new IllegalArgumentException("Código industrial obligatorio.");
        this.codigoIndustrial = c.trim();
    }

    public String getProcesoProductivo() { return procesoProductivo; }
    public void setProcesoProductivo(String p) { this.procesoProductivo = p; }

    public double getCargaContaminante() { return cargaContaminante; }
    public void setCargaContaminante(double c) {
        if (c < 0)                                                     // >= 0
            throw new IllegalArgumentException("La carga contaminante debe ser >= 0.");
        this.cargaContaminante = c;
    }

    // ---------- SOBREESCRITURA ----------
    @Override public boolean tieneExcepcion() { return false; }
    @Override public boolean puedeSerCortado() { return true; }
    @Override public String getTipoPersona() { return "Industrial"; }

    @Override
    public String toString() {
        return super.toString() + " | Cód. Ind: " + codigoIndustrial +
               " | DBO: " + cargaContaminante;
    }
}