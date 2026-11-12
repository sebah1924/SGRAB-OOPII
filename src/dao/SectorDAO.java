package dao;


import servicios.Conexion;
import modelo.Sector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO para sectores. */
public class SectorDAO {

    public void guardar(Sector s) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO sector (codigo,nombre,localidad,en_racionamiento) VALUES (?,?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,s.getCodigo()); ps.setString(2,s.getNombre());
            ps.setString(3,s.getLocalidad()); ps.setBoolean(4,s.isEnRacionamiento());
            ps.executeUpdate();
        }
    }

    public Sector buscar(String codigo) throws SQLException, ClassNotFoundException {
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM sector WHERE codigo=?")) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapear(rs); }
        }
        return null;
    }

    public List<Sector> listar() throws SQLException, ClassNotFoundException {
        List<Sector> l = new ArrayList<>();
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM sector");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) l.add(mapear(rs));
        }
        return l;
    }

    private Sector mapear(ResultSet rs) throws SQLException {
        Sector s = new Sector();
        s.setId(rs.getInt("id"));
        s.setCodigo(rs.getString("codigo"));
        s.setNombre(rs.getString("nombre"));
        s.setLocalidad(rs.getString("localidad"));
        s.setEnRacionamiento(rs.getBoolean("en_racionamiento"));
        return s;
    }
}