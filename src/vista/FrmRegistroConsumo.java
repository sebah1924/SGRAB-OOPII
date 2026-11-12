package vista;


import controlador.ConsumoControlador;
import controlador.UsuarioControlador;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Formulario de registro de consumos.
 * Cubre el ciclo completo del agua: lectura → promedio → factura.
 */


public class FrmRegistroConsumo extends JFrame {

    private JTextField txtCodigoSuscriptor, txtVolumen;
    private JSpinner spnFechaLectura;
    private JComboBox<String> cbxTipoLectura;
    private JLabel lblInfoUsuario, lblSugerencia;
    private JButton btnBuscarUsuario, btnRegistrar, btnLimpiar, btnSugerirPromedio;
    private JTable tblHistorial;
    private DefaultTableModel modelo;
    private Usuario usuarioActual;
    private final UsuarioControlador uCtrl = new UsuarioControlador();
    private final ConsumoControlador cCtrl = new ConsumoControlador();

    public FrmRegistroConsumo() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("EAAB - Registro de Consumos");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setBackground(UIStyle.BG_APP);

        JPanel header = new JPanel(null);
        header.setBackground(new Color(0, 102, 153));
        header.setBounds(0, 0, 1100, 60);
        JLabel titulo = new JLabel("REGISTRO DE LECTURAS DE CONSUMO");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(40, 15, 700, 30);
        header.add(titulo);
        add(header);

        JPanel pnlB = UIStyle.crearPanelBlanco();
        pnlB.setBounds(30, 80, 1040, 110);
        pnlB.add(lbl("Código Suscriptor:", 20, 20, 130));
        txtCodigoSuscriptor = UIStyle.crearCampoTexto();
        txtCodigoSuscriptor.setBounds(160, 20, 250, 30);
        pnlB.add(txtCodigoSuscriptor);
        btnBuscarUsuario = UIStyle.crearBoton("Buscar Usuario", UIStyle.PRIMARY);
        btnBuscarUsuario.setBounds(430, 20, 160, 30);
        pnlB.add(btnBuscarUsuario);
        lblInfoUsuario = new JLabel("Ingrese un código de suscriptor.");
        lblInfoUsuario.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfoUsuario.setForeground(Color.GRAY);
        lblInfoUsuario.setBounds(20, 65, 1000, 25);
        pnlB.add(lblInfoUsuario);
        add(pnlB);

        JPanel pnlR = UIStyle.crearPanelBlanco();
        pnlR.setBounds(30, 200, 1040, 180);

        pnlR.add(lbl("Fecha Lectura:", 20, 20, 100));
        spnFechaLectura = new JSpinner(new SpinnerDateModel());
        spnFechaLectura.setEditor(new JSpinner.DateEditor(spnFechaLectura, "yyyy-MM-dd"));
        spnFechaLectura.setBounds(130, 20, 180, 30);
        pnlR.add(spnFechaLectura);

        pnlR.add(lbl("Tipo Lectura:", 340, 20, 100));
        cbxTipoLectura = new JComboBox<>(new String[]{"REAL", "ESTIMADA"});
        cbxTipoLectura.setBounds(450, 20, 200, 30);
        pnlR.add(cbxTipoLectura);

        pnlR.add(lbl("Volumen (m³):", 20, 70, 110));
        txtVolumen = UIStyle.crearCampoTexto();
        txtVolumen.setBounds(130, 70, 220, 30);
        pnlR.add(txtVolumen);

        btnSugerirPromedio = UIStyle.crearBoton("Usar Promedio", UIStyle.WARNING);
        btnSugerirPromedio.setBounds(370, 70, 160, 30);
        pnlR.add(btnSugerirPromedio);

        lblSugerencia = new JLabel("");
        lblSugerencia.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSugerencia.setForeground(new Color(0, 102, 153));
        lblSugerencia.setBounds(550, 72, 460, 25);
        pnlR.add(lblSugerencia);

        btnRegistrar = UIStyle.crearBoton("Registrar Consumo", UIStyle.SUCCESS);
        btnRegistrar.setBounds(20, 120, 200, 40);
        pnlR.add(btnRegistrar);
        btnLimpiar = UIStyle.crearBoton("Limpiar", UIStyle.DANGER);
        btnLimpiar.setBounds(240, 120, 140, 40);
        pnlR.add(btnLimpiar);
        add(pnlR);

        JLabel lblHist = new JLabel("Historial de Consumos del Usuario:");
        lblHist.setFont(UIStyle.FONT_SUBTITLE);
        lblHist.setForeground(UIStyle.DARK_TEXT);
        lblHist.setBounds(30, 400, 400, 25);
        add(lblHist);

        modelo = new DefaultTableModel(new Object[]{"ID", "Volumen (m³)", "Fecha Lectura", "Tipo", "Tarifa Aplicada"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHistorial = new JTable(modelo);
        tblHistorial.setFont(UIStyle.FONT_TEXT);
        tblHistorial.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(tblHistorial);
        scroll.setBounds(30, 430, 1040, 300);
        add(scroll);

        btnBuscarUsuario.addActionListener(e -> buscarUsuario());
        btnRegistrar.addActionListener(e -> registrar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnSugerirPromedio.addActionListener(e -> sugerirPromedio());
        cbxTipoLectura.addActionListener(e -> {
            if ("ESTIMADA".equals(cbxTipoLectura.getSelectedItem())) sugerirPromedio();
        });
    }

    private JLabel lbl(String t, int x, int y, int w) {
        JLabel l = UIStyle.crearLabel(t); l.setBounds(x, y, w, 25); return l;
    }

    private void buscarUsuario() {
        try {
            usuarioActual = uCtrl.buscarPorCodigo(txtCodigoSuscriptor.getText().trim());
            if (usuarioActual == null) { lblInfoUsuario.setText("Usuario no encontrado."); modelo.setRowCount(0); return; }
            if (usuarioActual.getFechaBaja() != null) { lblInfoUsuario.setText("Usuario inactivo."); return; }
            
            // Formateamos el promedio con punto decimal para mostrarlo
            String promFmt = String.format(Locale.US, "%.2f", usuarioActual.getConsumoPromedio());
            lblInfoUsuario.setText("Usuario: " + usuarioActual.getNombre() + " " + usuarioActual.getApellido() + 
                    " | Promedio: " + promFmt + " m³");
            cargarHistorial();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void cargarHistorial() {
        if (usuarioActual == null) return;
        try {
            modelo.setRowCount(0);
            for (Consumo c : cCtrl.historial(usuarioActual)) {
                String tarifaCod = c.getTarifaAplicada() != null ? c.getTarifaAplicada().getCodigo() : "—";
                modelo.addRow(new Object[]{c.getId(), c.getVolumen(), c.getFechaLectura(), c.getTipoLectura(), tarifaCod});
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void sugerirPromedio() {
        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(this, "Busque un usuario primero.");
            return;
        }
        try {
            double sug = cCtrl.sugerirVolumenEstimado(usuarioActual);
            if (sug > 0) { 
                // FORZAMOS EL PUNTO DECIMAL usando Locale.US
                // Esto evita que Java lo cambie por coma en sistemas en español
                String sugFmt = String.format(Locale.US, "%.2f", sug);
                txtVolumen.setText(sugFmt); 
                lblSugerencia.setText("Sugerido: " + sugFmt + " m³"); 
            } else {
                lblSugerencia.setText("Sin historial previo para promediar.");
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void registrar() {
        if (usuarioActual == null) { JOptionPane.showMessageDialog(this, "Busque un usuario primero."); return; }
        try {
            // LEEMOS EL TEXTO Y REEMPLAZAMOS COMA POR PUNTO POR SI EL USUARIO ESCRIBE CON COMA
            String volStr = txtVolumen.getText().trim().replace(",", ".");
            if (volStr.isEmpty()) throw new Exception("Ingrese el volumen.");
            
            double vol = Double.parseDouble(volStr);
            LocalDate fecha = new java.sql.Date(((java.util.Date) spnFechaLectura.getValue()).getTime()).toLocalDate();
            TipoLectura tipo = TipoLectura.valueOf((String) cbxTipoLectura.getSelectedItem());
            
            Consumo c = new Consumo(vol, fecha, tipo);
            cCtrl.registrar(usuarioActual, c);
            
            String nuevoProm = String.format(Locale.US, "%.2f", usuarioActual.getConsumoPromedio());
            JOptionPane.showMessageDialog(this, "Consumo registrado.\nNuevo promedio: " + nuevoProm + " m³");
            
            txtVolumen.setText(""); 
            lblSugerencia.setText("");
            buscarUsuario();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El volumen ingresado no es un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void limpiar() { 
        txtVolumen.setText(""); 
        lblSugerencia.setText(""); 
    }
}