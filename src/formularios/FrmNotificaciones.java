package formularios; // Paquete de los formularios.

import controladores.ControladorNotificaciones;  // Importa controladores.ControladorNotificaciones
import controladores.ControladorUsuarios;  // Importa controladores.ControladorUsuarios
import controladores.Resultado;  // Importa controladores.Resultado
import java.awt.*;  // Importa java.awt.*
import java.util.ArrayList;  // Importa java.util.ArrayList
import java.util.List;  // Importa java.util.List
import javax.swing.*;  // Importa javax.swing.*
import modelo.*;  // Importa modelo.*

/**
 * Formulario de notificaciones (RF-10 y RF-11): envía avisos de corte programado y de cambios del calendario de
 * racionamiento a un grupo de usuarios (el "sector"), y muestra el historial de mensajes con su tasa de éxito.
 */
public class FrmNotificaciones extends FrmBase {  // Declaración de la clase FrmNotificaciones: Formulario de notificaciones (RF-10 y RF-11):

    private final ControladorNotificaciones controlador;  // Atributo «controlador» de tipo ControladorNotificaciones
    private final ControladorUsuarios usuarios;  // Atributo «usuarios» de tipo ControladorUsuarios

    // ---- Avisos
    private final JComboBox<String> cmbAviso = new JComboBox<>(new String[]{"Corte programado", "Cambio del calendario de racionamiento"});  // Atributo «cmbAviso» de tipo JComboBox<String>
    private final JTextArea txtDetalle = new JTextArea(5, 20);  // Atributo «txtDetalle» de tipo JTextArea
    private final JComboBox<Object> filtroTipo = new JComboBox<>();  // Atributo «filtroTipo» de tipo JComboBox<Object>
    private final JComboBox<Object> filtroEstrato = new JComboBox<>();  // Atributo «filtroEstrato» de tipo JComboBox<Object>
    private final JLabel lblSeleccion = new JLabel("0 usuario(s) seleccionado(s)");  // Atributo «lblSeleccion» de tipo JLabel
    private final ModeloTablaGenerico<Usuario> modeloUsuarios = new ModeloTablaGenerico<Usuario>()  // Instrucción: private final ModeloTablaGenerico<Usuario> modeloUsuarios...
            .columna("Código", Usuario::getCodigoSuscriptor).columna("Nombre", Usuario::getNombreCompleto)  // Encadena la siguiente llamada de la misma instrucción: .columna("Código", Usuario::getCodigoSuscriptor).columna(...
            .columna("Tipo", u -> u.getTipoUsuario().name()).columna("Estrato", u -> u.getEstrato())  // Encadena la siguiente llamada de la misma instrucción: .columna("Tipo", u -> u.getTipoUsuario().name()).columna(...
            .columna("Canal", u -> u.getCanalPreferido().name());  // Encadena la siguiente llamada de la misma instrucción: .columna("Canal", u -> u.getCanalPreferido().name());
    private final JTable tablaUsuarios = Estilo.crearTabla(modeloUsuarios);  // Atributo «tablaUsuarios» de tipo JTable

    // ---- Historial
    private final JLabel cifraEnvios = Estilo.valor(Estilo.VERDE);  // Atributo «cifraEnvios» de tipo JLabel
    private final JLabel cifraFallos = Estilo.valor(Estilo.ROJO);  // Atributo «cifraFallos» de tipo JLabel
    private final JProgressBar tasa = new JProgressBar(0, 100);  // Atributo «tasa» de tipo JProgressBar
    private final ModeloTablaGenerico<Mensaje> modeloMensajes = new ModeloTablaGenerico<Mensaje>()  // Instrucción: private final ModeloTablaGenerico<Mensaje> modeloMensajes...
            .columna("Fecha", m -> Formato.fechaHora(m.getFechaCreacion()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Fecha", m -> Formato.fechaHora(m.getFechaCreaci...
            .columna("Usuario", m -> m.getDestinatario().getNombreCompleto())  // Encadena la siguiente llamada de la misma instrucción: .columna("Usuario", m -> m.getDestinatario().getNombreCom...
            .columna("Canal", m -> m.getCanal().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Canal", m -> m.getCanal().name())
            .columna("Urgente", m -> m.esUrgente() ? "SÍ" : "no")  // Encadena la siguiente llamada de la misma instrucción: .columna("Urgente", m -> m.esUrgente() ? "SÍ" : "no")
            .columna("Título", Mensaje::getTitulo)  // Encadena la siguiente llamada de la misma instrucción: .columna("Título", Mensaje::getTitulo)
            .columna("Mensaje", Mensaje::getCuerpo);  // Encadena la siguiente llamada de la misma instrucción: .columna("Mensaje", Mensaje::getCuerpo);
    private final JTable tablaMensajes = Estilo.crearTabla(modeloMensajes);  // Atributo «tablaMensajes» de tipo JTable

    /** Construye el formulario. */
    public FrmNotificaciones(ControladorUsuarios usuarios, ControladorNotificaciones controlador) {  // Constructor: Construye el formulario
        super("Notificaciones", "Avisos a los usuarios por su canal preferido y historial de mensajes", Estilo.CELESTE, 1150, 680);  // Llama al constructor de la clase base para iniciar la parte heredada
        this.controlador = controlador;  // Asigna a controlador el valor: controlador
        this.usuarios = usuarios;  // Asigna a usuarios el valor: usuarios
        JTabbedPane pestanas = new JTabbedPane();  // Declara «pestanas» (JTabbedPane) con el valor: new JTabbedPane()
        pestanas.addTab("Avisos de racionamiento", construirAvisos());  // Agrega una pestaña con su título y contenido
        pestanas.addTab("Historial de mensajes", construirHistorial());  // Agrega una pestaña con su título y contenido
        contenido.add(pestanas, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
    }

    /** Pestaña para componer y enviar avisos a un sector. */
    private JComponent construirAvisos() {  // Método construirAvisos: Pestaña para componer y enviar avisos a un sector
        PanelFormulario f = new PanelFormulario();  // Declara «f» (PanelFormulario) con el valor: new PanelFormulario()
        f.agregar("Tipo de aviso", cmbAviso);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        txtDetalle.setLineWrap(true);  // Activa el salto de línea automático
        txtDetalle.setWrapStyleWord(true);  // Hace que el salto de línea respete palabras completas
        txtDetalle.setText("Sector norte, turno del lunes de 8:00 a.m. a 12:00 m.");  // Escribe el texto que muestra el componente
        f.agregar("Detalle", new JScrollPane(txtDetalle));  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        JButton enviar = Estilo.boton("Enviar aviso a los seleccionados", Estilo.CELESTE);  // Declara «enviar» (JButton) con el valor: Estilo.boton("Enviar aviso a los seleccionados"...
        enviar.addActionListener(e -> enviar());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        f.agregarAncho(enviar);  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        f.agregarAncho(new JLabel("<html>Los usuarios exentos no reciben el aviso de corte<br>porque no se les va a aplicar. Los avisos urgentes<br>"  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
                + "escalan de correo a SMS cuando es posible.</html>"));  // Continúa el texto de la instrucción anterior.
        JPanel grupoForm = new JPanel(new BorderLayout());  // Declara «grupoForm» (JPanel) con el valor: new JPanel(new BorderLayout())
        grupoForm.add(f.ancladoArriba(), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(grupoForm, "Redactar aviso", Estilo.CELESTE);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        grupoForm.setPreferredSize(new Dimension(420, 0));  // Sugiere al diseño el tamaño preferido del componente

        filtroTipo.addItem("Todos los tipos");  // Agrega una opción a la lista desplegable
        for (TipoUsuario t : TipoUsuario.values()) {  // Recorre cada elemento «t» de TipoUsuario.values()
            filtroTipo.addItem(t);  // Agrega una opción a la lista desplegable
        }
        filtroEstrato.addItem("Todos los estratos");  // Agrega una opción a la lista desplegable
        for (int i = 1; i <= 6; i++) {  // Ciclo for: int i = 1; i <= 6; i++
            filtroEstrato.addItem(i);  // Agrega una opción a la lista desplegable
        }
        filtroTipo.addActionListener(e -> recargarUsuarios());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        filtroEstrato.addActionListener(e -> recargarUsuarios());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        tablaUsuarios.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);  // Define si se permite seleccionar una o varias filas
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> lblSeleccion.setText(tablaUsuarios.getSelectedRowCount() + " usuario(s) seleccionado(s)"));  // Registra el manejador que reacciona a la selección de filas
        JButton todos = Estilo.boton("Seleccionar todos", Estilo.AZUL);  // Declara «todos» (JButton) con el valor: Estilo.boton("Seleccionar todos", Estilo.AZUL)
        todos.addActionListener(e -> tablaUsuarios.selectAll());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));  // Declara «barra» (JPanel) con el valor: new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4))
        barra.add(new JLabel("Sector:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(filtroTipo);  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(filtroEstrato);  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(todos);  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(lblSeleccion);  // Agrega el componente al contenedor; su posición la decide el diseño
        JPanel destinatarios = new JPanel(new BorderLayout(0, 4));  // Declara «destinatarios» (JPanel) con el valor: new JPanel(new BorderLayout(0, 4))
        destinatarios.add(barra, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        destinatarios.add(Estilo.desplazable(tablaUsuarios), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(destinatarios, "Destinatarios (usa Ctrl o Shift para elegir varios)", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        JPanel panel = new JPanel(new BorderLayout(8, 0));  // Declara «panel» (JPanel) con el valor: new JPanel(new BorderLayout(8, 0))
        panel.add(grupoForm, BorderLayout.WEST);  // Agrega el componente al contenedor; su posición la decide el diseño
        panel.add(destinatarios, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        return panel;  // Devuelve el valor de panel
    }

    /** Pestaña con las cifras de envío y el historial. */
    private JComponent construirHistorial() {  // Método construirHistorial: Pestaña con las cifras de envío y el historial
        tasa.setStringPainted(true);  // Muestra el texto dentro de la barra de progreso
        tasa.setForeground(Estilo.VERDE);  // Define el color del texto o primer plano
        JPanel cifras = new JPanel(new GridLayout(1, 6, 8, 0));  // Declara «cifras» (JPanel) con el valor: new JPanel(new GridLayout(1, 6, 8, 0))
        cifras.add(new JLabel("Mensajes enviados:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        cifras.add(cifraEnvios);  // Agrega el componente al contenedor; su posición la decide el diseño
        cifras.add(new JLabel("Envíos fallidos:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        cifras.add(cifraFallos);  // Agrega el componente al contenedor; su posición la decide el diseño
        cifras.add(new JLabel("Tasa de éxito:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        cifras.add(tasa);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(cifras, "Resumen de envíos", Estilo.VERDE);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        JPanel lista = new JPanel(new BorderLayout());  // Declara «lista» (JPanel) con el valor: new JPanel(new BorderLayout())
        lista.add(Estilo.desplazable(tablaMensajes), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(lista, "Historial de mensajes enviados", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        JPanel panel = new JPanel(new BorderLayout(0, 8));  // Declara «panel» (JPanel) con el valor: new JPanel(new BorderLayout(0, 8))
        panel.add(cifras, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        panel.add(lista, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        return panel;  // Devuelve el valor de panel
    }

    /** Recarga destinatarios e historial. */
    @Override
    public final void actualizar() {  // Método actualizar: Recarga destinatarios e historial
        recargarUsuarios();  // Ejecuta la operación «recargar usuarios» (definida en esta clase o en un objeto colaborador)
        modeloMensajes.setFilas(controlador.historial());  // Entrega las filas a la tabla para que se redibuje
        cifraEnvios.setText(String.valueOf(controlador.envios()));  // Escribe el texto que muestra el componente
        cifraFallos.setText(String.valueOf(controlador.fallos()));  // Escribe el texto que muestra el componente
        int porcentaje = (int) Math.round(controlador.tasaExito());  // Declara «porcentaje» (int) con el valor: (int) Math.round(controlador.tasaExito())
        tasa.setValue(porcentaje);  // Asigna el valor mostrado
        tasa.setString(porcentaje + " %");  // Ejecuta la operación «set string» (definida en esta clase o en un objeto colaborador)
    }

    /** Aplica los filtros del sector a la tabla de destinatarios. */
    private void recargarUsuarios() {  // Método recargarUsuarios: Aplica los filtros del sector a la tabla de destinatarios
        List<Usuario> filtrados = new ArrayList<>();  // Declara «filtrados» (List<Usuario>) con el valor: new ArrayList<>()
        for (Usuario u : usuarios.listar()) {  // Recorre cada elemento «u» de usuarios.listar()
            boolean tipoOk = !(filtroTipo.getSelectedItem() instanceof TipoUsuario) || u.getTipoUsuario() == filtroTipo.getSelectedItem();  // Declara «tipoOk» (boolean) con el valor: !(filtroTipo.getSelectedItem() instanceof TipoU...
            boolean estratoOk = !(filtroEstrato.getSelectedItem() instanceof Integer) || u.getEstrato() == (Integer) filtroEstrato.getSelectedItem();  // Declara «estratoOk» (boolean) con el valor: !(filtroEstrato.getSelectedItem() instanceof In...
            if (tipoOk && estratoOk) {  // Condición: si tipoOk y estratoOk
                filtrados.add(u);  // Agrega el componente al contenedor; su posición la decide el diseño
            }
        }
        modeloUsuarios.setFilas(filtrados);  // Entrega las filas a la tabla para que se redibuje
    }

    /** Envía el aviso a los usuarios seleccionados (RF-10). */
    private void enviar() {  // Método enviar: Envía el aviso a los usuarios seleccionados (RF-10)
        List<Usuario> elegidos = new ArrayList<>();  // Declara «elegidos» (List<Usuario>) con el valor: new ArrayList<>()
        for (int fila : tablaUsuarios.getSelectedRows()) { // Convierte cada fila de la vista al índice del modelo.
            elegidos.add(modeloUsuarios.getFila(tablaUsuarios.convertRowIndexToModel(fila)));  // Agrega el componente al contenedor; su posición la decide el diseño
        }
        boolean corte = cmbAviso.getSelectedIndex() == 0;  // Declara «corte» (boolean) con el valor: cmbAviso.getSelectedIndex() es igual a 0
        Resultado<Integer> r = corte ? controlador.avisarCorte(elegidos, txtDetalle.getText())  // Declara «r» (Resultado<Integer>) con el valor: corte ? controlador.avisarCorte(elegidos, txtDe...
                : controlador.avisarCalendario(elegidos, txtDetalle.getText());  // Continuación de la instrucción de la línea anterior.
        if (r.isExito()) {  // Condición: si r.isExito()
            int omitidos = elegidos.size() - r.getValor();  // Declara «omitidos» (int) con el valor: elegidos.size() - r.getValor()
            estado(r.getValor() + " mensaje(s) enviado(s)" + (omitidos > 0 ? "; " + omitidos + " sin envío (exentos o contacto inválido)." : "."), true);  // Ejecuta la operación «estado» (definida en esta clase o en un objeto colaborador)
            actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
        } else {  // Alternativa: se ejecuta cuando ninguna condición anterior se cumplió
            mostrar(r);  // Muestra al usuario el mensaje del resultado de la operación
        }
    }
}
