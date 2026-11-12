package vista;


import dao.ExcepcionDAO;
import modelo.Excepcion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Formulario de excepciones legales (Necesidad 4).
 */
public class FrmExcepcion extends JFrame {

    // ---------- Componentes ----------
    private JTextField txtCodigo, txtCategoria, txtJustificacion, txtDescripcion;
    private JCheckBox chkConstitucional;
    private JButton btnGuardar, btnLimpiar, btnListar;
    private JTable tblExcepciones;
    private DefaultTableModel modelo;

    // ---------- Dependencias ----------
    private final ExcepcionDAO dao = new ExcepcionDAO();

    // ---------- Constructor ----------
    public FrmExcepcion() {
        initComponents();
        setLocationRelativeTo(null);
        listar();
    }

    // ---------- Inicialización ----------
    private void initComponents() {
        setTitle("EAAB - Excepciones Legales");
        setSize(1050, 780);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setBackground(UIStyle.BG_APP);

        // Cabecera
        JPanel header = new JPanel(null);
        header.setBackground(UIStyle.INFO);
        header.setBounds(0, 0, 1050, 60);
        JLabel titulo = new JLabel("REGISTRO DE EXCEPCIONES LEGALES");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(40, 15, 700, 30);
        header.add(titulo);
        add(header);

        // ===== Panel de registro =====
        JPanel pnl = UIStyle.crearPanelBlanco();
        pnl.setBounds(30, 80, 980, 300);

        pnl.add(lbl("Código Único:", 20, 20, 100));
        txtCodigo = UIStyle.crearCampoTexto();
        txtCodigo.setBounds(140, 20, 250, 30);
        pnl.add(txtCodigo);

        pnl.add(lbl("Categoría Exenta:", 20, 70, 130));
        txtCategoria = UIStyle.crearCampoTexto();
        txtCategoria.setBounds(160, 70, 400, 30);
        pnl.add(txtCategoria);

        pnl.add(lbl("Descripción:", 20, 120, 100));
        txtDescripcion = UIStyle.crearCampoTexto();
        txtDescripcion.setBounds(140, 120, 800, 30);
        pnl.add(txtDescripcion);

        pnl.add(lbl("Justificación Legal:", 20, 170, 140));
        txtJustificacion = UIStyle.crearCampoTexto();
        txtJustificacion.setBounds(180, 170, 760, 30);
        pnl.add(txtJustificacion);

        chkConstitucional = new JCheckBox(
                "Es sujeto de especial protección constitucional");
        chkConstitucional.setBounds(20, 215, 500, 25);
        chkConstitucional.setBackground(Color.WHITE);
        chkConstitucional.setFont(UIStyle.FONT_TEXT);
        pnl.add(chkConstitucional);

        btnGuardar = UIStyle.crearBoton("Guardar Excepción", UIStyle.SUCCESS);
        btnGuardar.setBounds(20, 250, 200, 40);
        pnl.add(btnGuardar);

        btnLimpiar = UIStyle.crearBoton("Limpiar", UIStyle.WARNING);
        btnLimpiar.setBounds(230, 250, 140, 40);
        pnl.add(btnLimpiar);

        btnListar = UIStyle.crearBoton("Refrescar Listado", UIStyle.PRIMARY);
        btnListar.setBounds(380, 250, 200, 40);
        pnl.add(btnListar);

        add(pnl);

        // ===== Tabla =====
        modelo = new DefaultTableModel(new Object[]{
            "Código","Categoría","Descripción","Justificación Legal","Protec. Constitucional"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblExcepciones = new JTable(modelo);
        tblExcepciones.setFont(UIStyle.FONT_TEXT);
        tblExcepciones.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(tblExcepciones);
        scroll.setBounds(30, 400, 980, 350);
        add(scroll);

        // ===== Listeners =====
        btnGuardar.addActionListener(e -> guardar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnListar.addActionListener(e -> listar());
    }

    // ---------- Helper ----------
    private JLabel lbl(String t, int x, int y, int w) {
        JLabel l = UIStyle.crearLabel(t);
        l.setBounds(x, y, w, 25);
        return l;
    }

    // ---------- Guardar ----------
    private void guardar() {
        try {
            Excepcion ex = new Excepcion(
                txtCodigo.getText().trim(),
                txtDescripcion.getText().trim(),
                txtCategoria.getText().trim(),
                txtJustificacion.getText().trim(),
                chkConstitucional.isSelected());
            dao.guardar(ex);
            JOptionPane.showMessageDialog(this, "Excepción guardada.");
            limpiar();
            listar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Limpiar ----------
    private void limpiar() {
        txtCodigo.setText("");
        txtCategoria.setText("");
        txtDescripcion.setText("");
        txtJustificacion.setText("");
        chkConstitucional.setSelected(false);
    }

    // ---------- Listar ----------
    private void listar() {
        try {
            modelo.setRowCount(0);
            List<Excepcion> lista = dao.listar();
            for (Excepcion e : lista) {
                modelo.addRow(new Object[]{
                    e.getCodigo(),
                    e.getCategoriaExenta(),
                    e.getDescripcion(),
                    e.getJustificacionLegal(),
                    e.isProteccionConstitucional() ? "Sí" : "No"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}