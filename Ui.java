import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Ui extends JFrame {
    private JTextArea areaCodigo, consola, areaCodigoData;
    private JButton btnTokens, btnEstatutos, btnSimbolos, btnCodigoObjeto;
    private JTable tablaTokens;
    private DefaultTableModel modeloTablaTokens, modeloTablaEst, modeloTablaSimbolos;
    private JFileChooser fileChooser;
    private JLabel lblConsola;
    private Parser parser;
    private miEscaner scanner;
    private Semantico semantico;

    public Ui() {
        setTitle("Lenguaje: Splatt");
        setSize(1080, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Barra de menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Archivo");
        JMenuItem newFile = new JMenuItem("Nuevo");
        JMenuItem openFile = new JMenuItem("Abrir");
        JMenuItem saveFile = new JMenuItem("Guardar");

        newFile.addActionListener(e -> areaCodigo.setText(""));
        openFile.addActionListener(e -> abrirArchivo());
        saveFile.addActionListener(e -> guardarArchivo());

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        fileChooser = new JFileChooser();

        // Panel principal con 2 columnas
        JPanel panelCodigo = new JPanel(new GridLayout(0, 3, 10, 10));

        // area de codigo (izquierda)
        areaCodigo = new JTextArea();
        JScrollPane scrollCodigo = new JScrollPane(areaCodigo);
        panelCodigo.add(scrollCodigo);

        // Tabla de tokens (centro)
        String[] encabezados = { "Tipo", "Token" };
        modeloTablaTokens = new DefaultTableModel(encabezados, 0);
        tablaTokens = new JTable(modeloTablaTokens);
        JScrollPane scrollTabla = new JScrollPane(tablaTokens);
        tablaTokens.setDefaultEditor(Object.class, null); // no editable
        // Panel con boton de tokens
        JPanel panelTabla = new JPanel(new BorderLayout());
        btnTokens = new JButton("Tokens");
        panelTabla.add(btnTokens, BorderLayout.NORTH);
        panelTabla.add(scrollTabla, BorderLayout.CENTER);
        panelCodigo.add(panelTabla);
        add(panelCodigo, BorderLayout.CENTER);

        // tabla de estatutos (derecha)
        String[] encabezadosEst = { "Estatuto" };
        modeloTablaEst = new DefaultTableModel(encabezadosEst, 0);
        JTable tablaEst = new JTable(modeloTablaEst);
        JScrollPane scrollTablaEst = new JScrollPane(tablaEst);
        tablaEst.setDefaultEditor(Object.class, null); // no editable
        // Panel con boton de estatutos
        JPanel panelTablaEst = new JPanel(new BorderLayout());
        btnEstatutos = new JButton("Estatutos");
        panelTablaEst.add(btnEstatutos, BorderLayout.NORTH);
        panelTablaEst.add(scrollTablaEst, BorderLayout.CENTER);
        panelCodigo.add(panelTablaEst);

        // Consola (abajo)
        JPanel panelConsola = new JPanel(new BorderLayout());
        lblConsola = new JLabel("Consola:");
        lblConsola.setFont(new Font("Arial", Font.BOLD, 16));
        lblConsola.setHorizontalAlignment(SwingConstants.CENTER);
        panelConsola.add(lblConsola, BorderLayout.NORTH);
        add(panelConsola, BorderLayout.SOUTH);
        consola = new JTextArea(8, 1);
        consola.setEditable(false);
        JScrollPane scrollConsola = new JScrollPane(consola);
        panelConsola.add(scrollConsola, BorderLayout.CENTER);

        // tabla de simbolos (derecha - semantico)
        String[] encabezadosSim = { "nombre", "tipo", "valor" };
        modeloTablaSimbolos = new DefaultTableModel(encabezadosSim, 0);
        JTable tablaSimbolos = new JTable(modeloTablaSimbolos);
        JScrollPane scrollTablaSim = new JScrollPane(tablaSimbolos);
        tablaSimbolos.setDefaultEditor(Object.class, null); // no editable
        // Panel con boton de simbolos
        JPanel panelTablaSim = new JPanel(new BorderLayout());
        btnSimbolos = new JButton("Simbolos");
        panelTablaSim.add(btnSimbolos, BorderLayout.NORTH);
        panelTablaSim.add(scrollTablaSim, BorderLayout.CENTER);
        panelCodigo.add(panelTablaSim);

        // agregar un panel con codigo objeto de turbo gui assembler (.DATA)
        // Panel con boton de simbolos
        JPanel panelCodigoObjeto = new JPanel(new BorderLayout());
        btnCodigoObjeto = new JButton(".C.I");
        areaCodigoData = new JTextArea();
        JScrollPane scrollCodigoData = new JScrollPane(areaCodigoData);
        panelCodigoObjeto.add(scrollCodigoData, BorderLayout.CENTER);
        panelCodigoObjeto.add(btnCodigoObjeto, BorderLayout.NORTH);
        panelCodigo.add(panelCodigoObjeto);

        // Eventos de botones

        btnTokens.addActionListener(e -> {
            try {
                analizarTokens();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        btnEstatutos.addActionListener(e -> {
            try {
                analizarEstatutos();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        btnSimbolos.addActionListener(e -> {
            try {
                analizarSimbolos();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        btnCodigoObjeto.addActionListener(e -> {
            try {
                analizarCodigoIntermedio();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }

    public void analizarTokens() throws Exception {
        modeloTablaTokens.setRowCount(0); // limpia tabla

        String codigo = areaCodigo.getText();
        scanner = new miEscaner(codigo);

        try {
            while (!scanner.getToken(true).equals("")) {
                scanner.getToken(true);
            }
        } catch (Exception e) {
            consola.setText(e.getMessage());
            consola.setForeground(Color.RED);
        }

        ArrayList<String> tiposTokens = scanner.getTokens();
        ArrayList<String> naturalTokens = scanner.getNaturalTokens();

        for (int i = 0; i < tiposTokens.size(); i++) {
            modeloTablaTokens.addRow(new Object[] { tiposTokens.get(i), naturalTokens.get(i) });
        }

    }

    public void analizarEstatutos() throws Exception {
        modeloTablaEst.setRowCount(0); // limpia tabla

        String codigo = areaCodigo.getText();
        parser = new Parser(codigo);

        try {
            consola.setText("");
            parser.P();

        } catch (Exception e) {

            consola.setText(e.getMessage());
            consola.setForeground(Color.RED);

        }
        ArrayList<String> estatutos = parser.getTokens();

        for (String estatuto : estatutos) {
            modeloTablaEst.addRow(new Object[] { estatuto });
        }
    }

    public void analizarSimbolos() throws Exception {
        modeloTablaSimbolos.setRowCount(0); // limpia tabla

        ArrayList<String> tiposTokens = parser.getTokens();
        ArrayList<String> naturalTokens = parser.getTokensNaturales();
        HashMap<String, Variables> tabla = new HashMap<>();
        try {
            semantico = new Semantico(tiposTokens, naturalTokens);
            semantico.AnalizarTokens();
            tabla = semantico.getTablaSimbolos();
            consola.setText(tabla.toString());
            for (String key : tabla.keySet()) {
                Variables variable = tabla.get(key);
                // verificar si el tipo de dato es un int o un dou
                if (variable.getTipo().equals("int")) {
                    modeloTablaSimbolos.addRow(new Object[] { key, variable.getTipo(),
                            variable.getValorInt() });
                } else if (variable.getTipo().equals("dou")) {
                    modeloTablaSimbolos.addRow(new Object[] { key, variable.getTipo(),
                            variable.getValorDouble() });
                } else {
                    modeloTablaSimbolos.addRow(new Object[] { key, variable.getTipo(),
                            variable.getValorStr() });
                }
            }
            consola.setText("Análisis semántico correcto");
            consola.setForeground(Color.BLUE);
        } catch (Exception e) {
            consola.setText(e.getMessage());
            consola.setForeground(Color.RED);
        }
    }

    public void analizarCodigoIntermedio() throws Exception {
        try {
            areaCodigoData.setText("");
            consola.setText("");
            ArrayList<String> tiposTokens = parser.getTokens();
            ArrayList<String> naturalTokens = parser.getTokensNaturales();
            System.out.println("tiposTokens: " + tiposTokens.toString());
            System.out.println("naturalTokens: " + naturalTokens.toString());
            semantico = new Semantico(tiposTokens, naturalTokens);
            semantico.AnalizarTokens();
            CodigoIntermedio codigoIntermedio = new CodigoIntermedio(semantico, tiposTokens, naturalTokens);
            // semantico.AnalizarTokens();
            // tabla = semantico.getTablaSimbolos();
            String codigoIntermedioData = codigoIntermedio.PuntoData();
            System.out.println("aaa" + codigoIntermedioData);
            areaCodigoData.append(codigoIntermedioData);
            consola.setText("Análisis semántico correcto");
            consola.setForeground(Color.BLUE);
        } catch (Exception e) {
            consola.setText(e.getMessage());
            consola.setForeground(Color.RED);
        }
    }

    private void abrirArchivo() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                areaCodigo.read(reader, null);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al abrir el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarArchivo() {
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                areaCodigo.write(writer);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        Ui ide = new Ui();
        ide.setVisible(true);
    }
}
