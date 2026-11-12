package dao;


import servicios.Conexion;
import modelo.Reporte;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO para reportes financieros. */
public class ReporteDAO {

    public void guardar(Reporte r) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO reporte (codigo,fecha_generacion,autor," +
                     "ingresos_facturacion,cartera_pendiente,subsidios_otorgados,multas_recaudadas) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,r.getCodigo());
            ps.setDate(2, Date.valueOf(r.getFechaGeneracion()));
            ps.setString(3, r.getAutor());
            ps.setDouble(4, r.getIngresosFacturacion());
            ps.setDouble(5, r.getCarteraPendiente());
            ps.setDouble(6, r.getSubsidiosOtorgados());
            ps.setDouble(7, r.getMultasRecaudadas());
            ps.executeUpdate();
        }
    }

    public Reporte buscar(String codigo) throws SQLException, ClassNotFoundException {
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM reporte WHERE codigo=?")) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapear(rs); }
        }
        return null;
    }

    public List<Reporte> listar() throws SQLException, ClassNotFoundException {
        List<Reporte> l = new ArrayList<>();
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM reporte ORDER BY fecha_generacion DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) l.add(mapear(rs));
        }
        return l;
    }

    private Reporte mapear(ResultSet rs) throws SQLException {
        Reporte r = new Reporte();
        r.setCodigo(rs.getString("codigo"));
        r.setFechaGeneracion(rs.getDate("fecha_generacion").toLocalDate());
        r.setAutor(rs.getString("autor"));
        r.setIngresosFacturacion(rs.getDouble("ingresos_facturacion"));
        r.setCarteraPendiente(rs.getDouble("cartera_pendiente"));
        r.setSubsidiosOtorgados(rs.getDouble("subsidios_otorgados"));
        r.setMultasRecaudadas(rs.getDouble("multas_recaudadas"));
        return r;
    }
}