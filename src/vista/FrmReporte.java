package vista;


import controlador.ReporteControlador;
import modelo.Reporte;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;


/**
 * Formulario de reportes financieros (Necesidad 9).
 */
public class FrmReporte extends JFrame {

    // ---------- Componentes ----------
    private JTextField txtCodigo, txtAutor;
    private JSpinner spnDesde, spnHasta;
    private JTextArea txtResultado;
    private JButton btnGenerar, btnCSV, btnPDF;

    // ---------- Estado ----------
    private Reporte reporteActual;

    // ---------- Dependencias ----------
    private final ReporteControlador rCtrl = new ReporteControlador();

    // ---------- Constructor ----------
    public FrmReporte() {
        initComponents();
        setLocationRelativeTo(null);
    }

    // ---------- Inicialización ----------
    private void initComponents() {
        setTitle("EAAB - Reportes Financieros");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setBackground(UIStyle.BG_APP);

        // Cabecera
        JPanel header = new JPanel(null);
        header.setBackground(new Color(33, 47, 61));
        header.setBounds(0, 0, 1000, 60);
        JLabel titulo = new JLabel("GENERACIÓN DE REPORTES FINANCIEROS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(40, 15, 700, 30);
        header.add(titulo);
        add(header);

        // ===== Panel filtros =====
        JPanel pnl = UIStyle.crearPanelBlanco();
        pnl.setBounds(30, 80, 930, 200);

        pnl.add(lbl("Código Reporte:", 20, 20, 120));
        txtCodigo = UIStyle.crearCampoTexto();
        txtCodigo.setBounds(160, 20, 220, 30);
        pnl.add(txtCodigo);

        pnl.add(lbl("Autor (funcionario):", 410, 20, 140));
        txtAutor = UIStyle.crearCampoTexto();
        txtAutor.setBounds(560, 20, 350, 30);
        pnl.add(txtAutor);

        pnl.add(lbl("Desde:", 20, 70, 60));
        spnDesde = new JSpinner(new SpinnerDateModel());
        spnDesde.setEditor(new JSpinner.DateEditor(spnDesde, "yyyy-MM-dd"));
        spnDesde.setBounds(90, 70, 200, 30);
        pnl.add(spnDesde);

        pnl.add(lbl("Hasta:", 320, 70, 60));
        spnHasta = new JSpinner(new SpinnerDateModel());
        spnHasta.setEditor(new JSpinner.DateEditor(spnHasta, "yyyy-MM-dd"));
        spnHasta.setBounds(390, 70, 200, 30);
        pnl.add(spnHasta);

        btnGenerar = UIStyle.crearBoton("Generar Reporte", UIStyle.SUCCESS);
        btnGenerar.setBounds(20, 130, 180, 40);
        pnl.add(btnGenerar);

        btnCSV = UIStyle.crearBoton("Exportar CSV", UIStyle.PRIMARY);
        btnCSV.setBounds(210, 130, 170, 40);
        pnl.add(btnCSV);

        btnPDF = UIStyle.crearBoton("Exportar PDF", UIStyle.DANGER);
        btnPDF.setBounds(390, 130, 170, 40);
        pnl.add(btnPDF);

        add(pnl);

        // ===== Área resultados =====
        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtResultado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scroll = new JScrollPane(txtResultado);
        scroll.setBounds(30, 300, 930, 450);
        add(scroll);

        // ===== Listeners =====
        btnGenerar.addActionListener(e -> generar());
        btnCSV.addActionListener(e -> {
            if (reporteActual == null) {
                JOptionPane.showMessageDialog(this,
                        "Genere primero un reporte.");
                return;
            }
            txtResultado.setText(rCtrl.exportarCSV(reporteActual));
        });
        btnPDF.addActionListener(e -> {
            if (reporteActual == null) {
                JOptionPane.showMessageDialog(this,
                        "Genere primero un reporte.");
                return;
            }
            txtResultado.setText(rCtrl.exportarPDF(reporteActual));
        });
    }

    // ---------- Helper ----------
    private JLabel lbl(String t, int x, int y, int w) {
        JLabel l = UIStyle.crearLabel(t);
        l.setBounds(x, y, w, 25);
        return l;
    }

    // ---------- Generar ----------
    private void generar() {
        try {
            String cod = txtCodigo.getText().trim();
            String aut = txtAutor.getText().trim();
            if (cod.isEmpty() || aut.isEmpty())
                throw new Exception("Código y autor son obligatorios.");

            LocalDate d = new java.sql.Date(
                    ((java.util.Date)spnDesde.getValue()).getTime()).toLocalDate();
            LocalDate h = new java.sql.Date(
                    ((java.util.Date)spnHasta.getValue()).getTime()).toLocalDate();
            reporteActual = rCtrl.generar(cod, aut, d, h);
            txtResultado.setText(reporteActual.exportarPDF());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}