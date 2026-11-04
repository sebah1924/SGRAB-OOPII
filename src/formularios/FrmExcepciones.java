package formularios; // Paquete de los formularios.

import controladores.ControladorBase;  // Importa controladores.ControladorBase
import controladores.ControladorExcepciones;  // Importa controladores.ControladorExcepciones
import controladores.ControladorUsuarios;  // Importa controladores.ControladorUsuarios
import controladores.Resultado;  // Importa controladores.Resultado
import java.awt.*;  // Importa java.awt.*
import javax.swing.*;  // Importa javax.swing.*
import modelo.*;  // Importa modelo.*

/**
 * Formulario de excepciones al racionamiento (RF-12 y RF-13): registra las categorías exentas con su fundamento
 * legal y las asigna a los usuarios que no pueden ser cortados ni suspendidos.
 */
public class FrmExcepciones extends FrmBase {  // Declaración de la clase FrmExcepciones: Formulario de excepciones al racionamiento (RF-12 y RF-13):

    private final ControladorExcepciones controlador;  // Atributo «controlador» de tipo ControladorExcepciones
    private final ControladorUsuarios usuarios;  // Atributo «usuarios» de tipo ControladorUsuarios
    private final SelectorUsuario selector;  // Atributo «selector» de tipo SelectorUsuario
    private final JTextField txtCodigo = Estilo.campo(14);  // Atributo «txtCodigo» de tipo JTextField
    private final JTextField txtDescripcion = Estilo.campo(14);  // Atributo «txtDescripcion» de tipo JTextField
    private final JComboBox<CategoriaExenta> cmbCategoria = new JComboBox<>(CategoriaExenta.values());  // Atributo «cmbCategoria» de tipo JComboBox<CategoriaExenta>
    private final JTextArea txtJustificacion = new JTextArea(4, 20);  // Atributo «txtJustificacion» de tipo JTextArea
    private final JLabel lblFundamento = new JLabel(" ");  // Atributo «lblFundamento» de tipo JLabel
    private final JLabel lblEstadoUsuario = Estilo.negrita(" ");  // Atributo «lblEstadoUsuario» de tipo JLabel
    private final ModeloTablaGenerico<Excepcion> modelo = new ModeloTablaGenerico<Excepcion>()  // Instrucción: private final ModeloTablaGenerico<Excepcion> modelo = new...
            .columna("Código", Excepcion::getCodigo)  // Encadena la siguiente llamada de la misma instrucción: .columna("Código", Excepcion::getCodigo)
            .columna("Descripción", Excepcion::getDescripcion)  // Encadena la siguiente llamada de la misma instrucción: .columna("Descripción", Excepcion::getDescripcion)
            .columna("Categoría", e -> e.getCategoriaExenta().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Categoría", e -> e.getCategoriaExenta().name())
            .columna("Constitucional", e -> e.isEsProteccionConstitucional() ? "Sí" : "No")  // Encadena la siguiente llamada de la misma instrucción: .columna("Constitucional", e -> e.isEsProteccionConstituc...
            .columna("Fundamento legal", Excepcion::obtenerFundamentoLegal);  // Encadena la siguiente llamada de la misma instrucción: .columna("Fundamento legal", Excepcion::obtenerFundamento...
    private final JTable tabla = Estilo.crearTabla(modelo);  // Atributo «tabla» de tipo JTable

    /** Construye el formulario. */
    public FrmExcepciones(ControladorUsuarios usuarios, ControladorExcepciones controlador) {  // Constructor: Construye el formulario
        super("Excepciones", "Usuarios que no pueden ser cortados ni suspendidos (Decreto 137 de 2024)", Estilo.NARANJA, 1150, 680);  // Llama al constructor de la clase base para iniciar la parte heredada
        this.controlador = controlador;  // Asigna a controlador el valor: controlador
        this.usuarios = usuarios;  // Asigna a usuarios el valor: usuarios
        this.selector = new SelectorUsuario(usuarios);  // Asigna a selector el valor: new SelectorUsuario(usuarios)
        txtCodigo.setText(ControladorBase.generarCodigo("EXC"));  // Escribe el texto que muestra el componente

        PanelFormulario f = new PanelFormulario();  // Declara «f» (PanelFormulario) con el valor: new PanelFormulario()
        f.agregar("Código", txtCodigo);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Descripción", txtDescripcion);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Categoría exenta", cmbCategoria);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregarAncho(lblFundamento);  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        txtJustificacion.setLineWrap(true);  // Activa el salto de línea automático
        txtJustificacion.setWrapStyleWord(true);  // Hace que el salto de línea respete palabras completas
        f.agregar("Justificación legal", new JScrollPane(txtJustificacion));  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        JButton registrar = Estilo.boton("Registrar excepción", Estilo.VERDE);  // Declara «registrar» (JButton) con el valor: Estilo.boton("Registrar excepción", Estilo.VERDE)
        registrar.addActionListener(e -> registrar());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        f.agregarAncho(registrar);  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        JPanel grupoForm = new JPanel(new BorderLayout());  // Declara «grupoForm» (JPanel) con el valor: new JPanel(new BorderLayout())
        grupoForm.add(f.ancladoArriba(), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(grupoForm, "Nueva excepción", Estilo.NARANJA);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        grupoForm.setPreferredSize(new Dimension(420, 0));  // Sugiere al diseño el tamaño preferido del componente
        cmbCategoria.addActionListener(e -> mostrarFundamento());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        mostrarFundamento();  // Ejecuta la operación «mostrar fundamento» (definida en esta clase o en un objeto colaborador)

        PanelFormulario asignar = new PanelFormulario();  // Declara «asignar» (PanelFormulario) con el valor: new PanelFormulario()
        asignar.agregar("Usuario", selector);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        asignar.agregarAncho(lblEstadoUsuario);  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        JButton asignarBtn = Estilo.boton("Asignar excepción seleccionada", Estilo.VERDE);  // Declara «asignarBtn» (JButton) con el valor: Estilo.boton("Asignar excepción seleccionada", ...
        asignarBtn.addActionListener(e -> asignar());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        JButton quitar = Estilo.boton("Retirar excepción", Estilo.ROJO);  // Declara «quitar» (JButton) con el valor: Estilo.boton("Retirar excepción", Estilo.ROJO)
        quitar.addActionListener(e -> quitar());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        JPanel botones = new JPanel(new GridLayout(1, 2, 8, 0));  // Declara «botones» (JPanel) con el valor: new JPanel(new GridLayout(1, 2, 8, 0))
        botones.add(asignarBtn);  // Agrega el componente al contenedor; su posición la decide el diseño
        botones.add(quitar);  // Agrega el componente al contenedor; su posición la decide el diseño
        asignar.agregarAncho(botones);  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        JPanel grupoAsignar = new JPanel(new BorderLayout());  // Declara «grupoAsignar» (JPanel) con el valor: new JPanel(new BorderLayout())
        grupoAsignar.add(asignar, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(grupoAsignar, "Asignar a un usuario (elige antes una excepción en la tabla)", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        JPanel grupoLista = new JPanel(new BorderLayout());  // Declara «grupoLista» (JPanel) con el valor: new JPanel(new BorderLayout())
        grupoLista.add(Estilo.desplazable(tabla), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(grupoLista, "Catálogo de excepciones", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        JPanel derecha = new JPanel(new BorderLayout(0, 8));  // Declara «derecha» (JPanel) con el valor: new JPanel(new BorderLayout(0, 8))
        derecha.add(grupoLista, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        derecha.add(grupoAsignar, BorderLayout.SOUTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        contenido.add(grupoForm, BorderLayout.WEST);  // Agrega el componente al contenedor; su posición la decide el diseño
        contenido.add(derecha, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        selector.alCambiar(this::mostrarEstadoUsuario);  // Ejecuta la operación «al cambiar» (definida en esta clase o en un objeto colaborador)
        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
    }

    /** Muestra el fundamento legal de la categoría elegida (lo calcula el propio modelo). */
    private void mostrarFundamento() {  // Método mostrarFundamento: Muestra el fundamento legal de la categoría elegida (lo calcula el propio modelo)
        CategoriaExenta c = (CategoriaExenta) cmbCategoria.getSelectedItem();  // Declara «c» (CategoriaExenta) con el valor: (CategoriaExenta) cmbCategoria.getSelectedItem()
        Excepcion ejemplo = new Excepcion("X", "x", c, "x", false); // Objeto desechable solo para consultar el fundamento.
        lblFundamento.setText("<html>" + ejemplo.obtenerFundamentoLegal() + "</html>");  // Escribe el texto que muestra el componente
    }

    /** Muestra si el usuario elegido está protegido y por cuál excepción. */
    private void mostrarEstadoUsuario() {  // Método mostrarEstadoUsuario: Muestra si el usuario elegido está protegido y por cuál excepción
        Usuario u = selector.getUsuario();  // Declara «u» (Usuario) con el valor: selector.getUsuario()
        if (u == null) {  // Condición: si u es igual a nulo
            lblEstadoUsuario.setText(" ");  // Escribe el texto que muestra el componente
            return;  // Termina el método sin devolver valor
        }
        boolean exento = usuarios.esExento(u);  // Declara «exento» (boolean) con el valor: usuarios.esExento(u)
        lblEstadoUsuario.setForeground(exento ? Estilo.VERDE : Color.DARK_GRAY);  // Define el color del texto o primer plano
        lblEstadoUsuario.setText(exento ? "Protegido: no puede ser cortado ni suspendido"  // Escribe el texto que muestra el componente
                + (u.getExcepcion() != null ? " (" + u.getExcepcion().getCodigo() + ")" : " (usuario oficial)")  // Continúa el texto de la instrucción anterior.
                : "Sin excepción: puede ser cortado o suspendido");  // Continuación de la instrucción de la línea anterior.
    }

    /** Recarga el catálogo. */
    @Override
    public final void actualizar() {  // Método actualizar: Recarga el catálogo
        modelo.setFilas(controlador.listar());  // Entrega las filas a la tabla para que se redibuje
        selector.recargar();  // Ejecuta la operación «recargar» (definida en esta clase o en un objeto colaborador)
        mostrarEstadoUsuario();  // Ejecuta la operación «mostrar estado usuario» (definida en esta clase o en un objeto colaborador)
    }

    /** Registra la excepción (RF-12). */
    private void registrar() {  // Método registrar: Registra la excepción (RF-12)
        Resultado<Excepcion> r = controlador.registrar(txtCodigo.getText().trim(), txtDescripcion.getText(),  // Declara «r» (Resultado<Excepcion>) con el valor: controlador.registrar(txtCodigo.getText().trim(...
                (CategoriaExenta) cmbCategoria.getSelectedItem(), txtJustificacion.getText());  // Continuación de la instrucción de la línea anterior.
        mostrar(r);  // Muestra al usuario el mensaje del resultado de la operación
        if (r.isExito()) {  // Condición: si r.isExito()
            txtCodigo.setText(ControladorBase.generarCodigo("EXC"));  // Escribe el texto que muestra el componente
            txtDescripcion.setText("");  // Escribe el texto que muestra el componente
            txtJustificacion.setText("");  // Escribe el texto que muestra el componente
            actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
        }
    }

    /** Asigna la excepción seleccionada al usuario elegido. */
    private void asignar() {  // Método asignar: Asigna la excepción seleccionada al usuario elegido
        Usuario u = selector.getUsuario();  // Declara «u» (Usuario) con el valor: selector.getUsuario()
        Excepcion e = seleccionado(tabla, modelo);  // Declara «e» (Excepcion) con el valor: seleccionado(tabla, modelo)
        if (u == null || e == null) {  // Condición: si u es igual a nulo o e es igual a nulo
            avisar("Elige un usuario y una excepción de la tabla.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        mostrar(controlador.asignar(u, e));  // Muestra al usuario el mensaje del resultado de la operación
        mostrarEstadoUsuario();  // Ejecuta la operación «mostrar estado usuario» (definida en esta clase o en un objeto colaborador)
    }

    /** Retira la excepción del usuario elegido. */
    private void quitar() {  // Método quitar: Retira la excepción del usuario elegido
        Usuario u = selector.getUsuario();  // Declara «u» (Usuario) con el valor: selector.getUsuario()
        if (u == null) {  // Condición: si u es igual a nulo
            avisar("Elige un usuario.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        mostrar(controlador.quitar(u));  // Muestra al usuario el mensaje del resultado de la operación
        mostrarEstadoUsuario();  // Ejecuta la operación «mostrar estado usuario» (definida en esta clase o en un objeto colaborador)
    }
}
