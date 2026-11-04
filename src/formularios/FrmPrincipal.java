package formularios; // Paquete de los formularios.

import controladores.*;  // Importa controladores.*
import java.awt.*;  // Importa java.awt.*
import java.awt.event.WindowAdapter;  // Importa java.awt.event.WindowAdapter
import java.awt.event.WindowEvent;  // Importa java.awt.event.WindowEvent
import java.util.HashMap;  // Importa java.util.HashMap
import java.util.Map;  // Importa java.util.Map
import java.util.function.Supplier;  // Importa java.util.function.Supplier
import javax.swing.*;  // Importa javax.swing.*
import modelo.EstadoPago;  // Importa modelo.EstadoPago
import modelo.Usuario;  // Importa modelo.Usuario

/**
 * Ventana principal: menú superior, botones de módulos y resumen del estado del servicio y de la facturación.
 *
 * Desde aquí se abre cada formulario. Los números se actualizan solos cada pocos segundos y cuando se cierra un
 * formulario, de modo que siempre reflejan lo que acaba de pasar.
 */
public class FrmPrincipal extends JFrame {  // Declaración de la clase FrmPrincipal: Ventana principal:

    private final ControladorPanel cPanel;                      // Indicadores del resumen.
    private final ControladorUsuarios cUsuarios;                // Controladores compartidos con los formularios.
    private final ControladorConsumos cConsumos;  // Atributo «cConsumos» de tipo ControladorConsumos
    private final ControladorTarifas cTarifas;  // Atributo «cTarifas» de tipo ControladorTarifas
    private final ControladorFacturacion cFacturacion;  // Atributo «cFacturacion» de tipo ControladorFacturacion
    private final ControladorSanciones cSanciones;  // Atributo «cSanciones» de tipo ControladorSanciones
    private final ControladorExcepciones cExcepciones;  // Atributo «cExcepciones» de tipo ControladorExcepciones
    private final ControladorNotificaciones cNotificaciones;  // Atributo «cNotificaciones» de tipo ControladorNotificaciones
    private final ControladorReportes cReportes;  // Atributo «cReportes» de tipo ControladorReportes
    private final Map<String, FrmBase> abiertas = new HashMap<>(); // Formularios abiertos (para no duplicarlos).

    private final JLabel lblUsuarios = Estilo.valor(Estilo.AZUL);          // Resumen de usuarios.
    private final JLabel lblActivos = Estilo.valor(Estilo.VERDE);  // Atributo «lblActivos» de tipo JLabel
    private final JLabel lblEnDeuda = Estilo.valor(Estilo.NARANJA);  // Atributo «lblEnDeuda» de tipo JLabel
    private final JLabel lblSuspendidos = Estilo.valor(Estilo.ROJO);  // Atributo «lblSuspendidos» de tipo JLabel
    private final JLabel lblCortados = Estilo.valor(Estilo.ROJO);  // Atributo «lblCortados» de tipo JLabel
    private final JLabel lblFacturas = Estilo.valor(Estilo.AZUL);          // Resumen de facturación.
    private final JLabel lblPendientes = Estilo.valor(Estilo.NARANJA);  // Atributo «lblPendientes» de tipo JLabel
    private final JLabel lblVencidas = Estilo.valor(Estilo.ROJO);  // Atributo «lblVencidas» de tipo JLabel
    private final JLabel lblPagadas = Estilo.valor(Estilo.VERDE);  // Atributo «lblPagadas» de tipo JLabel
    private final JLabel lblFacturado = Estilo.valor(Estilo.AZUL);  // Atributo «lblFacturado» de tipo JLabel
    private final JLabel lblCartera = Estilo.valor(Estilo.NARANJA);  // Atributo «lblCartera» de tipo JLabel
    private final ModeloTablaGenerico<Usuario> modeloNovedades; // Usuarios cuyo servicio no está activo.
    private final JTable tablaNovedades;  // Atributo «tablaNovedades» de tipo JTable

    /** Construye la ventana y crea los controladores a partir del contexto. */
    public FrmPrincipal(ContextoSgrab contexto) {  // Constructor: Construye la ventana y crea los controladores a partir del contexto
        super("SGRAB - Sistema de Gestión de Usuarios y Facturación");  // Llama al constructor de la clase base para iniciar la parte heredada
        this.cPanel = new ControladorPanel(contexto);  // Asigna a cPanel el valor: new ControladorPanel(contexto)
        this.cUsuarios = new ControladorUsuarios(contexto);  // Asigna a cUsuarios el valor: new ControladorUsuarios(contexto)
        this.cConsumos = new ControladorConsumos(contexto);  // Asigna a cConsumos el valor: new ControladorConsumos(contexto)
        this.cTarifas = new ControladorTarifas(contexto);  // Asigna a cTarifas el valor: new ControladorTarifas(contexto)
        this.cFacturacion = new ControladorFacturacion(contexto);  // Asigna a cFacturacion el valor: new ControladorFacturacion(contexto)
        this.cSanciones = new ControladorSanciones(contexto);  // Asigna a cSanciones el valor: new ControladorSanciones(contexto)
        this.cExcepciones = new ControladorExcepciones(contexto);  // Asigna a cExcepciones el valor: new ControladorExcepciones(contexto)
        this.cNotificaciones = new ControladorNotificaciones(contexto);  // Asigna a cNotificaciones el valor: new ControladorNotificaciones(contexto)
        this.cReportes = new ControladorReportes(contexto);  // Asigna a cReportes el valor: new ControladorReportes(contexto)
        this.modeloNovedades = new ModeloTablaGenerico<Usuario>()  // Asigna a modeloNovedades el valor: new ModeloTablaGenerico<Usuario>()
                .columna("Código", Usuario::getCodigoSuscriptor)  // Encadena la siguiente llamada de la misma instrucción: .columna("Código", Usuario::getCodigoSuscriptor)
                .columna("Nombre", Usuario::getNombreCompleto)  // Encadena la siguiente llamada de la misma instrucción: .columna("Nombre", Usuario::getNombreCompleto)
                .columna("Tipo", u -> u.getTipoUsuario().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Tipo", u -> u.getTipoUsuario().name())
                .columna("Servicio", u -> u.getEstadoServicio().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Servicio", u -> u.getEstadoServicio().name())
                .columna("Facturas vencidas", u -> u.contarFacturasVencidas());  // Encadena la siguiente llamada de la misma instrucción: .columna("Facturas vencidas", u -> u.contarFacturasVencid...
        this.tablaNovedades = Estilo.crearTabla(modeloNovedades);  // Asigna a tablaNovedades el valor: Estilo.crearTabla(modeloNovedades)
        Estilo.columnaDeEstado(tablaNovedades, 3);  // Ejecuta la operación «columna de estado» (definida en esta clase o en un objeto colaborador)

        setDefaultCloseOperation(EXIT_ON_CLOSE); // Cerrar la ventana principal termina la aplicación.
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();  // Declara «pantalla» (Dimension) con el valor: Toolkit.getDefaultToolkit().getScreenSize()
        setSize(Math.min(1050, pantalla.width - 60), Math.min(680, pantalla.height - 80));  // Define el tamaño de la ventana en píxeles
        setLocationRelativeTo(null);  // Centra la ventana respecto a otra (null = centro de la pantalla)
        setJMenuBar(construirMenu());  // Ejecuta la operación «set jmenu bar» (definida en esta clase o en un objeto colaborador)
        getContentPane().setLayout(new BorderLayout(8, 8));  // Define el administrador de diseño que ubica los componentes hijos
        getContentPane().add(Estilo.cabecera("SGRAB", "Sistema de Gestión de Usuarios y Facturación del servicio de acueducto", Estilo.AZUL),  // Agrega el componente al contenedor; su posición la decide el diseño
                BorderLayout.NORTH);  // Continuación de la instrucción de la línea anterior.
        getContentPane().add(construirModulos(), BorderLayout.WEST);  // Agrega el componente al contenedor; su posición la decide el diseño
        getContentPane().add(construirResumen(), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño

        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
        new Timer(5000, e -> actualizar()).start(); // Refresco automático del resumen.
    }

    /** Barra de menú: Archivo, Módulos y Ayuda. */
    private JMenuBar construirMenu() {  // Método construirMenu: Barra de menú:
        JMenuBar barra = new JMenuBar();  // Declara «barra» (JMenuBar) con el valor: new JMenuBar()
        JMenu archivo = new JMenu("Archivo");  // Declara «archivo» (JMenu) con el valor: new JMenu("Archivo")
        JMenuItem salir = new JMenuItem("Salir");  // Declara «salir» (JMenuItem) con el valor: new JMenuItem("Salir")
        salir.addActionListener(e -> System.exit(0));  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        archivo.add(salir);  // Agrega el componente al contenedor; su posición la decide el diseño
        JMenu modulos = new JMenu("Módulos");  // Declara «modulos» (JMenu) con el valor: new JMenu("Módulos")
        modulos.add(itemMenu("Usuarios", () -> abrirUsuarios()));  // Agrega el componente al contenedor; su posición la decide el diseño
        modulos.add(itemMenu("Consumos", () -> abrirConsumos()));  // Agrega el componente al contenedor; su posición la decide el diseño
        modulos.add(itemMenu("Tarifas", () -> abrirTarifas()));  // Agrega el componente al contenedor; su posición la decide el diseño
        modulos.add(itemMenu("Facturación y pagos", () -> abrirFacturacion()));  // Agrega el componente al contenedor; su posición la decide el diseño
        modulos.add(itemMenu("Sanciones", () -> abrirSanciones()));  // Agrega el componente al contenedor; su posición la decide el diseño
        modulos.add(itemMenu("Excepciones", () -> abrirExcepciones()));  // Agrega el componente al contenedor; su posición la decide el diseño
        modulos.add(itemMenu("Notificaciones", () -> abrirNotificaciones()));  // Agrega el componente al contenedor; su posición la decide el diseño
        modulos.add(itemMenu("Reportes", () -> abrirReportes()));  // Agrega el componente al contenedor; su posición la decide el diseño
        JMenu ayuda = new JMenu("Ayuda");  // Declara «ayuda» (JMenu) con el valor: new JMenu("Ayuda")
        JMenuItem acerca = new JMenuItem("Acerca de SGRAB");  // Declara «acerca» (JMenuItem) con el valor: new JMenuItem("Acerca de SGRAB")
        acerca.addActionListener(e -> JOptionPane.showMessageDialog(this,  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
                "SGRAB - Gestión de usuarios y facturación del servicio de acueducto\nLey 142 de 1994 · Decreto 137 de 2024 · Resolución CRA 688 de 2014",  // Continúa el texto de la instrucción anterior.
                "Acerca de", JOptionPane.INFORMATION_MESSAGE));  // Ejecuta: "Acerca de", JOptionPane.INFORMATION_MESSAGE))
        ayuda.add(acerca);  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(archivo);  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(modulos);  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(ayuda);  // Agrega el componente al contenedor; su posición la decide el diseño
        return barra;  // Devuelve el valor de barra
    }

    /** Crea un elemento de menú que ejecuta una acción. */
    private JMenuItem itemMenu(String texto, Runnable accion) {  // Método itemMenu: Crea un elemento de menú que ejecuta una acción
        JMenuItem item = new JMenuItem(texto);  // Declara «item» (JMenuItem) con el valor: new JMenuItem(texto)
        item.addActionListener(e -> accion.run());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        return item;  // Devuelve el valor de item
    }

    /** Panel izquierdo con un botón de color por módulo. */
    private JComponent construirModulos() {  // Método construirModulos: Panel izquierdo con un botón de color por módulo
        JPanel panel = new JPanel(new GridLayout(8, 1, 8, 8));  // Declara «panel» (JPanel) con el valor: new JPanel(new GridLayout(8, 1, 8, 8))
        panel.add(botonModulo("Usuarios", Estilo.AZUL, () -> abrirUsuarios()));  // Agrega el componente al contenedor; su posición la decide el diseño
        panel.add(botonModulo("Consumos", Estilo.TURQUESA, () -> abrirConsumos()));  // Agrega el componente al contenedor; su posición la decide el diseño
        panel.add(botonModulo("Tarifas", Estilo.MORADO, () -> abrirTarifas()));  // Agrega el componente al contenedor; su posición la decide el diseño
        panel.add(botonModulo("Facturación y pagos", Estilo.VERDE, () -> abrirFacturacion()));  // Agrega el componente al contenedor; su posición la decide el diseño
        panel.add(botonModulo("Sanciones", Estilo.ROJO, () -> abrirSanciones()));  // Agrega el componente al contenedor; su posición la decide el diseño
        panel.add(botonModulo("Excepciones", Estilo.NARANJA, () -> abrirExcepciones()));  // Agrega el componente al contenedor; su posición la decide el diseño
        panel.add(botonModulo("Notificaciones", Estilo.CELESTE, () -> abrirNotificaciones()));  // Agrega el componente al contenedor; su posición la decide el diseño
        panel.add(botonModulo("Reportes", Estilo.GRIS, () -> abrirReportes()));  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(panel, "Módulos", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        JPanel envoltorio = new JPanel(new BorderLayout());  // Declara «envoltorio» (JPanel) con el valor: new JPanel(new BorderLayout())
        envoltorio.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));  // Asigna el borde (margen interno o línea) del componente
        envoltorio.add(panel, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        envoltorio.setPreferredSize(new Dimension(230, 0));  // Sugiere al diseño el tamaño preferido del componente
        return envoltorio;  // Devuelve el valor de envoltorio
    }

    /** Botón de color que abre un módulo. */
    private JButton botonModulo(String texto, Color color, Runnable accion) {  // Método botonModulo: Botón de color que abre un módulo
        JButton b = Estilo.boton(texto, color);  // Declara «b» (JButton) con el valor: Estilo.boton(texto, color)
        b.setFont(b.getFont().deriveFont(Font.BOLD, 14f));  // Define la tipografía
        b.addActionListener(e -> accion.run());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        return b;  // Devuelve el valor de b
    }

    /** Zona central: resumen de usuarios, resumen de facturación, acciones y tabla de novedades. */
    private JComponent construirResumen() {  // Método construirResumen: Zona central:
        JPanel usuarios = new JPanel(new GridLayout(0, 2, 6, 6));  // Declara «usuarios» (JPanel) con el valor: new JPanel(new GridLayout(0, 2, 6, 6))
        usuarios.add(new JLabel("Usuarios registrados:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        usuarios.add(lblUsuarios);  // Agrega el componente al contenedor; su posición la decide el diseño
        usuarios.add(new JLabel("Con servicio activo:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        usuarios.add(lblActivos);  // Agrega el componente al contenedor; su posición la decide el diseño
        usuarios.add(new JLabel("En deuda:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        usuarios.add(lblEnDeuda);  // Agrega el componente al contenedor; su posición la decide el diseño
        usuarios.add(new JLabel("Suspendidos:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        usuarios.add(lblSuspendidos);  // Agrega el componente al contenedor; su posición la decide el diseño
        usuarios.add(new JLabel("Cortados:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        usuarios.add(lblCortados);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(usuarios, "Resumen de usuarios", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        JPanel facturas = new JPanel(new GridLayout(0, 2, 6, 6));  // Declara «facturas» (JPanel) con el valor: new JPanel(new GridLayout(0, 2, 6, 6))
        facturas.add(new JLabel("Facturas emitidas:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        facturas.add(lblFacturas);  // Agrega el componente al contenedor; su posición la decide el diseño
        facturas.add(new JLabel("Pendientes:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        facturas.add(lblPendientes);  // Agrega el componente al contenedor; su posición la decide el diseño
        facturas.add(new JLabel("Vencidas:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        facturas.add(lblVencidas);  // Agrega el componente al contenedor; su posición la decide el diseño
        facturas.add(new JLabel("Pagadas:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        facturas.add(lblPagadas);  // Agrega el componente al contenedor; su posición la decide el diseño
        facturas.add(new JLabel("Total facturado:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        facturas.add(lblFacturado);  // Agrega el componente al contenedor; su posición la decide el diseño
        facturas.add(new JLabel("Cartera por cobrar:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        facturas.add(lblCartera);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(facturas, "Resumen de facturación", Estilo.VERDE);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        JPanel resumenes = new JPanel(new GridLayout(1, 2, 10, 0));  // Declara «resumenes» (JPanel) con el valor: new JPanel(new GridLayout(1, 2, 10, 0))
        resumenes.add(usuarios);  // Agrega el componente al contenedor; su posición la decide el diseño
        resumenes.add(facturas);  // Agrega el componente al contenedor; su posición la decide el diseño

        JButton emitir = Estilo.boton("Emitir facturación del periodo", Estilo.VERDE);  // Declara «emitir» (JButton) con el valor: Estilo.boton("Emitir facturación del periodo", ...
        emitir.addActionListener(e -> emitirFacturacion());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        JButton vencimientos = Estilo.boton("Revisar vencimientos", Estilo.NARANJA);  // Declara «vencimientos» (JButton) con el valor: Estilo.boton("Revisar vencimientos", Estilo.NAR...
        vencimientos.addActionListener(e -> revisarVencimientos());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));  // Declara «acciones» (JPanel) con el valor: new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6))
        acciones.add(emitir);  // Agrega el componente al contenedor; su posición la decide el diseño
        acciones.add(vencimientos);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(acciones, "Acciones", Estilo.GRIS);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        JPanel novedades = new JPanel(new BorderLayout());  // Declara «novedades» (JPanel) con el valor: new JPanel(new BorderLayout())
        novedades.add(Estilo.desplazable(tablaNovedades), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(novedades, "Usuarios con novedades (servicio en deuda, suspendido o cortado)", Estilo.ROJO);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        JPanel arriba = new JPanel(new BorderLayout(0, 8));  // Declara «arriba» (JPanel) con el valor: new JPanel(new BorderLayout(0, 8))
        arriba.add(resumenes, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        arriba.add(acciones, BorderLayout.SOUTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        JPanel centro = new JPanel(new BorderLayout(0, 8));  // Declara «centro» (JPanel) con el valor: new JPanel(new BorderLayout(0, 8))
        centro.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));  // Asigna el borde (margen interno o línea) del componente
        centro.add(arriba, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        centro.add(novedades, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        return centro;  // Devuelve el valor de centro
    }

    // ===================================================================== apertura de formularios
    private void abrirUsuarios() {  // Método abrirUsuarios (devuelve void)
        abrir("usuarios", () -> new FrmUsuarios(cUsuarios));  // Ejecuta la operación «abrir» (definida en esta clase o en un objeto colaborador)
    }

    private void abrirConsumos() {  // Método abrirConsumos (devuelve void)
        abrir("consumos", () -> new FrmConsumos(cUsuarios, cConsumos));  // Ejecuta la operación «abrir» (definida en esta clase o en un objeto colaborador)
    }

    private void abrirTarifas() {  // Método abrirTarifas (devuelve void)
        abrir("tarifas", () -> new FrmTarifas(cTarifas));  // Ejecuta la operación «abrir» (definida en esta clase o en un objeto colaborador)
    }

    private void abrirFacturacion() {  // Método abrirFacturacion (devuelve void)
        abrir("facturacion", () -> new FrmFacturacion(cUsuarios, cFacturacion));  // Ejecuta la operación «abrir» (definida en esta clase o en un objeto colaborador)
    }

    private void abrirSanciones() {  // Método abrirSanciones (devuelve void)
        abrir("sanciones", () -> new FrmSanciones(cUsuarios, cSanciones));  // Ejecuta la operación «abrir» (definida en esta clase o en un objeto colaborador)
    }

    private void abrirExcepciones() {  // Método abrirExcepciones (devuelve void)
        abrir("excepciones", () -> new FrmExcepciones(cUsuarios, cExcepciones));  // Ejecuta la operación «abrir» (definida en esta clase o en un objeto colaborador)
    }

    private void abrirNotificaciones() {  // Método abrirNotificaciones (devuelve void)
        abrir("notificaciones", () -> new FrmNotificaciones(cUsuarios, cNotificaciones));  // Ejecuta la operación «abrir» (definida en esta clase o en un objeto colaborador)
    }

    private void abrirReportes() {  // Método abrirReportes (devuelve void)
        abrir("reportes", () -> new FrmReportes(cReportes));  // Ejecuta la operación «abrir» (definida en esta clase o en un objeto colaborador)
    }

    /** Abre un formulario; si ya está abierto lo trae al frente y lo refresca. Al cerrarse actualiza el resumen. */
    private void abrir(String clave, Supplier<FrmBase> creador) {  // Método abrir: Abre un formulario; si ya está abierto lo trae al frente y lo refresca
        FrmBase existente = abiertas.get(clave);  // Declara «existente» (FrmBase) con el valor: abiertas.get(clave)
        if (existente != null && existente.isDisplayable()) { // Ya está abierto: se reutiliza.
            existente.actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
            existente.toFront();  // Ejecuta la operación «to front» (definida en esta clase o en un objeto colaborador)
            return;  // Termina el método sin devolver valor
        }
        FrmBase nuevo = creador.get();  // Declara «nuevo» (FrmBase) con el valor: creador.get()
        nuevo.addWindowListener(new WindowAdapter() { // Al cerrar, el resumen se pone al día.
            @Override
            public void windowClosed(WindowEvent e) {  // Método windowClosed (devuelve void)
                actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
            }
        });  // Cierra la llamada y el bloque (manejador o expresión lambda) abierto en las líneas anteriores
        abiertas.put(clave, nuevo);  // Guarda el valor asociado a la clave
        nuevo.setVisible(true);  // Muestra u oculta el componente
    }

    /** Relee los indicadores y redibuja el resumen. */
    public final void actualizar() {  // Método actualizar: Relee los indicadores y redibuja el resumen
        ControladorPanel.Indicadores i = cPanel.indicadores();  // Ejecuta: ControladorPanel.Indicadores i = cPanel.indicadores()
        lblUsuarios.setText(String.valueOf(i.usuarios));  // Escribe el texto que muestra el componente
        lblActivos.setText(String.valueOf(i.activos));  // Escribe el texto que muestra el componente
        lblEnDeuda.setText(String.valueOf(i.enDeuda));  // Escribe el texto que muestra el componente
        lblSuspendidos.setText(String.valueOf(i.suspendidos));  // Escribe el texto que muestra el componente
        lblCortados.setText(String.valueOf(i.cortados));  // Escribe el texto que muestra el componente
        lblFacturas.setText(String.valueOf(i.facturas));  // Escribe el texto que muestra el componente
        lblPendientes.setText(String.valueOf(i.pendientes));  // Escribe el texto que muestra el componente
        lblVencidas.setText(String.valueOf(i.vencidas));  // Escribe el texto que muestra el componente
        lblPagadas.setText(String.valueOf(i.pagadas));  // Escribe el texto que muestra el componente
        lblFacturado.setText(Formato.moneda(i.facturado));  // Escribe el texto que muestra el componente
        lblCartera.setText(Formato.moneda(i.cartera));  // Escribe el texto que muestra el componente
        java.util.List<Usuario> conNovedad = new java.util.ArrayList<>();  // Ejecuta: java.util.List<Usuario> conNovedad = new java.util.ArrayL...
        for (Usuario u : cUsuarios.listar()) { // Usuarios cuyo servicio no está activo.
            if (u.getEstadoServicio() != modelo.EstadoServicio.ACTIVO) {  // Condición: si u.getEstadoServicio() es distinto de modelo.EstadoServicio.ACTIVO
                conNovedad.add(u);  // Agrega el componente al contenedor; su posición la decide el diseño
            }
        }
        modeloNovedades.setFilas(conNovedad);  // Entrega las filas a la tabla para que se redibuje
        for (FrmBase f : abiertas.values()) { // Mantiene al día los formularios abiertos.
            if (f.isDisplayable()) {  // Condición: si f.isDisplayable()
                f.actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
            }
        }
    }

    /** Emite la facturación del periodo y resume el resultado. */
    private void emitirFacturacion() {  // Método emitirFacturacion: Emite la facturación del periodo y resume el resultado
        Resultado<ControladorFacturacion.ResumenFacturacion> r = cFacturacion.emitir();  // Ejecuta: Resultado<ControladorFacturacion.ResumenFacturacion> r = ...
        if (!r.isExito()) {  // Condición: si no r.isExito()
            JOptionPane.showMessageDialog(this, r.getMensaje(), "Facturación", JOptionPane.WARNING_MESSAGE);  // Ejecuta la operación «show message dialog» (definida en esta clase o en un objeto colaborador)
        } else {  // Alternativa: se ejecuta cuando ninguna condición anterior se cumplió
            ControladorFacturacion.ResumenFacturacion s = r.getValor();  // Ejecuta: ControladorFacturacion.ResumenFacturacion s = r.getValor()
            StringBuilder texto = new StringBuilder(s.getEmitidas() + " factura(s) emitida(s).");  // Declara «texto» (StringBuilder) con el valor: new StringBuilder(s.getEmitidas() + " factura(s...
            for (String o : s.getOmitidos()) { // Cada usuario omitido queda explicado.
                texto.append("\nOmitido ").append(o);  // Ejecuta la operación «append» (definida en esta clase o en un objeto colaborador)
            }
            JOptionPane.showMessageDialog(this, texto.toString(), "Facturación",  // Ejecuta la operación «show message dialog» (definida en esta clase o en un objeto colaborador)
                    s.getOmitidos().isEmpty() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);  // Continuación de la instrucción de la línea anterior.
        }
        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
    }

    /** Revisa los vencimientos de todas las facturas. */
    private void revisarVencimientos() {  // Método revisarVencimientos: Revisa los vencimientos de todas las facturas
        Resultado<Integer> r = cFacturacion.revisarVencimientos();  // Declara «r» (Resultado<Integer>) con el valor: cFacturacion.revisarVencimientos()
        JOptionPane.showMessageDialog(this, r.isExito() ? r.getValor() + " factura(s) pasaron a vencida(s)." : r.getMensaje(),  // Ejecuta la operación «show message dialog» (definida en esta clase o en un objeto colaborador)
                "Vencimientos", r.isExito() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);  // Continúa el texto de la instrucción anterior.
        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
    }
}
