package vista;


import controlador.FacturacionControlador;
import dao.FacturaDAO;
import dao.PagoDAO;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Formulario de registro de pagos (Necesidad 8).
 */


public class FrmRegistroPago extends JFrame {

    private JTextField txtFactura, txtCodigoPago, txtValor, txtReferencia;
    private JComboBox<String> cbxMetodo;
    private JSpinner spnFecha;
    private JLabel lblInfo;
    private JButton btnBuscar, btnRegistrar;
    private JTable tblPagos;
    private DefaultTableModel modelo;
    private Factura facturaActual;
    private final FacturacionControlador fCtrl = new FacturacionControlador();
    private final FacturaDAO facturaDAO = new FacturaDAO();
    private final PagoDAO pagoDAO = new PagoDAO();

    public FrmRegistroPago() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("EAAB - Registro de Pagos");
        setSize(1050, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setBackground(UIStyle.BG_APP);

        // Cabecera
        JPanel header = new JPanel(null);
        header.setBackground(UIStyle.SUCCESS);
        header.setBounds(0, 0, 1050, 60);
        JLabel titulo = new JLabel("REGISTRO DE PAGOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(40, 15, 500, 30);
        header.add(titulo);
        add(header);

        // ===== Panel factura =====
        JPanel pnlF = UIStyle.crearPanelBlanco();
        pnlF.setBounds(30, 80, 980, 130);

        pnlF.add(lbl("N° Factura:", 20, 20, 100));
        txtFactura = UIStyle.crearCampoTexto();
        txtFactura.setBounds(130, 20, 250, 30);
        pnlF.add(txtFactura);

        btnBuscar = UIStyle.crearBoton("Buscar Factura", UIStyle.PRIMARY);
        btnBuscar.setBounds(400, 20, 170, 30);
        pnlF.add(btnBuscar);

        lblInfo = new JLabel("—");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInfo.setForeground(UIStyle.SUCCESS);
        lblInfo.setBounds(20, 80, 940, 25);
        pnlF.add(lblInfo);

        add(pnlF);

        // ===== Panel datos pago =====
        JPanel pnlP = UIStyle.crearPanelBlanco();
        pnlP.setBounds(30, 220, 980, 220);

        pnlP.add(lbl("Código Pago:", 20, 20, 100));
        txtCodigoPago = UIStyle.crearCampoTexto();
        txtCodigoPago.setBounds(140, 20, 220, 30);
        txtCodigoPago.setEditable(false); // BLOQUEADO: El sistema lo genera automáticamente
        txtCodigoPago.setBackground(new Color(230, 230, 230)); // Color gris para indicar que no se edita
        pnlP.add(txtCodigoPago);

        pnlP.add(lbl("Fecha:", 390, 20, 60));
        spnFecha = new JSpinner(new SpinnerDateModel());
        spnFecha.setEditor(new JSpinner.DateEditor(spnFecha, "yyyy-MM-dd"));
        spnFecha.setBounds(450, 20, 180, 30);
        pnlP.add(spnFecha);

        pnlP.add(lbl("Valor ($):", 20, 70, 100));
        txtValor = UIStyle.crearCampoTexto();
        txtValor.setBounds(140, 70, 220, 30);
        pnlP.add(txtValor);

        pnlP.add(lbl("Método:", 390, 70, 70));
        cbxMetodo = new JComboBox<>(new String[]{"TARJETA","TRANSFERENCIA","EFECTIVO","PSE"});
        cbxMetodo.setBounds(470, 70, 210, 30);
        pnlP.add(cbxMetodo);

        pnlP.add(lbl("Referencia:", 20, 120, 100));
        txtReferencia = UIStyle.crearCampoTexto();
        txtReferencia.setBounds(140, 120, 540, 30);
        pnlP.add(txtReferencia);

        btnRegistrar = UIStyle.crearBoton("Registrar Pago", UIStyle.SUCCESS);
        btnRegistrar.setBounds(20, 170, 200, 40);
        pnlP.add(btnRegistrar);

        add(pnlP);

        // ===== Tabla historial =====
        JLabel lblHist = new JLabel("Historial de Pagos de la Factura:");
        lblHist.setFont(UIStyle.FONT_SUBTITLE);
        lblHist.setForeground(UIStyle.DARK_TEXT);
        lblHist.setBounds(30, 455, 400, 25);
        add(lblHist);

        modelo = new DefaultTableModel(new Object[]{
            "Código","Fecha","Valor","Método","Referencia"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPagos = new JTable(modelo);
        tblPagos.setFont(UIStyle.FONT_TEXT);
        tblPagos.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(tblPagos);
        scroll.setBounds(30, 485, 980, 270);
        add(scroll);

        // ===== Listeners =====
        btnBuscar.addActionListener(e -> buscar());
        btnRegistrar.addActionListener(e -> registrar());

        // Generar el primer código al abrir la ventana
        generarCodigoPago();
    }

    private JLabel lbl(String t, int x, int y, int w) {
        JLabel l = UIStyle.crearLabel(t);
        l.setBounds(x, y, w, 25);
        return l;
    }

    // Método que autogenera un código único basado en milisegundos
    private void generarCodigoPago() {
        String cod = "PAG-" + System.currentTimeMillis();
        txtCodigoPago.setText(cod);
    }

    private void buscar() {
        try {
            facturaActual = facturaDAO.buscar(txtFactura.getText().trim());
            if (facturaActual == null) {
                lblInfo.setText("Factura no encontrada.");
                modelo.setRowCount(0);
                return;
            }
            
            String totalFmt = String.format(Locale.US, "$%,.2f", facturaActual.getValorTotal());
            String pagadoFmt = String.format(Locale.US, "$%,.2f", facturaActual.getTotalPagado());
            String saldoFmt = String.format(Locale.US, "$%,.2f", facturaActual.getSaldoPendiente());
            
            lblInfo.setText("Factura " + facturaActual.getNumero() +
                    " | Total: " + totalFmt +
                    " | Pagado: " + pagadoFmt +
                    " | Saldo pendiente: " + saldoFmt +
                    " | Estado: " + facturaActual.getEstadoPago());
            cargarPagos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarPagos() {
        try {
            modelo.setRowCount(0);
            List<Pago> ps = pagoDAO.listarPorFactura(facturaActual.getNumero());
            for (Pago p : ps) {
                String valorFmt = String.format(Locale.US, "$%,.2f", p.getValor());
                modelo.addRow(new Object[]{
                    p.getCodigo(),
                    p.getFecha(),
                    valorFmt,
                    p.getMetodoPago(),
                    p.getReferencia()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrar() {
        if (facturaActual == null) {
            JOptionPane.showMessageDialog(this, "Primero busque una factura.");
            return;
        }
        try {
            // Ya no validamos si el campo está vacío porque el sistema lo genera solo
            String cod = txtCodigoPago.getText().trim();
            
            LocalDate f = new java.sql.Date(
                    ((java.util.Date)spnFecha.getValue()).getTime()).toLocalDate();
            
            String valStr = txtValor.getText().trim().replace(",", ".");
            double valor = Double.parseDouble(valStr);
            
            if (valor > facturaActual.getSaldoPendiente() + 0.01) { // +0.01 por precisión
                String saldoFmt = String.format(Locale.US, "$%,.2f", facturaActual.getSaldoPendiente());
                JOptionPane.showMessageDialog(this,
                        "El valor excede el saldo pendiente (" + saldoFmt + ").");
                return;
            }
            
            Pago p = new Pago(cod, f, valor,
                    MetodoPago.valueOf((String)cbxMetodo.getSelectedItem()),
                    txtReferencia.getText().trim());
            
            fCtrl.registrarPago(facturaActual, p);
            
            String valorFmt = String.format(Locale.US, "$%,.2f", valor);
            JOptionPane.showMessageDialog(this, "Pago de " + valorFmt + " registrado exitosamente.");
            
            // Limpiar campos y generar un NUEVO código para el próximo pago
            txtValor.setText("");
            txtReferencia.setText("");
            generarCodigoPago(); 
            
            buscar(); // Refresca los datos
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El valor ingresado no es un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // Aquí atrapamos cualquier otro error (como el Duplicate Entry) por si acaso
            JOptionPane.showMessageDialog(this, "Error al registrar pago: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}