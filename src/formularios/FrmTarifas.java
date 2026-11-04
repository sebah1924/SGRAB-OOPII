package formularios; // Paquete de los formularios.

import controladores.ControladorBase;  // Importa controladores.ControladorBase
import controladores.ControladorTarifas;  // Importa controladores.ControladorTarifas
import controladores.Resultado;  // Importa controladores.Resultado
import java.awt.*;  // Importa java.awt.*
import java.math.BigDecimal;  // Importa java.math.BigDecimal
import java.time.LocalDate;  // Importa java.time.LocalDate
import javax.swing.*;  // Importa javax.swing.*
import modelo.*;  // Importa modelo.*

/**
 * Formulario de tarifas del regulador (RF-14). Valida que la vigencia sea coherente y que no se solape con otra
 * tarifa del mismo tipo; el estado de cada tarifa (vigente, programada o vencida) se ve con colores.
 */
public class FrmTarifas extends FrmBase {  // Declaración de la clase FrmTarifas: Formulario de tarifas del regulador (RF-14)

    private final ControladorTarifas controlador;  // Atributo «controlador» de tipo ControladorTarifas
    private final JTextField txtCodigo = Estilo.campo(14);  // Atributo «txtCodigo» de tipo JTextField
    private final JComboBox<TipoUsuario> cmbTipo = new JComboBox<>(TipoUsuario.values());  // Atributo «cmbTipo» de tipo JComboBox<TipoUsuario>
    private final JTextField txtFijo = Estilo.campo(14);  // Atributo «txtFijo» de tipo JTextField
    private final JTextField txtVariable = Estilo.campo(14);  // Atributo «txtVariable» de tipo JTextField
    private final JSpinner spSubsidio = new JSpinner(new SpinnerNumberModel(0, 0, 70, 5)); // Porcentaje; el tope legal es 70 %.
    private final CampoFecha inicio = new CampoFecha(LocalDate.now());  // Atributo «inicio» de tipo CampoFecha
    private final CampoFecha fin = new CampoFecha(LocalDate.now().plusYears(1));  // Atributo «fin» de tipo CampoFecha
    private final ModeloTablaGenerico<Tarifa> modelo;  // Atributo «modelo» de tipo ModeloTablaGenerico<Tarifa>
    private final JTable tabla;  // Atributo «tabla» de tipo JTable

    /** Construye el formulario. */
    public FrmTarifas(ControladorTarifas controlador) {  // Constructor: Construye el formulario
        super("Tarifas", "Cargos fijo y variable por tipo de usuario (Resolución CRA 688 de 2014)", Estilo.MORADO, 1100, 580);  // Llama al constructor de la clase base para iniciar la parte heredada
        this.controlador = controlador;  // Asigna a controlador el valor: controlador
        this.modelo = new ModeloTablaGenerico<Tarifa>()  // Asigna a modelo el valor: new ModeloTablaGenerico<Tarifa>()
                .columna("Código", Tarifa::getCodigo)  // Encadena la siguiente llamada de la misma instrucción: .columna("Código", Tarifa::getCodigo)
                .columna("Tipo", t -> t.getTipoUsuario().name())  // Encadena la siguiente llamada de la misma instrucción: .columna("Tipo", t -> t.getTipoUsuario().name())
                .columna("Cargo fijo", t -> Formato.moneda(t.getCargoFijo()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Cargo fijo", t -> Formato.moneda(t.getCargoFijo...
                .columna("Cargo variable", t -> Formato.moneda(t.getCargoVariable()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Cargo variable", t -> Formato.moneda(t.getCargo...
                .columna("Subsidio", t -> Math.round(t.getSubsidio() * 100) + " %")  // Encadena la siguiente llamada de la misma instrucción: .columna("Subsidio", t -> Math.round(t.getSubsidio() * 10...
                .columna("Desde", t -> Formato.fecha(t.getVigenciaInicio()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Desde", t -> Formato.fecha(t.getVigenciaInicio()))
                .columna("Hasta", t -> Formato.fecha(t.getVigenciaFin()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Hasta", t -> Formato.fecha(t.getVigenciaFin()))
                .columna("Estado", t -> controlador.estado(t));  // Encadena la siguiente llamada de la misma instrucción: .columna("Estado", t -> controlador.estado(t));
        this.tabla = Estilo.crearTabla(modelo);  // Asigna a tabla el valor: Estilo.crearTabla(modelo)
        Estilo.columnaDeEstado(tabla, 7);  // Ejecuta la operación «columna de estado» (definida en esta clase o en un objeto colaborador)
        txtCodigo.setText(ControladorBase.generarCodigo("TAR"));  // Escribe el texto que muestra el componente
        txtFijo.setText("10000");  // Escribe el texto que muestra el componente
        txtVariable.setText("3000");  // Escribe el texto que muestra el componente

        PanelFormulario f = new PanelFormulario();  // Declara «f» (PanelFormulario) con el valor: new PanelFormulario()
        f.agregar("Código", txtCodigo);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Tipo de usuario", cmbTipo);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Cargo fijo (pesos/mes)", txtFijo);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Cargo variable (pesos/m3)", txtVariable);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Subsidio (0 a 70 %)", spSubsidio);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Vigencia desde", inicio);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        f.agregar("Vigencia hasta", fin);  // Ejecuta la operación «agregar» (definida en esta clase o en un objeto colaborador)
        JButton guardar = Estilo.boton("Registrar tarifa", Estilo.VERDE);  // Declara «guardar» (JButton) con el valor: Estilo.boton("Registrar tarifa", Estilo.VERDE)
        guardar.addActionListener(e -> registrar());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        f.agregarAncho(guardar);  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        f.agregarAncho(new JLabel("<html>No puede haber dos tarifas vigentes a la vez<br>para el mismo tipo de usuario.</html>"));  // Ejecuta la operación «agregar ancho» (definida en esta clase o en un objeto colaborador)
        JPanel grupoForm = new JPanel(new BorderLayout());  // Declara «grupoForm» (JPanel) con el valor: new JPanel(new BorderLayout())
        grupoForm.add(f.ancladoArriba(), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(grupoForm, "Nueva tarifa", Estilo.MORADO);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        grupoForm.setPreferredSize(new Dimension(380, 0));  // Sugiere al diseño el tamaño preferido del componente

        JPanel grupoLista = new JPanel(new BorderLayout());  // Declara «grupoLista» (JPanel) con el valor: new JPanel(new BorderLayout())
        grupoLista.add(Estilo.desplazable(tabla), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(grupoLista, "Catálogo de tarifas", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        contenido.add(grupoForm, BorderLayout.WEST);  // Agrega el componente al contenedor; su posición la decide el diseño
        contenido.add(grupoLista, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
    }

    /** Recarga el catálogo. */
    @Override
    public final void actualizar() {  // Método actualizar: Recarga el catálogo
        modelo.setFilas(controlador.listar());  // Entrega las filas a la tabla para que se redibuje
    }

    /** Convierte texto a dinero o lanza un mensaje comprensible. */
    private BigDecimal dinero(JTextField campo, String nombre) {  // Método dinero: Convierte texto a dinero o lanza un mensaje comprensible
        try {  // Bloque protegido: si algo falla, se captura abajo
            return new BigDecimal(campo.getText().trim().replace(',', '.'));  // Devuelve: new BigDecimal(campo.getText().trim().replace(',', '.'))
        } catch (NumberFormatException e) {  // Captura el error de tipo NumberFormatException e
            throw new IllegalArgumentException("El valor de «" + nombre + "» debe ser un número.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
    }

    /** Lee el formulario y registra la tarifa. */
    private void registrar() {  // Método registrar: Lee el formulario y registra la tarifa
        try {  // Bloque protegido: si algo falla, se captura abajo
            LocalDate ini = inicio.getFecha();  // Declara «ini» (LocalDate) con el valor: inicio.getFecha()
            LocalDate fi = fin.getFecha();  // Declara «fi» (LocalDate) con el valor: fin.getFecha()
            if (ini == null || fi == null) {  // Condición: si ini es igual a nulo o fi es igual a nulo
                throw new IllegalArgumentException("Las fechas de vigencia deben tener el formato AAAA-MM-DD.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
            Resultado<Tarifa> r = controlador.registrar(txtCodigo.getText().trim(), (TipoUsuario) cmbTipo.getSelectedItem(),  // Declara «r» (Resultado<Tarifa>) con el valor: controlador.registrar(txtCodigo.getText().trim(...
                    dinero(txtFijo, "Cargo fijo"), dinero(txtVariable, "Cargo variable"),  // Continuación de la instrucción de la línea anterior.
                    ((Integer) spSubsidio.getValue()) / 100.0, ini, fi);  // Ejecuta: ((Integer) spSubsidio.getValue()) / 100.0, ini, fi)
            mostrar(r);  // Muestra al usuario el mensaje del resultado de la operación
            if (r.isExito()) {  // Condición: si r.isExito()
                txtCodigo.setText(ControladorBase.generarCodigo("TAR"));  // Escribe el texto que muestra el componente
                actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
            }
        } catch (IllegalArgumentException e) {  // Captura el error de tipo IllegalArgumentException e
            avisar(e.getMessage(), false);  // Muestra un aviso emergente al usuario
        }
    }
}
