package dao;


import servicios.Conexion;
import modelo.Desincentivo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO para desincentivos. */
public class DesincentivoDAO {

    public void guardar(Desincentivo d) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO desincentivo (codigo,limite_bimestral,valor_metro_cubico) VALUES (?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,d.getCodigo()); ps.setDouble(2,d.getLimiteBimestral());
            ps.setDouble(3,d.getValorMetroCubico()); ps.executeUpdate();
        }
    }

    public Desincentivo buscar(String codigo) throws SQLException, ClassNotFoundException {
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM desincentivo WHERE codigo=?")) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Desincentivo d = new Desincentivo();
                    d.setCodigo(rs.getString("codigo"));
                    d.setLimiteBimestral(rs.getDouble("limite_bimestral"));
                    d.setValorMetroCubico(rs.getDouble("valor_metro_cubico"));
                    return d;
                }
            }
        }
        return null;
    }

    public List<Desincentivo> listar() throws SQLException, ClassNotFoundException {
        List<Desincentivo> l = new ArrayList<>();
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM desincentivo");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Desincentivo d = new Desincentivo();
                d.setCodigo(rs.getString("codigo"));
                d.setLimiteBimestral(rs.getDouble("limite_bimestral"));
                d.setValorMetroCubico(rs.getDouble("valor_metro_cubico"));
                l.add(d);
            }
        }
        return l;
    }
}