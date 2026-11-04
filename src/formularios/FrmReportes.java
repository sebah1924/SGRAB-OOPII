package formularios; // Paquete de los formularios.

import controladores.ControladorReportes;  // Importa controladores.ControladorReportes
import controladores.Resultado;  // Importa controladores.Resultado
import java.awt.*;  // Importa java.awt.*
import javax.swing.*;  // Importa javax.swing.*
import modelo.*;  // Importa modelo.*

/** Formulario de reportes financieros (RF-28 y RF-29): genera, previsualiza y exporta a PDF y CSV. */
public class FrmReportes extends FrmBase {  // Declaración de la clase FrmReportes: Formulario de reportes financieros (RF-28 y RF-29):

    private final ControladorReportes controlador;  // Atributo «controlador» de tipo ControladorReportes
    private final JTextField txtAutor = Estilo.campo(22);  // Atributo «txtAutor» de tipo JTextField
    private final JTextArea vistaPrevia = new JTextArea();  // Atributo «vistaPrevia» de tipo JTextArea
    private final JLabel cifraIngresos = Estilo.valor(Estilo.AZUL);  // Atributo «cifraIngresos» de tipo JLabel
    private final JLabel cifraCartera = Estilo.valor(Estilo.NARANJA);  // Atributo «cifraCartera» de tipo JLabel
    private final JLabel cifraSubsidios = Estilo.valor(Estilo.VERDE);  // Atributo «cifraSubsidios» de tipo JLabel
    private final JLabel cifraMultas = Estilo.valor(Estilo.ROJO);  // Atributo «cifraMultas» de tipo JLabel
    private final JLabel cifraNeto = Estilo.valor(Estilo.AZUL);  // Atributo «cifraNeto» de tipo JLabel
    private final JLabel cifraIndice = Estilo.valor(Estilo.NARANJA);  // Atributo «cifraIndice» de tipo JLabel
    private final ModeloTablaGenerico<ReporteFinanciero> modelo = new ModeloTablaGenerico<ReporteFinanciero>()  // Instrucción: private final ModeloTablaGenerico<ReporteFinanciero> mode...
            .columna("Código", ReporteFinanciero::getCodigo)  // Encadena la siguiente llamada de la misma instrucción: .columna("Código", ReporteFinanciero::getCodigo)
            .columna("Generado", r -> Formato.fechaHora(r.getFechaGeneracion()))  // Encadena la siguiente llamada de la misma instrucción: .columna("Generado", r -> Formato.fechaHora(r.getFechaGen...
            .columna("Autor", ReporteFinanciero::getAutor)  // Encadena la siguiente llamada de la misma instrucción: .columna("Autor", ReporteFinanciero::getAutor)
            .columna("Ingresos", r -> Formato.moneda(r.getIngresosFacturacion()));  // Encadena la siguiente llamada de la misma instrucción: .columna("Ingresos", r -> Formato.moneda(r.getIngresosFac...
    private final JTable tabla = Estilo.crearTabla(modelo);  // Atributo «tabla» de tipo JTable

    /** Construye el formulario. */
    public FrmReportes(ControladorReportes controlador) {  // Constructor: Construye el formulario
        super("Reportes financieros", "Ingresos, cartera, subsidios y multas, exportables a PDF y CSV", Estilo.GRIS, 1150, 680);  // Llama al constructor de la clase base para iniciar la parte heredada
        this.controlador = controlador;  // Asigna a controlador el valor: controlador
        txtAutor.setText("Funcionario EAAB");  // Escribe el texto que muestra el componente
        vistaPrevia.setEditable(false);  // Define si el usuario puede editar el contenido
        vistaPrevia.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));  // Define la tipografía

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));  // Declara «barra» (JPanel) con el valor: new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6))
        barra.add(new JLabel("Autor del reporte:"));  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(txtAutor);  // Agrega el componente al contenedor; su posición la decide el diseño
        JButton generar = Estilo.boton("Generar reporte", Estilo.VERDE);  // Declara «generar» (JButton) con el valor: Estilo.boton("Generar reporte", Estilo.VERDE)
        generar.addActionListener(e -> generar());  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        JButton pdf = Estilo.boton("Exportar PDF", Estilo.ROJO);  // Declara «pdf» (JButton) con el valor: Estilo.boton("Exportar PDF", Estilo.ROJO)
        pdf.addActionListener(e -> exportar(true));  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        JButton csv = Estilo.boton("Exportar CSV", Estilo.TURQUESA);  // Declara «csv» (JButton) con el valor: Estilo.boton("Exportar CSV", Estilo.TURQUESA)
        csv.addActionListener(e -> exportar(false));  // Registra el manejador que se ejecuta al activar el componente (clic o selección)
        barra.add(generar);  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(pdf);  // Agrega el componente al contenedor; su posición la decide el diseño
        barra.add(csv);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(barra, "Acciones", Estilo.GRIS);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        JPanel cifras = new JPanel(new GridLayout(2, 6, 8, 2));  // Declara «cifras» (JPanel) con el valor: new JPanel(new GridLayout(2, 6, 8, 2))
        for (String t : new String[]{"Ingresos", "Cartera", "Subsidios", "Multas", "Recaudo neto", "Índice de cartera"}) {  // Recorre cada elemento «t» de new String[]{"Ingresos", "Cartera", "Subsidios"...
            cifras.add(new JLabel(t));  // Agrega el componente al contenedor; su posición la decide el diseño
        }
        cifras.add(cifraIngresos);  // Agrega el componente al contenedor; su posición la decide el diseño
        cifras.add(cifraCartera);  // Agrega el componente al contenedor; su posición la decide el diseño
        cifras.add(cifraSubsidios);  // Agrega el componente al contenedor; su posición la decide el diseño
        cifras.add(cifraMultas);  // Agrega el componente al contenedor; su posición la decide el diseño
        cifras.add(cifraNeto);  // Agrega el componente al contenedor; su posición la decide el diseño
        cifras.add(cifraIndice);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(cifras, "Cifras del reporte seleccionado", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)

        JPanel lista = new JPanel(new BorderLayout());  // Declara «lista» (JPanel) con el valor: new JPanel(new BorderLayout())
        lista.add(Estilo.desplazable(tabla), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(lista, "Reportes generados", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        JPanel previa = new JPanel(new BorderLayout());  // Declara «previa» (JPanel) con el valor: new JPanel(new BorderLayout())
        previa.add(Estilo.desplazable(vistaPrevia), BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        Estilo.titulado(previa, "Vista previa del contenido", Estilo.AZUL);  // Ejecuta la operación «titulado» (definida en esta clase o en un objeto colaborador)
        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lista, previa);  // Declara «division» (JSplitPane) con el valor: new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lis...
        division.setResizeWeight(0.5);  // Define cómo se reparte el espacio sobrante entre los dos paneles
        JPanel arriba = new JPanel(new BorderLayout(0, 6));  // Declara «arriba» (JPanel) con el valor: new JPanel(new BorderLayout(0, 6))
        arriba.add(barra, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        arriba.add(cifras, BorderLayout.SOUTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        contenido.add(arriba, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        contenido.add(division, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        tabla.getSelectionModel().addListSelectionListener(e -> {  // Registra el manejador que reacciona a la selección de filas
            if (!e.getValueIsAdjusting()) {  // Condición: si no e.getValueIsAdjusting()
                mostrarSeleccionado();  // Ejecuta la operación «mostrar seleccionado» (definida en esta clase o en un objeto colaborador)
            }
        });  // Cierra la llamada y el bloque (manejador o expresión lambda) abierto en las líneas anteriores
        actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
    }

    /** Recarga la lista de reportes. */
    @Override
    public final void actualizar() {  // Método actualizar: Recarga la lista de reportes
        modelo.setFilas(controlador.listar());  // Entrega las filas a la tabla para que se redibuje
    }

    /** Muestra las cifras y el contenido del reporte seleccionado. */
    private void mostrarSeleccionado() {  // Método mostrarSeleccionado: Muestra las cifras y el contenido del reporte seleccionado
        ReporteFinanciero r = seleccionado(tabla, modelo);  // Declara «r» (ReporteFinanciero) con el valor: seleccionado(tabla, modelo)
        if (r == null) {  // Condición: si r es igual a nulo
            return;  // Termina el método sin devolver valor
        }
        cifraIngresos.setText(Formato.moneda(r.getIngresosFacturacion()));  // Escribe el texto que muestra el componente
        cifraCartera.setText(Formato.moneda(r.getCarteraPendiente()));  // Escribe el texto que muestra el componente
        cifraSubsidios.setText(Formato.moneda(r.getSubsidiosOtorgados()));  // Escribe el texto que muestra el componente
        cifraMultas.setText(Formato.moneda(r.getMultasRecaudadas()));  // Escribe el texto que muestra el componente
        cifraNeto.setText(Formato.moneda(r.calcularRecaudoNeto()));  // Escribe el texto que muestra el componente
        cifraIndice.setText(String.format("%.1f %%", r.calcularIndiceCartera() * 100));  // Escribe el texto que muestra el componente
        vistaPrevia.setText(r.generarContenido());  // Escribe el texto que muestra el componente
        vistaPrevia.setCaretPosition(0);  // Mueve el cursor de texto a la posición indicada
    }

    /** Genera un reporte nuevo y lo deja seleccionado (RF-28). */
    private void generar() {  // Método generar: Genera un reporte nuevo y lo deja seleccionado (RF-28)
        Resultado<ReporteFinanciero> r = controlador.generar(txtAutor.getText());  // Declara «r» (Resultado<ReporteFinan...) con el valor: controlador.generar(txtAutor.getText())
        mostrar(r);  // Muestra al usuario el mensaje del resultado de la operación
        if (r.isExito()) {  // Condición: si r.isExito()
            actualizar();  // Ejecuta la operación «actualizar» (definida en esta clase o en un objeto colaborador)
            int vista = tabla.convertRowIndexToView(modelo.getRowCount() - 1);  // Declara «vista» (int) con el valor: tabla.convertRowIndexToView(modelo.getRowCount(...
            tabla.setRowSelectionInterval(vista, vista);  // Selecciona un rango de filas
        }
    }

    /** Exporta el reporte seleccionado a PDF o CSV (RF-29). */
    private void exportar(boolean pdf) {  // Método exportar: Exporta el reporte seleccionado a PDF o CSV (RF-29)
        ReporteFinanciero r = seleccionado(tabla, modelo);  // Declara «r» (ReporteFinanciero) con el valor: seleccionado(tabla, modelo)
        if (r == null) {  // Condición: si r es igual a nulo
            avisar("Selecciona o genera primero un reporte.", false);  // Muestra un aviso emergente al usuario
            return;  // Termina el método sin devolver valor
        }
        Resultado<String> res = pdf ? controlador.exportarPdf(r) : controlador.exportarCsv(r);  // Declara «res» (Resultado<String>) con el valor: pdf ? controlador.exportarPdf(r) : controlador....
        avisar(res.isExito() ? "Archivo creado: " + res.getValor() : res.getMensaje(), res.isExito());  // Muestra un aviso emergente al usuario
    }
}
