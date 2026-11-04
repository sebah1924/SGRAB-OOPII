package modelo; // Paquete del dominio.

import java.math.BigDecimal;  // Valor de la factura en los mensajes.
import java.util.ArrayList;   // Lista dinámica.
import java.util.Collections; // Vistas de solo lectura.
import java.util.EnumMap;     // Mapa optimizado para claves de tipo enum.
import java.util.List;        // Interfaz de lista.
import java.util.Map;         // Interfaz de mapa.
import java.util.UUID;        // Generador de identificadores únicos universales.

/**
 * Orquesta las notificaciones de la EAAB. Es OBSERVADOR (IObservador) de Factura y EmpresaServicioPublico.
 *
 * PATRONES: Observer (recibe eventos) + Strategy (elige dinámicamente el canal con un
 * Map<CanalNotificacion, INotificable>, sin if/else por canal: se agregan canales nuevos registrándolos).
 *
 * REGLAS DE NEGOCIO (Necesidad 3):
 *  - las notificaciones se envían en tiempo real, por el canal PREFERIDO del usuario, y el canal se elige
 *    según la URGENCIA y el tipo de notificación;
 *  - todos los mensajes enviados quedan en un historial;
 *  - no se notifica un corte a un usuario exento (no se le aplicará).
 *
 * Criterio de urgencia adoptado: SUSPENSION_INMINENTE, SUSPENSION_EFECTIVA y CORTE_PROGRAMADO son urgentes.
 * Un aviso urgente no debe esperar a que el usuario lea el correo; por eso, si su canal preferido es EMAIL
 * y existe el canal SMS registrado, se envía por SMS. En los demás casos se respeta el canal preferido.
 */
public class GestorNotificaciones implements IObservador {  // Declaración de la clase GestorNotificaciones: Orquesta las notificaciones de la EAAB

    private Map<CanalNotificacion, INotificable> notificadores = new EnumMap<>(CanalNotificacion.class); // Canal -> implementación.
    private List<Mensaje> historialMensajes = new ArrayList<>(); // Mensajes enviados con éxito.
    private int contadorEnvios;  // Envíos exitosos.
    private int contadorFallos;  // Envíos fallidos.

    /** Constructor: comienza sin canales registrados. */
    public GestorNotificaciones() {  // Constructor: Constructor
        // Sin instrucciones: los atributos ya se inicializaron en su declaración.
    }

    /** @return los canales registrados (solo lectura). */
    public Map<CanalNotificacion, INotificable> getNotificadores() {  // Getter: devuelve notificadores
        return Collections.unmodifiableMap(notificadores); // Copia defensiva.
    }

    /** @return el historial de mensajes enviados (solo lectura). */
    public List<Mensaje> getHistorialMensajes() {  // Getter: devuelve historial mensajes
        return Collections.unmodifiableList(historialMensajes); // Copia defensiva.
    }

    /** @return la cantidad de envíos exitosos. */
    public int getContadorEnvios() {  // Getter: devuelve contador envios
        return contadorEnvios; // Lectura simple.
    }

    /** @return la cantidad de envíos fallidos. */
    public int getContadorFallos() {  // Getter: devuelve contador fallos
        return contadorFallos; // Lectura simple.
    }

    /** Registra (o reemplaza) la implementación de un canal. EXTENSIBILIDAD: un canal nuevo no exige cambiar el núcleo. */
    public void registrarNotificador(CanalNotificacion canal, INotificable notificador) {  // Método registrarNotificador: Registra (o reemplaza) la implementación de un canal
        if (canal == null || notificador == null) { // Ambos datos son indispensables.
            throw new IllegalArgumentException("El canal y el notificador son obligatorios.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        notificadores.put(canal, notificador); // put reemplaza si el canal ya existía.
    }

    /** Elimina un canal registrado. */
    public void eliminarNotificador(CanalNotificacion canal) {  // Método eliminarNotificador: Elimina un canal registrado
        notificadores.remove(canal); // No falla si no existía.
    }

    /** Eventos que exigen inmediatez. */
    private static boolean esUrgente(TipoEvento tipo) {  // Método esUrgente: Eventos que exigen inmediatez
        return tipo == TipoEvento.SUSPENSION_INMINENTE || tipo == TipoEvento.SUSPENSION_EFECTIVA  // Devuelve: tipo es igual a TipoEvento.SUSPENSION_INMINENTE o tipo es igual a TipoEv...
                || tipo == TipoEvento.CORTE_PROGRAMADO; // Criterio de urgencia documentado en la clase.
    }

    /**
     * Elige el canal: el preferido del usuario, salvo que el aviso sea urgente y el preferido sea EMAIL
     * (poco inmediato), caso en el que se usa SMS si está disponible.
     */
    public CanalNotificacion seleccionarCanal(Usuario usuario, TipoEvento tipoEvento) {  // Método seleccionarCanal: Elige el canal
        CanalNotificacion preferido = usuario.getCanalPreferido(); // Preferencia del usuario.
        if (esUrgente(tipoEvento) && preferido == CanalNotificacion.EMAIL  // Condición (continúa en la línea siguiente): si esUrgente(tipoEvento) y preferido es igual a CanalNotificacion.EMAIL
                && notificadores.containsKey(CanalNotificacion.SMS)) { // Escalamiento por urgencia.
            return CanalNotificacion.SMS;  // Devuelve el valor de CanalNotificacion.SMS
        }
        return preferido;  // Devuelve el valor de preferido
    }

    /** Envía un mensaje por el canal indicado; devuelve true si se envió y lo registra en el historial. */
    public boolean enviarPorCanal(Usuario usuario, Mensaje mensaje, CanalNotificacion canal) {  // Método enviarPorCanal: Envía un mensaje por el canal indicado; devuelve true si se envió y lo registra en el historial
        INotificable notificador = notificadores.get(canal); // Strategy: busca la implementación del canal.
        if (notificador == null) { // Canal no registrado: no se puede enviar.
            contadorFallos++;  // Incrementa el contador contadorFallos
            return false;  // Devuelve el valor de false
        }
        try {  // Bloque protegido: si algo falla, se captura abajo
            if (existeMensaje(mensaje.getCodigo())) {   // Regla: el código de cada mensaje es único.
                throw new IllegalArgumentException("Ya existe un mensaje con el código " + mensaje.getCodigo() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
            mensaje.setCanal(canal);                    // El mensaje queda marcado con el canal real de envío.
            notificador.notificar(usuario, mensaje);    // Polimorfismo: cada canal envía a su manera.
            historialMensajes.add(mensaje);             // "Todos los mensajes enviados quedan en un historial".
            contadorEnvios++;  // Incrementa el contador contadorEnvios
            return true;  // Devuelve el valor de true
        } catch (IllegalArgumentException | IllegalStateException e) { // Contacto inválido, canal sin configurar o código repetido.
            contadorFallos++;  // Incrementa el contador contadorFallos
            return false;  // Devuelve el valor de false
        }
    }

    /** Envía un mensaje por el canal preferido del usuario (sin considerar urgencia). */
    public boolean enviarPorCanalPreferido(Usuario usuario, Mensaje mensaje) {  // Método enviarPorCanalPreferido: Envía un mensaje por el canal preferido del usuario (sin considerar urgencia)
        return enviarPorCanal(usuario, mensaje, usuario.getCanalPreferido()); // Delegación.
    }

    /** @return true si ya se envió un mensaje con ese código. */
    private boolean existeMensaje(String codigo) {  // Método existeMensaje (devuelve boolean)
        for (Mensaje m : historialMensajes) { // Recorre el historial.
            if (m.getCodigo().equals(codigo)) { // Mismo código.
                return true;  // Devuelve el valor de true
            }
        }
        return false;  // Devuelve el valor de false
    }

    /** @return el historial completo. */
    public List<Mensaje> obtenerHistorial() {  // Método obtenerHistorial (devuelve List<Mensaje>)
        return getHistorialMensajes(); // Mismo resultado que el getter.
    }

    /** @return los mensajes enviados a un usuario concreto. */
    public List<Mensaje> obtenerHistorialPorUsuario(Usuario usuario) {  // Método obtenerHistorialPorUsuario (devuelve List<Mensaje>)
        List<Mensaje> resultado = new ArrayList<>(); // Lista de resultados.
        for (Mensaje m : historialMensajes) { // Recorre el historial.
            if (m.getDestinatario().equals(usuario)) { // Igualdad por identificación (Persona.equals).
                resultado.add(m);  // Invoca resultado.add(...)
            }
        }
        return resultado;  // Devuelve el valor de resultado
    }

    /** @return el porcentaje de envíos exitosos (0 a 100); 0 si no se ha intentado ningún envío. */
    public double calcularTasaExito() {  // Método calcularTasaExito (devuelve double)
        int total = contadorEnvios + contadorFallos; // Intentos totales.
        return total == 0 ? 0.0 : (contadorEnvios * 100.0) / total; // Evita dividir por cero.
    }

    /** Arma el mensaje del evento según su tipo (títulos cortos; los urgentes llevan prefijo URGENTE). */
    private Mensaje construirMensaje(Evento evento, CanalNotificacion canal) {  // Método construirMensaje: Arma el mensaje del evento según su tipo (títulos cortos; los urgentes llevan prefijo URGENTE)
        Usuario u = evento.getUsuario(); // Destinatario.
        Object datos = evento.getDatos(); // Información adicional.
        String titulo;                    // Asunto.
        String cuerpo;                    // Motivo.
        String factura = "";              // Detalle de la factura si el evento la trae.
        if (datos instanceof Factura) { // Los eventos de factura traen la factura como dato.
            Factura f = (Factura) datos;  // Declara «f» (Factura) con el valor: (Factura) datos
            BigDecimal valor = f.getValorTotal();  // Declara «valor» (BigDecimal) con el valor: f.getValorTotal()
            factura = "N° " + f.getNumero() + " por $" + valor;  // Actualiza «factura»: "N° " + f.getNumero() + " por $" + valor
        }
        switch (evento.getTipoEvento()) { // Un caso por cada tipo de evento del enunciado.
            case FACTURA_EMITIDA:  // Caso FACTURA_EMITIDA
                titulo = "Factura emitida";  // Actualiza «titulo»: "Factura emitida"
                cuerpo = "Se emitió su factura " + factura + ". Pague antes del vencimiento.";  // Actualiza «cuerpo»: "Se emitió su factura " + factura + ". Pague antes d...
                break;  // Termina el caso del switch
            case FACTURA_VENCIDA:  // Caso FACTURA_VENCIDA
                titulo = "Factura vencida";  // Actualiza «titulo»: "Factura vencida"
                cuerpo = "Su factura " + factura + " venció sin pago. Su servicio pasa a estado EN DEUDA.";  // Actualiza «cuerpo»: "Su factura " + factura + " venció sin pago. Su serv...
                break;  // Termina el caso del switch
            case SUSPENSION_INMINENTE:  // Caso SUSPENSION_INMINENTE
                titulo = Mensaje.PREFIJO_URGENTE + ": suspensión inminente";  // Actualiza «titulo»: Mensaje.PREFIJO_URGENTE + ": suspensión inminente"
                cuerpo = "Acumula " + u.contarFacturasVencidas() + " facturas vencidas. Pague para evitar la suspensión.";  // Actualiza «cuerpo»: "Acumula " + u.contarFacturasVencidas() + " facturas...
                break;  // Termina el caso del switch
            case SUSPENSION_EFECTIVA:  // Caso SUSPENSION_EFECTIVA
                titulo = Mensaje.PREFIJO_URGENTE + ": servicio suspendido";  // Actualiza «titulo»: Mensaje.PREFIJO_URGENTE + ": servicio suspendido"
                cuerpo = "Su servicio fue suspendido. Pague sus facturas vencidas y solicite la reactivación.";  // Actualiza «cuerpo»: "Su servicio fue suspendido. Pague sus facturas venc...
                break;  // Termina el caso del switch
            case CORTE_PROGRAMADO:  // Caso CORTE_PROGRAMADO
                titulo = Mensaje.PREFIJO_URGENTE + ": corte programado";  // Actualiza «titulo»: Mensaje.PREFIJO_URGENTE + ": corte programado"
                cuerpo = "Se programó un corte en su sector por racionamiento" + (datos instanceof String ? ": " + datos : ".");  // Actualiza «cuerpo»: "Se programó un corte en su sector por racionamiento...
                break;  // Termina el caso del switch
            case CALENDARIO_MODIFICADO:  // Caso CALENDARIO_MODIFICADO
                titulo = "Cambio en el calendario";  // Actualiza «titulo»: "Cambio en el calendario"
                cuerpo = "Se modificó el calendario de racionamiento" + (datos instanceof String ? ": " + datos : ".");  // Actualiza «cuerpo»: "Se modificó el calendario de racionamiento" + (dato...
                break;  // Termina el caso del switch
            default:  // Caso por defecto: cualquier otro valor
                titulo = "Notificación EAAB";  // Actualiza «titulo»: "Notificación EAAB"
                cuerpo = "Hay una novedad en su servicio.";  // Actualiza «cuerpo»: "Hay una novedad en su servicio."
        }
        return new Mensaje("MSG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), titulo, cuerpo, u, canal);  // Devuelve: new Mensaje("MSG-" + UUID.randomUUID().toString().substring(0, 8).toUppe...
    }

    /**
     * OBSERVER: reacciona a cada evento enviando la notificación correspondiente en tiempo real.
     * Regla: a un usuario exento no se le notifica un corte programado (no se le va a aplicar).
     */
    @Override
    public void actualizar(Evento evento) {  // Método actualizar: OBSERVER
        if (evento == null) { // Nada que procesar.
            return;  // Termina el método sin devolver valor
        }
        Usuario usuario = evento.getUsuario(); // Destinatario.
        if (evento.esTipo(TipoEvento.CORTE_PROGRAMADO) && usuario.esExento()) { // Exentos no se cortan.
            return;  // Termina el método sin devolver valor
        }
        CanalNotificacion canal = seleccionarCanal(usuario, evento.getTipoEvento()); // Strategy de canal.
        enviarPorCanal(usuario, construirMensaje(evento, canal), canal);             // Construye y envía.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "GestorNotificaciones [canales=" + notificadores.keySet() + ", envios=" + contadorEnvios  // Devuelve: "GestorNotificaciones [canales=" + notificadores.keySet() + ", envios=" ...
                + ", fallos=" + contadorFallos + "]"; // Resumen.
    }
}
