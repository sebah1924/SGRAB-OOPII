package vista;


import controlador.SancionControlador;
import controlador.UsuarioControlador;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


/**
 * Formulario de sanciones con ciclo de vida completo (Necesidad 7).
 */
public class FrmSancion extends JFrame {

    // ---------- Componentes ----------
    private JTextField txtCodigo, txtDescripcion, txtSalarios, txtCodigoSancion;
    private JComboBox<String> cbxTipo;
    private JButton btnImponer, btnListar, btnApelar, btnConfirmar, btnEjecutar, btnRevocar;
    private JTable tblSanciones;
    private DefaultTableModel modelo;

    // ---------- Dependencias ----------
    private final SancionControlador sCtrl = new SancionControlador();
    private final UsuarioControlador uCtrl = new UsuarioControlador();

    // ---------- Constructor ----------
    public FrmSancion() {
        initComponents();
        setLocationRelativeTo(null);
    }

    // ---------- Inicialización ----------
    private void initComponents() {
        setTitle("EAAB - Sanciones");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setBackground(UIStyle.BG_APP);

        // Cabecera
        JPanel header = new JPanel(null);
        header.setBackground(UIStyle.DANGER);
        header.setBounds(0, 0, 1100, 60);
        JLabel titulo = new JLabel("GESTIÓN DE SANCIONES POR DERROCHE");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(40, 15, 700, 30);
        header.add(titulo);
        add(header);

        // ===== Panel imponer =====
        JPanel pnlI = UIStyle.crearPanelBlanco();
        pnlI.setBounds(30, 80, 1030, 240);

        pnlI.add(lbl("Código Suscriptor:", 20, 20, 130));
        txtCodigo = UIStyle.crearCampoTexto();
        txtCodigo.setBounds(160, 20, 300, 30);
        pnlI.add(txtCodigo);

        pnlI.add(lbl("Tipo Sanción:", 20, 60, 100));
        cbxTipo = new JComboBox<>(new String[]{"ECONOMICA","SUSPENSION","PEDAGOGICA"});
        cbxTipo.setBounds(140, 60, 320, 30);
        pnlI.add(cbxTipo);

        pnlI.add(lbl("N° Salarios (1-16):", 20, 100, 160));
        txtSalarios = UIStyle.crearCampoTexto();
        txtSalarios.setBounds(190, 100, 200, 30);
        pnlI.add(txtSalarios);

        pnlI.add(lbl("Descripción:", 20, 140, 100));
        txtDescripcion = UIStyle.crearCampoTexto();
        txtDescripcion.setBounds(140, 140, 870, 30);
        pnlI.add(txtDescripcion);

        btnImponer = UIStyle.crearBoton("Imponer Sanción", UIStyle.DANGER);
        btnImponer.setBounds(20, 190, 190, 40);
        pnlI.add(btnImponer);

        btnListar = UIStyle.crearBoton("Listar Sanciones", UIStyle.PRIMARY);
        btnListar.setBounds(220, 190, 190, 40);
        pnlI.add(btnListar);

        add(pnlI);

        // ===== Panel ciclo de vida =====
        JPanel pnlC = UIStyle.crearPanelBlanco();
        pnlC.setBounds(30, 340, 1030, 100);

        pnlC.add(lbl("Código Sanción:", 20, 35, 120));
        txtCodigoSancion = UIStyle.crearCampoTexto();
        txtCodigoSancion.setBounds(150, 35, 200, 30);
        pnlC.add(txtCodigoSancion);

        btnApelar    = UIStyle.crearBoton("Apelar", UIStyle.WARNING);
        btnApelar.setBounds(380, 30, 130, 40);
        pnlC.add(btnApelar);

        btnConfirmar = UIStyle.crearBoton("Confirmar", UIStyle.SUCCESS);
        btnConfirmar.setBounds(520, 30, 130, 40);
        pnlC.add(btnConfirmar);

        btnEjecutar  = UIStyle.crearBoton("Ejecutar", UIStyle.PRIMARY);
        btnEjecutar.setBounds(660, 30, 130, 40);
        pnlC.add(btnEjecutar);

        btnRevocar   = UIStyle.crearBoton("Revocar", new Color(120, 0, 0));
        btnRevocar.setBounds(800, 30, 130, 40);
        pnlC.add(btnRevocar);

        add(pnlC);

        // ===== Tabla =====
        modelo = new DefaultTableModel(new Object[]{
            "Código","Tipo","Valor Multa","Descripción","Estado","Fecha"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSanciones = new JTable(modelo);
        tblSanciones.setFont(UIStyle.FONT_TEXT);
        tblSanciones.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(tblSanciones);
        scroll.setBounds(30, 460, 1030, 300);
        add(scroll);

        // ===== Listeners =====
        btnImponer.addActionListener(e -> imponer());
        btnListar.addActionListener(e -> listar());
        btnApelar.addActionListener(e -> ciclo(EstadoSancion.APELADA));
        btnConfirmar.addActionListener(e -> ciclo(EstadoSancion.CONFIRMADA));
        btnEjecutar.addActionListener(e -> ciclo(EstadoSancion.EJECUTADA));
        btnRevocar.addActionListener(e -> ciclo(EstadoSancion.REVOCADA));
    }

    // ---------- Helper ----------
    private JLabel lbl(String t, int x, int y, int w) {
        JLabel l = UIStyle.crearLabel(t);
        l.setBounds(x, y, w, 25);
        return l;
    }

    // ---------- Imponer ----------
    private void imponer() {
        try {
            Usuario u = uCtrl.buscarPorCodigo(txtCodigo.getText().trim());
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Usuario no encontrado.");
                return;
            }
            String tipo = (String) cbxTipo.getSelectedItem();
            String desc = txtDescripcion.getText().trim();
            if (desc.isEmpty())
                throw new Exception("La descripción es obligatoria.");

            Sancion s;
            switch (tipo) {
                case "ECONOMICA":
                    int sal = Integer.parseInt(txtSalarios.getText().trim());
                    s = sCtrl.imponerMulta(u, desc, sal);
                    break;
                case "SUSPENSION":
                    s = sCtrl.imponerSuspension(u, desc);
                    break;
                default:
                    s = sCtrl.imponerCurso(u, desc);
            }
            JOptionPane.showMessageDialog(this, "Sanción impuesta: " + s.getCodigo());
            listar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Listar ----------
    private void listar() {
        try {
            Usuario u = uCtrl.buscarPorCodigo(txtCodigo.getText().trim());
            if (u == null) return;
            modelo.setRowCount(0);
            List<Sancion> ss = sCtrl.listar(u);
            for (Sancion s : ss) {
                modelo.addRow(new Object[]{
                    s.getCodigo(),
                    s.getTipo(),
                    "$" + s.getValorMulta(),
                    s.getDescripcion(),
                    s.getEstado(),
                    s.getFechaImposicion()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Ciclo de vida ----------
    private void ciclo(EstadoSancion nuevo) {
        try {
            String cod = txtCodigoSancion.getText().trim();
            if (cod.isEmpty())
                throw new Exception("Ingrese el código de la sanción.");
            sCtrl.cambiarEstado(cod, nuevo);
            JOptionPane.showMessageDialog(this, "Estado cambiado a: " + nuevo);
            listar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}