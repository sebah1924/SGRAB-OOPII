package vista;


import javax.swing.*;
import java.awt.*;

/**
 * Menú principal de la aplicación EAAB.
 * Incluye los 10 módulos + el botón de Consumos.
 */
public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("EAAB - Sistema de Gestión de Facturación");
        setSize(1050, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setBackground(UIStyle.BG_APP);

        // --- CABECERA ---
        JPanel header = new JPanel(null);
        header.setBackground(new Color(33, 47, 61)); 
        header.setBounds(0, 0, 1050, 100);
        
        JLabel titulo = new JLabel("EAAB - Sistema de Gestión de Usuarios y Facturación");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(50, 30, 900, 40);
        header.add(titulo);
        add(header);

        // --- MENSAJE DE BIENVENIDA ---
        JLabel lblBienvenida = new JLabel("Seleccione el módulo que desea gestionar:");
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBienvenida.setForeground(UIStyle.DARK_TEXT);
        lblBienvenida.setBounds(50, 130, 500, 30);
        add(lblBienvenida);

        // ==========================================
        // TARJETAS DE MÓDULOS (3 columnas)
        // ==========================================

        // FILA 1
        crearBotonModulo("Registrar Usuario",    UIStyle.PRIMARY,             50, 180, new FrmRegistroUsuario());
        crearBotonModulo("Administrar Usuarios", UIStyle.INFO,               380, 180, new FrmAdministrarUsuario());
        crearBotonModulo("Registrar Consumo",    new Color(0, 102, 153),     710, 180, new FrmRegistroConsumo()); // <--- BOTÓN DE CONSUMOS
        
        // FILA 2
        crearBotonModulo("Facturación",          UIStyle.SUCCESS,             50, 320, new FrmFacturacion());
        crearBotonModulo("Registrar Pagos",      UIStyle.SUCCESS,            380, 320, new FrmRegistroPago());
        crearBotonModulo("Gestión de Sanciones", UIStyle.DANGER,             710, 320, new FrmSancion());
        
        // FILA 3
        crearBotonModulo("Notificaciones",       UIStyle.WARNING,             50, 460, new FrmNotificacion());
        crearBotonModulo("Racionamiento",        UIStyle.PRIMARY,            380, 460, new FrmRacionamiento());
        crearBotonModulo("Excepciones Legales",  UIStyle.INFO,               710, 460, new FrmExcepcion());

        // FILA 4
        crearBotonModulo("Reportes Financieros", UIStyle.DARK_TEXT,           50, 600, new FrmReporte());
        
        // BOTÓN SALIR
        JButton btnSalir = UIStyle.crearBoton("Salir del Sistema", UIStyle.DANGER);
        btnSalir.setBounds(380, 600, 290, 100);
        btnSalir.addActionListener(e -> System.exit(0));
        add(btnSalir);

        // PIE DE PÁGINA
        JLabel footer = new JLabel("Normatividad: Decreto 137 de 2024 | Resolución CRA 688 de 2014 | Decreto 1076 de 2015");
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footer.setForeground(Color.GRAY);
        footer.setBounds(250, 760, 600, 20);
        add(footer);
    }

    /**
     * Método auxiliar para crear los botones del menú de forma uniforme.
     */
    private void crearBotonModulo(String texto, Color color, int x, int y, JFrame ventana) {
        JButton btn = UIStyle.crearBoton(texto, color);
        btn.setBounds(x, y, 290, 100);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.addActionListener(e -> {
            ventana.setVisible(true);
            ventana.setLocationRelativeTo(null);
        });
        add(btn);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}