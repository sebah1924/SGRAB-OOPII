package vista;


import controlador.RacionamientoControlador;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Formulario de racionamiento (Necesidades 3 y 4).
 */
public class FrmRacionamiento extends JFrame {

    // ---------- Componentes ----------
    private JTextField txtCodigo, txtMotivo;
    private JComboBox<String> cbxSector;
    private JSpinner spnFecha, spnHoraIni, spnHoraFin;
    private JButton btnProgramar, btnAplicar, btnSuspender, btnReactivar;
    private JTable tblTurnos;
    private DefaultTableModel modelo;

    // ---------- Estado ----------
    private List<Sector> sectores;

    // ---------- Dependencias ----------
    private final RacionamientoControlador rCtrl = new RacionamientoControlador();

    // ---------- Constructor ----------
    public FrmRacionamiento() {
        initComponents();
        setLocationRelativeTo(null);
        cargarSectores();
    }

    // ---------- Inicialización ----------
    private void initComponents() {
        setTitle("EAAB - Racionamiento");
        setSize(1050, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setBackground(UIStyle.BG_APP);

        // Cabecera
        JPanel header = new JPanel(null);
        header.setBackground(UIStyle.PRIMARY);
        header.setBounds(0, 0, 1050, 60);
        JLabel titulo = new JLabel("GESTIÓN DE RACIONAMIENTO");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(40, 15, 500, 30);
        header.add(titulo);
        add(header);

        // ===== Panel programar =====
        JPanel pnl = UIStyle.crearPanelBlanco();
        pnl.setBounds(30, 80, 980, 280);

        pnl.add(lbl("Código Turno:", 20, 20, 100));
        txtCodigo = UIStyle.crearCampoTexto();
        txtCodigo.setBounds(140, 20, 200, 30);
        pnl.add(txtCodigo);

        pnl.add(lbl("Sector:", 370, 20, 60));
        cbxSector = new JComboBox<>();
        cbxSector.setBounds(440, 20, 280, 30);
        pnl.add(cbxSector);

        pnl.add(lbl("Fecha:", 20, 70, 60));
        spnFecha = new JSpinner(new SpinnerDateModel());
        spnFecha.setEditor(new JSpinner.DateEditor(spnFecha, "yyyy-MM-dd"));
        spnFecha.setBounds(100, 70, 160, 30);
        pnl.add(spnFecha);

        pnl.add(lbl("Hora Inicio:", 280, 70, 90));
        spnHoraIni = new JSpinner(new SpinnerDateModel());
        spnHoraIni.setEditor(new JSpinner.DateEditor(spnHoraIni, "HH:mm"));
        spnHoraIni.setBounds(380, 70, 100, 30);
        pnl.add(spnHoraIni);

        pnl.add(lbl("Hora Fin:", 500, 70, 80));
        spnHoraFin = new JSpinner(new SpinnerDateModel());
        spnHoraFin.setEditor(new JSpinner.DateEditor(spnHoraFin, "HH:mm"));
        spnHoraFin.setBounds(590, 70, 100, 30);
        pnl.add(spnHoraFin);

        pnl.add(lbl("Motivo:", 20, 120, 80));
        txtMotivo = UIStyle.crearCampoTexto();
        txtMotivo.setBounds(110, 120, 840, 30);
        pnl.add(txtMotivo);

        btnProgramar = UIStyle.crearBoton("Programar Turno", UIStyle.PRIMARY);
        btnProgramar.setBounds(20, 200, 180, 40);
        pnl.add(btnProgramar);

        btnAplicar = UIStyle.crearBoton("Aplicar Cortes", UIStyle.DANGER);
        btnAplicar.setBounds(210, 200, 180, 40);
        pnl.add(btnAplicar);

        btnSuspender = UIStyle.crearBoton("Suspender Racionamiento", UIStyle.WARNING);
        btnSuspender.setBounds(400, 200, 210, 40);
        pnl.add(btnSuspender);

        btnReactivar = UIStyle.crearBoton("Reactivar Racionamiento", UIStyle.SUCCESS);
        btnReactivar.setBounds(620, 200, 210, 40);
        pnl.add(btnReactivar);

        add(pnl);

        // ===== Tabla turnos =====
        JLabel lblTurnos = new JLabel("Turnos Activos del Sector Seleccionado:");
        lblTurnos.setFont(UIStyle.FONT_SUBTITLE);
        lblTurnos.setForeground(UIStyle.DARK_TEXT);
        lblTurnos.setBounds(30, 375, 400, 25);
        add(lblTurnos);

        modelo = new DefaultTableModel(new Object[]{
            "Código","Fecha","Hora Inicio","Hora Fin","Motivo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTurnos = new JTable(modelo);
        tblTurnos.setFont(UIStyle.FONT_TEXT);
        tblTurnos.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(tblTurnos);
        scroll.setBounds(30, 405, 980, 355);
        add(scroll);

        // ===== Listeners =====
        btnProgramar.addActionListener(e -> programar());
        btnAplicar.addActionListener(e -> aplicar());
        btnSuspender.addActionListener(e -> suspender());
        btnReactivar.addActionListener(e -> reactivar());
        cbxSector.addActionListener(e -> cargarTurnos());
    }

    // ---------- Helper ----------
    private JLabel lbl(String t, int x, int y, int w) {
        JLabel l = UIStyle.crearLabel(t);
        l.setBounds(x, y, w, 25);
        return l;
    }

    // ---------- Sectores ----------
    private void cargarSectores() {
        try {
            sectores = rCtrl.listarSectores();
            for (Sector s : sectores) {
                cbxSector.addItem(s.getCodigo() + " - " + s.getNombre());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error cargando sectores: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Sector getSectorSeleccionado() {
        int idx = cbxSector.getSelectedIndex();
        if (idx < 0 || sectores == null || idx >= sectores.size()) return null;
        return sectores.get(idx);
    }

    // ---------- Programar ----------
    private void programar() {
        try {
            Sector s = getSectorSeleccionado();
            if (s == null)
                throw new Exception("Seleccione un sector.");
            LocalDate f = new java.sql.Date(
                    ((java.util.Date)spnFecha.getValue()).getTime()).toLocalDate();
            LocalTime hi = new java.sql.Time(
                    ((java.util.Date)spnHoraIni.getValue()).getTime()).toLocalTime();
            LocalTime hf = new java.sql.Time(
                    ((java.util.Date)spnHoraFin.getValue()).getTime()).toLocalTime();
            rCtrl.programarTurno(txtCodigo.getText().trim(), s, f, hi, hf);
            JOptionPane.showMessageDialog(this,
                    "Turno programado. Notificaciones enviadas al sector.");
            cargarTurnos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Aplicar cortes ----------
    private void aplicar() {
        try {
            Sector s = getSectorSeleccionado();
            if (s == null) return;
            int aplicados = rCtrl.aplicarCortesPorSector(s.getId(),
                    LocalDate.now(), LocalTime.now());
            JOptionPane.showMessageDialog(this,
                    "Cortes aplicados: " + aplicados +
                    "\n(Los usuarios exentos NO fueron afectados)");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Suspender ----------
    private void suspender() {
        try {
            String motivo = JOptionPane.showInputDialog(this,
                    "Motivo de la suspensión del racionamiento:");
            if (motivo == null || motivo.trim().isEmpty()) return;
            rCtrl.suspenderCalendario("CAL001");
            JOptionPane.showMessageDialog(this,
                    "Racionamiento suspendido. Notificaciones enviadas.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Reactivar ----------
    private void reactivar() {
        try {
            rCtrl.reactivarCalendario("CAL001");
            JOptionPane.showMessageDialog(this, "Racionamiento reactivado.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Cargar turnos ----------
    private void cargarTurnos() {
        Sector s = getSectorSeleccionado();
        if (s == null) return;
        try {
            modelo.setRowCount(0);
            List<TurnoRacionamiento> ts = rCtrl.listarTurnos(s.getId());
            for (TurnoRacionamiento t : ts) {
                modelo.addRow(new Object[]{
                    t.getCodigo(),
                    t.getFecha(),
                    t.getHoraInicio(),
                    t.getHoraFin(),
                    t.getMotivo()
                });
            }
        } catch (Exception ignored) { }
    }
}