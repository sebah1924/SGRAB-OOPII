package dao;


import servicios.Conexion;
import modelo.Excepcion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO para excepciones legales. */
public class ExcepcionDAO {

    public void guardar(Excepcion e) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO excepcion (codigo,descripcion,categoria_exenta," +
                     "justificacion_legal,proteccion_constitucional) VALUES (?,?,?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,e.getCodigo()); ps.setString(2,e.getDescripcion());
            ps.setString(3,e.getCategoriaExenta()); ps.setString(4,e.getJustificacionLegal());
            ps.setBoolean(5,e.isProteccionConstitucional()); ps.executeUpdate();
        }
    }

    public Excepcion buscar(String codigo) throws SQLException, ClassNotFoundException {
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM excepcion WHERE codigo=?")) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapear(rs); }
        }
        return null;
    }

    public List<Excepcion> listar() throws SQLException, ClassNotFoundException {
        List<Excepcion> l = new ArrayList<>();
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM excepcion");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) l.add(mapear(rs));
        }
        return l;
    }

    private Excepcion mapear(ResultSet rs) throws SQLException {
        Excepcion e = new Excepcion();
        e.setCodigo(rs.getString("codigo"));
        e.setDescripcion(rs.getString("descripcion"));
        e.setCategoriaExenta(rs.getString("categoria_exenta"));
        e.setJustificacionLegal(rs.getString("justificacion_legal"));
        e.setProteccionConstitucional(rs.getBoolean("proteccion_constitucional"));
        return e;
    }
}