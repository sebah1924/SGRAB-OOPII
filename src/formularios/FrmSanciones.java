package formularios; // Paquete de los formularios.

import controladores.ControladorSanciones;  // Importa controladores.ControladorSanciones
import controladores.ControladorUsuarios;  // Importa controladores.ControladorUsuarios
import controladores.Resultado;  // Importa controladores.Resultado
import java.awt.*;  // Importa java.awt.*
import java.util.function.Function;  // Importa java.util.function.Function
import javax.swing.*;  // Importa javax.swing.*
import modelo.*;  // Importa modelo.*

/**
 * Formulario de sanciones por derroche de agua (RF-21 y RF-22). La multa se calcula al elegir el número de salarios
 * mínimos diarios y el ciclo de vida se ve como una línea de estados donde se resalta el estado actual.
 */
public class FrmSanciones extends FrmBase {  // Declaración de la clase FrmSanciones: Formulario de sanciones por derroche de agua (RF-21 y RF-22)

    private final ControladorSanciones controlador;  // Atributo «controlador» de tipo ControladorSanciones
    private final SelectorUsuario selector;  // Atributo «selector» de tipo SelectorUsuario
    private final JComboBox<TipoSancion> cmbTipo = new JComboBox<>(TipoSancion.values());  // Atributo «cmbTipo» de tipo JComboBox<TipoSancion>
    private final JSpinner spSmdlv = new JSpinner(new SpinnerNumberModel(10, 1, Sancion.MAXIMO_SMDLV, 1));  // Atributo «spSmdlv» de tipo JSpinner
    private final JLabel lblMulta = Estilo.negrita(" ");  // Atributo «lblMulta» de tipo JLabel
    private final JTextArea txtDescripcion = new JTextArea(3, 20);  // Atributo «txtDescripcion» de tipo JTextArea
    private final ModeloTablaGenerico<Sancion> modelo = new ModeloTablaGenerico<Sancion>()  // Instrucción: private final ModeloTablaGenerico<Sancion> modelo = new M...
            .columna("Código", Sancion::getCodigoSancion)  // Encadena la siguiente llamada de la misma instrucción: .columna("Código", Sancion::getCodigoSancion)
            .columna("Usuario", s -> s.getUsuario().getNombreCompleto())  // Encadena la siguiente llamada de la misma instrucción: .columna("Usuario", s -> s.getUsuario().getNombreCompleto())
            .columna("Tipo", s -> s.getTipo().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Tipo", s -> s.getTipo().name())
            .columna("Multa", s -> Formato.moneda(s.getValorMulta()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Multa", s -> Formato.moneda(s.getValorMulta()))
            .columna("Estado", s -> s.getEstado().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Estado", s -> s.getEstado().name())
            .columna("Impuesta", s -> Formato.fecha(s.getFechaImposicion()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Impuesta", s -> Formato.fecha(s.getFechaImposic...
            .columna("Descripción", Sancion::getDescripcion);  // Encadena la siguiente llamada de la misma instrucción: .columna("Descripción", Sancion::getDescripcion);
    private final JTable tabla = Estilo.crearTabla(modelo);  // Atributo «tabla» de tipo JTable
    private final JPanel linea = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4)); // Línea visual del ciclo de vida.

    /** Construye el formulario. */
    public FrmSanciones(ControladorUsuarios usuarios, ControladorSanciones controlador) {  // Constructor: Construye el formulario
        super("Sanciones", "Decreto 137 de 2024: multas, suspensión parcial y cursos pedagógicos", Estilo.ROJO, 1150, 650);  // Llama al constructor de la clase base para iniciar la parte heredada
        this.controlador = controlador;  // Asigna a controlador el valor: controlador
        this.selector = new SelectorUsuario(usuarios);  // Asigna a selector el valor: new SelectorUsuario(usuarios)
        Estilo.columnaDeEstado(tabla, 4);  // Ejecuta la operación «columna de estado» (definida en esta clase o en un objeto colaborador)
        tabla.getSelectionModel().addListSelectionListener(e -> {  // Registra el manejador que reacciona a la selección de filas
            if (!e.getValueIsAdjusting()) {  // Condición: si no e.getValueIsAdjusting()
                dibujarLinea();  // Ejecuta la operación «dibujar linea» (definida en esta clase o en un objeto colaborador)
            }
        });  // Cierra la llamada y el bloque (manejador o expresión lambda) abierto en las líneas anteriores

        PanelFormulario f = new PanelFormulario();  // Declara «f» (PanelFormulario) con el valor: new PanelFormulario()
        f.agregar("Usuario", selector);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Tipo de sanción", cmbTipo);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Salarios mín. diarios", spSmdlv);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregarAncho(lblMulta);  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        txtDescripcion.setLineWrap(true);  // Activa el salto de línea automático
        txtDescripcion.setWrapStyleWord(true);  // Hace que el salto de línea respete palabras completas
        f.agregar("Conducta", new JScrollPane(txtDescripcion));  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        JButton imponer = Estilo.boton("Imponer sanción", Estilo.ROJO);  // Declara «imponer» (JButton) con el valor: Estilo.boton("Imponer sanción", Estilo.ROJO)
        imponer.addActionListener(e -> imponer());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        f.agregarAncho(imponer);  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        f.agregarAncho(new JLabel("<html>La suspensión parcial solo aplica a establecimientos<br>reincidentes y nunca a usuarios exentos.</html>"));  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        JPanel grupoForm = new JPanel(new BorderLayout());  // Declara «grupoForm» (JPanel) con el valor: new JPanel(new BorderLayout())
        grupoForm.add(f.ancladoArriba(), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(grupoForm, "Imponer sanción", Estilo.ROJO);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        grupoForm.setPreferredSize(new Dimension(410, 0));  // Sugiere al diseño el tamaño preferido del componente

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));  // Declara «acciones» (JPanel) con el valor: new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4))
        acciones.add(accion("Apelar", Estilo.NARANJA, controlador::apelar));  // Agrega el componente al contenedor; su posición la decide el diseño
        acciones.add(accion("Confirmar", Estilo.CELESTE, controlador::confirmar));  // Agrega el componente al contenedor; su posición la decide el diseño
        acciones.add(accion("Revocar", Estilo.GRIS, controlador::revocar));  // Agrega el componente al contenedor; su posición la decide el diseño
        acciones.add(accion("Ejecutar", Estilo.ROJO, controlador::ejecutarSancion));  // Agrega el componente al contenedor; su posición la decide el diseño
        JPanel sur = new JPanel(new BorderLayout());  // Declara «sur» (JPanel) con el valor: new JPanel(new BorderLayout())
        sur.add(linea, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        sur.add(acciones, BorderLayout.SOUTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        JPanel grupoLista = new JPanel(new BorderLayout(0, 6));  // Declara «grupoLista» (JPanel) con el valor: new JPanel(new BorderLayout(0, 6))
        grupoLista.add(Estilo.desplazable(tabla), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        grupoLista.add(sur, BorderLayout.SOUTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(grupoLista, "Sanciones registradas", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        contenido.add(grupoForm, BorderLayout.WEST);  // Agrega el componente al contenedor; su posición la decide el diseño
        contenido.add(grupoLista, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        spSmdlv.addChangeListener(e -> calcularMultaVivo());  // Registra el manejador que se ejecuta cuando cambia el valor
        cmbTipo.addActionListener(e -> calcularMultaVivo());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        calcularMultaVivo();  // Ejecuta la operación «calcular multa vivo» (definida en esta clase o en un objeto colaborador)
        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
    }

    /** Crea un botón que aplica una transición de estado a la sanción seleccionada. */
    private JButton accion(String texto, Color color, Function<Sancion, Resultado<Sancion>> operacion) {  // Método accion: Crea un botón que aplica una transición de estado a la sanción seleccionada
        JButton b = Estilo.boton(texto, color);  // Declara «b» (JButton) con el valor: Estilo.boton(texto, color)
        b.addActionListener(e -> {  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
            Sancion s = seleccionado(tabla, modelo);  // Declara «s» (Sancion) con el valor: seleccionado(tabla, modelo)
            if (s == null) {  // Condición: si s es igual a nulo
                avisar("Selecciona primero una sanción de la tabla.", false);  // Muestra un aviso emergente al usuario
                return;  // Termina el método sin devolver valor
            }
            mostrar(operacion.apply(s));  // Muestra al usuario el mensaje del resultado de la operación
            actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
        });  // Cierra la llamada y el bloque (manejador o expresión lambda) abierto en las líneas anteriores
        return b;  // Devuelve el valor de b
    }

    /** Muestra el valor de la multa mientras se elige el número de salarios mínimos. */
    private void calcularMultaVivo() {  // Método calcularMultaVivo: Muestra el valor de la multa mientras se elige el número de salarios mínimos
        boolean economica = cmbTipo.getSelectedItem() == TipoSancion.ECONOMICA;  // Declara «economica» (boolean) con el valor: cmbTipo.getSelectedItem() es igual a TipoSancio...
        spSmdlv.setEnabled(economica);  // Habilita o deshabilita el componente
        lblMulta.setForeground(Estilo.AZUL);  // Define el color del texto o primer plano
        lblMulta.setText(economica ? "Multa: " + Formato.moneda(controlador.calcularMulta((Integer) spSmdlv.getValue()))  // Escribe el texto que muestra el componente
                : "Esta sanción no lleva multa.");  // Continuación de la instrucción de la línea anterior.
    }

    /** Dibuja el ciclo de vida marcando el estado de la sanción seleccionada. */
    private void dibujarLinea() {  // Método dibujarLinea: Dibuja el ciclo de vida marcando el estado de la sanción seleccionada
        linea.removeAll();  // Quita todos los componentes hijos
        Sancion s = seleccionado(tabla, modelo);  // Declara «s» (Sancion) con el valor: seleccionado(tabla, modelo)
        EstadoSancion actual = s == null ? null : s.getEstado();  // Declara «actual» (EstadoSancion) con el valor: s es igual a nulo ? nulo : s.getEstado()
        linea.add(new JLabel("Ciclo de vida:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        EstadoSancion[] camino = {EstadoSancion.IMPUESTA, EstadoSancion.APELADA, EstadoSancion.CONFIRMADA, EstadoSancion.EJECUTADA, EstadoSancion.REVOCADA};  // Declara «camino» (EstadoSancion[]) con el valor: {EstadoSancion.IMPUESTA, EstadoSancion.APELADA,...
        for (EstadoSancion e : camino) {  // Recorre cada elemento «e» de camino
            JLabel chip = Estilo.chip(e.name());  // Declara «chip» (JLabel) con el valor: Estilo.chip(e.name())
            if (actual != null && actual != e) { // Los estados que no son el actual se atenúan.
                chip.setBackground(Color.LIGHT_GRAY);  // Define el color de fondo
            }
            linea.add(chip);  // Agrega el componente al contenedor; su posición la decide el diseño
            if (e != EstadoSancion.EJECUTADA && e != EstadoSancion.REVOCADA) {  // Condición: si e es distinto de EstadoSancion.EJECUTADA y e es distinto de EstadoSancio...
                linea.add(new JLabel("→"));  // Agrega el componente al contenedor; su posición la decide el diseño
            }
        }
        linea.revalidate();  // Pide recalcular el diseño del contenedor
        linea.repaint();  // Pide redibujar el componente
    }

    /** Recarga la tabla. */
    @Override
    public final void actualizar() {  // Método actualizar: Recarga la tabla
        modelo.setFilas(controlador.listarTodas());  // Entrega las filas a la tabla para que se redibuje
        dibujarLinea();  // Ejecuta la operación «dibujar linea» (definida en esta clase o en un objeto colaborador)
    }

    /** Impone la sanción con los datos del formulario (RF-21). */
    private void imponer() {  // Método imponer: Impone la sanción con los datos del formulario (RF-21)
        Usuario u = selector.getUsuario();  // Declara «u» (Usuario) con el valor: selector.getUsuario()
        if (u == null) {  // Condición: si u es igual a nulo
            avisar("Selecciona primero un usuario.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        Resultado<Sancion> r = controlador.imponer(u, (TipoSancion) cmbTipo.getSelectedItem(), (Integer) spSmdlv.getValue(),  // Declara «r» (Resultado<Sancion>) con el valor: controlador.imponer(u, (TipoSancion) cmbTipo.ge...
                txtDescripcion.getText());  // Continuación de la instrucción de la línea anterior.
        mostrar(r);  // Muestra al usuario el mensaje del resultado de la operación
        if (r.isExito()) {  // Condición: si r.isExito()
            txtDescripcion.setText("");  // Escribe el texto que muestra el componente
            actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
        }
    }
}
