package modelo;
                                              // Paquete único

/**
 * Usuario OFICIAL (entidad pública). HEREDA porque aporta atributos propios.
 * Está exento de cortes por normativa.
 */
public class Oficial extends Usuario {

    // ---------- ATRIBUTOS PROPIOS ----------
    private String entidadPublica;                                     // Obligatorio
    private double asignacionPresupuestal;                             // > 0

    // ---------- CONSTRUCTORES ----------
    public Oficial() { super(); }

    public Oficial(String identificacion, String nombre, String apellido,
                   String codigoSuscriptor, int estrato,
                   String entidadPublica, double asignacion) {
        super(identificacion, nombre, apellido, codigoSuscriptor, estrato);
        setEntidadPublica(entidadPublica);
        setAsignacionPresupuestal(asignacion);
        setTipoUsuario(TipoUsuario.OFICIAL);
    }

    public Oficial(String identificacion, String nombre, String apellido,
                   java.time.LocalDate fechaNacimiento, String direccion,
                   String telefono, String correo, String codigoSuscriptor,
                   int estrato, String sector, CanalNotificacion canal,
                   String entidadPublica, double asignacion) {
        super(identificacion, nombre, apellido, fechaNacimiento, direccion,
              telefono, correo, codigoSuscriptor, estrato, sector, canal,
              TipoUsuario.OFICIAL);
        setEntidadPublica(entidadPublica);
        setAsignacionPresupuestal(asignacion);
    }

    // ---------- GETTERS / SETTERS ----------
    public String getEntidadPublica() { return entidadPublica; }
    public void setEntidadPublica(String e) {
        if (e == null || e.trim().isEmpty())                           // Obligatorio
            throw new IllegalArgumentException("La entidad pública es obligatoria.");
        this.entidadPublica = e.trim();
    }

    public double getAsignacionPresupuestal() { return asignacionPresupuestal; }
    public void setAsignacionPresupuestal(double a) {
        if (a <= 0)                                                    // Debe ser > 0
            throw new IllegalArgumentException("La asignación presupuestal debe ser > 0.");
        this.asignacionPresupuestal = a;
    }

    // ---------- SOBREESCRITURA ----------
    @Override public boolean tieneExcepcion() { return true; }         // SIEMPRE exento
    @Override public boolean puedeSerCortado() { return false; }       // NUNCA se corta
    @Override public String getTipoPersona() { return "Oficial"; }

    @Override
    public String toString() {
        return super.toString() + " | " + entidadPublica;
    }
}