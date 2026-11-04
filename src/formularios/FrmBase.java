package formularios; // Paquete de los formularios.

import controladores.Resultado;  // Importa controladores.Resultado
import java.awt.*;  // Importa java.awt.*
import javax.swing.*;  // Importa javax.swing.*

/**
 * Ventana base de todos los formularios (patrón Template Method + herencia).
 *
 * Define una sola vez lo común: encabezado de color con título, área de contenido, barra de estado inferior, cierre
 * con la tecla ESC y mensajes al usuario. Cada formulario hereda de esta clase, arma su contenido y sobrescribe
 * actualizar() para refrescar sus datos. Así todos se ven y se comportan igual.
 */
public abstract class FrmBase extends JFrame {  // Declaración de la clase FrmBase: Ventana base de todos los formularios (patrón Template Method + herencia)

    protected final JPanel contenido = new JPanel(new BorderLayout(8, 8)); // Zona donde cada formulario pone lo suyo.
    private final JLabel barraEstado = new JLabel(" Listo");               // Mensajes de resultado de las operaciones.

    /** Construye la ventana con título, subtítulo, color de encabezado y tamaño (ajustado al tamaño de la pantalla). */
    protected FrmBase(String titulo, String subtitulo, Color color, int ancho, int alto) {  // Constructor: Construye la ventana con título, subtítulo, color de encabezado y tamaño (ajustado al tamaño de la pantalla)
        super(titulo + " - SGRAB");  // Llama al constructor de la clase base para iniciar la parte heredada
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Cerrar un formulario no cierra la aplicación.
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();  // Declara «pantalla» (Dimension) con el valor: Toolkit.getDefaultToolkit().getScreenSize()
        setSize(Math.min(ancho, pantalla.width - 60), Math.min(alto, pantalla.height - 80)); // Nunca más grande que la pantalla.
        setLocationRelativeTo(null);  // Centra la ventana respecto a otra (null = centro de la pantalla)
        getContentPane().setLayout(new BorderLayout());  // Define el administrador de diseño que ubica los componentes hijos
        getContentPane().add(Estilo.cabecera(titulo, subtitulo, color), BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        contenido.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));  // Asigna el borde (margen interno o línea) del componente
        getContentPane().add(contenido, BorderLayout.CENTER);  // Agrega el componente al contenedor; su posición la decide el diseño
        barraEstado.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),  // Asigna el borde (margen interno o línea) del componente
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));  // Continuación de la instrucción de la línea anterior.
        getContentPane().add(barraEstado, BorderLayout.SOUTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        getRootPane().registerKeyboardAction(e -> dispose(), // Atajo: ESC cierra la ventana.
                KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);  // Continuación de la instrucción de la línea anterior.
    }

    /** Refresca los datos que muestra el formulario (cada subclase decide qué volver a leer). */
    public abstract void actualizar();  // Método actualizar: Refresca los datos que muestra el formulario (cada subclase decide qué volver a leer)

    /** Muestra un mensaje en la barra de estado (verde si es correcto, rojo si es un problema). */
    protected void estado(String texto, boolean exito) {  // Método estado: Muestra un mensaje en la barra de estado (verde si es correcto, rojo si es un problema)
        barraEstado.setForeground(exito ? Estilo.VERDE : Estilo.ROJO);  // Define el color del texto o primer plano
        barraEstado.setText(" " + texto);  // Escribe el texto que muestra el componente
    }

    /** Muestra el mensaje de un Resultado: en la barra de estado si salió bien; además en un cuadro si falló. */
    protected void mostrar(Resultado<?> resultado) {  // Método mostrar: Muestra el mensaje de un Resultado:
        avisar(resultado.getMensaje(), resultado.isExito());  // Muestra un aviso emergente al usuario
    }

    /** Muestra un aviso: los errores se destacan con un cuadro de diálogo para que no pasen desapercibidos. */
    protected void avisar(String texto, boolean exito) {  // Método avisar: Muestra un aviso:
        estado(texto, exito);  // Ejecuta la operación «estado» (definida en esta clase o en un objeto colaborador)
        if (!exito) {  // Condición: si no exito
            JOptionPane.showMessageDialog(this, texto, "Revisa los datos", JOptionPane.WARNING_MESSAGE);  // Ejecuta la operación «show message dialog» (definida en esta clase o en un objeto colaborador)
        }
    }

    /** Pide confirmación antes de una acción delicada. */
    protected boolean confirmar(String pregunta) {  // Método confirmar: Pide confirmación antes de una acción delicada
        return JOptionPane.showConfirmDialog(this, pregunta, "Confirmar", JOptionPane.YES_NO_OPTION,  // Devuelve: JOptionPane.showConfirmDialog(this, pregunta, "Confirmar", JOptionPane.Y...
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;  // Continuación de la instrucción de la línea anterior.
    }

    /** @return el objeto de la fila seleccionada de la tabla (o null si no hay selección). */
    protected static <T> T seleccionado(JTable tabla, ModeloTablaGenerico<T> modelo) {  // Método seleccionado (devuelve T)
        int vista = tabla.getSelectedRow(); // Índice en la vista (puede estar ordenada).
        if (vista < 0) {  // Condición: si vista es menor que 0
            return null;  // Devuelve el valor de null
        }
        return modelo.getFila(tabla.convertRowIndexToModel(vista)); // Convierte al índice del modelo.
    }
}
