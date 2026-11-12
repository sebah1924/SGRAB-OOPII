package dao;


import servicios.Conexion;
import modelo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;                                                       // Interfaz List (polimorfismo)

/**
 * DAO (Data Access Object) para la tabla `factura`.
 * ------------------------------------------------------------------
 * Responsabilidades:
 *   - GUARDAR una factura + sus consumos + sus sanciones (relaciones N:M).
 *   - ACTUALIZAR estado de pago y valor total.
 *   - BUSCAR una factura por número cargando usuario, consumos y pagos.
 *   - LISTAR facturas por usuario cargando también sus relaciones.
 *   - MARCAR como VENCIDAS las facturas pendientes que ya pasaron su fecha.
 * ------------------------------------------------------------------
 * IMPORTANTE: Al usar Singleton (Conexion.obtener()), NO podemos hacer
 * consultas anidadas mientras un ResultSet está abierto (error "Operation 
 * not allowed after ResultSet closed"). Por eso:
 *   1) Primero leemos todo de una tabla, cerramos el ResultSet.
 *   2) Luego hacemos las consultas auxiliares (consumos, pagos) con la
 *      conexión liberada.
 */
public class FacturaDAO {

    // =====================================================================
    // 1) GUARDAR — INSERT principal + relaciones N:M
    // =====================================================================
    /**
     * Guarda una factura completa en la base de datos.
     * Incluye:
     *   1) INSERT en `factura`.
     *   2) INSERT en `factura_consumo` (relación N:M con consumos).
     *   3) INSERT en `factura_sancion` (relación N:M con sanciones).
     */
    public void guardar(Factura f) throws SQLException, ClassNotFoundException {
        // SQL con 9 marcadores '?' para los 9 campos
        String sql = "INSERT INTO factura (numero,periodo_inicio,periodo_fin," +
                     "fecha_emision,fecha_vencimiento,valor_total,estado_pago," +
                     "usuario_id,desincentivo_codigo) VALUES (?,?,?,?,?,?,?,?,?)";
        
        // try-with-resources cierra automáticamente Connection y PreparedStatement
        try (Connection c = Conexion.obtener();                                 // Abre conexión Singleton
             PreparedStatement ps = c.prepareStatement(sql)) {                  // Prepara el INSERT
            ps.setString(1, f.getNumero());                                     // Parámetro 1: número único de factura (PK)
            ps.setDate(2, Date.valueOf(f.getPeriodo().getFechaInicio()));       // Parámetro 2: LocalDate → java.sql.Date
            ps.setDate(3, Date.valueOf(f.getPeriodo().getFechaFin()));          // Parámetro 3: LocalDate → java.sql.Date
            ps.setDate(4, Date.valueOf(f.getFechaEmision()));                   // Parámetro 4: fecha de emisión
            ps.setDate(5, Date.valueOf(f.getFechaVencimiento()));               // Parámetro 5: fecha de vencimiento
            ps.setDouble(6, f.getValorTotal());                                 // Parámetro 6: valor total (incluye subsidios/descuentos)
            ps.setString(7, f.getEstadoPago().name());                          // Parámetro 7: enum → String ("PENDIENTE", "PAGADA", ...)
            ps.setInt(8, f.getUsuario().getId());                               // Parámetro 8: FK al usuario (id INT)
            
            // Parámetro 9: código del desincentivo (puede ser NULL si no aplica)
            if (f.getDesincentivo() != null) 
                ps.setString(9, f.getDesincentivo().getCodigo());               // Hay desincentivo → guardar código
            else 
                ps.setNull(9, Types.VARCHAR);                                   // Sin desincentivo → NULL
            
            ps.executeUpdate();                                                 // Ejecuta el INSERT
        }                                                                       // Se cierran automáticamente c y ps
        
        // ----- RELACIÓN N:M con CONSUMOS -----
        // Cada consumo asociado a la factura requiere una fila en factura_consumo
        for (Consumo co : f.getConsumos()) {                                    // Recorre la lista de consumos
            try (Connection c = Conexion.obtener();                             // Nueva conexión por cada INSERT
                 PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO factura_consumo (factura_numero,consumo_id) VALUES (?,?)")) {
                ps.setString(1, f.getNumero());                                 // FK a factura.numero
                ps.setInt(2, co.getId());                                       // FK a consumo.id (autoincremental)
                ps.executeUpdate();                                             // Ejecuta INSERT
            }
        }
        
        // ----- RELACIÓN N:M con SANCIONES -----
        // Cada sanción cobrada en esta factura requiere una fila en factura_sancion
        for (Sancion s : f.getSanciones()) {                                    // Recorre la lista de sanciones
            try (Connection c = Conexion.obtener();
                 PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO factura_sancion (factura_numero,sancion_codigo) VALUES (?,?)")) {
                ps.setString(1, f.getNumero());                                 // FK a factura.numero
                ps.setString(2, s.getCodigo());                                 // FK a sancion.codigo
                ps.executeUpdate();                                             // Ejecuta INSERT
            }
        }
    }

    // =====================================================================
    // 2) ACTUALIZAR SOLO EL ESTADO DE PAGO
    // =====================================================================
    /**
     * Cambia el estado de pago de una factura.
     * Usado cuando se registra un pago (PAGADA), se vence (VENCIDA) o
     * se firma un acuerdo de pago (EN_ACUERDO_PAGO).
     */
    public void actualizarEstado(String numero, EstadoPago estado) throws SQLException, ClassNotFoundException {
        try (Connection c = Conexion.obtener();                                 // Abre conexión
             PreparedStatement ps = c.prepareStatement(
                 "UPDATE factura SET estado_pago=? WHERE numero=?")) {          // UPDATE con 2 parámetros
            ps.setString(1, estado.name());                                     // Nuevo estado como String
            ps.setString(2, numero);                                            // Número de factura (WHERE)
            ps.executeUpdate();                                                 // Ejecuta el UPDATE
        }
    }

    // =====================================================================
    // 3) ACTUALIZAR SOLO EL VALOR TOTAL
    // =====================================================================
    /**
     * Recalcula y persiste el valor total de una factura.
     * Usado cuando se agregan sanciones o desincentivos después de emitir.
     */
    public void actualizarTotal(String numero, double total) throws SQLException, ClassNotFoundException {
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(
                 "UPDATE factura SET valor_total=? WHERE numero=?")) {          // UPDATE con 2 parámetros
            ps.setDouble(1, total);                                             // Nuevo valor total
            ps.setString(2, numero);                                            // WHERE numero
            ps.executeUpdate();                                                 // Ejecuta el UPDATE
        }
    }

    // =====================================================================
    // 4) BUSCAR UNA FACTURA POR NÚMERO (carga usuario + consumos + pagos)
    // =====================================================================
    /**
     * Busca una factura por su número único y retorna un objeto COMPLETO
     * (con usuario, consumos y pagos cargados).
     * CLAVE: Se separa el ResultSet de las consultas auxiliares para evitar
     * el error "Operation not allowed after ResultSet closed" del Singleton.
     */
    public Factura buscar(String numero) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM factura WHERE numero=?";                    // Consulta principal
        Factura f = null;                                                       // Factura a retornar
        int userId = -1;                                                        // FK usuario (temporal para después buscar el objeto)
        
        // --- PASO 1: Leer la fila de factura y CERRAR el ResultSet ---
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, numero);                                            // Parámetro WHERE
            try (ResultSet rs = ps.executeQuery()) {                            // Ejecuta SELECT
                if (rs.next()) {                                                // Hay resultado
                    f = mapearBasico(rs);                                       // Construye Factura con datos básicos
                    userId = rs.getInt("usuario_id");                           // Guarda FK usuario
                }
            }                                                                   // Se cierra el ResultSet aquí
        }
        
        // --- PASO 2: Enriquecer la factura con consultas auxiliares ---
        if (f != null) {                                                        // Solo si se encontró
            f.setUsuario(new UsuarioDAO().buscarPorCodigoPorId(userId));        // Carga el usuario completo por su id
            cargarConsumosFactura(f);                                           // Carga los consumos asociados
            cargarPagosFactura(f);  
            f.actualizarEstado(); 
        }
        return f;                                                               // Devuelve la factura completa (o null)
    }

    // =====================================================================
    // 5) LISTAR FACTURAS DE UN USUARIO (con sus relaciones completas)
    // =====================================================================
    /**
     * Retorna todas las facturas de un usuario, ordenadas por fecha de
     * emisión descendente. Cada factura viene con:
     *   - Usuario cargado
     *   - Lista de consumos
     *   - Lista de pagos
     * Así se pueden calcular Total, Pagado, Saldo y Estado.
     */
    public List<Factura> listarPorUsuario(int usuarioId) throws SQLException, ClassNotFoundException {
        List<Factura> lista = new ArrayList<>();                                // Lista resultado
        String sql = "SELECT * FROM factura WHERE usuario_id=? ORDER BY fecha_emision DESC";
        
        // --- PASO 1: Leer TODAS las filas y cerrar el ResultSet ---
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);                                            // Filtro por usuario
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearBasico(rs));                  // Mapea cada fila
            }
        }
        
        // --- PASO 2: Enriquecer cada factura con relaciones ---
        UsuarioDAO uDao = new UsuarioDAO();                                     // Instancia reutilizable del DAO de usuarios
        for (Factura f : lista) {
            f.setUsuario(uDao.buscarPorCodigoPorId(usuarioId));                 // Mismo usuario para todas
            cargarConsumosFactura(f);                                           // Carga consumos N:M
            cargarPagosFactura(f);  
            f.actualizarEstado();
        }
        return lista;                                                           // Retorna todas listas para pintar
    }

    // =====================================================================
    // 6) MARCAR VENCIDAS — UPDATE masivo programado
    // =====================================================================
    /**
     * Marca como VENCIDA toda factura que:
     *   - Esté en estado PENDIENTE
     *   - Su fecha_vencimiento sea ANTERIOR a hoy
     * Se usa desde procesarVencimientos() en el controlador.
     * @return número de filas afectadas
     */
    public int marcarVencidas() throws SQLException, ClassNotFoundException {
        String sql = "UPDATE factura SET estado_pago='VENCIDA' " +
                     "WHERE estado_pago='PENDIENTE' AND fecha_vencimiento < CURDATE()";
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            return ps.executeUpdate();                                          // Retorna cuántas filas se actualizaron
        }
    }

    // =====================================================================
    // 7) MAPEAR — ResultSet (una fila de `factura`) → Objeto Factura básico
    // =====================================================================
    /**
     * Convierte una fila del ResultSet en una Factura con SOLO los datos
     * propios de la tabla `factura` (sin usuario, consumos ni pagos).
     * Esos se cargan aparte para evitar consultas anidadas con el Singleton.
     */
    private Factura mapearBasico(ResultSet rs) throws SQLException {
        Factura f = new Factura();                                              // Objeto vacío
        f.setNumero(rs.getString("numero"));                                    // Número único (PK)
        f.setPeriodo(new Periodo(                                               // Construye Periodo desde dos columnas
                rs.getDate("periodo_inicio").toLocalDate(),                     // java.sql.Date → LocalDate
                rs.getDate("periodo_fin").toLocalDate()));
        f.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());           // Fecha emisión
        f.setFechaVencimiento(rs.getDate("fecha_vencimiento").toLocalDate());   // Fecha vencimiento
        f.setValorTotal(rs.getDouble("valor_total"));                           // Valor total calculado
        f.setEstadoPago(EstadoPago.valueOf(rs.getString("estado_pago")));       // String → enum
        return f;                                                               // Retorna la factura básica
    }

    // =====================================================================
    // 8) CARGAR CONSUMOS ASOCIADOS A LA FACTURA (relación N:M)
    // =====================================================================
    /**
     * Carga en la factura todos los consumos asociados vía `factura_consumo`.
     * Usa INNER JOIN para traer los datos completos del consumo.
     */
    private void cargarConsumosFactura(Factura f) throws SQLException, ClassNotFoundException {
        String sql = "SELECT c.* FROM consumo c " +
                     "INNER JOIN factura_consumo fc ON c.id = fc.consumo_id " +
                     "WHERE fc.factura_numero = ?";                             // Join por número de factura
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, f.getNumero());                                     // Parámetro WHERE
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {                                             // Por cada consumo encontrado
                    // Construye el Consumo con el constructor (volumen, fecha, tipo)
                    Consumo con = new Consumo(
                        rs.getDouble("volumen"),                                // m³ consumidos
                        rs.getDate("fecha_lectura").toLocalDate(),              // Fecha de lectura
                        TipoLectura.valueOf(rs.getString("tipo_lectura")));     // REAL / ESTIMADA
                    con.setId(rs.getInt("id"));                                 // ← IMPORTANTE: asigna el ID para poder referenciarlo
                    f.agregarConsumo(con);                                      // Lo añade a la lista de la factura
                }
            }
        }
    }

    // =====================================================================
    // 9) CARGAR PAGOS ASOCIADOS A LA FACTURA (relación 1:N)
    // =====================================================================
    /**
     * Carga en la factura todos los pagos asociados (tabla `pago`).
     * Sin esto, getSaldoPendiente() siempre devolvería el valor total.
     */
    private void cargarPagosFactura(Factura f) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM pago WHERE factura_numero = ?";             // Filtro por número
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, f.getNumero());                                     // Parámetro WHERE
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {                                             // Por cada pago
                    Pago p = new Pago();                                        // Objeto vacío
                    p.setCodigo(rs.getString("codigo"));                        // Código único del pago
                    p.setFecha(rs.getDate("fecha").toLocalDate());              // Fecha (no futura)
                    p.setValor(rs.getDouble("valor"));                          // Monto pagado
                    p.setMetodoPago(MetodoPago.valueOf(                         // String → enum
                        rs.getString("metodo_pago")));
                    p.setReferencia(rs.getString("referencia"));                // Comprobante o N° de transacción
                    f.agregarPago(p);                                           // Lo añade a la lista de la factura
                }
            }
        }
    }
}                                                                               // ← Fin de la clase FacturaDAO