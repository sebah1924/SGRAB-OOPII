package dao;
                                                          // Paquete DAO

import servicios.Conexion;                                             // Singleton
import modelo.*;                                                      // Modelo (un paquete único)

import java.sql.*;                                                             // JDBC
import java.util.ArrayList;                                                    // Lista
import java.util.List;                                                         // Interfaz

/**
 * DAO para el árbol Usuario + subclases.
 * CAMBIO: la FK a persona ahora es `persona_identificacion` (String).
 * El `id` del usuario sigue siendo INT AUTO_INCREMENT.
 */
public class UsuarioDAO {

    private final PersonaDAO personaDAO = new PersonaDAO();
    private final SectorDAO  sectorDAO  = new SectorDAO();

    public void guardar(Usuario u) throws SQLException, ClassNotFoundException {
        // 1. Insertar la persona (identificacion es PK)
        personaDAO.guardar(u);                                                 // Ahora no retorna nada

        // 2. Insertar el usuario con la FK por identificación
        String sqlU = "INSERT INTO usuario (persona_identificacion, codigo_suscriptor, " +
                      "fecha_registro, tipo_usuario, estrato, sector_id, canal_notificacion, " +
                      "estado_servicio, consumo_promedio, excepcion_codigo) " +
                      "VALUES (?,?,?,?,?,?,?,?,?,?)";

        int usuarioId;
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sqlU, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getIdentificacion());                            // ← FK persona_identificacion
            ps.setString(2, u.getCodigoSuscriptor());
            ps.setDate(3, Date.valueOf(u.getFechaRegistro()));
            ps.setString(4, u.getTipoUsuario().name());
            ps.setInt(5, u.getEstrato());

            // Sector: conversión robusta
            if (u.getSector() != null && !u.getSector().trim().isEmpty()) {
                String sectorStr = u.getSector().trim();
                int sectorId = -1;
                try { sectorId = Integer.parseInt(sectorStr); }
                catch (NumberFormatException nfe) {
                    Sector sec = sectorDAO.buscar(sectorStr);
                    if (sec != null) sectorId = sec.getId();
                }
                if (sectorId > 0) ps.setInt(6, sectorId);
                else ps.setNull(6, Types.INTEGER);
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setString(7, u.getCanalNotificacionPreferido() != null
                    ? u.getCanalNotificacionPreferido().name() : "EMAIL");
            ps.setString(8, u.getEstadoServicio().name());
            ps.setDouble(9, u.getConsumoPromedio());

            if (u.getExcepcionAplicable() != null) {
                ps.setString(10, u.getExcepcionAplicable().getCodigo());
            } else {
                ps.setNull(10, Types.VARCHAR);
            }

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) usuarioId = rs.getInt(1);
                else return;
            }
        }

        // 3. Insertar en la subclase concreta
        if (u instanceof Residencial)     guardarResidencial((Residencial) u, usuarioId);
        else if (u instanceof Comercial)  guardarComercial((Comercial) u, usuarioId);
        else if (u instanceof Industrial) guardarIndustrial((Industrial) u, usuarioId);
        else if (u instanceof Oficial)    guardarOficial((Oficial) u, usuarioId);

        // 4. Asignar el id al objeto en memoria
        u.setId(usuarioId);
    }

    // --- GUARDADO DE SUBCLASES (usuario_id es PK Y FK a la vez) ---
    private void guardarResidencial(Residencial r, int id) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO residencial (usuario_id,numero_habitantes,recibe_subsidio," +
                     "paga_contribucion,porcentaje_subsidio,porcentaje_contribucion) VALUES (?,?,?,?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, r.getNumeroHabitantes());
            ps.setBoolean(3, r.isRecibeSubsidio());
            ps.setBoolean(4, r.isPagaContribucion());
            ps.setDouble(5, r.getPorcentajeSubsidio());
            ps.setDouble(6, r.getPorcentajeContribucion());
            ps.executeUpdate();
        }
    }

    private void guardarComercial(Comercial co, int id) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO comercial (usuario_id,nombre_establecimiento,actividad_economica,area_operativa) VALUES (?,?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, co.getNombreEstablecimiento());
            ps.setString(3, co.getActividadEconomica());
            ps.setDouble(4, co.getAreaOperativa());
            ps.executeUpdate();
        }
    }

    private void guardarIndustrial(Industrial i, int id) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO industrial (usuario_id,codigo_industrial,proceso_productivo,carga_contaminante) VALUES (?,?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, i.getCodigoIndustrial());
            ps.setString(3, i.getProcesoProductivo());
            ps.setDouble(4, i.getCargaContaminante());
            ps.executeUpdate();
        }
    }

    private void guardarOficial(Oficial o, int id) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO oficial (usuario_id,entidad_publica,asignacion_presupuestal) VALUES (?,?,?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, o.getEntidadPublica());
            ps.setDouble(3, o.getAsignacionPresupuestal());
            ps.executeUpdate();
        }
    }

    // --- BÚSQUEDAS ---
    public Usuario buscarPorCodigo(String codigo) throws SQLException, ClassNotFoundException {
        String sql = "SELECT u.*, p.identificacion AS p_identificacion, p.nombre AS p_nombre, " +
                     "p.apellido AS p_apellido, p.fecha_nacimiento AS p_fecha_nac, " +
                     "p.direccion AS p_direccion, p.telefono AS p_telefono, p.correo AS p_correo " +
                     "FROM usuario u INNER JOIN persona p ON p.identificacion = u.persona_identificacion " +
                     "WHERE u.codigo_suscriptor = ?";
        Usuario u = null; int usuarioId = -1;
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = mapearBasico(rs);
                    usuarioId = rs.getInt("u.id");
                }
            }
        }
        if (u != null) {
            cargarSubclase(u, usuarioId);
            u.setEstrategia(EstrategiaFactory.crear(u.getTipoUsuario()));
        }
        return u;
    }

    public List<Usuario> listar() throws SQLException, ClassNotFoundException {
        List<Usuario> lista = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT u.*, p.identificacion AS p_identificacion, p.nombre AS p_nombre, " +
                     "p.apellido AS p_apellido, p.fecha_nacimiento AS p_fecha_nac, " +
                     "p.direccion AS p_direccion, p.telefono AS p_telefono, p.correo AS p_correo " +
                     "FROM usuario u INNER JOIN persona p ON p.identificacion = u.persona_identificacion";
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearBasico(rs));
                ids.add(rs.getInt("u.id"));
            }
        }
        for (int i = 0; i < lista.size(); i++) {
            cargarSubclase(lista.get(i), ids.get(i));
            lista.get(i).setEstrategia(EstrategiaFactory.crear(lista.get(i).getTipoUsuario()));
        }
        return lista;
    }

    public Usuario buscarPorCodigoPorId(int id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT codigo_suscriptor FROM usuario WHERE id=?";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return buscarPorCodigo(rs.getString("codigo_suscriptor"));
            }
        }
        return null;
    }

    // --- MAPEO ---
    private Usuario mapearBasico(ResultSet rs) throws SQLException {
        String tipo = rs.getString("u.tipo_usuario");
        Usuario u;
        switch (tipo) {
            case "RESIDENCIAL": u = new Residencial(); break;
            case "COMERCIAL":   u = new Comercial();   break;
            case "INDUSTRIAL":  u = new Industrial();  break;
            default:            u = new Oficial();     break;
        }

        // ← ID del usuario (INT autoincrement, del propio usuario)
        u.setId(rs.getInt("u.id"));

        // ← Identificación de la persona (String, PK)
        u.setIdentificacion(rs.getString("p_identificacion"));
        u.setNombre(rs.getString("p_nombre"));
        u.setApellido(rs.getString("p_apellido"));
        Date fn = rs.getDate("p_fecha_nac");
        if (fn != null) u.setFechaNacimiento(fn.toLocalDate());
        u.setDireccion(rs.getString("p_direccion"));
        u.setTelefono(rs.getString("p_telefono"));
        u.setCorreo(rs.getString("p_correo"));

        // Datos del suscriptor
        u.setCodigoSuscriptor(rs.getString("u.codigo_suscriptor"));
        u.setFechaRegistro(rs.getDate("u.fecha_registro").toLocalDate());
        u.setTipoUsuario(TipoUsuario.valueOf(tipo));
        u.setEstrato(rs.getInt("u.estrato"));
        u.setEstadoServicio(EstadoServicio.valueOf(rs.getString("u.estado_servicio")));
        u.setConsumoPromedio(rs.getDouble("u.consumo_promedio"));
        String canal = rs.getString("u.canal_notificacion");
        if (canal != null) u.setCanalNotificacionPreferido(CanalNotificacion.valueOf(canal));

        return u;
    }

    private void cargarSubclase(Usuario u, int usuarioId) throws SQLException {
        if (u instanceof Residencial) {
            try (Connection c = Conexion.obtener();
                 PreparedStatement ps = c.prepareStatement("SELECT * FROM residencial WHERE usuario_id=?")) {
                ps.setInt(1, usuarioId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Residencial r = (Residencial) u;
                        r.setNumeroHabitantes(rs.getInt("numero_habitantes"));
                        r.setRecibeSubsidio(rs.getBoolean("recibe_subsidio"));
                        r.setPagaContribucion(rs.getBoolean("paga_contribucion"));
                        r.setPorcentajeSubsidio(rs.getDouble("porcentaje_subsidio"));
                        r.setPorcentajeContribucion(rs.getDouble("porcentaje_contribucion"));
                    }
                }
            }
        } else if (u instanceof Comercial) {
            try (Connection c = Conexion.obtener();
                 PreparedStatement ps = c.prepareStatement("SELECT * FROM comercial WHERE usuario_id=?")) {
                ps.setInt(1, usuarioId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Comercial co = (Comercial) u;
                        co.setNombreEstablecimiento(rs.getString("nombre_establecimiento"));
                        co.setActividadEconomica(rs.getString("actividad_economica"));
                        co.setAreaOperativa(rs.getDouble("area_operativa"));
                    }
                }
            }
        } else if (u instanceof Industrial) {
            try (Connection c = Conexion.obtener();
                 PreparedStatement ps = c.prepareStatement("SELECT * FROM industrial WHERE usuario_id=?")) {
                ps.setInt(1, usuarioId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Industrial i = (Industrial) u;
                        i.setCodigoIndustrial(rs.getString("codigo_industrial"));
                        i.setProcesoProductivo(rs.getString("proceso_productivo"));
                        i.setCargaContaminante(rs.getDouble("carga_contaminante"));
                    }
                }
            }
        } else if (u instanceof Oficial) {
            try (Connection c = Conexion.obtener();
                 PreparedStatement ps = c.prepareStatement("SELECT * FROM oficial WHERE usuario_id=?")) {
                ps.setInt(1, usuarioId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Oficial o = (Oficial) u;
                        o.setEntidadPublica(rs.getString("entidad_publica"));
                        o.setAsignacionPresupuestal(rs.getDouble("asignacion_presupuestal"));
                    }
                }
            }
        }
    }

    // --- ACTUALIZACIONES ---
    public void actualizarEstado(int usuarioId, EstadoServicio nuevo) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE usuario SET estado_servicio=? WHERE id=?";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nuevo.name());
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        }
    }

    public void actualizarPromedio(int usuarioId, double promedio) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE usuario SET consumo_promedio=? WHERE id=?";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, promedio);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        }
    }

    public void eliminar(int usuarioId, String motivo) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE usuario SET fecha_baja=CURDATE(), motivo_baja=? WHERE id=?";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        }
    }
}