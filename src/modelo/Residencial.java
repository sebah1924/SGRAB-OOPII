package modelo;
                                              // Paquete único

/**
 * Usuario RESIDENCIAL (estratos 1-6).
 * HEREDA porque aporta atributos propios (habitantes, subsidio, contribución).
 */
public class Residencial extends Usuario {

    // ---------- ATRIBUTOS PROPIOS ----------
    private int numeroHabitantes;                                      // >= 1
    private boolean recibeSubsidio;                                    // Estratos 1-3
    private boolean pagaContribucion;                                  // Estratos 5-6
    private double porcentajeSubsidio;                                 // Hasta 70 %
    private double porcentajeContribucion;                             // 20 %

    // ---------- CONSTRUCTORES ----------
    public Residencial() { super(); }                                  // Vacío

    public Residencial(String identificacion, String nombre, String apellido,
                       String codigoSuscriptor, int estrato, int numeroHabitantes) {
        super(identificacion, nombre, apellido, codigoSuscriptor, estrato); // Llama al padre
        setNumeroHabitantes(numeroHabitantes);                         // Valida habitantes
        setTipoUsuario(TipoUsuario.RESIDENCIAL);                       // Fija tipo
        actualizarBeneficios();                                        // Calcula subsidio
    }

    public Residencial(String identificacion, String nombre, String apellido,
                       java.time.LocalDate fechaNacimiento, String direccion,
                       String telefono, String correo, String codigoSuscriptor,
                       int estrato, String sector, CanalNotificacion canal,
                       int numeroHabitantes) {                         // Completo
        super(identificacion, nombre, apellido, fechaNacimiento,
              direccion, telefono, correo, codigoSuscriptor,
              estrato, sector, canal, TipoUsuario.RESIDENCIAL);
        setNumeroHabitantes(numeroHabitantes);
        actualizarBeneficios();
    }

    // ---------- GETTERS / SETTERS ----------
    public int getNumeroHabitantes() { return numeroHabitantes; }
    public void setNumeroHabitantes(int numeroHabitantes) {
        if (numeroHabitantes < 1)                                      // Regla: >=1
            throw new IllegalArgumentException("El número de habitantes debe ser >= 1.");
        this.numeroHabitantes = numeroHabitantes;
    }

    public boolean isRecibeSubsidio() { return recibeSubsidio; }
    public void setRecibeSubsidio(boolean recibeSubsidio) { this.recibeSubsidio = recibeSubsidio; }

    public boolean isPagaContribucion() { return pagaContribucion; }
    public void setPagaContribucion(boolean pagaContribucion) { this.pagaContribucion = pagaContribucion; }

    public double getPorcentajeSubsidio() { return porcentajeSubsidio; }
    public void setPorcentajeSubsidio(double p) {
        if (p < 0 || p > 70)                                           // Máx 70 %
            throw new IllegalArgumentException("El subsidio no puede superar el 70%.");
        this.porcentajeSubsidio = p;
    }

    public double getPorcentajeContribucion() { return porcentajeContribucion; }
    public void setPorcentajeContribucion(double p) {
        if (p < 0 || p > 20)                                           // Máx 20 %
            throw new IllegalArgumentException("La contribución no puede superar el 20%.");
        this.porcentajeContribucion = p;
    }

    // ---------- MÉTODOS DE NEGOCIO ----------
    /** Ajusta beneficios según el estrato actual. */
    public void actualizarBeneficios() {
        int e = getEstrato();
        if (e >= 1 && e <= 3) {                                        // Subsidio
            recibeSubsidio = true;
            pagaContribucion = false;
            porcentajeContribucion = 0;
            if (e == 1) porcentajeSubsidio = 70;                       // Hasta 70 %
            else if (e == 2) porcentajeSubsidio = 40;                  // Típico estrato 2
            else porcentajeSubsidio = 15;                              // Típico estrato 3
        } else if (e == 4) {                                           // Neutro
            recibeSubsidio = false;
            pagaContribucion = false;
            porcentajeSubsidio = 0;
            porcentajeContribucion = 0;
        } else {                                                       // Contribución
            recibeSubsidio = false;
            pagaContribucion = true;
            porcentajeSubsidio = 0;
            porcentajeContribucion = 20;
        }
    }

    // ---------- SOBREESCRITURA ----------
    @Override public boolean tieneExcepcion() { return false; }        // No exento por defecto
    @Override public boolean puedeSerCortado() { return true; }        // Sí puede cortarse
    @Override public String getTipoPersona() { return "Residencial"; }

    @Override
    public String toString() {
        return super.toString() + " | Habitantes: " + numeroHabitantes +
               " | Subsidio: " + porcentajeSubsidio + "%";
    }
}