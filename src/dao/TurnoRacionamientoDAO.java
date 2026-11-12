package dao;


import servicios.Conexion;
import modelo.Sector;
import modelo.TurnoRacionamiento;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** DAO para turnos de racionamiento. */
public class TurnoRacionamientoDAO {

    private final SectorDAO sectorDAO = new SectorDAO();

    public void guardar(TurnoRacionamiento t) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO turno_racionamiento (codigo,fecha,hora_inicio,hora_fin," +
                     "sector_id,activo,motivo) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,t.getCodigo()); ps.setDate(2, Date.valueOf(t.getFecha()));
            ps.setTime(3, Time.valueOf(t.getHoraInicio())); ps.setTime(4, Time.valueOf(t.getHoraFin()));
            ps.setInt(5,t.getSector().getId()); ps.setBoolean(6,t.isActivo());
            ps.setString(7, t.getMotivo()); ps.executeUpdate();
        }
    }

    public List<TurnoRacionamiento> listarPorSector(int sectorId)
            throws SQLException, ClassNotFoundException {
        List<TurnoRacionamiento> l = new ArrayList<>();
        String sql = "SELECT t.*,s.codigo AS s_cod,s.nombre AS s_nom,s.localidad AS s_loc " +
                     "FROM turno_racionamiento t INNER JOIN sector s ON s.id=t.sector_id " +
                     "WHERE t.sector_id=? AND t.activo=1 ORDER BY t.fecha";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sectorId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) l.add(mapear(rs)); }
        }
        return l;
    }

    /** Verifica si un sector está en turno para una fecha/hora. */
    public boolean hayTurno(int sectorId, java.time.LocalDate fecha, LocalTime hora)
            throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM turno_racionamiento WHERE sector_id=? AND fecha=? " +
                     "AND activo=1 AND ? BETWEEN hora_inicio AND hora_fin";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sectorId); ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(hora));
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        }
        return false;
    }

    private TurnoRacionamiento mapear(ResultSet rs) throws SQLException {
        TurnoRacionamiento t = new TurnoRacionamiento();
        t.setId(rs.getInt("id")); t.setCodigo(rs.getString("codigo"));
        t.setFecha(rs.getDate("fecha").toLocalDate());
        t.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
        t.setHoraFin(rs.getTime("hora_fin").toLocalTime());
        Sector s = new Sector();
        s.setId(rs.getInt("sector_id")); s.setCodigo(rs.getString("s_cod"));
        s.setNombre(rs.getString("s_nom")); s.setLocalidad(rs.getString("s_loc"));
        t.setSector(s); t.setActivo(rs.getBoolean("activo")); t.setMotivo(rs.getString("motivo"));
        return t;
    }
}