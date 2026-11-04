package formularios; // Paquete de los formularios.

import controladores.ControladorBase;  // Importa controladores.ControladorBase
import controladores.ControladorConsumos;  // Importa controladores.ControladorConsumos
import controladores.ControladorUsuarios;  // Importa controladores.ControladorUsuarios
import controladores.Resultado;  // Importa controladores.Resultado
import java.awt.*;  // Importa java.awt.*
import java.math.BigDecimal;  // Importa java.math.BigDecimal
import java.time.LocalDate;  // Importa java.time.LocalDate
import javax.swing.*;  // Importa javax.swing.*
import javax.swing.event.DocumentEvent;  // Importa javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener;  // Importa javax.swing.event.DocumentListener
import modelo.*;  // Importa modelo.*

/**
 * Formulario de lecturas de consumo (RF-15). Muestra, mientras se escribe, la tarifa vigente que se aplicará y una
 * estimación de lo que se cobrará (con subsidio o contribución), para que no haya sorpresas.
 */
public class FrmConsumos extends FrmBase {  // Declaración de la clase FrmConsumos: Formulario de lecturas de consumo (RF-15)

    private final ControladorConsumos controlador;  // Atributo «controlador» de tipo ControladorConsumos
    private final SelectorUsuario selector;                                  // Elección del usuario.
    private final JTextField txtCodigo = Estilo.campo(14);  // Atributo «txtCodigo» de tipo JTextField
    private final JSpinner spVolumen = new JSpinner(new SpinnerNumberModel(10.0, 0.0, 100000.0, 0.5));  // Atributo «spVolumen» de tipo JSpinner
    private final CampoFecha fecha = new CampoFecha(LocalDate.now().minusDays(1));  // Atributo «fecha» de tipo CampoFecha
    private final JComboBox<TipoLectura> cmbLectura = new JComboBox<>(TipoLectura.values());  // Atributo «cmbLectura» de tipo JComboBox<TipoLectura>
    private final JLabel lblTarifa = new JLabel(" ");                        // Tarifa vigente detectada.
    private final JLabel lblEstimado = Estilo.negrita(" ");                  // Cobro estimado.
    private final JLabel lblPromedio = Estilo.negrita("Consumo promedio: -");  // Atributo «lblPromedio» de tipo JLabel
    private final ModeloTablaGenerico<Consumo> modelo = new ModeloTablaGenerico<Consumo>()  // Instrucción: private final ModeloTablaGenerico<Consumo> modelo = new M...
            .columna("Código", Consumo::getCodigo)  // Encadena la siguiente llamada de la misma instrucción: .columna("Código", Consumo::getCodigo)
            .columna("Fecha", c -> Formato.fecha(c.getFechaLectura()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Fecha", c -> Formato.fecha(c.getFechaLectura()))
            .columna("Volumen (m3)", Consumo::getVolumenM3)  // Encadena la siguiente llamada de la misma instrucción: .columna("Volumen (m3)", Consumo::getVolumenM3)
            .columna("Lectura", c -> c.getTipoLectura().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Lectura", c -> c.getTipoLectura().name())
            .columna("Tarifa", c -> c.getTarifaAplicada().getCodigo())  // Encadena la siguiente llamada de la misma instrucción: .columna("Tarifa", c -> c.getTarifaAplicada().getCodigo())
            .columna("Costo base", c -> Formato.moneda(c.calcularCosto()));  // Encadena la siguiente llamada de la misma instrucción: .columna("Costo base", c -> Formato.moneda(c.calcularCost...
    private final JTable tabla = Estilo.crearTabla(modelo);  // Atributo «tabla» de tipo JTable

    /** Construye el formulario. */
    public FrmConsumos(ControladorUsuarios usuarios, ControladorConsumos controlador) {  // Constructor: Construye el formulario
        super("Consumos", "Registro de las lecturas mensuales; la tarifa vigente se detecta sola", Estilo.TURQUESA, 1050, 600);  // Llama al constructor de la clase base para iniciar la parte heredada
        this.controlador = controlador;  // Asigna a controlador el valor: controlador
        this.selector = new SelectorUsuario(usuarios);  // Asigna a selector el valor: new SelectorUsuario(usuarios)
        txtCodigo.setText(ControladorBase.generarCodigo("CON"));  // Escribe el texto que muestra el componente

        PanelFormulario f = new PanelFormulario();  // Declara «f» (PanelFormulario) con el valor: new PanelFormulario()
        f.agregar("Usuario", selector);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Código consumo", txtCodigo);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Volumen (m3)", spVolumen);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Fecha lectura", fecha);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Tipo de lectura", cmbLectura);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregarAncho(lblTarifa);  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        f.agregarAncho(lblEstimado);  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        JButton guardar = Estilo.boton("Registrar consumo", Estilo.VERDE);  // Declara «guardar» (JButton) con el valor: Estilo.boton("Registrar consumo", Estilo.VERDE)
        guardar.addActionListener(e -> registrar());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        f.agregarAncho(guardar);  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        JPanel grupoForm = new JPanel(new BorderLayout());  // Declara «grupoForm» (JPanel) con el valor: new JPanel(new BorderLayout())
        grupoForm.add(f.ancladoArriba(), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(grupoForm, "Nueva lectura", Estilo.TURQUESA);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        grupoForm.setPreferredSize(new Dimension(400, 0));  // Sugiere al diseño el tamaño preferido del componente

        JPanel grupoHistorial = new JPanel(new BorderLayout(0, 6));  // Declara «grupoHistorial» (JPanel) con el valor: new JPanel(new BorderLayout(0, 6))
        grupoHistorial.add(lblPromedio, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        grupoHistorial.add(Estilo.desplazable(tabla), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(grupoHistorial, "Historial de consumos del usuario", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        contenido.add(grupoForm, BorderLayout.WEST);  // Agrega el componente al contenedor; su posición la decide el diseño
        contenido.add(grupoHistorial, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño

        selector.alCambiar(this::actualizar); // Al elegir otro usuario se recarga todo.
        DocumentListener recalcular = new DocumentListener() { // Vista previa al cambiar la fecha.
            @Override
            public void insertUpdate(DocumentEvent e) {  // Método insertUpdate (devuelve void)
                vistaPrevia();  // Ejecuta la operación «vista previa» (definida en esta clase o en un objeto colaborador)
            }

            @Override
            public void removeUpdate(DocumentEvent e) {  // Método removeUpdate (devuelve void)
                vistaPrevia();  // Ejecuta la operación «vista previa» (definida en esta clase o en un objeto colaborador)
            }

            @Override
            public void changedUpdate(DocumentEvent e) {  // Método changedUpdate (devuelve void)
                vistaPrevia();  // Ejecuta la operación «vista previa» (definida en esta clase o en un objeto colaborador)
            }
        };
        fecha.getDocument().addDocumentListener(recalcular);  // Registra el manejador que reacciona a cada cambio del texto escrito
        spVolumen.addChangeListener(e -> vistaPrevia());  // Registra el manejador que se ejecuta cuando cambia el valor
        cmbLectura.addActionListener(e -> vistaPrevia());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
    }

    /** Recarga la tabla del usuario elegido y la vista previa. */
    @Override
    public final void actualizar() {  // Método actualizar: Recarga la tabla del usuario elegido y la vista previa
        Usuario u = selector.getUsuario();  // Declara «u» (Usuario) con el valor: selector.getUsuario()
        modelo.setFilas(controlador.listar(u));  // Entrega las filas a la tabla para que se redibuje
        lblPromedio.setText(u == null ? "Consumo promedio: -"  // Escribe el texto que muestra el componente
                : "Consumo promedio de " + u.getNombreCompleto() + ": " + String.format("%.2f", u.getConsumoPromedioM3()) + " m3");  // Continuación de la instrucción de la línea anterior.
        vistaPrevia();  // Ejecuta la operación «vista previa» (definida en esta clase o en un objeto colaborador)
    }

    /** Muestra la tarifa vigente detectada y el cobro estimado. */
    private void vistaPrevia() {  // Método vistaPrevia: Muestra la tarifa vigente detectada y el cobro estimado
        Usuario u = selector.getUsuario();  // Declara «u» (Usuario) con el valor: selector.getUsuario()
        LocalDate fe = fecha.getFecha();  // Declara «fe» (LocalDate) con el valor: fecha.getFecha()
        lblEstimado.setText(" ");  // Escribe el texto que muestra el componente
        if (u == null) {  // Condición: si u es igual a nulo
            lblTarifa.setForeground(Color.DARK_GRAY);  // Define el color del texto o primer plano
            lblTarifa.setText("Selecciona un usuario.");  // Escribe el texto que muestra el componente
            return;  // Termina el método sin devolver valor
        }
        if (fe == null) {  // Condición: si fe es igual a nulo
            lblTarifa.setForeground(Estilo.NARANJA);  // Define el color del texto o primer plano
            lblTarifa.setText("Escribe una fecha válida (AAAA-MM-DD).");  // Escribe el texto que muestra el componente
            return;  // Termina el método sin devolver valor
        }
        Tarifa t = controlador.tarifaVigente(u, fe);  // Declara «t» (Tarifa) con el valor: controlador.tarifaVigente(u, fe)
        if (t == null) {  // Condición: si t es igual a nulo
            lblTarifa.setForeground(Estilo.ROJO);  // Define el color del texto o primer plano
            lblTarifa.setText("<html>No hay tarifa " + u.getTipoUsuario() + " vigente el " + fe + ".<br>Regístrala primero en Tarifas.</html>");  // Escribe el texto que muestra el componente
            return;  // Termina el método sin devolver valor
        }
        lblTarifa.setForeground(Estilo.VERDE);  // Define el color del texto o primer plano
        lblTarifa.setText("<html>Tarifa vigente: " + t.getCodigo() + "<br>Fijo " + Formato.moneda(t.getCargoFijo())  // Escribe el texto que muestra el componente
                + " · variable " + Formato.moneda(t.getCargoVariable()) + " por m3</html>");  // Continúa el texto de la instrucción anterior.
        BigDecimal estimado = controlador.estimarCobro(u, (Double) spVolumen.getValue(), fe, (TipoLectura) cmbLectura.getSelectedItem());  // Declara «estimado» (BigDecimal) con el valor: controlador.estimarCobro(u, (Double) spVolumen....
        lblEstimado.setForeground(Estilo.AZUL);  // Define el color del texto o primer plano
        lblEstimado.setText(estimado == null ? " " : "Cobro estimado: " + Formato.moneda(estimado));  // Escribe el texto que muestra el componente
    }

    /** Registra la lectura (RF-15). */
    private void registrar() {  // Método registrar: Registra la lectura (RF-15)
        Usuario u = selector.getUsuario();  // Declara «u» (Usuario) con el valor: selector.getUsuario()
        if (u == null) {  // Condición: si u es igual a nulo
            avisar("Selecciona primero un usuario.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        LocalDate fe = fecha.getFecha();  // Declara «fe» (LocalDate) con el valor: fecha.getFecha()
        if (fe == null) {  // Condición: si fe es igual a nulo
            avisar("La fecha debe tener el formato AAAA-MM-DD.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        Resultado<Consumo> r = controlador.registrar(u, txtCodigo.getText().trim(), (Double) spVolumen.getValue(), fe,  // Declara «r» (Resultado<Consumo>) con el valor: controlador.registrar(u, txtCodigo.getText().tr...
                (TipoLectura) cmbLectura.getSelectedItem());  // Continuación de la instrucción de la línea anterior.
        mostrar(r);  // Muestra al usuario el mensaje del resultado de la operación
        if (r.isExito()) {  // Condición: si r.isExito()
            txtCodigo.setText(ControladorBase.generarCodigo("CON")); // Nuevo código sugerido para la siguiente lectura.
            actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
        }
    }
}
