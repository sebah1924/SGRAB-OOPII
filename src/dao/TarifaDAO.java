package dao;


import servicios.Conexion;
import modelo.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** DAO para tarifas. */
public class TarifaDAO {

    public void guardar(Tarifa t) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO tarifa (codigo,cargo_fijo,cargo_variable,subsidio," +
                     "fecha_inicio,fecha_fin,tipo_usuario) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,t.getCodigo()); ps.setDouble(2,t.getCargoFijo());
            ps.setDouble(3,t.getCargoVariable()); ps.setDouble(4,t.getSubsidio());
            ps.setDate(5, Date.valueOf(t.getVigencia().getFechaInicio()));
            ps.setDate(6, Date.valueOf(t.getVigencia().getFechaFin()));
            ps.setString(7,t.getTipoUsuario().name());
            ps.executeUpdate();
        }
    }

    /** Verifica si ya existe tarifa vigente solapada del mismo tipo. */
    public boolean existeSolape(Tarifa t) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM tarifa WHERE tipo_usuario=? AND id<>? " +
                     "AND fecha_inicio <= ? AND fecha_fin >= ?";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getTipoUsuario().name());
            ps.setInt(2, t.getId());
            ps.setDate(3, Date.valueOf(t.getVigencia().getFechaFin()));
            ps.setDate(4, Date.valueOf(t.getVigencia().getFechaInicio()));
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        }
        return false;
    }

    /** Busca la tarifa vigente para un tipo y fecha. */
    public Tarifa buscarVigente(TipoUsuario tipo, LocalDate fecha) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM tarifa WHERE tipo_usuario=? AND ? BETWEEN fecha_inicio AND fecha_fin LIMIT 1";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tipo.name()); ps.setDate(2, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapear(rs); }
        }
        return null;
    }

    public List<Tarifa> listar() throws SQLException, ClassNotFoundException {
        List<Tarifa> lista = new ArrayList<>();
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM tarifa");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Tarifa mapear(ResultSet rs) throws SQLException {
        Tarifa t = new Tarifa();
        t.setId(rs.getInt("id"));
        t.setCodigo(rs.getString("codigo"));
        t.setCargoFijo(rs.getDouble("cargo_fijo"));
        t.setCargoVariable(rs.getDouble("cargo_variable"));
        t.setSubsidio(rs.getDouble("subsidio"));
        t.setVigencia(new Periodo(rs.getDate("fecha_inicio").toLocalDate(),
                                  rs.getDate("fecha_fin").toLocalDate()));
        t.setTipoUsuario(TipoUsuario.valueOf(rs.getString("tipo_usuario")));
        return t;
    }
}