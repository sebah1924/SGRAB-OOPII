package dao;
                                                          // Paquete DAO

import servicios.Conexion;                                             // Singleton de conexión
import modelo.Persona;                                                // Entidad Persona

import java.sql.*;                                                             // JDBC
import java.time.LocalDate;                                                    // Fechas


/**
 * DAO para la tabla `persona`.
 * CAMBIO: `identificacion` es ahora la PK. Ya NO se retorna un `int id`.
 */
public class PersonaDAO {

    /** Inserta una persona (identificación es PK). */
    public void guardar(Persona p) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO persona (identificacion, nombre, apellido, fecha_nacimiento, " +
                     "direccion, telefono, correo) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getIdentificacion());                            // PK (String)
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getApellido());
            ps.setDate(4, p.getFechaNacimiento() != null
                    ? Date.valueOf(p.getFechaNacimiento()) : null);
            ps.setString(5, p.getDireccion());
            ps.setString(6, p.getTelefono());
            ps.setString(7, p.getCorreo());
            ps.executeUpdate();                                                // Sin getGeneratedKeys
        }
    }

    /** Busca una persona por identificación (PK). */
    public Persona buscarPorIdentificacion(String identificacion)
            throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM persona WHERE identificacion = ?";
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    /** Actualiza datos personales (identificación NO cambia). */
    public void actualizar(Persona p) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE persona SET nombre=?, apellido=?, fecha_nacimiento=?, " +
                     "direccion=?, telefono=?, correo=? WHERE identificacion=?";
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getApellido());
            ps.setDate(3, p.getFechaNacimiento() != null
                    ? Date.valueOf(p.getFechaNacimiento()) : null);
            ps.setString(4, p.getDireccion());
            ps.setString(5, p.getTelefono());
            ps.setString(6, p.getCorreo());
            ps.setString(7, p.getIdentificacion());                            // WHERE por PK
            ps.executeUpdate();
        }
    }

    /** Elimina una persona por identificación. */
    public void eliminar(String identificacion) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM persona WHERE identificacion = ?";
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, identificacion);
            ps.executeUpdate();
        }
    }

    /** Mapea un ResultSet a Persona (instancia anónima). */
    private Persona mapear(ResultSet rs) throws SQLException {
        // Ya NO se lee "id", porque la tabla no lo tiene
        final String idc = rs.getString("identificacion");
        final String nom = rs.getString("nombre");
        final String ape = rs.getString("apellido");
        final Date fn = rs.getDate("fecha_nacimiento");
        final String dir = rs.getString("direccion");
        final String tel = rs.getString("telefono");
        final String cor = rs.getString("correo");

        Persona p = new Persona(idc, nom, ape) {
            @Override public String getTipoPersona() { return "Genérica"; }
        };
        if (fn != null) p.setFechaNacimiento(fn.toLocalDate());
        p.setDireccion(dir);
        p.setTelefono(tel);
        p.setCorreo(cor);
        return p;
    }
}