package controladores; // Paquete de los controladores.

import java.util.ArrayList;  // Importa java.util.ArrayList
import java.util.List;  // Importa java.util.List
import modelo.*;  // Importa modelo.*

/** Controlador de las notificaciones (RF-09, RF-10 y RF-11). */
public class ControladorNotificaciones extends ControladorBase {  // Declaración de la clase ControladorNotificaciones: Controlador de las notificaciones (RF-09, RF-10 y RF-11)

    /** Inyecta el contexto. */
    public ControladorNotificaciones(ContextoSgrab contexto) {  // Constructor: Inyecta el contexto
        super(contexto);  // Llama al constructor de la clase base para iniciar la parte heredada
    }

    /** @return el historial de mensajes enviados (RF-11). */
    public List<Mensaje> historial() {  // Método historial (devuelve List<Mensaje>)
        return new ArrayList<>(contexto.getGestor().getHistorialMensajes());  // Devuelve: new ArrayList<>(contexto.getGestor().getHistorialMensajes())
    }

    /** @return el porcentaje de envíos exitosos. */
    public double tasaExito() {  // Método tasaExito (devuelve double)
        return contexto.getGestor().calcularTasaExito();  // Devuelve: contexto.getGestor().calcularTasaExito()
    }

    /** @return envíos exitosos. */
    public int envios() {  // Método envios (devuelve int)
        return contexto.getGestor().getContadorEnvios();  // Devuelve: contexto.getGestor().getContadorEnvios()
    }

    /** @return envíos fallidos. */
    public int fallos() {  // Método fallos (devuelve int)
        return contexto.getGestor().getContadorFallos();  // Devuelve: contexto.getGestor().getContadorFallos()
    }

    /** Avisa un corte programado a los usuarios del sector (RF-10); devuelve cuántos mensajes se enviaron. */
    public Resultado<Integer> avisarCorte(List<Usuario> sector, String detalle) {  // Método avisarCorte: Avisa un corte programado a los usuarios del sector (RF-10); devuelve cuántos mensajes se enviaron
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            int antes = contexto.getGestor().getContadorEnvios();  // Declara «antes» (int) con el valor: contexto.getGestor().getContadorEnvios()
            contexto.getEmpresa().notificarCorteProgramado(sector, detalle);  // Ejecuta la operación «notificar corte programado» (definida en esta clase o en un objeto colaborador)
            return contexto.getGestor().getContadorEnvios() - antes; // Los exentos no reciben el aviso de corte.
        }, "Aviso de corte enviado.");  // Ejecuta: }, "Aviso de corte enviado.")
    }

    /** Avisa un cambio del calendario de racionamiento (RF-10). */
    public Resultado<Integer> avisarCalendario(List<Usuario> usuarios, String detalle) {  // Método avisarCalendario: Avisa un cambio del calendario de racionamiento (RF-10)
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            int antes = contexto.getGestor().getContadorEnvios();  // Declara «antes» (int) con el valor: contexto.getGestor().getContadorEnvios()
            contexto.getEmpresa().notificarCambioCalendario(usuarios, detalle);  // Ejecuta la operación «notificar cambio calendario» (definida en esta clase o en un objeto colaborador)
            return contexto.getGestor().getContadorEnvios() - antes;  // Devuelve: contexto.getGestor().getContadorEnvios() - antes
        }, "Aviso de cambio de calendario enviado.");  // Ejecuta: }, "Aviso de cambio de calendario enviado.")
    }
}
