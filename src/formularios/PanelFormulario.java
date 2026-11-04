package formularios; // Paquete de los formularios.

import java.awt.*;  // Importa java.awt.*
import javax.swing.*;  // Importa javax.swing.*

/**
 * Panel de formulario compacto: cada fila tiene la etiqueta a la izquierda y el campo a la derecha (como en un
 * formulario armado con el diseñador). Ocupa poco alto, de modo que los datos no se recortan, y evita repetir en
 * cada formulario el código de GridBagLayout.
 */
public class PanelFormulario extends JPanel {  // Declaración de la clase PanelFormulario: Panel de formulario compacto:

    private int fila = 0; // Siguiente fila libre de la cuadrícula.

    /** Crea el panel con diseño de cuadrícula. */
    public PanelFormulario() {  // Constructor: Crea el panel con diseño de cuadrícula
        super(new GridBagLayout());  // Llama al constructor de la clase base para iniciar la parte heredada
    }

    /** Agrega una fila: etiqueta a la izquierda y componente a la derecha (se estira a lo ancho). */
    public void agregar(String titulo, JComponent componente) {  // Método agregar: Agrega una fila:
        GridBagConstraints g = new GridBagConstraints();  // Declara «g» (GridBagConstraints) con el valor: new GridBagConstraints()
        g.gridy = fila++;  // Ejecuta: g.gridy = fila++
        g.insets = new Insets(4, 6, 4, 6);  // Ejecuta: g.insets = new Insets(4, 6, 4, 6)
        g.gridx = 0;  // Ejecuta: g.gridx = 0
        g.anchor = GridBagConstraints.EAST;  // Ejecuta: g.anchor = GridBagConstraints.EAST
        add(new JLabel(titulo + ":"), g);  // Agrega el componente al contenedor; su posición la decide el diseño
        g.gridx = 1;  // Ejecuta: g.gridx = 1
        g.weightx = 1;  // Ejecuta: g.weightx = 1
        g.fill = GridBagConstraints.HORIZONTAL;  // Ejecuta: g.fill = GridBagConstraints.HORIZONTAL
        g.anchor = GridBagConstraints.WEST;  // Ejecuta: g.anchor = GridBagConstraints.WEST
        add(componente, g);  // Agrega el componente al contenedor; su posición la decide el diseño
    }

    /** Agrega un componente que ocupa las dos columnas (botones, avisos). */
    public void agregarAncho(JComponent componente) {  // Método agregarAncho: Agrega un componente que ocupa las dos columnas (botones, avisos)
        GridBagConstraints g = new GridBagConstraints();  // Declara «g» (GridBagConstraints) con el valor: new GridBagConstraints()
        g.gridy = fila++;  // Ejecuta: g.gridy = fila++
        g.gridx = 0;  // Ejecuta: g.gridx = 0
        g.gridwidth = 2;  // Ejecuta: g.gridwidth = 2
        g.weightx = 1;  // Ejecuta: g.weightx = 1
        g.fill = GridBagConstraints.HORIZONTAL;  // Ejecuta: g.fill = GridBagConstraints.HORIZONTAL
        g.insets = new Insets(8, 6, 4, 6);  // Ejecuta: g.insets = new Insets(8, 6, 4, 6)
        add(componente, g);  // Agrega el componente al contenedor; su posición la decide el diseño
    }

    /** Empuja el contenido hacia arriba ocupando el espacio sobrante. */
    public void rellenar() {  // Método rellenar: Empuja el contenido hacia arriba ocupando el espacio sobrante
        GridBagConstraints g = new GridBagConstraints();  // Declara «g» (GridBagConstraints) con el valor: new GridBagConstraints()
        g.gridy = fila++;  // Ejecuta: g.gridy = fila++
        g.weighty = 1;  // Ejecuta: g.weighty = 1
        g.fill = GridBagConstraints.VERTICAL;  // Ejecuta: g.fill = GridBagConstraints.VERTICAL
        add(Box.createVerticalGlue(), g);  // Agrega el componente al contenedor; su posición la decide el diseño
    }

    /** @return este panel envuelto en otro que lo ancla arriba (para que no se estire a todo el alto disponible). */
    public JPanel ancladoArriba() {  // Método ancladoArriba (devuelve JPanel)
        JPanel envoltorio = new JPanel(new BorderLayout());  // Declara «envoltorio» (JPanel) con el valor: new JPanel(new BorderLayout())
        envoltorio.add(this, BorderLayout.NORTH);  // Agrega el componente al contenedor; su posición la decide el diseño
        return envoltorio;  // Devuelve el valor de envoltorio
    }
}
