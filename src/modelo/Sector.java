package modelo;
                                              // Paquete único

import java.util.Objects;                                              // equals/hashCode

/** Sector de la ciudad que puede estar sujeto a racionamiento. */
public class Sector {

    // ---------- ATRIBUTOS ----------
    private int id;                                                    // ID interno BD
    private String codigo;                                             // Código único
    private String nombre;                                             // Nombre del sector
    private String localidad;                                          // Localidad
    private boolean enRacionamiento;                                   // Bandera rápida

    // ---------- CONSTRUCTORES ----------
    public Sector() { }

    public Sector(String codigo, String nombre, String localidad) {
        setCodigo(codigo);
        setNombre(nombre);
        setLocalidad(localidad);
        this.enRacionamiento = false;                                  // Por defecto NO
    }

    // ---------- GETTERS / SETTERS ----------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String c) {
        if (c == null || c.trim().isEmpty())
            throw new IllegalArgumentException("Código del sector obligatorio.");
        this.codigo = c.trim();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String n) {
        if (n == null || n.trim().isEmpty())
            throw new IllegalArgumentException("Nombre del sector obligatorio.");
        this.nombre = n.trim();
    }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String l) { this.localidad = l; }

    public boolean isEnRacionamiento() { return enRacionamiento; }
    public void setEnRacionamiento(boolean r) { this.enRacionamiento = r; }

    @Override
    public String toString() { return codigo + " - " + nombre + " (" + localidad + ")"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sector)) return false;
        return Objects.equals(codigo, ((Sector) o).codigo);
    }

    @Override
    public int hashCode() { return Objects.hash(codigo); }
}