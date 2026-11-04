package formularios; // Paquete de los formularios.

import controladores.ControladorUsuarios;  // Importa controladores.ControladorUsuarios
import java.awt.*;  // Importa java.awt.*
import java.util.List;  // Importa java.util.List
import javax.swing.*;  // Importa javax.swing.*
import javax.swing.event.DocumentEvent;  // Importa javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener;  // Importa javax.swing.event.DocumentListener
import modelo.Usuario;  // Importa modelo.Usuario

/**
 * Componente reutilizable para ELEGIR un usuario: un cuadro de búsqueda (nombre, código o identificación) y una
 * lista desplegable que se filtra mientras se escribe. Lo usan consumos, facturación, sanciones y excepciones, de
 * modo que el funcionario siempre elige usuarios de la misma manera.
 *
 * Es un componente COMPUESTO (composición): contiene un JTextField y un JComboBox y expone una API sencilla.
 */
public class SelectorUsuario extends JPanel {  // Declaración de la clase SelectorUsuario: Componente reutilizable para ELEGIR un usuario:

    private final ControladorUsuarios controlador;              // Fuente de datos (inyectada).
    private final JTextField buscador = new JTextField(14);     // Texto de búsqueda.
    private final JComboBox<Usuario> lista = new JComboBox<>(); // Resultados filtrados.
    private Runnable alCambiar = () -> { };                     // Acción a ejecutar cuando cambia la selección.

    /** Crea el selector con su controlador. */
    public SelectorUsuario(ControladorUsuarios controlador) {  // Constructor: Crea el selector con su controlador
        super(new GridLayout(2, 1, 0, 3));  // Llama al constructor de la clase base para iniciar la parte heredada
        this.controlador = controlador;  // Asigna a controlador el valor: controlador
        buscador.setToolTipText("Escribe un nombre, código o identificación para filtrar la lista");  // Define el texto de ayuda que aparece al dejar el ratón encima
        lista.setRenderer(new DefaultListCellRenderer() { // Cada usuario se muestra como "código - nombre (tipo)".
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {  // Getter: devuelve list cell renderer component
                super.getListCellRendererComponent(l, v, i, s, f);  // Ejecuta la operación «get list cell renderer component» (definida en esta clase o en un objeto colaborador)
                if (v instanceof Usuario) {  // Condición: si v instanceof Usuario
                    Usuario u = (Usuario) v;  // Declara «u» (Usuario) con el valor: (Usuario) v
                    setText(u.getCodigoSuscriptor() + " - " + u.getNombreCompleto() + " (" + u.getTipoUsuario() + ")");  // Escribe el texto que muestra el componente
                }
                return this;  // Devuelve el valor de this
            }
        });  // Cierra la llamada y el bloque (manejador o expresión lambda) abierto en las líneas anteriores
        add(buscador);  // Agrega el componente al contenedor; su posición la decide el diseño
        add(lista);  // Agrega el componente al contenedor; su posición la decide el diseño
        buscador.getDocument().addDocumentListener(new DocumentListener() { // Filtra mientras se escribe.
            @Override
            public void insertUpdate(DocumentEvent e) {  // Método insertUpdate (devuelve void)
                recargar();  // Ejecuta la operación «recargar» (definida en esta clase o en un objeto colaborador)
            }

            @Override
            public void removeUpdate(DocumentEvent e) {  // Método removeUpdate (devuelve void)
                recargar();  // Ejecuta la operación «recargar» (definida en esta clase o en un objeto colaborador)
            }

            @Override
            public void changedUpdate(DocumentEvent e) {  // Método changedUpdate (devuelve void)
                recargar();  // Ejecuta la operación «recargar» (definida en esta clase o en un objeto colaborador)
            }
        });  // Cierra la llamada y el bloque (manejador o expresión lambda) abierto en las líneas anteriores
        lista.addActionListener(e -> alCambiar.run()); // Avisa cuando se elige otro usuario.
        recargar();  // Ejecuta la operación «recargar» (definida en esta clase o en un objeto colaborador)
    }

    /** Vuelve a leer los usuarios que cumplen la búsqueda, conservando la selección si sigue disponible. */
    public final void recargar() {  // Método recargar: Vuelve a leer los usuarios que cumplen la búsqueda, conservando la selección si sigue disponible
        Usuario anterior = getUsuario();  // Declara «anterior» (Usuario) con el valor: getUsuario()
        List<Usuario> encontrados = controlador.buscar(buscador.getText());  // Declara «encontrados» (List<Usuario>) con el valor: controlador.buscar(buscador.getText())
        DefaultComboBoxModel<Usuario> modelo = new DefaultComboBoxModel<>();  // Declara «modelo» (DefaultComboBoxModel<U...) con el valor: new DefaultComboBoxModel<>()
        for (Usuario u : encontrados) {  // Recorre cada elemento «u» de encontrados
            modelo.addElement(u);  // Ejecuta la operación «add element» (definida en esta clase o en un objeto colaborador)
        }
        lista.setModel(modelo);  // Asigna el modelo de datos del componente
        if (anterior != null && encontrados.contains(anterior)) {  // Condición: si anterior es distinto de nulo y encontrados.contains(anterior)
            lista.setSelectedItem(anterior);  // Selecciona una opción de la lista
        }
        alCambiar.run();  // Ejecuta la operación «run» (definida en esta clase o en un objeto colaborador)
    }

    /** @return el usuario elegido, o null si no hay ninguno. */
    public Usuario getUsuario() {  // Getter: devuelve usuario
        return (Usuario) lista.getSelectedItem();  // Devuelve: (Usuario) lista.getSelectedItem()
    }

    /** Registra qué hacer cuando cambia la selección (por ejemplo, recargar una tabla). */
    public void alCambiar(Runnable accion) {  // Método alCambiar: Registra qué hacer cuando cambia la selección (por ejemplo, recargar una tabla)
        this.alCambiar = accion;  // Asigna a alCambiar el valor: accion
    }
}
