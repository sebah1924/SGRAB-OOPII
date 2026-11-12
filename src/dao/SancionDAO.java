package dao;


import servicios.Conexion;
import modelo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO para sanciones (ciclo de vida completo). */
public class SancionDAO {

    public void guardar(Sancion s, int usuarioId) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO sancion (codigo,tipo,valor_multa,descripcion,estado," +
                     "fecha_imposicion,usuario_id) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,s.getCodigo()); ps.setString(2,s.getTipo().name());
            ps.setDouble(3,s.getValorMulta()); ps.setString(4,s.getDescripcion());
            ps.setString(5,s.getEstado().name()); ps.setDate(6, Date.valueOf(s.getFechaImposicion()));
            ps.setInt(7,usuarioId); ps.executeUpdate();
        }
    }

    public void actualizarEstado(String codigo, EstadoSancion nuevo) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE sancion SET estado=? WHERE codigo=?";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nuevo.name()); ps.setString(2, codigo); ps.executeUpdate();
        }
    }

    public Sancion buscar(String codigo) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM sancion WHERE codigo=?";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapear(rs); }
        }
        return null;
    }

    public List<Sancion> listarPorUsuario(int usuarioId) throws SQLException, ClassNotFoundException {
        List<Sancion> l = new ArrayList<>();
        String sql = "SELECT * FROM sancion WHERE usuario_id=?";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) l.add(mapear(rs)); }
        }
        return l;
    }

    private Sancion mapear(ResultSet rs) throws SQLException {
        Sancion s = new Sancion();
        s.setCodigo(rs.getString("codigo"));
        s.setTipo(TipoSancion.valueOf(rs.getString("tipo")));
        s.setValorMulta(rs.getDouble("valor_multa"));
        s.setDescripcion(rs.getString("descripcion"));
        s.setEstado(EstadoSancion.valueOf(rs.getString("estado")));
        s.setFechaImposicion(rs.getDate("fecha_imposicion").toLocalDate());
        return s;
    }
}