package formularios; // Paquete de los formularios.

import controladores.ControladorBase;  // Importa controladores.ControladorBase
import controladores.ControladorFacturacion;  // Importa controladores.ControladorFacturacion
import controladores.ControladorUsuarios;  // Importa controladores.ControladorUsuarios
import controladores.Resultado;  // Importa controladores.Resultado
import java.awt.*;  // Importa java.awt.*
import java.math.BigDecimal;  // Importa java.math.BigDecimal
import java.math.RoundingMode;  // Importa java.math.RoundingMode
import java.time.LocalDate;  // Importa java.time.LocalDate
import java.time.LocalTime;  // Importa java.time.LocalTime
import java.time.format.DateTimeFormatter;  // Importa java.time.format.DateTimeFormatter
import java.util.ArrayList;  // Importa java.util.ArrayList
import java.util.List;  // Importa java.util.List
import javax.swing.*;  // Importa javax.swing.*
import modelo.*;  // Importa modelo.*

/**
 * Formulario de facturación y pagos: emisión del periodo, revisión de vencimientos, historial con filtros por
 * fecha y estado, y registro de pagos con barra de avance. Cubre RF-23 a RF-27.
 */
public class FrmFacturacion extends FrmBase {  // Declaración de la clase FrmFacturacion: Formulario de facturación y pagos:

    private final ControladorFacturacion controlador;  // Atributo «controlador» de tipo ControladorFacturacion
    private final SelectorUsuario selector;  // Atributo «selector» de tipo SelectorUsuario
    private final JTextArea registro = new JTextArea(); // Bitácora de lo realizado en esta sesión.

    // ---- Historial
    private final CampoFecha desde = new CampoFecha(null);  // Atributo «desde» de tipo CampoFecha
    private final CampoFecha hasta = new CampoFecha(null);  // Atributo «hasta» de tipo CampoFecha
    private final JComboBox<Object> cmbEstado = new JComboBox<>();  // Atributo «cmbEstado» de tipo JComboBox<Object>
    private List<Factura> mostradas = new ArrayList<>(); // Facturas que muestra la tabla ahora.
    private final ModeloTablaGenerico<Factura> modeloFacturas = new ModeloTablaGenerico<Factura>()  // Instrucción: private final ModeloTablaGenerico<Factura> modeloFacturas...
            .columna("Número", Factura::getNumero)  // Encadena la siguiente llamada de la misma instrucción: .columna("Número", Factura::getNumero)
            .columna("Usuario", f -> f.getUsuario().getNombreCompleto())  // Encadena la siguiente llamada de la misma instrucción: .columna("Usuario", f -> f.getUsuario().getNombreCompleto())
            .columna("Emisión", f -> Formato.fecha(f.getFechaEmision()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Emisión", f -> Formato.fecha(f.getFechaEmision()))
            .columna("Vence", f -> Formato.fecha(f.getFechaVencimiento()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Vence", f -> Formato.fecha(f.getFechaVencimient...
            .columna("Total", f -> Formato.moneda(f.getValorTotal()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Total", f -> Formato.moneda(f.getValorTotal()))
            .columna("Saldo", f -> Formato.moneda(f.calcularSaldoPendiente()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Saldo", f -> Formato.moneda(f.calcularSaldoPend...
            .columna("Estado", f -> f.getEstadoPago().name());  // Encadena la siguiente llamada de la misma instrucción: .columna("Estado", f -> f.getEstadoPago().name());
    private final JTable tablaFacturas = Estilo.crearTabla(modeloFacturas);  // Atributo «tablaFacturas» de tipo JTable
    private final ModeloTablaGenerico<Pago> modeloPagos = new ModeloTablaGenerico<Pago>()  // Instrucción: private final ModeloTablaGenerico<Pago> modeloPagos = new...
            .columna("Código", Pago::getCodigo).columna("Fecha", p -> Formato.fecha(p.getFecha()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Código", Pago::getCodigo).columna("Fecha", p ->...
            .columna("Valor", p -> Formato.moneda(p.getValor())).columna("Método", p -> p.getMetodoPago().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Valor", p -> Formato.moneda(p.getValor())).colu...
            .columna("Referencia", Pago::getReferencia);  // Encadena la siguiente llamada de la misma instrucción: .columna("Referencia", Pago::getReferencia);
    private final JTable tablaPagos = Estilo.crearTabla(modeloPagos);  // Atributo «tablaPagos» de tipo JTable

    // ---- Formulario de pago
    private final JTextField txtCodigoPago = Estilo.campo(14);  // Atributo «txtCodigoPago» de tipo JTextField
    private final CampoFecha fecPago = new CampoFecha(LocalDate.now());  // Atributo «fecPago» de tipo CampoFecha
    private final JTextField txtValor = Estilo.campo(14);  // Atributo «txtValor» de tipo JTextField
    private final JComboBox<MetodoPago> cmbMetodo = new JComboBox<>(MetodoPago.values());  // Atributo «cmbMetodo» de tipo JComboBox<MetodoPago>
    private final JTextField txtReferencia = Estilo.campo(14);  // Atributo «txtReferencia» de tipo JTextField
    private final JLabel lblSaldo = Estilo.negrita("Selecciona una factura");  // Atributo «lblSaldo» de tipo JLabel
    private final JProgressBar avance = new JProgressBar(0, 100);  // Atributo «avance» de tipo JProgressBar

    /** Construye el formulario. */
    public FrmFacturacion(ControladorUsuarios usuarios, ControladorFacturacion controlador) {  // Constructor: Construye el formulario
        super("Facturación y pagos", "Emisión de facturas, control de vencimientos y registro de pagos", Estilo.VERDE, 1200, 740);  // Llama al constructor de la clase base para iniciar la parte heredada
        this.controlador = controlador;  // Asigna a controlador el valor: controlador
        this.selector = new SelectorUsuario(usuarios);  // Asigna a selector el valor: new SelectorUsuario(usuarios)
        Estilo.columnaDeEstado(tablaFacturas, 6);  // Ejecuta la operación «columna de estado» (definida en esta clase o en un objeto colaborador)
        cmbEstado.addItem("Todos los estados");  // Agrega una opción a la lista desplegable
        for (EstadoPago e : EstadoPago.values()) {  // Recorre cada elemento «e» de EstadoPago.values()
            cmbEstado.addItem(e);  // Agrega una opción a la lista desplegable
        }
        txtCodigoPago.setText(ControladorBase.generarCodigo("PAG"));  // Escribe el texto que muestra el componente
        avance.setStringPainted(true);  // Muestra el texto dentro de la barra de progreso
        avance.setForeground(Estilo.VERDE);  // Define el color del texto o primer plano
        tablaFacturas.getSelectionModel().addListSelectionListener(e -> { // Al elegir una factura se cargan sus pagos.
            if (!e.getValueIsAdjusting()) {  // Condición: si no e.getValueIsAdjusting()
                cargarFacturaSeleccionada();  // Ejecuta la operación «cargar factura seleccionada» (definida en esta clase o en un objeto colaborador)
            }
        });  // Cierra la llamada y el bloque (manejador o expresión lambda) abierto en las líneas anteriores
        JTabbedPane pestanas = new JTabbedPane();  // Declara «pestanas» (JTabbedPane) con el valor: new JTabbedPane()
        pestanas.addTab("Emitir y vencimientos", construirEmision());  // Agrega una pestaña con su título y contenido
        pestanas.addTab("Historial y pagos", construirHistorial());  // Agrega una pestaña con su título y contenido
        contenido.add(pestanas, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        mostradas = controlador.listarTodas();  // Actualiza «mostradas»: controlador.listarTodas()
        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
    }

    // ===================================================================== EMISIÓN
    /** Pestaña con las dos acciones masivas: emitir el periodo y revisar vencimientos. */
    private JComponent construirEmision() {  // Método construirEmision: Pestaña con las dos acciones masivas:
        JPanel emitir = new JPanel(new BorderLayout(0, 8));  // Declara «emitir» (JPanel) con el valor: new JPanel(new BorderLayout(0, 8))
        emitir.add(new JLabel("<html>Factura a cada usuario sus consumos pendientes, con subsidio o contribución,<br>"  // Agrega el componente al contenedor; su posición la decide el diseño
                + "desincentivo por consumo excesivo y sanciones económicas activas.<br>"  // Continúa el texto de la instrucción anterior.
                + "Vence a los " + EmpresaServicioPublico.DIAS_PLAZO_PAGO + " días de la emisión y notifica al usuario.</html>"), BorderLayout.CENTER);  // Continúa el texto de la instrucción anterior.
        JButton b1 = Estilo.boton("Emitir facturas ahora", Estilo.VERDE);  // Declara «b1» (JButton) con el valor: Estilo.boton("Emitir facturas ahora", Estilo.VE...
        b1.addActionListener(e -> emitir());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        emitir.add(b1, BorderLayout.SOUTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(emitir, "Emitir facturación del periodo", Estilo.VERDE);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        JPanel vencer = new JPanel(new BorderLayout(0, 8));  // Declara «vencer» (JPanel) con el valor: new JPanel(new BorderLayout(0, 8))
        vencer.add(new JLabel("<html>Marca como vencidas las facturas cuyo plazo terminó sin pago completo,<br>"  // Agrega el componente al contenedor; su posición la decide el diseño
                + "pasa al usuario a «en deuda» y avisa; con dos vencidas avisa la suspensión inminente.</html>"), BorderLayout.CENTER);  // Continúa el texto de la instrucción anterior.
        JButton b2 = Estilo.boton("Revisar vencimientos ahora", Estilo.NARANJA);  // Declara «b2» (JButton) con el valor: Estilo.boton("Revisar vencimientos ahora", Esti...
        b2.addActionListener(e -> revisar());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        vencer.add(b2, BorderLayout.SOUTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(vencer, "Revisar vencimientos", Estilo.NARANJA);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        JPanel arriba = new JPanel(new GridLayout(1, 2, 10, 0));  // Declara «arriba» (JPanel) con el valor: new JPanel(new GridLayout(1, 2, 10, 0))
        arriba.add(emitir);  // Agrega el componente al contenedor; su posición la decide el diseño
        arriba.add(vencer);  // Agrega el componente al contenedor; su posición la decide el diseño
        registro.setEditable(false);  // Define si el usuario puede editar el contenido
        JPanel bitacora = new JPanel(new BorderLayout());  // Declara «bitacora» (JPanel) con el valor: new JPanel(new BorderLayout())
        bitacora.add(Estilo.desplazable(registro), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(bitacora, "Bitácora de esta sesión", Estilo.GRIS);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        JPanel panel = new JPanel(new BorderLayout(0, 8));  // Declara «panel» (JPanel) con el valor: new JPanel(new BorderLayout(0, 8))
        panel.add(arriba, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        panel.add(bitacora, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        return panel;  // Devuelve el valor de panel
    }

    /** Agrega una línea con la hora a la bitácora. */
    private void anotar(String texto) {  // Método anotar: Agrega una línea con la hora a la bitácora
        registro.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "  " + texto + "\n");  // Ejecuta la operación «format» (definida en esta clase o en un objeto colaborador)
    }

    /** Emite las facturas del periodo (RF-25). */
    private void emitir() {  // Método emitir: Emite las facturas del periodo (RF-25)
        Resultado<ControladorFacturacion.ResumenFacturacion> r = controlador.emitir();  // Ejecuta: Resultado<ControladorFacturacion.ResumenFacturacion> r = ...
        if (!r.isExito()) {  // Condición: si no r.isExito()
            avisar(r.getMensaje(), false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        ControladorFacturacion.ResumenFacturacion s = r.getValor();  // Ejecuta: ControladorFacturacion.ResumenFacturacion s = r.getValor()
        anotar("Facturación: " + s.getEmitidas() + " factura(s) emitida(s).");  // Ejecuta la operación «anotar» (definida en esta clase o en un objeto colaborador)
        for (String o : s.getOmitidos()) { // Cada usuario omitido queda explicado.
            anotar("   Omitido " + o);  // Ejecuta la operación «anotar» (definida en esta clase o en un objeto colaborador)
        }
        estado(s.getEmitidas() + " factura(s) emitida(s)" + (s.getOmitidos().isEmpty() ? "." : " y " + s.getOmitidos().size() + " omitido(s)."),  // Ejecuta la operación «estado» (definida en esta clase o en un objeto colaborador)
                s.getOmitidos().isEmpty());  // Continuación de la instrucción de la línea anterior.
        mostradas = controlador.listarTodas();  // Actualiza «mostradas»: controlador.listarTodas()
        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
    }

    /** Revisa vencimientos (RF-26). */
    private void revisar() {  // Método revisar: Revisa vencimientos (RF-26)
        Resultado<Integer> r = controlador.revisarVencimientos();  // Declara «r» (Resultado<Integer>) con el valor: controlador.revisarVencimientos()
        if (r.isExito()) {  // Condición: si r.isExito()
            anotar("Vencimientos: " + r.getValor() + " factura(s) pasaron a vencida(s).");  // Ejecuta la operación «anotar» (definida en esta clase o en un objeto colaborador)
            estado(r.getValor() + " factura(s) pasaron a vencida(s).", true);  // Ejecuta la operación «estado» (definida en esta clase o en un objeto colaborador)
        } else {  // Alternativa: se ejecuta cuando ninguna condición anterior se cumplió
            avisar(r.getMensaje(), false);  // Muestra un aviso emergente al usuario
        }
        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
    }

    // ===================================================================== HISTORIAL Y PAGOS
    /** Pestaña con filtros, tabla de facturas y panel de pagos. */
    private JComponent construirHistorial() {  // Método construirHistorial: Pestaña con filtros, tabla de facturas y panel de pagos
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));  // Declara «filtros» (JPanel) con el valor: new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4))
        selector.setPreferredSize(new Dimension(320, 54));  // Sugiere al diseño el tamaño preferido del componente
        filtros.add(new JLabel("Usuario:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        filtros.add(selector);  // Agrega el componente al contenedor; su posición la decide el diseño
        filtros.add(new JLabel("Desde:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        filtros.add(desde);  // Agrega el componente al contenedor; su posición la decide el diseño
        filtros.add(new JLabel("Hasta:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        filtros.add(hasta);  // Agrega el componente al contenedor; su posición la decide el diseño
        filtros.add(cmbEstado);  // Agrega el componente al contenedor; su posición la decide el diseño
        JButton consultar = Estilo.boton("Consultar usuario", Estilo.AZUL);  // Declara «consultar» (JButton) con el valor: Estilo.boton("Consultar usuario", Estilo.AZUL)
        consultar.addActionListener(e -> consultar());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        JButton todas = Estilo.boton("Ver todas", Estilo.GRIS);  // Declara «todas» (JButton) con el valor: Estilo.boton("Ver todas", Estilo.GRIS)
        todas.addActionListener(e -> {  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
            mostradas = controlador.listarTodas();  // Actualiza «mostradas»: controlador.listarTodas()
            actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
        });  // Cierra la llamada y el bloque (manejador o expresión lambda) abierto en las líneas anteriores
        filtros.add(consultar);  // Agrega el componente al contenedor; su posición la decide el diseño
        filtros.add(todas);  // Agrega el componente al contenedor; su posición la decide el diseño

        PanelFormulario f = new PanelFormulario();  // Declara «f» (PanelFormulario) con el valor: new PanelFormulario()
        f.agregar("Código pago", txtCodigoPago);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Fecha", fecPago);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Valor", txtValor);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Método", cmbMetodo);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Referencia", txtReferencia);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        JButton pagar = Estilo.boton("Registrar pago", Estilo.VERDE);  // Declara «pagar» (JButton) con el valor: Estilo.boton("Registrar pago", Estilo.VERDE)
        pagar.addActionListener(e -> pagar());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        JButton total = Estilo.boton("Pagar saldo completo", Estilo.TURQUESA);  // Declara «total» (JButton) con el valor: Estilo.boton("Pagar saldo completo", Estilo.TUR...
        total.addActionListener(e -> {  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
            Factura fa = seleccionado(tablaFacturas, modeloFacturas);  // Declara «fa» (Factura) con el valor: seleccionado(tablaFacturas, modeloFacturas)
            if (fa != null) {  // Condición: si fa es distinto de nulo
                txtValor.setText(fa.calcularSaldoPendiente().toPlainString());  // Escribe el texto que muestra el componente
            }
        });  // Cierra la llamada y el bloque (manejador o expresión lambda) abierto en las líneas anteriores
        JButton acuerdo = Estilo.boton("Pactar acuerdo de pago", Estilo.NARANJA);  // Declara «acuerdo» (JButton) con el valor: Estilo.boton("Pactar acuerdo de pago", Estilo.N...
        acuerdo.addActionListener(e -> pactar());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        JPanel botones = new JPanel(new GridLayout(1, 3, 6, 0));  // Declara «botones» (JPanel) con el valor: new JPanel(new GridLayout(1, 3, 6, 0))
        botones.add(pagar);  // Agrega el componente al contenedor; su posición la decide el diseño
        botones.add(total);  // Agrega el componente al contenedor; su posición la decide el diseño
        botones.add(acuerdo);  // Agrega el componente al contenedor; su posición la decide el diseño
        JPanel cabeza = new JPanel(new BorderLayout(0, 4));  // Declara «cabeza» (JPanel) con el valor: new JPanel(new BorderLayout(0, 4))
        cabeza.add(lblSaldo, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        cabeza.add(avance, BorderLayout.SOUTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        JPanel formPago = new JPanel(new BorderLayout(0, 6));  // Declara «formPago» (JPanel) con el valor: new JPanel(new BorderLayout(0, 6))
        formPago.add(cabeza, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        formPago.add(f.ancladoArriba(), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        formPago.add(botones, BorderLayout.SOUTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(formPago, "Pago de la factura seleccionada", Estilo.VERDE);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        formPago.setPreferredSize(new Dimension(430, 0));  // Sugiere al diseño el tamaño preferido del componente

        JPanel pagos = new JPanel(new BorderLayout());  // Declara «pagos» (JPanel) con el valor: new JPanel(new BorderLayout())
        pagos.add(Estilo.desplazable(tablaPagos), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(pagos, "Pagos registrados en la factura", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        JPanel abajo = new JPanel(new BorderLayout(8, 0));  // Declara «abajo» (JPanel) con el valor: new JPanel(new BorderLayout(8, 0))
        abajo.add(pagos, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        abajo.add(formPago, BorderLayout.EAST);  // Agrega el componente al contenedor; su posición la decide el diseño

        JPanel listaFacturas = new JPanel(new BorderLayout());  // Declara «listaFacturas» (JPanel) con el valor: new JPanel(new BorderLayout())
        listaFacturas.add(Estilo.desplazable(tablaFacturas), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(listaFacturas, "Facturas", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listaFacturas, abajo);  // Declara «division» (JSplitPane) con el valor: new JSplitPane(JSplitPane.VERTICAL_SPLIT, lista...
        division.setResizeWeight(0.5);  // Define cómo se reparte el espacio sobrante entre los dos paneles
        JPanel panel = new JPanel(new BorderLayout(0, 6));  // Declara «panel» (JPanel) con el valor: new JPanel(new BorderLayout(0, 6))
        panel.add(filtros, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        panel.add(division, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        return panel;  // Devuelve el valor de panel
    }

    /** Consulta el historial del usuario con los filtros (RF-27). */
    private void consultar() {  // Método consultar: Consulta el historial del usuario con los filtros (RF-27)
        Usuario u = selector.getUsuario();  // Declara «u» (Usuario) con el valor: selector.getUsuario()
        if (u == null) {  // Condición: si u es igual a nulo
            avisar("Selecciona un usuario.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        if ((!desde.getText().trim().isEmpty() && desde.getFecha() == null) || (!hasta.getText().trim().isEmpty() && hasta.getFecha() == null)) {  // Condición: si (no desde.getText().trim() está vacío y desde.getFecha() es igual a nulo...
            avisar("Las fechas del filtro deben tener el formato AAAA-MM-DD (o dejarse vacías).", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        EstadoPago est = cmbEstado.getSelectedItem() instanceof EstadoPago ? (EstadoPago) cmbEstado.getSelectedItem() : null;  // Declara «est» (EstadoPago) con el valor: cmbEstado.getSelectedItem() instanceof EstadoPa...
        Resultado<List<Factura>> r = controlador.consultar(u, desde.getFecha(), hasta.getFecha(), est);  // Declara «r» (Resultado<List<Factura>>) con el valor: controlador.consultar(u, desde.getFecha(), hast...
        if (r.isExito()) {  // Condición: si r.isExito()
            mostradas = r.getValor();  // Actualiza «mostradas»: r.getValor()
            actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
            estado(mostradas.size() + " factura(s) encontrada(s).", true);  // Ejecuta la operación «estado» (definida en esta clase o en un objeto colaborador)
        } else {  // Alternativa: se ejecuta cuando ninguna condición anterior se cumplió
            mostrar(r);  // Muestra al usuario el mensaje del resultado de la operación
        }
    }

    /** Recarga la tabla y el panel de pago. */
    @Override
    public final void actualizar() {  // Método actualizar: Recarga la tabla y el panel de pago
        modeloFacturas.setFilas(mostradas);  // Entrega las filas a la tabla para que se redibuje
        cargarFacturaSeleccionada();  // Ejecuta la operación «cargar factura seleccionada» (definida en esta clase o en un objeto colaborador)
    }

    /** Actualiza pagos, saldo y barra de avance de la factura seleccionada. */
    private void cargarFacturaSeleccionada() {  // Método cargarFacturaSeleccionada: Actualiza pagos, saldo y barra de avance de la factura seleccionada
        Factura fa = seleccionado(tablaFacturas, modeloFacturas);  // Declara «fa» (Factura) con el valor: seleccionado(tablaFacturas, modeloFacturas)
        if (fa == null) {  // Condición: si fa es igual a nulo
            modeloPagos.setFilas(new ArrayList<Pago>());  // Entrega las filas a la tabla para que se redibuje
            lblSaldo.setText("Selecciona una factura");  // Escribe el texto que muestra el componente
            avance.setValue(0);  // Asigna el valor mostrado
            avance.setString("");  // Ejecuta la operación «set string» (definida en esta clase o en un objeto colaborador)
            return;  // Termina el método sin devolver valor
        }
        modeloPagos.setFilas(new ArrayList<>(fa.getListaPagos()));  // Entrega las filas a la tabla para que se redibuje
        BigDecimal saldo = fa.calcularSaldoPendiente();  // Declara «saldo» (BigDecimal) con el valor: fa.calcularSaldoPendiente()
        lblSaldo.setText("Saldo: " + Formato.moneda(saldo));  // Escribe el texto que muestra el componente
        BigDecimal total = fa.getValorTotal();  // Declara «total» (BigDecimal) con el valor: fa.getValorTotal()
        int porcentaje = total.signum() == 0 ? 0  // Declara «porcentaje» (int) con el valor: total (signo) es igual a 0 ? 0
                : total.subtract(saldo).multiply(BigDecimal.valueOf(100)).divide(total, 0, RoundingMode.HALF_UP).intValue();  // Continuación de la instrucción de la línea anterior.
        avance.setValue(Math.max(0, Math.min(100, porcentaje)));  // Asigna el valor mostrado
        avance.setString(porcentaje + " % pagado");  // Ejecuta la operación «set string» (definida en esta clase o en un objeto colaborador)
    }

    /** Registra un pago sobre la factura seleccionada (RF-23). */
    private void pagar() {  // Método pagar: Registra un pago sobre la factura seleccionada (RF-23)
        Factura fa = seleccionado(tablaFacturas, modeloFacturas);  // Declara «fa» (Factura) con el valor: seleccionado(tablaFacturas, modeloFacturas)
        if (fa == null) {  // Condición: si fa es igual a nulo
            avisar("Selecciona primero una factura.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        LocalDate fe = fecPago.getFecha();  // Declara «fe» (LocalDate) con el valor: fecPago.getFecha()
        if (fe == null) {  // Condición: si fe es igual a nulo
            avisar("La fecha del pago debe tener el formato AAAA-MM-DD.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        BigDecimal valor;  // Ejecuta: BigDecimal valor
        try {  // Bloque protegido: si algo falla, se captura abajo
            valor = new BigDecimal(txtValor.getText().trim().replace(',', '.'));  // Actualiza «valor»: new BigDecimal(txtValor.getText().trim().replace(','...
        } catch (NumberFormatException e) {  // Captura el error de tipo NumberFormatException e
            avisar("El valor del pago debe ser un número.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        Resultado<Pago> r = controlador.registrarPago(fa, txtCodigoPago.getText().trim(), fe, valor,  // Declara «r» (Resultado<Pago>) con el valor: controlador.registrarPago(fa, txtCodigoPago.get...
                (MetodoPago) cmbMetodo.getSelectedItem(), txtReferencia.getText().trim());  // Continuación de la instrucción de la línea anterior.
        mostrar(r);  // Muestra al usuario el mensaje del resultado de la operación
        if (r.isExito()) {  // Condición: si r.isExito()
            txtCodigoPago.setText(ControladorBase.generarCodigo("PAG"));  // Escribe el texto que muestra el componente
            txtValor.setText("");  // Escribe el texto que muestra el componente
            txtReferencia.setText("");  // Escribe el texto que muestra el componente
            actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
        }
    }

    /** Marca la factura como en acuerdo de pago. */
    private void pactar() {  // Método pactar: Marca la factura como en acuerdo de pago
        Factura fa = seleccionado(tablaFacturas, modeloFacturas);  // Declara «fa» (Factura) con el valor: seleccionado(tablaFacturas, modeloFacturas)
        if (fa == null) {  // Condición: si fa es igual a nulo
            avisar("Selecciona primero una factura.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        mostrar(controlador.pactarAcuerdo(fa));  // Muestra al usuario el mensaje del resultado de la operación
        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
    }
}
