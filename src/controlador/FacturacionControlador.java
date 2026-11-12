package controlador;
                                                  // Paquete

import dao.*;                                                         // DAOs
import modelo.*;                                                      // Modelo
import java.time.LocalDate;                                                    // Fechas
import java.util.List;                                                         // Listas
import java.util.Locale;

/**
 * Controlador de FACTURACIÓN (Módulo 2 – Necesidades 5, 6 y 9).
 * Responsabilidad ÚNICA: emitir facturas calculando subsidios, contribución,
 * desincentivo y sanciones; gestionar pagos y estados.
 */


public class FacturacionControlador {

    private final FacturaDAO facturaDAO = new FacturaDAO();
    private final ConsumoDAO consumoDAO = new ConsumoDAO();
    private final TarifaDAO tarifaDAO = new TarifaDAO();
    private final PagoDAO pagoDAO = new PagoDAO();
    private final DesincentivoDAO desincentivoDAO = new DesincentivoDAO();
    private final SancionDAO sancionDAO = new SancionDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final NotificacionControlador notifCtrl = new NotificacionControlador();

    public Factura emitir(Usuario u, Periodo periodo, LocalDate vencimiento, String numeroFactura) throws Exception {
        Tarifa t = tarifaDAO.buscarVigente(u.getTipoUsuario(), LocalDate.now());
        if (t == null) throw new Exception("No hay tarifa vigente para " + u.getTipoUsuario());

        List<Factura> previas = facturaDAO.listarPorUsuario(u.getId());
        for (Factura f : previas) {
            if (f.getPeriodo().seSolapaCon(periodo))
                throw new Exception("Ya existe la factura N° " + f.getNumero() + " para el periodo " + f.getPeriodo());
        }

        List<Consumo> consumos = consumoDAO.listarPorUsuarioEnPeriodo(u.getId(), periodo.getFechaInicio(), periodo.getFechaFin());
        if (consumos.isEmpty()) throw new Exception("No hay consumos registrados en ese periodo.");

        Factura f = new Factura(numeroFactura, periodo, LocalDate.now(), vencimiento, u);
        for (Consumo c : consumos) f.agregarConsumo(c);

        if (u instanceof Residencial) {
            Desincentivo d = desincentivoDAO.buscar("DES01");
            if (d != null && f.consumoTotal() > d.getLimiteBimestral()) f.setDesincentivo(d);
        }

        List<Sancion> sanciones = sancionDAO.listarPorUsuario(u.getId());
        for (Sancion s : sanciones) {
            if (s.getTipo() == TipoSancion.ECONOMICA && 
               (s.getEstado() == EstadoSancion.IMPUESTA || s.getEstado() == EstadoSancion.CONFIRMADA)) {
                f.agregarSancion(s);
            }
        }

        f.calcularValorTotal(t);
        
        // GUARDAMOS LA FACTURA (Si falla aquí, se cancela todo)
        facturaDAO.guardar(f);
        u.agregarFactura(f);

        // CORRECCIÓN: Envolvemos la notificación en un try-catch.
        // Si la notificación falla por red o BD, la factura ya quedó guardada y NO se muestra error al usuario.
        try {
            String totalFmt = String.format(Locale.US, "%,.2f", f.getValorTotal());
            notifCtrl.enviarNotificacion(u, EventoNotificacion.EMISION_FACTURA,
                    "Factura emitida #" + numeroFactura,
                    "Se emitió su factura por $" + totalFmt + ". Vence el " + vencimiento);
        } catch (Exception e) {
            // Solo se imprime en consola, la facturación ya fue exitosa
            System.err.println("Advertencia: La factura se guardó, pero la notificación falló: " + e.getMessage());
        }

        return f;
    }

    public List<Factura> listarFacturas(Usuario u) throws Exception {
        return facturaDAO.listarPorUsuario(u.getId());
    }

    public void registrarPago(Factura f, Pago p) throws Exception {
        pagoDAO.guardar(p, f.getNumero());
        f.agregarPago(p);
        f.actualizarEstado(); 
        
        facturaDAO.actualizarEstado(f.getNumero(), f.getEstadoPago());
        facturaDAO.actualizarTotal(f.getNumero(), f.getValorTotal());
        
        notifCtrl.enviarNotificacion(f.getUsuario(), EventoNotificacion.PAGO_REGISTRADO, 
                "Pago registrado", "Pago de $" + String.format("%,.2f", p.getValor()) + " registrado exitosamente.");

        if (f.getEstadoPago() == EstadoPago.PAGADA) {
            Usuario u = f.getUsuario();
            u.actualizarEstadoPorDeudas();
            usuarioDAO.actualizarEstado(u.getId(), u.getEstadoServicio());
        }
    }
}