package vista;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Clase centralizada de estilos para toda la aplicación.
 * Aplica paleta de colores, fuentes y efectos de hover.
 */
public class UIStyle {

    // --- PALETA DE COLORES (Material Design) ---
    public static final Color BG_APP = new Color(245, 247, 250);       // Fondo gris muy claro
    public static final Color PANEL_WHITE = Color.WHITE;               // Fondo de paneles
    public static final Color PRIMARY = new Color(41, 128, 185);       // Azul principal
    public static final Color SUCCESS = new Color(39, 174, 96);        // Verde (Guardar)
    public static final Color WARNING = new Color(243, 156, 18);       // Naranja (Editar/Limpiar)
    public static final Color DANGER = new Color(192, 57, 43);         // Rojo (Eliminar/Cortar)
    public static final Color INFO = new Color(142, 68, 173);          // Morado (Consultas)
    public static final Color DARK_TEXT = new Color(44, 62, 80);       // Texto oscuro
    public static final Color BORDER_COLOR = new Color(189, 195, 199); // Bordes grises

    // --- FUENTES ---
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 13);

    /**
     * Crea un botón moderno con efecto hover (cambia de color al pasar el mouse).
     * @param texto El texto del botón.
     * @param colorBase El color de fondo.
     * @return JButton estilizado.
     */
    public static JButton crearBoton(String texto, Color colorBase) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_SUBTITLE);
        btn.setForeground(Color.WHITE);
        btn.setBackground(colorBase);
        btn.setOpaque(true);
        btn.setBorderPainted(false); // Quita el borde por defecto feo
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto Hover (Hace que se ilumine al pasar el mouse)
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(colorBase.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(colorBase);
            }
        });
        return btn;
    }

    /**
     * Crea un campo de texto con bordes suaves.
     */
    public static JTextField crearCampoTexto() {
        JTextField txt = new JTextField();
        txt.setFont(FONT_TEXT);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(4, 8, 4, 8) // Padding interno
        ));
        return txt;
    }

    /**
     * Crea un panel blanco con un borde sutil.
     */
    public static JPanel crearPanelBlanco() {
        JPanel p = new JPanel(null);
        p.setBackground(PANEL_WHITE);
        p.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return p;
    }
    
    /**
     * Etiqueta de texto estándar.
     */
    public static JLabel crearLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FONT_TEXT);
        l.setForeground(DARK_TEXT);
        return l;
    }
}