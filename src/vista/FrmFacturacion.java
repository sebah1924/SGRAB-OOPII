package vista;


import controlador.*;
import dao.TarifaDAO;
import modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Formulario de facturación.
 * Cubre las Necesidades 5, 6 y 9 completas.
 */

public class FrmFacturacion extends JFrame {

    private JTextField txtCodigoUsuario, txtNumFactura;
    private JSpinner spnIni, spnFin, spnVenc;
    private JComboBox<String> cbxEstado;
    private JSpinner spnFiltroDesde, spnFiltroHasta;
    private JButton btnBuscarUser, btnEmitir, btnFiltrar, btnVerTodas;
    private JTable tblFacturas;
    private DefaultTableModel modelo;
    private JLabel lblInfoUser;
    private Usuario usuarioActual;
    
    private final UsuarioControlador uCtrl = new UsuarioControlador();
    private final FacturacionControlador fCtrl = new FacturacionControlador();
    private final TarifaDAO tarifaDAO = new TarifaDAO();

    public FrmFacturacion() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("EAAB - Facturación");
        setSize(1150, 850);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setBackground(UIStyle.BG_APP);

        JPanel header = new JPanel(null);
        header.setBackground(UIStyle.SUCCESS);
        header.setBounds(0, 0, 1150, 60);
        JLabel titulo = new JLabel("EMISIÓN Y CONSULTA DE FACTURAS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(40, 15, 700, 30);
        header.add(titulo);
        add(header);

        // Panel búsqueda
        JPanel pnlB = UIStyle.crearPanelBlanco();
        pnlB.setBounds(30, 80, 1080, 110);
        pnlB.add(lbl("Código Suscriptor:", 20, 20, 130));
        txtCodigoUsuario = UIStyle.crearCampoTexto();
        txtCodigoUsuario.setBounds(150, 20, 250, 30);
        pnlB.add(txtCodigoUsuario);
        btnBuscarUser = UIStyle.crearBoton("Buscar Usuario", UIStyle.PRIMARY);
        btnBuscarUser.setBounds(420, 20, 160, 30);
        pnlB.add(btnBuscarUser);
        lblInfoUser = new JLabel("Ingrese un código y presione Buscar.");
        lblInfoUser.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfoUser.setForeground(Color.GRAY);
        lblInfoUser.setBounds(20, 65, 1040, 25);
        pnlB.add(lblInfoUser);
        add(pnlB);

        // Panel emisión
        JPanel pnlE = UIStyle.crearPanelBlanco();
        pnlE.setBounds(30, 200, 1080, 150);
        
        pnlE.add(lbl("N° Factura:", 20, 20, 90));
        txtNumFactura = UIStyle.crearCampoTexto();
        txtNumFactura.setBounds(120, 20, 180, 30);
        // CORRECCIÓN: Bloquear el campo para que no se edite a mano
        txtNumFactura.setEditable(false);
        txtNumFactura.setBackground(new Color(230, 230, 230));
        pnlE.add(txtNumFactura);
        
        pnlE.add(lbl("Periodo Inicio:", 320, 20, 100));
        spnIni = new JSpinner(new SpinnerDateModel());
        spnIni.setEditor(new JSpinner.DateEditor(spnIni, "yyyy-MM-dd"));
        spnIni.setBounds(430, 20, 150, 30);
        pnlE.add(spnIni);
        pnlE.add(lbl("Periodo Fin:", 600, 20, 90));
        spnFin = new JSpinner(new SpinnerDateModel());
        spnFin.setEditor(new JSpinner.DateEditor(spnFin, "yyyy-MM-dd"));
        spnFin.setBounds(700, 20, 150, 30);
        pnlE.add(spnFin);
        pnlE.add(lbl("Vencimiento:", 20, 80, 100));
        spnVenc = new JSpinner(new SpinnerDateModel());
        spnVenc.setEditor(new JSpinner.DateEditor(spnVenc, "yyyy-MM-dd"));
        spnVenc.setBounds(130, 80, 170, 30);
        pnlE.add(spnVenc);
        btnEmitir = UIStyle.crearBoton("Emitir Factura", UIStyle.SUCCESS);
        btnEmitir.setBounds(330, 80, 200, 40);
        pnlE.add(btnEmitir);
        add(pnlE);

        // Panel filtros
        JPanel pnlF = UIStyle.crearPanelBlanco();
        pnlF.setBounds(30, 360, 1080, 110);
        pnlF.add(lbl("Desde:", 20, 35, 60));
        spnFiltroDesde = new JSpinner(new SpinnerDateModel());
        spnFiltroDesde.setEditor(new JSpinner.DateEditor(spnFiltroDesde, "yyyy-MM-dd"));
        spnFiltroDesde.setBounds(90, 35, 160, 30);
        pnlF.add(spnFiltroDesde);
        pnlF.add(lbl("Hasta:", 270, 35, 60));
        spnFiltroHasta = new JSpinner(new SpinnerDateModel());
        spnFiltroHasta.setEditor(new JSpinner.DateEditor(spnFiltroHasta, "yyyy-MM-dd"));
        spnFiltroHasta.setBounds(340, 35, 160, 30);
        pnlF.add(spnFiltroHasta);
        pnlF.add(lbl("Estado:", 520, 35, 60));
        cbxEstado = new JComboBox<>(new String[]{"TODOS","PENDIENTE","PAGADA","VENCIDA","EN_ACUERDO_PAGO"});
        cbxEstado.setBounds(590, 35, 200, 30);
        pnlF.add(cbxEstado);
        btnFiltrar = UIStyle.crearBoton("Aplicar Filtros", UIStyle.INFO);
        btnFiltrar.setBounds(800, 30, 130, 35);
        pnlF.add(btnFiltrar);
        btnVerTodas = UIStyle.crearBoton("Ver TODAS", UIStyle.WARNING);
        btnVerTodas.setBounds(940, 30, 120, 35);
        pnlF.add(btnVerTodas);
        add(pnlF);

        // Tabla
        modelo = new DefaultTableModel(new Object[]{"Número","Periodo","Emisión","Vencimiento","Total","Pagado","Saldo","Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblFacturas = new JTable(modelo);
        tblFacturas.setFont(UIStyle.FONT_TEXT);
        tblFacturas.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(tblFacturas);
        scroll.setBounds(30, 480, 1080, 320);
        add(scroll);

        // Precarga de fechas
        LocalDate hoy = LocalDate.now();
        spnIni.setValue(java.sql.Date.valueOf(hoy.withDayOfMonth(1)));
        spnFin.setValue(java.sql.Date.valueOf(hoy.withDayOfMonth(hoy.lengthOfMonth())));
        spnVenc.setValue(java.sql.Date.valueOf(hoy.plusDays(15)));
        spnFiltroDesde.setValue(java.sql.Date.valueOf(hoy.minusMonths(6)));
        spnFiltroHasta.setValue(java.sql.Date.valueOf(hoy));

        // Listeners
        btnBuscarUser.addActionListener(e -> buscarUsuario());
        btnEmitir.addActionListener(e -> emitir());
        btnFiltrar.addActionListener(e -> cargarFacturas(false));
        btnVerTodas.addActionListener(e -> cargarFacturas(true));

        // Generar el primer número al abrir
        generarNumeroFactura();
    }

    private JLabel lbl(String t, int x, int y, int w) {
        JLabel l = UIStyle.crearLabel(t);
        l.setBounds(x, y, w, 25);
        return l;
    }

    // CORRECCIÓN: Método que autogenera un número de factura único
    private void generarNumeroFactura() {
        txtNumFactura.setText("FAC-" + System.currentTimeMillis());
    }

    private void buscarUsuario() {
        try {
            usuarioActual = uCtrl.buscarPorCodigo(txtCodigoUsuario.getText().trim());
            if (usuarioActual == null) {
                lblInfoUser.setText("Usuario no encontrado.");
                return;
            }
            Tarifa t = tarifaDAO.buscarVigente(usuarioActual.getTipoUsuario(), LocalDate.now());
            String info = "Usuario: " + usuarioActual.getNombre() + " " + usuarioActual.getApellido() +
                    " | Tipo: " + usuarioActual.getTipoUsuario() + " | Estrato: " + usuarioActual.getEstrato() +
                    " | Estado: " + usuarioActual.getEstadoServicio();
            if (t != null) {
                info += " | Tarifa: " + t.getCodigo() + " (Fijo $" + t.getCargoFijo() + " + Var $" + t.getCargoVariable() + "/m³)";
            }
            lblInfoUser.setText(info);
            sugerirProximoPeriodo();
            cargarFacturas(false);
            // Generar número por si se va a emitir de una vez
            generarNumeroFactura(); 
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sugerirProximoPeriodo() {
        try {
            List<Factura> facturas = fCtrl.listarFacturas(usuarioActual);
            LocalDate ini, fin;
            if (facturas.isEmpty()) {
                LocalDate hoy = LocalDate.now();
                ini = hoy.withDayOfMonth(1);
                fin = hoy.withDayOfMonth(hoy.lengthOfMonth());
            } else {
                LocalDate ultimaFin = null;
                for (Factura f : facturas) {
                    if (ultimaFin == null || f.getPeriodo().getFechaFin().isAfter(ultimaFin)) {
                        ultimaFin = f.getPeriodo().getFechaFin();
                    }
                }
                ini = ultimaFin.plusDays(1);
                fin = ini.plusMonths(1).minusDays(1);
            }
            spnIni.setValue(java.sql.Date.valueOf(ini));
            spnFin.setValue(java.sql.Date.valueOf(fin));
            spnVenc.setValue(java.sql.Date.valueOf(fin.plusDays(15)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void emitir() {
        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(this, "Primero busque un usuario.");
            return;
        }
        try {
            // CORRECCIÓN: Ya no leemos el campo, lo generamos aquí para asegurar que sea nuevo
            String num = txtNumFactura.getText().trim();
            if (num.isEmpty()) {
                generarNumeroFactura();
                num = txtNumFactura.getText().trim();
            }
            
            LocalDate ini = new java.sql.Date(((java.util.Date)spnIni.getValue()).getTime()).toLocalDate();
            LocalDate fin = new java.sql.Date(((java.util.Date)spnFin.getValue()).getTime()).toLocalDate();
            LocalDate venc = new java.sql.Date(((java.util.Date)spnVenc.getValue()).getTime()).toLocalDate();
            
            Periodo p = new Periodo(ini, fin);
            Factura f = fCtrl.emitir(usuarioActual, p, venc, num);
            
            String totalFormat = String.format(Locale.US, "$%,.2f", f.getValorTotal());
            
            JOptionPane.showMessageDialog(this,
                "✔ Factura emitida correctamente.\n" +
                "Número: " + f.getNumero() + "\n" +
                "Periodo: " + f.getPeriodo() + "\n" +
                "Total: " + totalFormat);
            
            // CORRECCIÓN: Generar nuevo número y avanzar periodo para la próxima factura
            generarNumeroFactura();
            sugerirProximoPeriodo();
            cargarFacturas(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            // Si falló, generamos otro código por si el anterior quedó "quemado"
            generarNumeroFactura();
        }
    }

    private void cargarFacturas(boolean sinFiltro) {
        if (usuarioActual == null) return;
        try {
            modelo.setRowCount(0);
            List<Factura> fs = fCtrl.listarFacturas(usuarioActual);

            LocalDate desde = null, hasta = null;
            String estadoFiltro = "TODOS";
            if (!sinFiltro) {
                desde = new java.sql.Date(((java.util.Date)spnFiltroDesde.getValue()).getTime()).toLocalDate();
                hasta = new java.sql.Date(((java.util.Date)spnFiltroHasta.getValue()).getTime()).toLocalDate();
                estadoFiltro = (String) cbxEstado.getSelectedItem();
            }

            for (Factura f : fs) {
                if (!sinFiltro) {
                    if (f.getFechaEmision().isBefore(desde) || f.getFechaEmision().isAfter(hasta)) continue;
                    if (!estadoFiltro.equals("TODOS") && !f.getEstadoPago().name().equals(estadoFiltro)) continue;
                }
                
                String totalFmt = String.format(Locale.US, "$%,.2f", f.getValorTotal());
                String pagadoFmt = String.format(Locale.US, "$%,.2f", f.getTotalPagado());
                String saldoFmt = String.format(Locale.US, "$%,.2f", f.getSaldoPendiente());

                modelo.addRow(new Object[]{
                    f.getNumero(),
                    f.getPeriodo(),
                    f.getFechaEmision(),
                    f.getFechaVencimiento(),
                    totalFmt,
                    pagadoFmt,
                    saldoFmt,
                    f.getEstadoPago()
                });
            }
            if (sinFiltro)
                lblInfoUser.setText(lblInfoUser.getText().split(" \\| Mostrando")[0] + " | Mostrando TODAS las facturas (" + modelo.getRowCount() + ")");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}