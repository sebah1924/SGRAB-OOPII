package modelo;
                                              // Paquete único

import java.util.Arrays;                                              // Lista de actividades válidas
import java.util.List;                                                // Interfaz List

/**
 * Usuario COMERCIAL. HEREDA porque aporta atributos propios.
 */
public class Comercial extends Usuario {

    // ---------- ATRIBUTOS PROPIOS ----------
    private String nombreEstablecimiento;                              // Obligatorio
    private String actividadEconomica;                                 // Restringida
    private double areaOperativa;                                      // > 0

    // Actividades permitidas según el enunciado
    private static final List<String> ACTIVIDADES_VALIDAS =
            Arrays.asList("restaurante", "panadería", "panaderia", "supermercado");

    // ---------- CONSTRUCTORES ----------
    public Comercial() { super(); }

    public Comercial(String identificacion, String nombre, String apellido,
                     String codigoSuscriptor, int estrato,
                     String nombreEstablecimiento, String actividad, double area) {
        super(identificacion, nombre, apellido, codigoSuscriptor, estrato);
        setNombreEstablecimiento(nombreEstablecimiento);
        setActividadEconomica(actividad);
        setAreaOperativa(area);
        setTipoUsuario(TipoUsuario.COMERCIAL);
    }

    public Comercial(String identificacion, String nombre, String apellido,
                     java.time.LocalDate fechaNacimiento, String direccion,
                     String telefono, String correo, String codigoSuscriptor,
                     int estrato, String sector, CanalNotificacion canal,
                     String nombreEstablecimiento, String actividad, double area) {
        super(identificacion, nombre, apellido, fechaNacimiento, direccion,
              telefono, correo, codigoSuscriptor, estrato, sector, canal,
              TipoUsuario.COMERCIAL);
        setNombreEstablecimiento(nombreEstablecimiento);
        setActividadEconomica(actividad);
        setAreaOperativa(area);
    }

    // ---------- GETTERS / SETTERS ----------
    public String getNombreEstablecimiento() { return nombreEstablecimiento; }
    public void setNombreEstablecimiento(String n) {
        if (n == null || n.trim().isEmpty())                           // Obligatorio
            throw new IllegalArgumentException("Nombre del establecimiento obligatorio.");
        this.nombreEstablecimiento = n.trim();
    }

    public String getActividadEconomica() { return actividadEconomica; }
    public void setActividadEconomica(String a) {
        if (a == null || !ACTIVIDADES_VALIDAS.contains(a.toLowerCase())) // Lista cerrada
            throw new IllegalArgumentException("Actividad inválida (restaurante/panadería/supermercado).");
        this.actividadEconomica = a;
    }

    public double getAreaOperativa() { return areaOperativa; }
    public void setAreaOperativa(double area) {
        if (area <= 0)                                                 // Debe ser positiva
            throw new IllegalArgumentException("El área operativa debe ser > 0.");
        this.areaOperativa = area;
    }

    // ---------- SOBREESCRITURA ----------
    @Override public boolean tieneExcepcion() { return false; }
    @Override public boolean puedeSerCortado() { return true; }
    @Override public String getTipoPersona() { return "Comercial"; }

    @Override
    public String toString() {
        return super.toString() + " | " + nombreEstablecimiento + " (" + actividadEconomica + ")";
    }
}