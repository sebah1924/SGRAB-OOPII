package formularios; // Paquete de los formularios (vistas Swing).

import java.awt.*;  // Importa java.awt.*
import java.util.function.Predicate;  // Importa java.util.function.Predicate
import javax.swing.*;  // Importa javax.swing.*
import javax.swing.border.Border;  // Importa javax.swing.border.Border
import javax.swing.border.TitledBorder;  // Importa javax.swing.border.TitledBorder
import javax.swing.table.TableModel;  // Importa javax.swing.table.TableModel
import javax.swing.text.JTextComponent;  // Importa javax.swing.text.JTextComponent

/**
 * Paleta de colores y fábricas de componentes con el aspecto clásico de las aplicaciones Swing hechas con el
 * diseñador de NetBeans: componentes estándar, bordes con título y botones de colores.
 *
 * Buena práctica (principio DRY): ningún formulario define colores o fuentes por su cuenta; todos los toman de
 * aquí, de modo que cambiar la identidad visual de TODA la aplicación es editar esta clase. Es una clase de
 * utilidades: no se instancia (constructor privado) y todo es estático.
 */
public final class Estilo {  // Declaración de la clase Estilo: Paleta de colores y fábricas de componentes con el aspecto clásico de las aplicaciones Swing hechas con el diseñador de NetBeans:

    // ---------------------------------------------------------------- Paleta
    public static final Color AZUL = new Color(0x1F4E79);       // Usuarios y encabezado principal.
    public static final Color TURQUESA = new Color(0x00796B);   // Consumos.
    public static final Color MORADO = new Color(0x6A1B9A);     // Tarifas.
    public static final Color VERDE = new Color(0x2E7D32);      // Facturación y acciones correctas.
    public static final Color ROJO = new Color(0xC62828);       // Sanciones y acciones delicadas.
    public static final Color NARANJA = new Color(0xE65100);    // Excepciones y advertencias.
    public static final Color CELESTE = new Color(0x0277BD);    // Notificaciones.
    public static final Color GRIS = new Color(0x455A64);       // Reportes y acciones neutras.

    /** Constructor privado: clase de utilidades. */
    private Estilo() {  // Constructor: Constructor privado:
    }

    /** Aplica el aspecto estándar multiplataforma de Java (el mismo que usa el diseñador de NetBeans por defecto). */
    public static void instalar() {  // Método instalar: Aplica el aspecto estándar multiplataforma de Java (el mismo que usa el diseñador de NetBeans por defecto)
        try {  // Bloque protegido: si algo falla, se captura abajo
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());  // Ejecuta la operación «set look and feel» (definida en esta clase o en un objeto colaborador)
        } catch (Exception e) { // Si falla se conserva el aspecto por defecto.
            System.err.println("No se pudo aplicar el aspecto visual: " + e.getMessage());  // Ejecuta la operación «println» (definida en esta clase o en un objeto colaborador)
        }
    }

    /** @return botón estándar con color de fondo y texto blanco en negrita. */
    public static JButton boton(String texto, Color color) {  // Método boton (devuelve JButton)
        JButton b = new JButton(texto);  // Declara «b» (JButton) con el valor: new JButton(texto)
        b.setBackground(color);  // Define el color de fondo
        b.setForeground(Color.WHITE);  // Define el color del texto o primer plano
        b.setFont(b.getFont().deriveFont(Font.BOLD));  // Define la tipografía
        b.setFocusPainted(false);  // Activa o desactiva el recuadro de foco
        b.setOpaque(true);  // Indica si el componente pinta su propio fondo (false = transparente)
        return b;  // Devuelve el valor de b
    }

    /** Pone a un panel un borde de línea de color con un título (el estilo típico de los grupos del diseñador). */
    public static <T extends JComponent> T titulado(T panel, String titulo, Color color) {  // Método titulado: Pone a un panel un borde de línea de color con un título (el estilo típico de los grupos del diseñador)
        Border linea = BorderFactory.createLineBorder(color, 2);  // Declara «linea» (Border) con el valor: BorderFactory.createLineBorder(color, 2)
        panel.setBorder(BorderFactory.createTitledBorder(linea, " " + titulo + " ", TitledBorder.LEFT, TitledBorder.TOP,  // Asigna el borde (margen interno o línea) del componente
                panel.getFont().deriveFont(Font.BOLD, 13f), color));  // Continuación de la instrucción de la línea anterior.
        return panel;  // Devuelve el valor de panel
    }

    /** @return franja de encabezado de color con el título del formulario y una descripción corta. */
    public static JPanel cabecera(String titulo, String subtitulo, Color color) {  // Método cabecera (devuelve JPanel)
        JPanel p = new JPanel(new GridLayout(2, 1));  // Declara «p» (JPanel) con el valor: new JPanel(new GridLayout(2, 1))
        p.setBackground(color);  // Define el color de fondo
        p.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));  // Asigna el borde (margen interno o línea) del componente
        JLabel t = new JLabel(titulo);  // Declara «t» (JLabel) con el valor: new JLabel(titulo)
        t.setFont(t.getFont().deriveFont(Font.BOLD, 22f));  // Define la tipografía
        t.setForeground(Color.WHITE);  // Define el color del texto o primer plano
        JLabel s = new JLabel(subtitulo);  // Declara «s» (JLabel) con el valor: new JLabel(subtitulo)
        s.setFont(s.getFont().deriveFont(Font.PLAIN, 12f));  // Define la tipografía
        s.setForeground(new Color(0xE3F2FD));  // Define el color del texto o primer plano
        p.add(t);  // Agrega el componente al contenedor; su posición la decide el diseño
        p.add(s);  // Agrega el componente al contenedor; su posición la decide el diseño
        return p;  // Devuelve el valor de p
    }

    /** @return campo de texto estándar de las columnas indicadas. */
    public static JTextField campo(int columnas) {  // Método campo (devuelve JTextField)
        return new JTextField(columnas);  // Devuelve: new JTextField(columnas)
    }

    /** @return etiqueta en negrita (para cifras y datos destacados). */
    public static JLabel negrita(String texto) {  // Método negrita (devuelve JLabel)
        JLabel l = new JLabel(texto);  // Declara «l» (JLabel) con el valor: new JLabel(texto)
        l.setFont(l.getFont().deriveFont(Font.BOLD));  // Define la tipografía
        return l;  // Devuelve el valor de l
    }

    /** @return etiqueta de color en negrita (para valores del resumen). */
    public static JLabel valor(Color color) {  // Método valor (devuelve JLabel)
        JLabel l = new JLabel("0");  // Declara «l» (JLabel) con el valor: new JLabel("0")
        l.setFont(l.getFont().deriveFont(Font.BOLD, 15f));  // Define la tipografía
        l.setForeground(color);  // Define el color del texto o primer plano
        return l;  // Devuelve el valor de l
    }

    /** @return una "píldora" opaca de color con el texto del estado. */
    public static JLabel chip(String texto) {  // Método chip (devuelve JLabel)
        JLabel l = new JLabel(" " + texto + " ", SwingConstants.CENTER);  // Declara «l» (JLabel) con el valor: new JLabel(" " + texto + " ", SwingConstants.CE...
        l.setOpaque(true);  // Indica si el componente pinta su propio fondo (false = transparente)
        l.setFont(l.getFont().deriveFont(Font.BOLD));  // Define la tipografía
        l.setForeground(Color.WHITE);  // Define el color del texto o primer plano
        l.setBackground(colorDeEstado(texto));  // Define el color de fondo
        return l;  // Devuelve el valor de l
    }

    /** @return el color asociado a un estado (por palabra clave). */
    public static Color colorDeEstado(String estado) {  // Método colorDeEstado (devuelve Color)
        String e = estado == null ? "" : estado.toUpperCase();  // Declara «e» (String) con el valor: estado es igual a nulo ? "" : estado.toUpperCase()
        if (e.matches(".*(ACTIVO|PAGADA|VIGENTE|EJECUTADA|REAL|EXENTO).*")) {  // Condición: si e.matches(".*(ACTIVO|PAGADA|VIGENTE|EJECUTADA|REAL|EXENTO).*")
            return VERDE;  // Devuelve el valor de VERDE
        }
        if (e.matches(".*(SUSPENDIDO|VENCIDA|VENCIDO|CORTADO|REVOCADA).*")) {  // Condición: si e.matches(".*(SUSPENDIDO|VENCIDA|VENCIDO|CORTADO|REVOCADA).*")
            return ROJO;  // Devuelve el valor de ROJO
        }
        if (e.matches(".*(CONFIRMADA).*")) {  // Condición: si e.matches(".*(CONFIRMADA).*")
            return CELESTE;  // Devuelve el valor de CELESTE
        }
        return NARANJA; // Pendiente, en deuda, impuesta, apelada, programada, estimada, acuerdo de pago...
    }

    /** @return una tabla estándar con orden por clic en el encabezado y encabezado en negrita. */
    public static JTable crearTabla(TableModel modelo) {  // Método crearTabla (devuelve JTable)
        JTable tabla = new JTable(modelo);  // Declara «tabla» (JTable) con el valor: new JTable(modelo)
        tabla.setRowHeight(24);  // Define el alto de las filas de la tabla
        tabla.setAutoCreateRowSorter(true); // Clic en el encabezado ordena la columna.
        tabla.setFillsViewportHeight(true);  // La tabla ocupa todo el alto visible
        tabla.getTableHeader().setReorderingAllowed(false);  // Permite o impide reordenar columnas
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD));  // Define la tipografía
        return tabla;  // Devuelve el valor de tabla
    }

    /** Aplica el color de estado al texto de una columna. */
    public static void columnaDeEstado(JTable tabla, int columna) {  // Método columnaDeEstado: Aplica el color de estado al texto de una columna
        tabla.getColumnModel().getColumn(columna).setCellRenderer(new RenderizadorEstado());  // Ejecuta la operación «set cell renderer» (definida en esta clase o en un objeto colaborador)
    }

    /** @return un desplazador estándar que contiene al componente. */
    public static JScrollPane desplazable(Component c) {  // Método desplazable (devuelve JScrollPane)
        return new JScrollPane(c);  // Devuelve: new JScrollPane(c)
    }

    /** Pone el borde de un campo en el color indicado (null = borde normal). */
    public static void marcarBorde(JComponent c, Color color) {  // Método marcarBorde: Pone el borde de un campo en el color indicado (null = borde normal)
        if (color == null) {  // Condición: si color es igual a nulo
            c.setBorder(UIManager.getBorder("TextField.border")); // Borde estándar.
        } else {  // Alternativa: se ejecuta cuando ninguna condición anterior se cumplió
            c.setBorder(BorderFactory.createLineBorder(color, 2));  // Asigna el borde (margen interno o línea) del componente
        }
    }

    /**
     * Validación EN VIVO: mientras se escribe, el borde se pone verde (válido) o rojo (inválido) y el motivo
     * aparece al dejar el ratón sobre el campo (texto de ayuda). Da retroalimentación sin ocupar espacio.
     */
    public static void validarEnVivo(JTextComponent campo, Predicate<String> valido, String mensajeError) {  // Método validarEnVivo: Validación EN VIVO:
        campo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {  // Registra el manejador que reacciona a cada cambio del texto escrito
            /** Reevalúa la regla cada vez que cambia el texto. */
            private void revisar() {  // Método revisar: Reevalúa la regla cada vez que cambia el texto
                String texto = campo.getText().trim();  // Declara «texto» (String) con el valor: campo.getText().trim()
                if (texto.isEmpty()) { // Vacío: estado neutro.
                    marcarBorde(campo, null);  // Ejecuta la operación «marcar borde» (definida en esta clase o en un objeto colaborador)
                    campo.setToolTipText(null);  // Define el texto de ayuda que aparece al dejar el ratón encima
                } else if (valido.test(texto)) { // Cumple la regla.
                    marcarBorde(campo, VERDE);  // Ejecuta la operación «marcar borde» (definida en esta clase o en un objeto colaborador)
                    campo.setToolTipText(null);  // Define el texto de ayuda que aparece al dejar el ratón encima
                } else { // No cumple: se marca y se explica.
                    marcarBorde(campo, ROJO);  // Ejecuta la operación «marcar borde» (definida en esta clase o en un objeto colaborador)
                    campo.setToolTipText(mensajeError);  // Define el texto de ayuda que aparece al dejar el ratón encima
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {  // Método insertUpdate (devuelve void)
                revisar();  // Ejecuta la operación «revisar» (definida en esta clase o en un objeto colaborador)
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {  // Método removeUpdate (devuelve void)
                revisar();  // Ejecuta la operación «revisar» (definida en esta clase o en un objeto colaborador)
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {  // Método changedUpdate (devuelve void)
                revisar();  // Ejecuta la operación «revisar» (definida en esta clase o en un objeto colaborador)
            }
        });  // Cierra la llamada y el bloque (manejador o expresión lambda) abierto en las líneas anteriores
    }
}
