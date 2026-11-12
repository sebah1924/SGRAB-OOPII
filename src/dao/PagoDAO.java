package dao;


import servicios.Conexion;
import modelo.Pago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO para pagos. */
public class PagoDAO {

    public void guardar(Pago p, String facturaNumero) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO pago (codigo,fecha,valor,metodo_pago,referencia,factura_numero) " +
                     "VALUES (?,?,?,?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setDate(2, Date.valueOf(p.getFecha()));
            ps.setDouble(3, p.getValor());
            ps.setString(4, p.getMetodoPago().name());
            ps.setString(5, p.getReferencia());
            ps.setString(6, facturaNumero);
            ps.executeUpdate();
        }
    }

    /** Suma de pagos de una factura. */
    public double totalPagado(String facturaNumero) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COALESCE(SUM(valor),0) FROM pago WHERE factura_numero=?";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, facturaNumero);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getDouble(1); }
        }
        return 0;
    }

    public List<Pago> listarPorFactura(String facturaNumero) throws SQLException, ClassNotFoundException {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT * FROM pago WHERE factura_numero=? ORDER BY fecha DESC";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, facturaNumero);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pago p = new Pago();
                    p.setCodigo(rs.getString("codigo"));
                    p.setFecha(rs.getDate("fecha").toLocalDate());
                    p.setValor(rs.getDouble("valor"));
                    p.setMetodoPago(modelo.MetodoPago.valueOf(rs.getString("metodo_pago")));
                    p.setReferencia(rs.getString("referencia"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }
}