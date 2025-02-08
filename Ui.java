import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class Ui extends JFrame {
    private JTextArea areaCodigo;
    private JButton btnTokens;
    private JTable tablaTokens;
    private DefaultTableModel modeloTabla;
    private JFileChooser fileChooser;

    public Ui() {
        setTitle("Splatt IDE");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Barra de menú
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
        JPanel panelCodigo = new JPanel(new GridLayout(1, 2, 10, 10));

        // Área de código (izquierda)
        areaCodigo = new JTextArea();
        JScrollPane scrollCodigo = new JScrollPane(areaCodigo);
        panelCodigo.add(scrollCodigo);

        // Tabla de tokens (derecha)
        String[] encabezados = { "Tipo", "Token" };
        modeloTabla = new DefaultTableModel(encabezados, 0);
        tablaTokens = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaTokens);
        panelCodigo.add(scrollTabla);

        add(panelCodigo, BorderLayout.CENTER);

        // Botón de análisis
        JPanel bottomPanel = new JPanel();
        btnTokens = new JButton("Tokens");
        btnTokens.addActionListener(e -> analizarTokens());
        bottomPanel.add(btnTokens);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void analizarTokens() {
        modeloTabla.setRowCount(0); // Limpiar la tabla antes de analizar

        String codigo = areaCodigo.getText();
        miEscaner scanner = new miEscaner(codigo);

        while (!scanner.getToken(true).equals("FinArchivo")) {
            scanner.getToken(true);
        }

        ArrayList<String> tiposTokens = scanner.getTokens();
        ArrayList<String> naturalTokens = scanner.getNaturalTokens();

        for (int i = 0; i < tiposTokens.size(); i++) {
            modeloTabla.addRow(new Object[] { tiposTokens.get(i), naturalTokens.get(i) });
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
        SwingUtilities.invokeLater(() -> {
            Ui ide = new Ui();
            ide.setVisible(true);
        });
    }
}
