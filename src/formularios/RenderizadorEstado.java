package formularios; // Paquete de los formularios.

import java.awt.Component;  // Importa java.awt.Component
import java.awt.Font;  // Importa java.awt.Font
import javax.swing.JLabel;  // Importa javax.swing.JLabel
import javax.swing.JTable;  // Importa javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer;  // Importa javax.swing.table.DefaultTableCellRenderer

/** Pinta el texto de una celda con el color de su estado (verde, naranja, rojo), en negrita y centrado. */
public class RenderizadorEstado extends DefaultTableCellRenderer {  // Declaración de la clase RenderizadorEstado: Pinta el texto de una celda con el color de su estado (verde, naranja, rojo), en negrita y centrado

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada, boolean foco,  // Getter: devuelve table cell renderer component
                                                   int fila, int columna) {  // Continuación de la instrucción de la línea anterior.
        JLabel l = (JLabel) super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);  // Declara «l» (JLabel) con el valor: (JLabel) super.getTableCellRendererComponent(ta...
        l.setHorizontalAlignment(CENTER);  // Alinea el contenido horizontalmente
        l.setFont(l.getFont().deriveFont(Font.BOLD));  // Define la tipografía
        if (!seleccionada) { // Si la fila está seleccionada se respeta el color de selección.
            l.setForeground(Estilo.colorDeEstado(valor == null ? "" : valor.toString()));  // Define el color del texto o primer plano
        }
        return l;  // Devuelve el valor de l
    }
}
