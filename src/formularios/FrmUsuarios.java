package formularios; // Paquete de los formularios.

import controladores.ControladorBase;  // Importa controladores.ControladorBase
import controladores.ControladorUsuarios;  // Importa controladores.ControladorUsuarios
import controladores.Resultado;  // Importa controladores.Resultado
import java.awt.*;  // Importa java.awt.*
import java.time.LocalDate;  // Importa java.time.LocalDate
import java.util.ArrayList;  // Importa java.util.ArrayList
import java.util.HashMap;  // Importa java.util.HashMap
import java.util.List;  // Importa java.util.List
import java.util.Map;  // Importa java.util.Map
import java.util.function.Function;  // Importa java.util.function.Function
import javax.swing.*;  // Importa javax.swing.*
import javax.swing.event.DocumentEvent;  // Importa javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener;  // Importa javax.swing.event.DocumentListener
import modelo.*;  // Importa modelo.*

/**
 * Formulario de usuarios: directorio con búsqueda y acciones (editar, suspender, reactivar, cortar, dar de baja,
 * ver historial) y pestaña de registro con campos que cambian según el tipo de usuario.
 * Cubre los requerimientos RF-01 a RF-08.
 */
public class FrmUsuarios extends FrmBase {  // Declaración de la clase FrmUsuarios: Formulario de usuarios:

    private final ControladorUsuarios controlador; // Único punto de contacto con el modelo (inyectado).

    // ---- Directorio
    private final JTextField buscador = Estilo.campo(24);  // Atributo «buscador» de tipo JTextField
    private final JComboBox<Object> filtroTipo = new JComboBox<>();  // Atributo «filtroTipo» de tipo JComboBox<Object>
    private final JComboBox<Object> filtroEstado = new JComboBox<>();  // Atributo «filtroEstado» de tipo JComboBox<Object>
    private final JLabel cantidad = new JLabel(" ");  // Atributo «cantidad» de tipo JLabel
    private final ModeloTablaGenerico<Usuario> modelo; // Se crea en el constructor porque usa el controlador inyectado.
    private final JTable tabla;  // Atributo «tabla» de tipo JTable
    private final JTabbedPane pestanas = new JTabbedPane();  // Atributo «pestanas» de tipo JTabbedPane

    // ---- Registro: datos personales y del suscriptor
    private final JTextField txtId = Estilo.campo(16);  // Atributo «txtId» de tipo JTextField
    private final JTextField txtNombre = Estilo.campo(16);  // Atributo «txtNombre» de tipo JTextField
    private final JTextField txtApellido = Estilo.campo(16);  // Atributo «txtApellido» de tipo JTextField
    private final CampoFecha fecNac = new CampoFecha(null);  // Atributo «fecNac» de tipo CampoFecha
    private final JTextField txtDireccion = Estilo.campo(16);  // Atributo «txtDireccion» de tipo JTextField
    private final JTextField txtTelefono = Estilo.campo(16);  // Atributo «txtTelefono» de tipo JTextField
    private final JTextField txtEmail = Estilo.campo(16);  // Atributo «txtEmail» de tipo JTextField
    private final JTextField txtCodigo = Estilo.campo(14);  // Atributo «txtCodigo» de tipo JTextField
    private final CampoFecha fecReg = new CampoFecha(LocalDate.now());  // Atributo «fecReg» de tipo CampoFecha
    private final JSpinner spEstrato = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));  // Atributo «spEstrato» de tipo JSpinner
    private final JComboBox<CanalNotificacion> cmbCanal = new JComboBox<>(CanalNotificacion.values());  // Atributo «cmbCanal» de tipo JComboBox<CanalNotificacion>
    private final JComboBox<TipoUsuario> cmbTipo = new JComboBox<>(TipoUsuario.values());  // Atributo «cmbTipo» de tipo JComboBox<TipoUsuario>
    private final JPanel porTipo = new JPanel(new CardLayout()); // Un panel de campos distinto por tipo de usuario.
    // ---- Campos propios de cada tipo
    private final JSpinner spHabitantes = new JSpinner(new SpinnerNumberModel(1, 1, 60, 1));  // Atributo «spHabitantes» de tipo JSpinner
    private final JCheckBox chkSubsidio = new JCheckBox("Recibe subsidio (estratos 1 a 3)");  // Atributo «chkSubsidio» de tipo JCheckBox
    private final JTextField txtEstablecimiento = Estilo.campo(16);  // Atributo «txtEstablecimiento» de tipo JTextField
    private final JTextField txtActividad = Estilo.campo(16);  // Atributo «txtActividad» de tipo JTextField
    private final JTextField txtArea = Estilo.campo(16);  // Atributo «txtArea» de tipo JTextField
    private final JTextField txtVertimiento = Estilo.campo(16);  // Atributo «txtVertimiento» de tipo JTextField
    private final JTextField txtCodigoInd = Estilo.campo(16);  // Atributo «txtCodigoInd» de tipo JTextField
    private final JTextField txtProceso = Estilo.campo(16);  // Atributo «txtProceso» de tipo JTextField
    private final JTextField txtCarga = Estilo.campo(16);  // Atributo «txtCarga» de tipo JTextField
    private final JTextField txtValorContam = Estilo.campo(16);  // Atributo «txtValorContam» de tipo JTextField
    private final JTextField txtDemanda = Estilo.campo(16);  // Atributo «txtDemanda» de tipo JTextField
    private final JTextField txtEntidad = Estilo.campo(16);  // Atributo «txtEntidad» de tipo JTextField
    private final JTextField txtPresupuesto = Estilo.campo(16);  // Atributo «txtPresupuesto» de tipo JTextField

    /** Construye el formulario con su controlador. */
    public FrmUsuarios(ControladorUsuarios controlador) {  // Constructor: Construye el formulario con su controlador
        super("Usuarios", "Registro, búsqueda y administración de los suscriptores del servicio", Estilo.AZUL, 1150, 700);  // Llama al constructor de la clase base para iniciar la parte heredada
        this.controlador = controlador;  // Asigna a controlador el valor: controlador
        this.modelo = new ModeloTablaGenerico<Usuario>() // Columnas de la tabla (cada una con su función extractora).
                .columna("Código", Usuario::getCodigoSuscriptor)  // Encadena la siguiente llamada de la misma instrucción: .columna("Código", Usuario::getCodigoSuscriptor)
                .columna("Identificación", Usuario::getIdentificacion)  // Encadena la siguiente llamada de la misma instrucción: .columna("Identificación", Usuario::getIdentificacion)
                .columna("Nombre", Usuario::getNombreCompleto)  // Encadena la siguiente llamada de la misma instrucción: .columna("Nombre", Usuario::getNombreCompleto)
                .columna("Tipo", u -> u.getTipoUsuario().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Tipo", u -> u.getTipoUsuario().name())
                .columna("Estrato", u -> u.getEstrato())  // Encadena la siguiente llamada de la misma instrucción: .columna("Estrato", u -> u.getEstrato())
                .columna("Servicio", u -> u.getEstadoServicio().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Servicio", u -> u.getEstadoServicio().name())
                .columna("Canal", u -> u.getCanalPreferido().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Canal", u -> u.getCanalPreferido().name())
                .columna("Protección", u -> controlador.esExento(u) ? "EXENTO" : "-");  // Encadena la siguiente llamada de la misma instrucción: .columna("Protección", u -> controlador.esExento(u) ? "EX...
        this.tabla = Estilo.crearTabla(modelo);  // Asigna a tabla el valor: Estilo.crearTabla(modelo)
        pestanas.addTab("Directorio", construirDirectorio());  // Agrega una pestaña con su título y contenido
        pestanas.addTab("Registrar usuario", construirRegistro());  // Agrega una pestaña con su título y contenido
        contenido.add(pestanas, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
    }

    // ===================================================================== DIRECTORIO
    /** Pestaña con búsqueda, filtros, tabla y botones de acción. */
    private JComponent construirDirectorio() {  // Método construirDirectorio: Pestaña con búsqueda, filtros, tabla y botones de acción
        filtroTipo.addItem("Todos los tipos");  // Agrega una opción a la lista desplegable
        for (TipoUsuario t : TipoUsuario.values()) {  // Recorre cada elemento «t» de TipoUsuario.values()
            filtroTipo.addItem(t);  // Agrega una opción a la lista desplegable
        }
        filtroEstado.addItem("Todos los estados");  // Agrega una opción a la lista desplegable
        for (EstadoServicio e : EstadoServicio.values()) {  // Recorre cada elemento «e» de EstadoServicio.values()
            filtroEstado.addItem(e);  // Agrega una opción a la lista desplegable
        }
        buscador.setToolTipText("Escribe un nombre (sin importar tildes), un código o una identificación");  // Define el texto de ayuda que aparece al dejar el ratón encima
        buscador.getDocument().addDocumentListener(new DocumentListener() { // Búsqueda mientras se escribe.
            @Override
            public void insertUpdate(DocumentEvent e) {  // Método insertUpdate (devuelve void)
                refrescarTabla();  // Ejecuta la operación «refrescar tabla» (definida en esta clase o en un objeto colaborador)
            }

            @Override
            public void removeUpdate(DocumentEvent e) {  // Método removeUpdate (devuelve void)
                refrescarTabla();  // Ejecuta la operación «refrescar tabla» (definida en esta clase o en un objeto colaborador)
            }

            @Override
            public void changedUpdate(DocumentEvent e) {  // Método changedUpdate (devuelve void)
                refrescarTabla();  // Ejecuta la operación «refrescar tabla» (definida en esta clase o en un objeto colaborador)
            }
        });  // Cierra la llamada y el bloque (manejador o expresión lambda) abierto en las líneas anteriores
        filtroTipo.addActionListener(e -> refrescarTabla());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        filtroEstado.addActionListener(e -> refrescarTabla());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        Estilo.columnaDeEstado(tabla, 5);  // Ejecuta la operación «columna de estado» (definida en esta clase o en un objeto colaborador)
        Estilo.columnaDeEstado(tabla, 7);  // Ejecuta la operación «columna de estado» (definida en esta clase o en un objeto colaborador)

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));  // Declara «barra» (JPanel) con el valor: new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4))
        barra.add(new JLabel("Buscar:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(buscador);  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(filtroTipo);  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(filtroEstado);  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(cantidad);  // Agrega el componente al contenedor; su posición la decide el diseño

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));  // Declara «acciones» (JPanel) con el valor: new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6))
        acciones.add(accion("Editar", Estilo.AZUL, e -> editar()));  // Agrega el componente al contenedor; su posición la decide el diseño
        acciones.add(accion("Suspender", Estilo.NARANJA, e -> cambiarEstado("suspender el servicio de", controlador::suspender)));  // Agrega el componente al contenedor; su posición la decide el diseño
        acciones.add(accion("Reactivar", Estilo.VERDE, e -> cambiarEstado(null, controlador::reactivar)));  // Agrega el componente al contenedor; su posición la decide el diseño
        acciones.add(accion("Cortar", Estilo.ROJO, e -> cambiarEstado("cortar el servicio de", controlador::cortar)));  // Agrega el componente al contenedor; su posición la decide el diseño
        acciones.add(accion("Dar de baja", Estilo.MORADO, e -> darDeBaja()));  // Agrega el componente al contenedor; su posición la decide el diseño
        acciones.add(accion("Ver historial", Estilo.TURQUESA, e -> verHistorial()));  // Agrega el componente al contenedor; su posición la decide el diseño

        JPanel panel = new JPanel(new BorderLayout(0, 6));  // Declara «panel» (JPanel) con el valor: new JPanel(new BorderLayout(0, 6))
        panel.add(barra, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        panel.add(Estilo.desplazable(tabla), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        panel.add(acciones, BorderLayout.SOUTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        return panel;  // Devuelve el valor de panel
    }

    /** Crea un botón de acción con su manejador. */
    private JButton accion(String texto, Color color, java.awt.event.ActionListener l) {  // Método accion: Crea un botón de acción con su manejador
        JButton b = Estilo.boton(texto, color);  // Declara «b» (JButton) con el valor: Estilo.boton(texto, color)
        b.addActionListener(l);  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        return b;  // Devuelve el valor de b
    }

    /** Vuelve a consultar el modelo aplicando la búsqueda y los filtros de las listas. */
    private void refrescarTabla() {  // Método refrescarTabla: Vuelve a consultar el modelo aplicando la búsqueda y los filtros de las listas
        List<Usuario> encontrados = new ArrayList<>();  // Declara «encontrados» (List<Usuario>) con el valor: new ArrayList<>()
        for (Usuario u : controlador.buscar(buscador.getText())) { // Búsqueda del controlador (RF-02).
            boolean tipoOk = !(filtroTipo.getSelectedItem() instanceof TipoUsuario) || u.getTipoUsuario() == filtroTipo.getSelectedItem();  // Declara «tipoOk» (boolean) con el valor: !(filtroTipo.getSelectedItem() instanceof TipoU...
            boolean estadoOk = !(filtroEstado.getSelectedItem() instanceof EstadoServicio) || u.getEstadoServicio() == filtroEstado.getSelectedItem();  // Declara «estadoOk» (boolean) con el valor: !(filtroEstado.getSelectedItem() instanceof Est...
            if (tipoOk && estadoOk) {  // Condición: si tipoOk y estadoOk
                encontrados.add(u);  // Agrega el componente al contenedor; su posición la decide el diseño
            }
        }
        modelo.setFilas(encontrados);  // Entrega las filas a la tabla para que se redibuje
        cantidad.setText(encontrados.size() + " usuario(s)");  // Escribe el texto que muestra el componente
    }

    /** Refresca los datos (lo invoca el formulario principal). */
    @Override
    public final void actualizar() {  // Método actualizar: Refresca los datos (lo invoca el formulario principal)
        refrescarTabla();  // Ejecuta la operación «refrescar tabla» (definida en esta clase o en un objeto colaborador)
        if (txtCodigo.getText().isEmpty()) {  // Condición: si txtCodigo.getText() está vacío
            txtCodigo.setText(ControladorBase.generarCodigo("SUS"));  // Escribe el texto que muestra el componente
        }
    }

    /** Aplica una operación de estado sobre el usuario seleccionado, con confirmación si es delicada. */
    private void cambiarEstado(String verbo, Function<Usuario, Resultado<Usuario>> operacion) {  // Método cambiarEstado: Aplica una operación de estado sobre el usuario seleccionado, con confirmación si es delicada
        Usuario u = seleccionado(tabla, modelo);  // Declara «u» (Usuario) con el valor: seleccionado(tabla, modelo)
        if (u == null) {  // Condición: si u es igual a nulo
            avisar("Selecciona primero un usuario de la tabla.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        if (verbo != null && !confirmar("¿Seguro que deseas " + verbo + " " + u.getNombreCompleto() + "?")) {  // Condición: si verbo es distinto de nulo y no confirmar("¿Seguro que deseas " + verbo +...
            return;  // Termina el método sin devolver valor
        }
        mostrar(operacion.apply(u));  // Muestra al usuario el mensaje del resultado de la operación
        refrescarTabla();  // Ejecuta la operación «refrescar tabla» (definida en esta clase o en un objeto colaborador)
    }

    /** Pide el motivo y da de baja al usuario (RF-07). */
    private void darDeBaja() {  // Método darDeBaja: Pide el motivo y da de baja al usuario (RF-07)
        Usuario u = seleccionado(tabla, modelo);  // Declara «u» (Usuario) con el valor: seleccionado(tabla, modelo)
        if (u == null) {  // Condición: si u es igual a nulo
            avisar("Selecciona primero un usuario de la tabla.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        String motivo = JOptionPane.showInputDialog(this, "Motivo de la baja de " + u.getNombreCompleto() + ":",  // Declara «motivo» (String) con el valor: JOptionPane.showInputDialog(this, "Motivo de la...
                "Dar de baja", JOptionPane.QUESTION_MESSAGE);  // Continúa el texto de la instrucción anterior.
        if (motivo == null) { // El funcionario canceló.
            return;  // Termina el método sin devolver valor
        }
        mostrar(controlador.eliminar(u, motivo));  // Muestra al usuario el mensaje del resultado de la operación
        refrescarTabla();  // Ejecuta la operación «refrescar tabla» (definida en esta clase o en un objeto colaborador)
    }

    /** Diálogo de edición de dirección, teléfono, correo, estrato y canal (RF-03). */
    private void editar() {  // Método editar: Diálogo de edición de dirección, teléfono, correo, estrato y canal (RF-03)
        Usuario u = seleccionado(tabla, modelo);  // Declara «u» (Usuario) con el valor: seleccionado(tabla, modelo)
        if (u == null) {  // Condición: si u es igual a nulo
            avisar("Selecciona primero un usuario de la tabla.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        JDialog dlg = new JDialog(this, "Editar - " + u.getNombreCompleto(), true);  // Declara «dlg» (JDialog) con el valor: new JDialog(this, "Editar - " + u.getNombreComp...
        JTextField dir = Estilo.campo(22);  // Declara «dir» (JTextField) con el valor: Estilo.campo(22)
        dir.setText(u.getDireccion());  // Escribe el texto que muestra el componente
        JTextField tel = Estilo.campo(22);  // Declara «tel» (JTextField) con el valor: Estilo.campo(22)
        tel.setText(u.getTelefono());  // Escribe el texto que muestra el componente
        JTextField mail = Estilo.campo(22);  // Declara «mail» (JTextField) con el valor: Estilo.campo(22)
        mail.setText(u.getEmail());  // Escribe el texto que muestra el componente
        JSpinner estrato = new JSpinner(new SpinnerNumberModel(u.getEstrato(), 1, 6, 1));  // Declara «estrato» (JSpinner) con el valor: new JSpinner(new SpinnerNumberModel(u.getEstrat...
        JComboBox<CanalNotificacion> canal = new JComboBox<>(CanalNotificacion.values());  // Declara «canal» (JComboBox<CanalNotific...) con el valor: new JComboBox<>(CanalNotificacion.values())
        canal.setSelectedItem(u.getCanalPreferido());  // Selecciona una opción de la lista
        Estilo.validarEnVivo(tel, t -> t.matches("\\d{7,10}"), "Debe tener entre 7 y 10 dígitos");  // Ejecuta la operación «validar en vivo» (definida en esta clase o en un objeto colaborador)
        Estilo.validarEnVivo(mail, t -> t.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$"), "Formato: usuario@dominio.com");  // Ejecuta la operación «validar en vivo» (definida en esta clase o en un objeto colaborador)
        PanelFormulario f = new PanelFormulario();  // Declara «f» (PanelFormulario) con el valor: new PanelFormulario()
        f.agregar("Dirección", dir);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Teléfono", tel);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Correo", mail);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Estrato", estrato);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Canal preferido", canal);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        JButton guardar = Estilo.boton("Guardar cambios", Estilo.VERDE);  // Declara «guardar» (JButton) con el valor: Estilo.boton("Guardar cambios", Estilo.VERDE)
        guardar.addActionListener(e -> {  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
            Resultado<Usuario> r = controlador.editar(u, dir.getText(), tel.getText().trim(), mail.getText().trim(),  // Declara «r» (Resultado<Usuario>) con el valor: controlador.editar(u, dir.getText(), tel.getTex...
                    (Integer) estrato.getValue(), (CanalNotificacion) canal.getSelectedItem());  // Continuación de la instrucción de la línea anterior.
            if (r.isExito()) { // Si todo salió bien se cierra; si no, se muestra el motivo y se permite corregir.
                dlg.dispose();  // Cierra la ventana y libera sus recursos
                mostrar(r);  // Muestra al usuario el mensaje del resultado de la operación
                refrescarTabla();  // Ejecuta la operación «refrescar tabla» (definida en esta clase o en un objeto colaborador)
            } else {  // Alternativa: se ejecuta cuando ninguna condición anterior se cumplió
                JOptionPane.showMessageDialog(dlg, r.getMensaje(), "Revisa los datos", JOptionPane.WARNING_MESSAGE);  // Ejecuta la operación «show message dialog» (definida en esta clase o en un objeto colaborador)
            }
        });  // Cierra la llamada y el bloque (manejador o expresión lambda) abierto en las líneas anteriores
        f.agregarAncho(guardar);  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        dlg.getContentPane().add(f);  // Agrega el componente al contenedor; su posición la decide el diseño
        dlg.pack();  // Ajusta la ventana al tamaño preferido de su contenido
        dlg.setLocationRelativeTo(this);  // Centra la ventana respecto a otra (null = centro de la pantalla)
        dlg.setVisible(true);  // Muestra u oculta el componente
    }

    /** Diálogo con el historial de consumos, facturas y sanciones del usuario, y su consumo promedio (RF-08). */
    private void verHistorial() {  // Método verHistorial: Diálogo con el historial de consumos, facturas y sanciones del usuario, y su consumo promedio (RF-08)
        Usuario u = seleccionado(tabla, modelo);  // Declara «u» (Usuario) con el valor: seleccionado(tabla, modelo)
        if (u == null) {  // Condición: si u es igual a nulo
            avisar("Selecciona primero un usuario de la tabla.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        ModeloTablaGenerico<Consumo> mc = new ModeloTablaGenerico<Consumo>()  // Declara «mc» (ModeloTablaGenerico<Co...) con el valor: new ModeloTablaGenerico<Consumo>()
                .columna("Código", Consumo::getCodigo).columna("Fecha", c -> Formato.fecha(c.getFechaLectura()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Código", Consumo::getCodigo).columna("Fecha", c...
                .columna("Volumen (m3)", Consumo::getVolumenM3).columna("Lectura", c -> c.getTipoLectura().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Volumen (m3)", Consumo::getVolumenM3).columna("...
                .columna("Tarifa", c -> c.getTarifaAplicada().getCodigo());  // Encadena la siguiente llamada de la misma instrucción: .columna("Tarifa", c -> c.getTarifaAplicada().getCodigo());
        mc.setFilas(controlador.consumosDe(u));  // Entrega las filas a la tabla para que se redibuje
        ModeloTablaGenerico<Factura> mf = new ModeloTablaGenerico<Factura>()  // Declara «mf» (ModeloTablaGenerico<Fa...) con el valor: new ModeloTablaGenerico<Factura>()
                .columna("Número", Factura::getNumero).columna("Emisión", f -> Formato.fecha(f.getFechaEmision()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Número", Factura::getNumero).columna("Emisión",...
                .columna("Vencimiento", f -> Formato.fecha(f.getFechaVencimiento()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Vencimiento", f -> Formato.fecha(f.getFechaVenc...
                .columna("Total", f -> Formato.moneda(f.getValorTotal())).columna("Saldo", f -> Formato.moneda(f.calcularSaldoPendiente()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Total", f -> Formato.moneda(f.getValorTotal()))...
                .columna("Estado", f -> f.getEstadoPago().name());  // Encadena la siguiente llamada de la misma instrucción: .columna("Estado", f -> f.getEstadoPago().name());
        mf.setFilas(controlador.facturasDe(u));  // Entrega las filas a la tabla para que se redibuje
        ModeloTablaGenerico<Sancion> ms = new ModeloTablaGenerico<Sancion>()  // Declara «ms» (ModeloTablaGenerico<Sa...) con el valor: new ModeloTablaGenerico<Sancion>()
                .columna("Código", Sancion::getCodigoSancion).columna("Tipo", s -> s.getTipo().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Código", Sancion::getCodigoSancion).columna("Ti...
                .columna("Multa", s -> Formato.moneda(s.getValorMulta())).columna("Estado", s -> s.getEstado().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Multa", s -> Formato.moneda(s.getValorMulta()))...
                .columna("Fecha", s -> Formato.fecha(s.getFechaImposicion()));  // Encadena la siguiente llamada de la misma instrucción: .columna("Fecha", s -> Formato.fecha(s.getFechaImposicion...
        ms.setFilas(controlador.sancionesDe(u));  // Entrega las filas a la tabla para que se redibuje
        JTable tf = Estilo.crearTabla(mf);  // Declara «tf» (JTable) con el valor: Estilo.crearTabla(mf)
        Estilo.columnaDeEstado(tf, 5);  // Ejecuta la operación «columna de estado» (definida en esta clase o en un objeto colaborador)
        JTable ts = Estilo.crearTabla(ms);  // Declara «ts» (JTable) con el valor: Estilo.crearTabla(ms)
        Estilo.columnaDeEstado(ts, 3);  // Ejecuta la operación «columna de estado» (definida en esta clase o en un objeto colaborador)
        JTabbedPane tp = new JTabbedPane();  // Declara «tp» (JTabbedPane) con el valor: new JTabbedPane()
        tp.addTab("Consumos (" + mc.getRowCount() + ")", Estilo.desplazable(Estilo.crearTabla(mc)));  // Agrega una pestaña con su título y contenido
        tp.addTab("Facturas (" + mf.getRowCount() + ")", Estilo.desplazable(tf));  // Agrega una pestaña con su título y contenido
        tp.addTab("Sanciones (" + ms.getRowCount() + ")", Estilo.desplazable(ts));  // Agrega una pestaña con su título y contenido
        JDialog dlg = new JDialog(this, "Historial - " + u.getNombreCompleto(), true);  // Declara «dlg» (JDialog) con el valor: new JDialog(this, "Historial - " + u.getNombreC...
        JLabel promedio = Estilo.negrita("Consumo promedio: " + String.format("%.2f", u.getConsumoPromedioM3()) + " m3");  // Declara «promedio» (JLabel) con el valor: Estilo.negrita("Consumo promedio: " + String.fo...
        promedio.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));  // Asigna el borde (margen interno o línea) del componente
        dlg.getContentPane().setLayout(new BorderLayout());  // Define el administrador de diseño que ubica los componentes hijos
        dlg.getContentPane().add(promedio, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        dlg.getContentPane().add(tp, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        dlg.setSize(820, 440);  // Define el tamaño de la ventana en píxeles
        dlg.setLocationRelativeTo(this);  // Centra la ventana respecto a otra (null = centro de la pantalla)
        dlg.setVisible(true);  // Muestra u oculta el componente
    }

    // ===================================================================== REGISTRO
    /** Pestaña de registro: tres grupos (datos personales, del suscriptor y propios del tipo). */
    private JComponent construirRegistro() {  // Método construirRegistro: Pestaña de registro:
        PanelFormulario personal = new PanelFormulario();  // Declara «personal» (PanelFormulario) con el valor: new PanelFormulario()
        personal.agregar("Identificación", txtId);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        personal.agregar("Nombres", txtNombre);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        personal.agregar("Apellidos", txtApellido);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        personal.agregar("Fecha nacimiento", fecNac);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        personal.agregar("Dirección", txtDireccion);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        personal.agregar("Teléfono", txtTelefono);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        personal.agregar("Correo", txtEmail);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        Estilo.validarEnVivo(fecNac, t -> fecNac.getFecha() != null && !fecNac.getFecha().isAfter(LocalDate.now()),  // Ejecuta la operación «validar en vivo» (definida en esta clase o en un objeto colaborador)
                "Fecha válida y no futura (AAAA-MM-DD)");  // Continúa el texto de la instrucción anterior.
        Estilo.validarEnVivo(txtTelefono, t -> t.matches("\\d{7,10}"), "Debe tener entre 7 y 10 dígitos numéricos");  // Ejecuta la operación «validar en vivo» (definida en esta clase o en un objeto colaborador)
        Estilo.validarEnVivo(txtEmail, t -> t.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$"), "Formato: usuario@dominio.com");  // Ejecuta la operación «validar en vivo» (definida en esta clase o en un objeto colaborador)
        JPanel grupoPersonal = new JPanel(new BorderLayout());  // Declara «grupoPersonal» (JPanel) con el valor: new JPanel(new BorderLayout())
        grupoPersonal.add(personal.ancladoArriba(), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(grupoPersonal, "1. Datos personales", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        PanelFormulario suscriptor = new PanelFormulario();  // Declara «suscriptor» (PanelFormulario) con el valor: new PanelFormulario()
        suscriptor.agregar("Tipo de usuario", cmbTipo);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        suscriptor.agregar("Código suscriptor", txtCodigo);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        suscriptor.agregar("Fecha de registro", fecReg);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        suscriptor.agregar("Estrato (1 a 6)", spEstrato);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        suscriptor.agregar("Canal preferido", cmbCanal);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        txtCodigo.setText(ControladorBase.generarCodigo("SUS"));  // Escribe el texto que muestra el componente
        cmbCanal.setSelectedItem(CanalNotificacion.SMS);  // Selecciona una opción de la lista
        spEstrato.addChangeListener(e -> sincronizarSubsidio()); // El subsidio solo se ofrece en estratos 1 a 3.
        JPanel grupoSuscriptor = new JPanel(new BorderLayout());  // Declara «grupoSuscriptor» (JPanel) con el valor: new JPanel(new BorderLayout())
        grupoSuscriptor.add(suscriptor.ancladoArriba(), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(grupoSuscriptor, "2. Datos del suscriptor", Estilo.TURQUESA);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        porTipo.add(camposResidencial(), TipoUsuario.RESIDENCIAL.name());  // Agrega el componente al contenedor; su posición la decide el diseño
        porTipo.add(camposComercial(), TipoUsuario.COMERCIAL.name());  // Agrega el componente al contenedor; su posición la decide el diseño
        porTipo.add(camposIndustrial(), TipoUsuario.INDUSTRIAL.name());  // Agrega el componente al contenedor; su posición la decide el diseño
        porTipo.add(camposOficial(), TipoUsuario.OFICIAL.name());  // Agrega el componente al contenedor; su posición la decide el diseño
        cmbTipo.addActionListener(e -> ((CardLayout) porTipo.getLayout()).show(porTipo, ((TipoUsuario) cmbTipo.getSelectedItem()).name()));  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        sincronizarSubsidio();  // Ejecuta la operación «sincronizar subsidio» (definida en esta clase o en un objeto colaborador)
        JPanel grupoTipo = new JPanel(new BorderLayout());  // Declara «grupoTipo» (JPanel) con el valor: new JPanel(new BorderLayout())
        grupoTipo.add(porTipo, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(grupoTipo, "3. Datos propios del tipo", Estilo.MORADO);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        JButton registrar = Estilo.boton("Registrar usuario", Estilo.VERDE);  // Declara «registrar» (JButton) con el valor: Estilo.boton("Registrar usuario", Estilo.VERDE)
        registrar.addActionListener(e -> registrar());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        JButton limpiar = Estilo.boton("Limpiar formulario", Estilo.GRIS);  // Declara «limpiar» (JButton) con el valor: Estilo.boton("Limpiar formulario", Estilo.GRIS)
        limpiar.addActionListener(e -> limpiar());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));  // Declara «botones» (JPanel) con el valor: new JPanel(new FlowLayout(FlowLayout.RIGHT, 10,...
        botones.add(limpiar);  // Agrega el componente al contenedor; su posición la decide el diseño
        botones.add(registrar);  // Agrega el componente al contenedor; su posición la decide el diseño

        JPanel columnas = new JPanel(new GridLayout(1, 3, 10, 0));  // Declara «columnas» (JPanel) con el valor: new JPanel(new GridLayout(1, 3, 10, 0))
        columnas.add(grupoPersonal);  // Agrega el componente al contenedor; su posición la decide el diseño
        columnas.add(grupoSuscriptor);  // Agrega el componente al contenedor; su posición la decide el diseño
        columnas.add(grupoTipo);  // Agrega el componente al contenedor; su posición la decide el diseño
        JPanel panel = new JPanel(new BorderLayout(0, 6));  // Declara «panel» (JPanel) con el valor: new JPanel(new BorderLayout(0, 6))
        panel.add(new JScrollPane(columnas), BorderLayout.CENTER); // Con barras de desplazamiento si la ventana es pequeña.
        panel.add(botones, BorderLayout.SOUTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        return panel;  // Devuelve el valor de panel
    }

    /** Campos del usuario residencial. */
    private JComponent camposResidencial() {  // Método camposResidencial: Campos del usuario residencial
        PanelFormulario f = new PanelFormulario();  // Declara «f» (PanelFormulario) con el valor: new PanelFormulario()
        f.agregar("Habitantes", spHabitantes);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregarAncho(chkSubsidio);  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        f.agregarAncho(new JLabel("<html>Subsidio: estrato 1 hasta 70 %, 2 hasta 40 %,<br>3 hasta 15 %. Estratos 5 y 6 pagan contribución<br>del 20 %.</html>"));  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        return f.ancladoArriba();  // Devuelve: f.ancladoArriba()
    }

    /** Campos del usuario comercial. */
    private JComponent camposComercial() {  // Método camposComercial: Campos del usuario comercial
        PanelFormulario f = new PanelFormulario();  // Declara «f» (PanelFormulario) con el valor: new PanelFormulario()
        f.agregar("Establecimiento", txtEstablecimiento);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Actividad económica", txtActividad);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Área operativa m2", txtArea);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Vertimiento (opcional)", txtVertimiento);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        return f.ancladoArriba();  // Devuelve: f.ancladoArriba()
    }

    /** Campos del usuario industrial. */
    private JComponent camposIndustrial() {  // Método camposIndustrial: Campos del usuario industrial
        PanelFormulario f = new PanelFormulario();  // Declara «f» (PanelFormulario) con el valor: new PanelFormulario()
        f.agregar("Código industrial", txtCodigoInd);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Proceso productivo", txtProceso);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Carga contaminante", txtCarga);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Valor unit. contam.", txtValorContam);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Cargo demanda", txtDemanda);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        return f.ancladoArriba();  // Devuelve: f.ancladoArriba()
    }

    /** Campos del usuario oficial. */
    private JComponent camposOficial() {  // Método camposOficial: Campos del usuario oficial
        PanelFormulario f = new PanelFormulario();  // Declara «f» (PanelFormulario) con el valor: new PanelFormulario()
        f.agregar("Entidad pública", txtEntidad);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Presupuesto", txtPresupuesto);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregarAncho(new JLabel("<html>Los usuarios oficiales están siempre exentos<br>de cortes y suspensiones.</html>"));  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        return f.ancladoArriba();  // Devuelve: f.ancladoArriba()
    }

    /** Habilita el subsidio solo cuando el estrato es elegible (1, 2 o 3). */
    private void sincronizarSubsidio() {  // Método sincronizarSubsidio: Habilita el subsidio solo cuando el estrato es elegible (1, 2 o 3)
        boolean elegible = (Integer) spEstrato.getValue() <= 3;  // Declara «elegible» (boolean) con el valor: (Integer) spEstrato.getValue() es menor o igual...
        chkSubsidio.setEnabled(elegible);  // Habilita o deshabilita el componente
        if (!elegible) {  // Condición: si no elegible
            chkSubsidio.setSelected(false);  // Marca o desmarca la casilla
        }
    }

    /** Convierte el texto de un campo en número o lanza un error comprensible. */
    private double numero(JTextField campo, String nombre) {  // Método numero: Convierte el texto de un campo en número o lanza un error comprensible
        try {  // Bloque protegido: si algo falla, se captura abajo
            return Double.parseDouble(campo.getText().trim().replace(',', '.'));  // Devuelve: Double.parseDouble(campo.getText().trim().replace(',', '.'))
        } catch (NumberFormatException e) {  // Captura el error de tipo NumberFormatException e
            throw new IllegalArgumentException("El valor de «" + nombre + "» debe ser un número.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
    }

    /** Lee el formulario, arma el mapa que espera la fábrica y pide el registro al controlador (RF-01). */
    private void registrar() {  // Método registrar: Lee el formulario, arma el mapa que espera la fábrica y pide el registro al controlador (RF-01)
        TipoUsuario tipo = (TipoUsuario) cmbTipo.getSelectedItem();  // Declara «tipo» (TipoUsuario) con el valor: (TipoUsuario) cmbTipo.getSelectedItem()
        Map<String, Object> d = new HashMap<>();  // Declara «d» (Map<String, Object>) con el valor: new HashMap<>()
        try {  // Bloque protegido: si algo falla, se captura abajo
            LocalDate nac = fecNac.getFecha();  // Declara «nac» (LocalDate) con el valor: fecNac.getFecha()
            LocalDate reg = fecReg.getFecha();  // Declara «reg» (LocalDate) con el valor: fecReg.getFecha()
            if (nac == null || reg == null) {  // Condición: si nac es igual a nulo o reg es igual a nulo
                throw new IllegalArgumentException("Las fechas deben tener el formato AAAA-MM-DD.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
            d.put(UsuarioFactory.IDENTIFICACION, txtId.getText().trim());  // Guarda el valor asociado a la clave
            d.put(UsuarioFactory.NOMBRE, txtNombre.getText().trim());  // Guarda el valor asociado a la clave
            d.put(UsuarioFactory.APELLIDO, txtApellido.getText().trim());  // Guarda el valor asociado a la clave
            d.put(UsuarioFactory.FECHA_NACIMIENTO, nac);  // Guarda el valor asociado a la clave
            d.put(UsuarioFactory.DIRECCION, txtDireccion.getText().trim());  // Guarda el valor asociado a la clave
            d.put(UsuarioFactory.TELEFONO, txtTelefono.getText().trim());  // Guarda el valor asociado a la clave
            d.put(UsuarioFactory.EMAIL, txtEmail.getText().trim());  // Guarda el valor asociado a la clave
            d.put(UsuarioFactory.CODIGO_SUSCRIPTOR, txtCodigo.getText().trim());  // Guarda el valor asociado a la clave
            d.put(UsuarioFactory.FECHA_REGISTRO, reg);  // Guarda el valor asociado a la clave
            d.put(UsuarioFactory.ESTRATO, spEstrato.getValue());  // Guarda el valor asociado a la clave
            d.put(UsuarioFactory.CANAL_PREFERIDO, cmbCanal.getSelectedItem());  // Guarda el valor asociado a la clave
            switch (tipo) { // Datos propios según el tipo.
                case RESIDENCIAL:  // Caso RESIDENCIAL
                    d.put(UsuarioFactory.NUMERO_HABITANTES, spHabitantes.getValue());  // Guarda el valor asociado a la clave
                    d.put(UsuarioFactory.BENEFICIO_SUBSIDIO, chkSubsidio.isSelected());  // Guarda el valor asociado a la clave
                    break;  // Termina el caso del switch
                case COMERCIAL:  // Caso COMERCIAL
                    d.put(UsuarioFactory.NOMBRE_ESTABLECIMIENTO, txtEstablecimiento.getText().trim());  // Guarda el valor asociado a la clave
                    d.put(UsuarioFactory.ACTIVIDAD_ECONOMICA, txtActividad.getText().trim());  // Guarda el valor asociado a la clave
                    d.put(UsuarioFactory.AREA_OPERACION_M2, numero(txtArea, "Área operativa"));  // Guarda el valor asociado a la clave
                    if (!txtVertimiento.getText().trim().isEmpty()) {  // Condición: si no txtVertimiento.getText().trim() está vacío
                        d.put(UsuarioFactory.CARGO_VERTIMIENTO, numero(txtVertimiento, "Cargo por vertimiento"));  // Guarda el valor asociado a la clave
                    }
                    break;  // Termina el caso del switch
                case INDUSTRIAL:  // Caso INDUSTRIAL
                    d.put(UsuarioFactory.CODIGO_INDUSTRIAL, txtCodigoInd.getText().trim());  // Guarda el valor asociado a la clave
                    d.put(UsuarioFactory.PROCESO_PRODUCTIVO, txtProceso.getText().trim());  // Guarda el valor asociado a la clave
                    d.put(UsuarioFactory.CARGA_CONTAMINANTE, numero(txtCarga, "Carga contaminante"));  // Guarda el valor asociado a la clave
                    if (!txtValorContam.getText().trim().isEmpty()) {  // Condición: si no txtValorContam.getText().trim() está vacío
                        d.put(UsuarioFactory.VALOR_UNITARIO_CONTAMINACION, numero(txtValorContam, "Valor unitario de contaminación"));  // Guarda el valor asociado a la clave
                    }
                    if (!txtDemanda.getText().trim().isEmpty()) {  // Condición: si no txtDemanda.getText().trim() está vacío
                        d.put(UsuarioFactory.CARGO_DEMANDA, numero(txtDemanda, "Cargo por demanda"));  // Guarda el valor asociado a la clave
                    }
                    break;  // Termina el caso del switch
                default: // OFICIAL
                    d.put(UsuarioFactory.ENTIDAD_PUBLICA, txtEntidad.getText().trim());  // Guarda el valor asociado a la clave
                    d.put(UsuarioFactory.ASIGNACION_PRESUPUESTAL, numero(txtPresupuesto, "Presupuesto"));  // Guarda el valor asociado a la clave
                    break;  // Termina el caso del switch
            }
        } catch (IllegalArgumentException e) {  // Captura el error de tipo IllegalArgumentException e
            avisar(e.getMessage(), false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        Resultado<Usuario> r = controlador.registrar(tipo, d);  // Declara «r» (Resultado<Usuario>) con el valor: controlador.registrar(tipo, d)
        mostrar(r);  // Muestra al usuario el mensaje del resultado de la operación
        if (r.isExito()) { // Si se registró, se limpia el formulario y se muestra el directorio.
            limpiar();  // Ejecuta la operación «limpiar» (definida en esta clase o en un objeto colaborador)
            refrescarTabla();  // Ejecuta la operación «refrescar tabla» (definida en esta clase o en un objeto colaborador)
            pestanas.setSelectedIndex(0);  // Ejecuta la operación «set selected index» (definida en esta clase o en un objeto colaborador)
        }
    }

    /** Deja el formulario de registro en blanco y sugiere un nuevo código. */
    private void limpiar() {  // Método limpiar: Deja el formulario de registro en blanco y sugiere un nuevo código
        for (JTextField t : new JTextField[]{txtId, txtNombre, txtApellido, txtDireccion, txtTelefono, txtEmail, txtEstablecimiento,  // Instrucción: for (JTextField t : new JTextField[]{txtId, txtNombre, tx...
                txtActividad, txtArea, txtVertimiento, txtCodigoInd, txtProceso, txtCarga, txtValorContam, txtDemanda, txtEntidad, txtPresupuesto}) {  // Continuación de la instrucción de la línea anterior.
            t.setText("");  // Escribe el texto que muestra el componente
        }
        fecNac.setFecha(null);  // Ejecuta la operación «set fecha» (definida en esta clase o en un objeto colaborador)
        fecReg.setFecha(LocalDate.now());  // Ejecuta la operación «set fecha» (definida en esta clase o en un objeto colaborador)
        txtCodigo.setText(ControladorBase.generarCodigo("SUS"));  // Escribe el texto que muestra el componente
        spHabitantes.setValue(1);  // Asigna el valor mostrado
        chkSubsidio.setSelected(false);  // Marca o desmarca la casilla
    }
}
