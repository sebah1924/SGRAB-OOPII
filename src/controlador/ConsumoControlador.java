package controlador;


import dao.*;
import modelo.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de CONSUMOS (soporta Necesidades 2, 5, 6 y 9).
 * Responsabilidad ÚNICA: registrar lecturas de consumo, calcular
 * promedios y consultar historial. También sugiere lecturas estimadas.
 */

public class ConsumoControlador {

    private final ConsumoDAO consumoDAO = new ConsumoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final TarifaDAO tarifaDAO = new TarifaDAO();

    public void registrar(Usuario u, Consumo c) throws Exception {
        if (u == null) throw new Exception("Usuario nulo.");
        if (u.getFechaBaja() != null) throw new Exception("El usuario está dado de baja.");

        Tarifa t = tarifaDAO.buscarVigente(u.getTipoUsuario(), LocalDate.now());
        if (t == null) throw new Exception("No hay tarifa vigente para " + u.getTipoUsuario());
        c.setTarifaAplicada(t); // Asigna tarifa antes de guardar

        // Si la lectura es estimada y viene vacía, se calcula automáticamente
        if (c.getTipoLectura() == TipoLectura.ESTIMADA && c.getVolumen() == 0) {
            List<Consumo> hist = consumoDAO.listarPorUsuario(u.getId());
            if (!hist.isEmpty()) {
                double suma = 0;
                for (Consumo h : hist) suma += h.getVolumen();
                c.setVolumen(suma / hist.size());
            }
        }

        int id = consumoDAO.guardar(c, u.getId()); // Retorna el consecutivo generado
        if (id <= 0) throw new Exception("No se pudo guardar el consumo.");
        c.setId(id);

        u.agregarConsumo(c); // Actualiza objeto en memoria
        double prom = u.calcularConsumoPromedio();
        usuarioDAO.actualizarPromedio(u.getId(), prom); // Persiste el promedio
    }

    public List<Consumo> historial(Usuario u) throws Exception {
        return consumoDAO.listarPorUsuario(u.getId());
    }

    public List<Consumo> consumosEnPeriodo(Usuario u, LocalDate desde, LocalDate hasta) throws Exception {
        return consumoDAO.listarPorUsuarioEnPeriodo(u.getId(), desde, hasta);
    }

    public double sugerirVolumenEstimado(Usuario u) throws Exception {
        List<Consumo> hist = consumoDAO.listarPorUsuario(u.getId());
        if (hist.isEmpty()) return 0;
        double suma = 0;
        for (Consumo h : hist) suma += h.getVolumen();
        return suma / hist.size();
    }
}