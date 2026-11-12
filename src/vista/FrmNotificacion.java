package vista;


import controlador.NotificacionControlador;
import controlador.UsuarioControlador;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Formulario de notificaciones (Necesidad 3).
 */
public class FrmNotificacion extends JFrame {

    // ---------- Componentes ----------
    private JTextField txtCodigo, txtTitulo;
    private JTextArea txtCuerpo;
    private JComboBox<String> cbxEvento;
    private JButton btnEnviar, btnHistorialGlobal;
    private JTable tblHistorial;
    private DefaultTableModel modelo;

    // ---------- Dependencias ----------
    private final NotificacionControlador nCtrl = new NotificacionControlador();
    private final UsuarioControlador uCtrl = new UsuarioControlador();

    // ---------- Constructor ----------
    public FrmNotificacion() {
        initComponents();
        setLocationRelativeTo(null);
    }

    // ---------- Inicialización ----------
    private void initComponents() {
        setTitle("EAAB - Notificaciones");
        setSize(1050, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setBackground(UIStyle.BG_APP);

        // Cabecera
        JPanel header = new JPanel(null);
        header.setBackground(UIStyle.WARNING);
        header.setBounds(0, 0, 1050, 60);
        JLabel titulo = new JLabel("ENVÍO DE NOTIFICACIONES");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(40, 15, 500, 30);
        header.add(titulo);
        add(header);

        // ===== Panel envío =====
        JPanel pnlE = UIStyle.crearPanelBlanco();
        pnlE.setBounds(30, 80, 980, 340);

        pnlE.add(lbl("Código Suscriptor:", 20, 20, 130));
        txtCodigo = UIStyle.crearCampoTexto();
        txtCodigo.setBounds(160, 20, 260, 30);
        pnlE.add(txtCodigo);

        pnlE.add(lbl("Evento:", 440, 20, 60));
        cbxEvento = new JComboBox<>(new String[]{
            "EMISION_FACTURA","FACTURA_VENCIDA","SUSPENSION_INMINENTE",
            "SUSPENSION_EFECTIVA","CORTE_PROGRAMADO","CAMBIO_CALENDARIO",
            "SANCION_IMPUESTA","PAGO_REGISTRADO"});
        cbxEvento.setBounds(510, 20, 440, 30);
        pnlE.add(cbxEvento);

        pnlE.add(lbl("Título:", 20, 70, 60));
        txtTitulo = UIStyle.crearCampoTexto();
        txtTitulo.setBounds(90, 70, 860, 30);
        pnlE.add(txtTitulo);

        pnlE.add(lbl("Cuerpo:", 20, 120, 60));
        txtCuerpo = new JTextArea();
        txtCuerpo.setLineWrap(true);
        txtCuerpo.setFont(UIStyle.FONT_TEXT);
        txtCuerpo.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER_COLOR));
        JScrollPane sp = new JScrollPane(txtCuerpo);
        sp.setBounds(90, 120, 860, 130);
        pnlE.add(sp);

        JLabel lblNota = new JLabel("La notificación se enviará por el canal preferido del usuario.");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(Color.GRAY);
        lblNota.setBounds(20, 265, 600, 20);
        pnlE.add(lblNota);

        btnEnviar = UIStyle.crearBoton("Enviar Notificación", UIStyle.SUCCESS);
        btnEnviar.setBounds(20, 290, 210, 40);
        pnlE.add(btnEnviar);

        btnHistorialGlobal = UIStyle.crearBoton("Ver Historial Global", UIStyle.PRIMARY);
        btnHistorialGlobal.setBounds(240, 290, 210, 40);
        pnlE.add(btnHistorialGlobal);

        add(pnlE);

        // ===== Tabla historial =====
        modelo = new DefaultTableModel(new Object[]{
            "Código","Título","Canal","Evento","Fecha","Enviada"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHistorial = new JTable(modelo);
        tblHistorial.setFont(UIStyle.FONT_TEXT);
        tblHistorial.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(tblHistorial);
        scroll.setBounds(30, 440, 980, 320);
        add(scroll);

        // ===== Listeners =====
        btnEnviar.addActionListener(e -> enviar());
        btnHistorialGlobal.addActionListener(e -> verHistorial());
    }

    // ---------- Helper ----------
    private JLabel lbl(String t, int x, int y, int w) {
        JLabel l = UIStyle.crearLabel(t);
        l.setBounds(x, y, w, 25);
        return l;
    }

    // ---------- Enviar ----------
    private void enviar() {
        try {
            Usuario u = uCtrl.buscarPorCodigo(txtCodigo.getText().trim());
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Usuario no encontrado.");
                return;
            }
            String t = txtTitulo.getText().trim();
            String c = txtCuerpo.getText().trim();
            if (t.isEmpty() || c.isEmpty())
                throw new Exception("Título y cuerpo son obligatorios.");

            nCtrl.enviarNotificacion(u,
                    EventoNotificacion.valueOf((String)cbxEvento.getSelectedItem()),
                    t, c);
            JOptionPane.showMessageDialog(this, "Notificación enviada.");
            txtCodigo.setText("");
            txtTitulo.setText("");
            txtCuerpo.setText("");
            verHistorial();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Ver historial ----------
    private void verHistorial() {
        try {
            modelo.setRowCount(0);
            List<Notificacion> ns = nCtrl.historialGlobal();
            for (Notificacion n : ns) {
                modelo.addRow(new Object[]{
                    n.getCodigo(),
                    n.getTitulo(),
                    n.getCanal(),
                    n.getEvento(),
                    n.getFechaCreacion(),
                    n.isEnviada() ? "Sí" : "No"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}