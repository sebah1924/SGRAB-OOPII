package dao;


import servicios.Conexion;
import modelo.AcuerdoPago;
import modelo.CuotaAcuerdo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO para acuerdos de pago y sus cuotas. */
public class AcuerdoPagoDAO {

    public void guardar(AcuerdoPago a) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO acuerdo_pago (codigo,factura_numero,fecha_acuerdo," +
                     "numero_cuotas,valor_total) VALUES (?,?,?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,a.getCodigo());
            ps.setString(2, a.getFactura().getNumero());
            ps.setDate(3, Date.valueOf(a.getFechaAcuerdo()));
            ps.setInt(4, a.getNumeroCuotas());
            ps.setDouble(5, a.getValorTotal());
            ps.executeUpdate();
        }
        // Insertar cuotas
        for (CuotaAcuerdo cu : a.getCuotas()) {
            try (Connection c = Conexion.obtener();
                 PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO cuota_acuerdo (acuerdo_codigo,numero,valor,fecha_vencimiento,pagada,fecha_pago) " +
                    "VALUES (?,?,?,?,?,?)")) {
                ps.setString(1, a.getCodigo());
                ps.setInt(2, cu.getNumero());
                ps.setDouble(3, cu.getValor());
                ps.setDate(4, Date.valueOf(cu.getFechaVencimiento()));
                ps.setBoolean(5, cu.isPagada());
                if (cu.getFechaPago()!=null) ps.setDate(6, Date.valueOf(cu.getFechaPago()));
                else ps.setNull(6, Types.DATE);
                ps.executeUpdate();
            }
        }
    }

    /** Marca una cuota como pagada. */
    public void marcarCuotaPagada(String acuerdo, int numero) throws SQLException, ClassNotFoundException {
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(
                "UPDATE cuota_acuerdo SET pagada=1, fecha_pago=CURDATE() " +
                "WHERE acuerdo_codigo=? AND numero=?")) {
            ps.setString(1, acuerdo); ps.setInt(2, numero); ps.executeUpdate();
        }
    }

    public AcuerdoPago buscar(String codigo) throws SQLException, ClassNotFoundException {
        AcuerdoPago a = null;
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM acuerdo_pago WHERE codigo=?")) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    a = new AcuerdoPago();
                    a.setCodigo(rs.getString("codigo"));
                    a.setFechaAcuerdo(rs.getDate("fecha_acuerdo").toLocalDate());
                    a.setNumeroCuotas(rs.getInt("numero_cuotas"));
                    a.setValorTotal(rs.getDouble("valor_total"));
                }
            }
        }
        return a;
    }

    public List<CuotaAcuerdo> listarCuotas(String acuerdo)
            throws SQLException, ClassNotFoundException {
        List<CuotaAcuerdo> l = new ArrayList<>();
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM cuota_acuerdo WHERE acuerdo_codigo=? ORDER BY numero")) {
            ps.setString(1, acuerdo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CuotaAcuerdo cu = new CuotaAcuerdo();
                    cu.setNumero(rs.getInt("numero"));
                    cu.setValor(rs.getDouble("valor"));
                    cu.setFechaVencimiento(rs.getDate("fecha_vencimiento").toLocalDate());
                    cu.setPagada(rs.getBoolean("pagada"));
                    Date fp = rs.getDate("fecha_pago");
                    if (fp!=null) cu.setFechaPago(fp.toLocalDate());
                    l.add(cu);
                }
            }
        }
        return l;
    }
}