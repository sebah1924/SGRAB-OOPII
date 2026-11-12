package controlador;
                                                  // Paquete controladores

import dao.*;                                                         // Acceso a DAOs
import modelo.*;                                                      // Modelo
import java.util.List;                                                         // Listas

/**
 * Controlador de USUARIOS (Módulo 1 – Necesidades 1 y 2).
 * Responsabilidad ÚNICA: orquestar registro, búsqueda, edición,
 * suspensión, reactivación, corte y baja de suscriptores.
 */
public class UsuarioControlador {

    // Dependencias inyectadas por composición
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();                    // CRUD usuario
    private final PersonaDAO personaDAO = new PersonaDAO();                    // CRUD persona
    private final ConsumoDAO consumoDAO = new ConsumoDAO();                    // Historial consumos
    private final FacturaDAO facturaDAO = new FacturaDAO();                    // Historial facturas
    private final NotificacionControlador notifCtrl = new NotificacionControlador(); // Notifica

    /** Registra un usuario validando unicidad de identificación y código. */
    public void registrar(Usuario u) throws Exception {
        if (personaDAO.buscarPorIdentificacion(u.getIdentificacion()) != null) // Valida identificación única
            throw new Exception("La identificación ya está registrada.");
        if (usuarioDAO.buscarPorCodigo(u.getCodigoSuscriptor()) != null)       // Valida código único
            throw new Exception("El código de suscriptor ya existe.");
        u.setEstrategia(EstrategiaFactory.crear(u.getTipoUsuario()));          // Asigna estrategia tarifaria
        usuarioDAO.guardar(u);                                                 // Persiste en BD (transacción polimórfica)
    }

    /** Busca usuario por código de suscriptor. */
    public Usuario buscarPorCodigo(String codigo) throws Exception {
        return usuarioDAO.buscarPorCodigo(codigo);                             // Delega al DAO
    }

    /** Lista todos los usuarios. */
    public List<Usuario> listarTodos() throws Exception {
        return usuarioDAO.listar();                                            // Delega
    }

    /** Edita datos personales y del suscriptor. */
    public void editar(Usuario u) throws Exception {
        personaDAO.actualizar(u);                                              // Actualiza tabla persona
        // (Estrato y estado se actualizan vía métodos específicos)
    }

    /** Suspende el servicio si el usuario NO está exento. */
    public void suspender(Usuario u) throws Exception {
        if (!u.puedeSerCortado())                                              // Regla: exento = no suspender
            throw new Exception("Usuario exento, no puede suspenderse.");
        u.suspender();                                                         // Cambia estado en objeto
        usuarioDAO.actualizarEstado(u.getId(), u.getEstadoServicio());          // Persiste estado
        notifCtrl.enviarNotificacion(u, EventoNotificacion.SUSPENSION_EFECTIVA,
                "Servicio suspendido", "Su servicio ha sido suspendido. Pague para reactivar."); // Notifica
    }

    /** Reactiva el servicio. */
    public void reactivar(Usuario u) throws Exception {
        u.reactivar();                                                         // Cambia estado en objeto
        usuarioDAO.actualizarEstado(u.getId(), u.getEstadoServicio());          // Persiste
    }

    /** Corta el servicio si el usuario NO está exento. */
    public void cortar(Usuario u) throws Exception {
        if (!u.puedeSerCortado())                                              // Regla: exento = no cortar
            throw new Exception("Usuario exento, no puede cortarse.");
        u.cortar();
        usuarioDAO.actualizarEstado(u.getId(), u.getEstadoServicio());
        notifCtrl.enviarNotificacion(u, EventoNotificacion.CORTE_PROGRAMADO,
                "Corte aplicado", "Se ha aplicado el corte por racionamiento.");
    }

    /** Elimina al usuario registrando fecha y motivo (baja lógica). */
    public void eliminar(Usuario u, String motivo) throws Exception {
        if (motivo == null || motivo.trim().isEmpty())
            throw new Exception("Debe indicar el motivo de la baja.");
        u.eliminar(motivo);                                                    // Marca fecha y motivo
        usuarioDAO.eliminar(u.getId(), motivo);                                 // Persiste
    }

    /** Retorna el historial de consumos del usuario. */
    public List<Consumo> historialConsumos(Usuario u) throws Exception {
        return consumoDAO.listarPorUsuario(u.getId());                         // Delega
    }

    /** Retorna el historial de facturas del usuario. */
    public List<Factura> historialFacturas(Usuario u) throws Exception {
        return facturaDAO.listarPorUsuario(u.getId());                         // Delega
    }

    /** Calcula y persiste el consumo promedio. */
    public double calcularPromedio(Usuario u) throws Exception {
        List<Consumo> cs = historialConsumos(u);                               // Trae historial
        if (cs.isEmpty()) return 0;                                            // Sin datos = 0
        double suma = 0;                                                       // Acumulador
        for (Consumo c : cs) suma += c.getVolumen();                           // Suma
        double prom = suma / cs.size();                                        // Divide
        usuarioDAO.actualizarPromedio(u.getId(), prom);                        // Persiste
        u.setConsumoPromedio(prom);                                            // Actualiza objeto
        return prom;
    }

    /** Verifica si el usuario es exento de cortes. */
    public boolean estaExento(Usuario u) {
        return u.tieneExcepcion();                                             // Delegado a subclase
    }
}