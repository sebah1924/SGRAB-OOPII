package vista;


import controlador.UsuarioControlador;
import dao.PersonaDAO;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Formulario de administración de usuarios.
 * Cubre la Necesidad 2 completa: buscar, editar, suspender,
 * reactivar, cortar, eliminar (baja), historial de consumos y facturas.
 */
public class FrmAdministrarUsuario extends JFrame {

    // ---------- Componentes ----------
    private JTextField txtBuscar;
    private JComboBox<String> cbxFiltro;
    private JButton btnBuscar, btnEditar, btnSuspender, btnReactivar,
                    btnCortar, btnEliminar, btnHistorial;
    private JTable tblUsuarios;
    private DefaultTableModel modelo;

    // ---------- Estado ----------
    private Usuario seleccionado;

    // ---------- Dependencias ----------
    private final UsuarioControlador ctrl = new UsuarioControlador();

    // ---------- Constructor ----------
    public FrmAdministrarUsuario() {
        initComponents();
        setLocationRelativeTo(null);
        cargar();
    }

    // ---------- Inicialización de UI ----------
    private void initComponents() {
        setTitle("EAAB - Administrar Usuarios");
        setSize(1150, 780);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setBackground(UIStyle.BG_APP);

        // ============ CABECERA ============
        JPanel header = new JPanel(null);
        header.setBackground(UIStyle.PRIMARY);
        header.setBounds(0, 0, 1150, 60);
        JLabel titulo = new JLabel("ADMINISTRACIÓN DE USUARIOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(40, 15, 700, 30);
        header.add(titulo);
        add(header);

        // ============ PANEL BÚSQUEDA Y ACCIONES ============
        JPanel pnl = UIStyle.crearPanelBlanco();
        pnl.setBounds(30, 80, 1080, 180);

        pnl.add(lbl("Criterio:", 20, 20, 80));
        cbxFiltro = new JComboBox<>(new String[]{"Código Suscriptor", "Identificación", "Nombre"});
        cbxFiltro.setBounds(110, 20, 180, 30);
        cbxFiltro.setFont(UIStyle.FONT_TEXT);
        pnl.add(cbxFiltro);

        txtBuscar = UIStyle.crearCampoTexto();
        txtBuscar.setBounds(300, 20, 350, 30);
        pnl.add(txtBuscar);

        btnBuscar = UIStyle.crearBoton("Buscar", UIStyle.PRIMARY);
        btnBuscar.setBounds(670, 20, 130, 30);
        pnl.add(btnBuscar);

        // Segunda fila: botones de acción
        btnEditar    = UIStyle.crearBoton("Editar Datos", UIStyle.INFO);
        btnEditar.setBounds(20, 80, 160, 40);
        pnl.add(btnEditar);

        btnSuspender = UIStyle.crearBoton("Suspender", UIStyle.WARNING);
        btnSuspender.setBounds(190, 80, 160, 40);
        pnl.add(btnSuspender);

        btnReactivar = UIStyle.crearBoton("Reactivar", UIStyle.SUCCESS);
        btnReactivar.setBounds(360, 80, 160, 40);
        pnl.add(btnReactivar);

        btnCortar    = UIStyle.crearBoton("Cortar Servicio", UIStyle.DANGER);
        btnCortar.setBounds(530, 80, 160, 40);
        pnl.add(btnCortar);

        btnEliminar  = UIStyle.crearBoton("Dar de Baja", new Color(120, 0, 0));
        btnEliminar.setBounds(700, 80, 160, 40);
        pnl.add(btnEliminar);

        btnHistorial = UIStyle.crearBoton("Ver Historial", UIStyle.PRIMARY);
        btnHistorial.setBounds(870, 80, 160, 40);
        pnl.add(btnHistorial);

        add(pnl);

        // ============ TABLA ============
        modelo = new DefaultTableModel(new Object[]{
            "Código","Identificación","Nombre","Tipo","Estrato","Estado","Consumo Prom."}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUsuarios = new JTable(modelo);
        tblUsuarios.setFont(UIStyle.FONT_TEXT);
        tblUsuarios.setRowHeight(24);
        tblUsuarios.getSelectionModel().addListSelectionListener(e -> seleccionar());
        JScrollPane scroll = new JScrollPane(tblUsuarios);
        scroll.setBounds(30, 280, 1080, 440);
        add(scroll);

        // ============ LISTENERS ============
        btnBuscar.addActionListener(e -> cargar());
        btnEditar.addActionListener(e -> editar());
        btnSuspender.addActionListener(e -> accion("SUSPENDER"));
        btnReactivar.addActionListener(e -> accion("REACTIVAR"));
        btnCortar.addActionListener(e -> accion("CORTAR"));
        btnEliminar.addActionListener(e -> eliminar());
        btnHistorial.addActionListener(e -> verHistorial());
    }

    // ---------- Helper de etiqueta ----------
    private JLabel lbl(String t, int x, int y, int w) {
        JLabel l = UIStyle.crearLabel(t);
        l.setBounds(x, y, w, 25);
        return l;
    }

    // ---------- Cargar la lista ----------
    private void cargar() {
        try {
            modelo.setRowCount(0);
            List<Usuario> lista = ctrl.listarTodos();
            String filtro = txtBuscar.getText().trim().toLowerCase();
            String crit = (String) cbxFiltro.getSelectedItem();

            for (Usuario u : lista) {
                boolean coincide;
                if (filtro.isEmpty()) coincide = true;
                else if (crit.equals("Código Suscriptor"))
                    coincide = u.getCodigoSuscriptor().toLowerCase().contains(filtro);
                else if (crit.equals("Identificación"))
                    coincide = u.getIdentificacion().toLowerCase().contains(filtro);
                else
                    coincide = u.getNombre().toLowerCase().contains(filtro) ||
                               u.getApellido().toLowerCase().contains(filtro);
                if (!coincide) continue;

                modelo.addRow(new Object[]{
                    u.getCodigoSuscriptor(),
                    u.getIdentificacion(),
                    u.getNombre() + " " + u.getApellido(),
                    u.getTipoUsuario(),
                    u.getEstrato(),
                    u.getEstadoServicio(),
                    String.format("%.2f m³", u.getConsumoPromedio())
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Al seleccionar fila ----------
    private void seleccionar() {
        int row = tblUsuarios.getSelectedRow();
        if (row < 0) { seleccionado = null; return; }
        try {
            String cod = (String) modelo.getValueAt(row, 0);
            seleccionado = ctrl.buscarPorCodigo(cod);
        } catch (Exception ignored) { }
    }

    // ---------- Editar datos personales ----------
    private void editar() {
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }
        JTextField dir = new JTextField(seleccionado.getDireccion());
        JTextField tel = new JTextField(seleccionado.getTelefono());
        JTextField cor = new JTextField(seleccionado.getCorreo());

        Object[] msg = {
            "Dirección:", dir,
            "Teléfono (7-10 dígitos):", tel,
            "Correo:", cor
        };
        int opt = JOptionPane.showConfirmDialog(this, msg,
                "Editar Datos Personales", JOptionPane.OK_CANCEL_OPTION);
        if (opt != JOptionPane.OK_OPTION) return;

        try {
            seleccionado.setDireccion(dir.getText().trim());
            seleccionado.setTelefono(tel.getText().trim());
            seleccionado.setCorreo(cor.getText().trim());
            ctrl.editar(seleccionado);
            JOptionPane.showMessageDialog(this, "Datos actualizados correctamente.");
            cargar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Acciones sobre el servicio ----------
    private void accion(String tipo) {
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }
        try {
            switch (tipo) {
                case "SUSPENDER": ctrl.suspender(seleccionado); break;
                case "REACTIVAR": ctrl.reactivar(seleccionado); break;
                case "CORTAR":    ctrl.cortar(seleccionado);    break;
            }
            JOptionPane.showMessageDialog(this, "Acción ejecutada: " + tipo);
            cargar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Baja con motivo ----------
    private void eliminar() {
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }
        String motivo = JOptionPane.showInputDialog(this,
                "Motivo de la baja (obligatorio):");
        if (motivo == null || motivo.trim().isEmpty()) return;
        try {
            ctrl.eliminar(seleccionado, motivo);
            JOptionPane.showMessageDialog(this, "Usuario dado de baja.");
            cargar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Historial de consumos y facturas ----------
    private void verHistorial() {
    if (seleccionado == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
        return;
    }
    try {
        List<Consumo> consumos = ctrl.historialConsumos(seleccionado);
        DefaultTableModel mCons = new DefaultTableModel(new Object[]{"ID", "Volumen (m³)", "Fecha Lectura", "Tipo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Consumo c : consumos) {
            mCons.addRow(new Object[]{c.getId(), c.getVolumen(), c.getFechaLectura(), c.getTipoLectura()}); // CORREGIDO: c.getId() en vez de c.getCodigo()
        }
        JTable tCons = new JTable(mCons);
        tCons.setFont(UIStyle.FONT_TEXT);

        List<Factura> facturas = ctrl.historialFacturas(seleccionado);
        DefaultTableModel mFac = new DefaultTableModel(new Object[]{"Número", "Emisión", "Vencimiento", "Total", "Saldo", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Factura f : facturas) {
            // Se añadió la columna Saldo para ver la relación con los pagos
            mFac.addRow(new Object[]{f.getNumero(), f.getFechaEmision(), f.getFechaVencimiento(), "$" + f.getValorTotal(), "$" + f.getSaldoPendiente(), f.getEstadoPago()});
        }
        JTable tFac = new JTable(mFac);
        tFac.setFont(UIStyle.FONT_TEXT);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Consumos", new JScrollPane(tCons));
        tabs.addTab("Facturas", new JScrollPane(tFac));

        JDialog dlg = new JDialog(this, "Historial de " + seleccionado.getNombre(), true);
        dlg.setSize(850, 550);
        dlg.getContentPane().add(tabs);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
}