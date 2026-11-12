package modelo;
                                              // Paquete único

import java.time.LocalDate;                                            // Para la fecha de nacimiento
import java.time.Period;                                               // Para calcular edad
import java.util.Objects;                                              // Para equals/hashCode
import java.util.regex.Pattern;                                        // Para validar formatos                                                 // Validaciones regex

/**
 * Clase ABSTRACTA que representa a toda persona del sistema.
 * CAMBIO: la identificación es ahora el identificador único (PK).
 * Se eliminó el atributo `id` porque era redundante.
 */
public abstract class Persona {

    // ---------- ATRIBUTOS ----------
    private String identificacion;                        // ← PK única (obligatoria)
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String correo;

    // Regex: teléfono de 7 a 10 dígitos exactos
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^\\d{7,10}$");
    // Regex: correo electrónico estándar
    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    // ---------- CONSTRUCTORES ----------
    public Persona() { }

    public Persona(String identificacion, String nombre, String apellido) {
        setIdentificacion(identificacion);
        setNombre(nombre);
        setApellido(apellido);
    }

    public Persona(String identificacion, String nombre, String apellido,
                   LocalDate fechaNacimiento, String direccion,
                   String telefono, String correo) {
        setIdentificacion(identificacion);
        setNombre(nombre);
        setApellido(apellido);
        setFechaNacimiento(fechaNacimiento);
        setDireccion(direccion);
        setTelefono(telefono);
        setCorreo(correo);
    }

    // ---------- GETTERS / SETTERS ----------
    // Ya NO existe getId() ni setId() en Persona

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) {
        if (identificacion == null || identificacion.trim().isEmpty())
            throw new IllegalArgumentException("La identificación es obligatoria.");
        this.identificacion = identificacion.trim();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        this.nombre = nombre.trim();
    }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty())
            throw new IllegalArgumentException("El apellido es obligatorio.");
        this.apellido = apellido.trim();
    }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento != null && fechaNacimiento.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) {
        if (telefono != null && !PATRON_TELEFONO.matcher(telefono).matches())
            throw new IllegalArgumentException("El teléfono debe tener entre 7 y 10 dígitos.");
        this.telefono = telefono;
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) {
        if (correo != null && !PATRON_CORREO.matcher(correo).matches())
            throw new IllegalArgumentException("Correo electrónico inválido.");
        this.correo = correo;
    }

    // ---------- MÉTODOS DE NEGOCIO ----------
    public String getNombreCompleto() { return nombre + " " + apellido; }

    public int calcularEdad() {
        if (fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public boolean esMayorDeEdad() { return calcularEdad() >= 18; }

    // ---------- MÉTODO ABSTRACTO ----------
    public abstract String getTipoPersona();

    // ---------- SOBREESCRITURA ----------
    @Override
    public String toString() {
        return getNombreCompleto() + " (" + identificacion + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Persona)) return false;
        Persona p = (Persona) o;
        return Objects.equals(identificacion, p.identificacion);              // Compara por identificación (PK)
    }

    @Override
    public int hashCode() { return Objects.hash(identificacion); }
}