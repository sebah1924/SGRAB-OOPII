package dao;


import servicios.Conexion;
import modelo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO para notificaciones (historial completo). */
public class NotificacionDAO {

    public void guardar(Notificacion n, int destinatarioId) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO notificacion (codigo,titulo,cuerpo,destinatario_id,canal," +
                     "evento,fecha_creacion,enviada) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,n.getCodigo()); ps.setString(2,n.getTitulo());
            ps.setString(3,n.getCuerpo()); ps.setInt(4,destinatarioId);
            ps.setString(5,n.getCanal().name());
            ps.setString(6, n.getEvento()!=null ? n.getEvento().name() : "EMISION_FACTURA");
            ps.setDate(7, Date.valueOf(n.getFechaCreacion()));
            ps.setBoolean(8, n.isEnviada());
            ps.executeUpdate();
        }
    }

    public List<Notificacion> listarPorUsuario(int destinatarioId)
            throws SQLException, ClassNotFoundException {
        List<Notificacion> l = new ArrayList<>();
        String sql = "SELECT * FROM notificacion WHERE destinatario_id=? ORDER BY fecha_creacion DESC";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, destinatarioId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) l.add(mapear(rs)); }
        }
        return l;
    }

    public List<Notificacion> listarTodas() throws SQLException, ClassNotFoundException {
        List<Notificacion> l = new ArrayList<>();
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM notificacion ORDER BY fecha_creacion DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) l.add(mapear(rs));
        }
        return l;
    }

    private Notificacion mapear(ResultSet rs) throws SQLException {
        Notificacion n = new Notificacion();
        n.setCodigo(rs.getString("codigo"));
        n.setTitulo(rs.getString("titulo"));
        n.setCuerpo(rs.getString("cuerpo"));
        n.setCanal(modelo.CanalNotificacion.valueOf(rs.getString("canal")));
        n.setFechaCreacion(rs.getDate("fecha_creacion").toLocalDate());
        n.setEnviada(rs.getBoolean("enviada"));
        return n;
    }
}