package vista;


import controlador.UsuarioControlador;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Date;


public class FrmRegistroUsuario extends JFrame {

    private JTextField txtIdentificacion, txtNombre, txtApellido, txtDireccion, txtTelefono, txtCorreo;
    private JSpinner spnFechaNac;
    private JTextField txtCodigo;
    private JComboBox<String> cbxEstrato, cbxCanal, cbxTipo;
    private JPanel pnlEspecifico;
    private CardLayout cardLayout;
    
    // Campos dinámicos
    private JTextField txtHabitantes, txtEstablecimiento, txtArea, txtCodigoInd, txtProceso, txtCarga, txtEntidad, txtAsignacion;
    private JComboBox<String> cbxActividad;
    
    private final UsuarioControlador ctrl = new UsuarioControlador();

    public FrmRegistroUsuario() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("EAAB - Registrar Suscriptor");
        setSize(1100, 780);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setBackground(UIStyle.BG_APP);

        // --- CABECERA ---
        JPanel header = new JPanel(null);
        header.setBackground(UIStyle.PRIMARY);
        header.setBounds(0, 0, 1100, 60);
        JLabel titulo = new JLabel("REGISTRO DE NUEVO SUSCRIPTOR");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(40, 15, 500, 30);
        header.add(titulo);
        add(header);

        // --- PANEL DATOS PERSONALES ---
        JPanel pnlPersonales = UIStyle.crearPanelBlanco();
        pnlPersonales.setBounds(40, 80, 1020, 200);
        
        JLabel lblSec1 = new JLabel("1. DATOS PERSONALES");
        lblSec1.setFont(UIStyle.FONT_SUBTITLE);
        lblSec1.setForeground(UIStyle.PRIMARY);
        lblSec1.setBounds(20, 10, 300, 25);
        pnlPersonales.add(lblSec1);

        pnlPersonales.add(lbl("Identificación:", 20, 45));
        txtIdentificacion = campo(140, 45, 200); pnlPersonales.add(txtIdentificacion);

        pnlPersonales.add(lbl("Nombre:", 370, 45));
        txtNombre = campo(440, 45, 200); pnlPersonales.add(txtNombre);

        pnlPersonales.add(lbl("Apellido:", 670, 45));
        txtApellido = campo(740, 45, 200); pnlPersonales.add(txtApellido);

        pnlPersonales.add(lbl("Fecha Nacimiento:", 20, 95));
        spnFechaNac = new JSpinner(new SpinnerDateModel());
        spnFechaNac.setEditor(new JSpinner.DateEditor(spnFechaNac, "yyyy-MM-dd"));
        spnFechaNac.setBounds(140, 95, 200, 30); pnlPersonales.add(spnFechaNac);

        pnlPersonales.add(lbl("Teléfono (7-10):", 370, 95));
        txtTelefono = campo(490, 95, 150); pnlPersonales.add(txtTelefono);

        pnlPersonales.add(lbl("Correo:", 670, 95));
        txtCorreo = campo(740, 95, 200); pnlPersonales.add(txtCorreo);

        pnlPersonales.add(lbl("Dirección:", 20, 145));
        txtDireccion = campo(140, 145, 800); pnlPersonales.add(txtDireccion);

        add(pnlPersonales);

        // --- PANEL SUSCRIPTOR ---
        JPanel pnlSuscriptor = UIStyle.crearPanelBlanco();
        pnlSuscriptor.setBounds(40, 300, 1020, 130);

        JLabel lblSec2 = new JLabel("2. DATOS DEL SUSCRIPTOR");
        lblSec2.setFont(UIStyle.FONT_SUBTITLE);
        lblSec2.setForeground(UIStyle.PRIMARY);
        lblSec2.setBounds(20, 10, 300, 25);
        pnlSuscriptor.add(lblSec2);

        pnlSuscriptor.add(lbl("Código Suscriptor:", 20, 45));
        txtCodigo = campo(160, 45, 200); pnlSuscriptor.add(txtCodigo);

        pnlSuscriptor.add(lbl("Estrato (1-6):", 390, 45));
        cbxEstrato = new JComboBox<>(new String[]{"1","2","3","4","5","6"});
        cbxEstrato.setBounds(500, 45, 150, 30); pnlSuscriptor.add(cbxEstrato);

        pnlSuscriptor.add(lbl("Canal Notificación:", 680, 45));
        cbxCanal = new JComboBox<>(new String[]{"EMAIL","SMS","PUSH","LLAMADA"});
        cbxCanal.setBounds(820, 45, 150, 30); pnlSuscriptor.add(cbxCanal);

        pnlSuscriptor.add(lbl("Tipo de Usuario:", 20, 85));
        cbxTipo = new JComboBox<>(new String[]{"RESIDENCIAL","COMERCIAL","INDUSTRIAL","OFICIAL"});
        cbxTipo.setBounds(160, 85, 250, 30); 
        cbxTipo.setFont(UIStyle.FONT_SUBTITLE);
        cbxTipo.setForeground(UIStyle.SUCCESS);
        pnlSuscriptor.add(cbxTipo);

        add(pnlSuscriptor);

        // --- PANEL DINÁMICO ---
        JPanel pnlWrapper = UIStyle.crearPanelBlanco();
        pnlWrapper.setBounds(40, 450, 1020, 180);

        JLabel lblSec3 = new JLabel("3. DATOS ESPECÍFICOS DEL TIPO SELECCIONADO");
        lblSec3.setFont(UIStyle.FONT_SUBTITLE);
        lblSec3.setForeground(UIStyle.PRIMARY);
        lblSec3.setBounds(20, 10, 400, 25);
        pnlWrapper.add(lblSec3);

        cardLayout = new CardLayout();
        pnlEspecifico = new JPanel(cardLayout);
        pnlEspecifico.setBounds(20, 40, 980, 130);
        pnlEspecifico.setBackground(Color.WHITE);

        // Residencial
        JPanel pnlRes = new JPanel(null); pnlRes.setBackground(Color.WHITE);
        pnlRes.add(lbl("Número de Habitantes:", 20, 20));
        txtHabitantes = campo(180, 20, 200); pnlRes.add(txtHabitantes);
        pnlEspecifico.add(pnlRes, "RESIDENCIAL");

        // Comercial
        JPanel pnlCom = new JPanel(null); pnlCom.setBackground(Color.WHITE);
        pnlCom.add(lbl("Nombre Establecimiento:", 20, 20));
        txtEstablecimiento = campo(190, 20, 250); pnlCom.add(txtEstablecimiento);
        pnlCom.add(lbl("Actividad:", 460, 20));
        cbxActividad = new JComboBox<>(new String[]{"restaurante","panadería","supermercado"});
        cbxActividad.setBounds(550, 20, 180, 30); pnlCom.add(cbxActividad);
        pnlCom.add(lbl("Área Operativa (m²):", 20, 70));
        txtArea = campo(190, 70, 250); pnlCom.add(txtArea);
        pnlEspecifico.add(pnlCom, "COMERCIAL");

        // Industrial
        JPanel pnlInd = new JPanel(null); pnlInd.setBackground(Color.WHITE);
        pnlInd.add(lbl("Código Industrial:", 20, 20));
        txtCodigoInd = campo(170, 20, 200); pnlInd.add(txtCodigoInd);
        pnlInd.add(lbl("Proceso Productivo:", 400, 20));
        txtProceso = campo(530, 20, 250); pnlInd.add(txtProceso);
        pnlInd.add(lbl("Carga Contaminante (DBO):", 20, 70));
        txtCarga = campo(220, 70, 200); pnlInd.add(txtCarga);
        pnlEspecifico.add(pnlInd, "INDUSTRIAL");

        // Oficial
        JPanel pnlOfi = new JPanel(null); pnlOfi.setBackground(Color.WHITE);
        pnlOfi.add(lbl("Entidad Pública:", 20, 20));
        txtEntidad = campo(170, 20, 400); pnlOfi.add(txtEntidad);
        pnlOfi.add(lbl("Asignación Presupuestal ($):", 20, 70));
        txtAsignacion = campo(240, 70, 200); pnlOfi.add(txtAsignacion);
        pnlEspecifico.add(pnlOfi, "OFICIAL");

        pnlWrapper.add(pnlEspecifico);
        add(pnlWrapper);

        // Acción dinámica
        cbxTipo.addActionListener(e -> cardLayout.show(pnlEspecifico, (String) cbxTipo.getSelectedItem()));

        // --- BOTONES ---
        JButton btnGuardar = UIStyle.crearBoton("Guardar Registro", UIStyle.SUCCESS);
        btnGuardar.setBounds(650, 660, 190, 45);
        btnGuardar.addActionListener(e -> guardar());
        add(btnGuardar);

        JButton btnLimpiar = UIStyle.crearBoton("Limpiar Formulario", UIStyle.WARNING);
        btnLimpiar.setBounds(850, 660, 190, 45);
        btnLimpiar.addActionListener(e -> limpiar());
        add(btnLimpiar);
    }

    // Helpers para no repetir código
    private JLabel lbl(String t, int x, int y) {
        JLabel l = UIStyle.crearLabel(t); l.setBounds(x, y, 180, 25); return l;
    }
    private JTextField campo(int x, int y, int w) {
        JTextField t = UIStyle.crearCampoTexto(); t.setBounds(x, y, w, 30); return t;
    }

    private void guardar() {
        try {
            String id = txtIdentificacion.getText().trim();
            String nom = txtNombre.getText().trim();
            String ape = txtApellido.getText().trim();
            String dir = txtDireccion.getText().trim();
            String tel = txtTelefono.getText().trim();
            String cor = txtCorreo.getText().trim();
            Date d = (Date) spnFechaNac.getValue();
            LocalDate fn = new java.sql.Date(d.getTime()).toLocalDate();
            String cod = txtCodigo.getText().trim();
            int estrato = Integer.parseInt((String) cbxEstrato.getSelectedItem());
            String canalS = (String) cbxCanal.getSelectedItem();
            String tipoS = (String) cbxTipo.getSelectedItem();

            Usuario u;
            switch (tipoS) {
                case "RESIDENCIAL":
                    u = new Residencial(id, nom, ape, cod, estrato, Integer.parseInt(txtHabitantes.getText().trim()));
                    break;
                case "COMERCIAL":
                    u = new Comercial(id, nom, ape, cod, estrato, txtEstablecimiento.getText().trim(), 
                            (String) cbxActividad.getSelectedItem(), Double.parseDouble(txtArea.getText().trim()));
                    break;
                case "INDUSTRIAL":
                    u = new Industrial(id, nom, ape, cod, estrato, txtCodigoInd.getText().trim(), 
                            txtProceso.getText().trim(), Double.parseDouble(txtCarga.getText().trim()));
                    break;
                default:
                    u = new Oficial(id, nom, ape, cod, estrato, txtEntidad.getText().trim(), 
                            Double.parseDouble(txtAsignacion.getText().trim()));
            }

            u.setFechaNacimiento(fn);
            u.setDireccion(dir); u.setTelefono(tel); u.setCorreo(cor);
            u.setCanalNotificacionPreferido(CanalNotificacion.valueOf(canalS));

            ctrl.registrar(u);
            JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtIdentificacion.setText(""); txtNombre.setText(""); txtApellido.setText("");
        txtDireccion.setText(""); txtTelefono.setText(""); txtCorreo.setText("");
        txtCodigo.setText(""); txtHabitantes.setText(""); txtEstablecimiento.setText("");
        txtArea.setText(""); txtCodigoInd.setText(""); txtProceso.setText("");
        txtCarga.setText(""); txtEntidad.setText(""); txtAsignacion.setText("");
        cbxEstrato.setSelectedIndex(0); cbxCanal.setSelectedIndex(0); cbxTipo.setSelectedIndex(0);
    }
}