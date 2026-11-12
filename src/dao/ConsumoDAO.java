package dao;

import servicios.Conexion;
import modelo.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConsumoDAO {

    // INSERTAR (Guarda el consumo y devuelve el ID autogenerado)
    public int guardar(Consumo c, int usuarioId) throws SQLException, ClassNotFoundException {
        // Ya NO se incluye el campo 'codigo' en el INSERT
        String sql = "INSERT INTO consumo (volumen, fecha_lectura, tarifa_id, tipo_lectura, usuario_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setDouble(1, c.getVolumen());
            ps.setDate(2, Date.valueOf(c.getFechaLectura()));
            
            if (c.getTarifaAplicada() != null && c.getTarifaAplicada().getId() > 0) {
                ps.setInt(3, c.getTarifaAplicada().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            
            ps.setString(4, c.getTipoLectura() != null ? c.getTipoLectura().name() : "REAL");
            ps.setInt(5, usuarioId);
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1); // Retorna el ID (consecutivo)
            }
        }
        return -1;
    }

    // LISTAR HISTORIAL (Hace JOIN con tarifa para traer los datos completos en una sola consulta)
    public List<Consumo> listarPorUsuario(int usuarioId) throws SQLException, ClassNotFoundException {
        List<Consumo> lista = new ArrayList<>();
        String sql = "SELECT c.*, t.codigo AS t_codigo, t.cargo_fijo, t.cargo_variable, t.subsidio, " +
                     "t.fecha_inicio, t.fecha_fin, t.tipo_usuario " +
                     "FROM consumo c LEFT JOIN tarifa t ON c.tarifa_id = t.id " +
                     "WHERE c.usuario_id = ? ORDER BY c.fecha_lectura DESC";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // LISTAR EN UN PERIODO (Para facturar solo lo del mes correspondiente)
    public List<Consumo> listarPorUsuarioEnPeriodo(int usuarioId, LocalDate desde, LocalDate hasta)
            throws SQLException, ClassNotFoundException {
        if (desde == null || hasta == null || desde.isAfter(hasta)) return new ArrayList<>();
        
        List<Consumo> lista = new ArrayList<>();
        String sql = "SELECT c.*, t.codigo AS t_codigo, t.cargo_fijo, t.cargo_variable, t.subsidio, " +
                     "t.fecha_inicio, t.fecha_fin, t.tipo_usuario " +
                     "FROM consumo c LEFT JOIN tarifa t ON c.tarifa_id = t.id " +
                     "WHERE c.usuario_id = ? AND c.fecha_lectura BETWEEN ? AND ? " +
                     "ORDER BY c.fecha_lectura ASC";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setDate(2, Date.valueOf(desde));
            ps.setDate(3, Date.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // MAPEAR (Convierte la fila SQL en objeto Consumo)
    private Consumo mapear(ResultSet rs) throws SQLException {
        Consumo c = new Consumo();
        c.setId(rs.getInt("id"));                  // ID consecutivo
        c.setVolumen(rs.getDouble("volumen"));
        Date f = rs.getDate("fecha_lectura");
        if (f != null) c.setFechaLectura(f.toLocalDate());
        
        String tipo = rs.getString("tipo_lectura");
        if (tipo != null) c.setTipoLectura(TipoLectura.valueOf(tipo));

        if (rs.getString("t_codigo") != null) {    // Si el JOIN trajo tarifa
            Tarifa t = new Tarifa();
            t.setId(rs.getInt("tarifa_id"));
            t.setCodigo(rs.getString("t_codigo"));
            t.setCargoFijo(rs.getDouble("cargo_fijo"));
            t.setCargoVariable(rs.getDouble("cargo_variable"));
            t.setSubsidio(rs.getDouble("subsidio"));
            t.setVigencia(new Periodo(rs.getDate("fecha_inicio").toLocalDate(), rs.getDate("fecha_fin").toLocalDate()));
            t.setTipoUsuario(TipoUsuario.valueOf(rs.getString("tipo_usuario")));
            c.setTarifaAplicada(t);
        }
        return c;
    }
}