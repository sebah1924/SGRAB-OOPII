package dao;


import servicios.Conexion;
import modelo.CalendarioRacionamiento;
import modelo.Periodo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO para calendarios de racionamiento. */
public class CalendarioRacionamientoDAO {

    public void guardar(CalendarioRacionamiento c1) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO calendario_racionamiento (codigo,fecha_inicio,fecha_fin,suspendido) " +
                     "VALUES (?,?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,c1.getCodigo());
            ps.setDate(2, Date.valueOf(c1.getVigencia().getFechaInicio()));
            ps.setDate(3, Date.valueOf(c1.getVigencia().getFechaFin()));
            ps.setBoolean(4,c1.isSuspendido()); ps.executeUpdate();
        }
    }

    public void actualizarSuspension(String codigo, boolean suspendido)
            throws SQLException, ClassNotFoundException {
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(
                "UPDATE calendario_racionamiento SET suspendido=? WHERE codigo=?")) {
            ps.setBoolean(1, suspendido); ps.setString(2, codigo); ps.executeUpdate();
        }
    }

    public CalendarioRacionamiento buscar(String codigo) throws SQLException, ClassNotFoundException {
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM calendario_racionamiento WHERE codigo=?")) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CalendarioRacionamiento c1 = new CalendarioRacionamiento();
                    c1.setCodigo(rs.getString("codigo"));
                    c1.setVigencia(new Periodo(rs.getDate("fecha_inicio").toLocalDate(),
                                               rs.getDate("fecha_fin").toLocalDate()));
                    c1.setSuspendido(rs.getBoolean("suspendido"));
                    return c1;
                }
            }
        }
        return null;
    }

    public List<CalendarioRacionamiento> listar() throws SQLException, ClassNotFoundException {
        List<CalendarioRacionamiento> l = new ArrayList<>();
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM calendario_racionamiento");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CalendarioRacionamiento c1 = new CalendarioRacionamiento();
                c1.setCodigo(rs.getString("codigo"));
                c1.setVigencia(new Periodo(rs.getDate("fecha_inicio").toLocalDate(),
                                           rs.getDate("fecha_fin").toLocalDate()));
                c1.setSuspendido(rs.getBoolean("suspendido"));
                l.add(c1);
            }
        }
        return l;
    }
}