import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Ui extends JFrame {
    private JTextArea areaCodigo;
    private JButton btnTokens;
    private JTextArea areaTokens;
    private JFileChooser fileChooser;

    public Ui() {
        setTitle("Splatt IDE");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

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

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        areaCodigo = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(areaCodigo);
        centerPanel.add(scrollPane);

        areaTokens = new JTextArea();
        areaTokens.setEditable(false);
        JScrollPane tokensScrollPane = new JScrollPane(areaTokens);
        centerPanel.add(tokensScrollPane);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        btnTokens = new JButton("Tokens");
        btnTokens.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analizarTokens();
            }
        });
        bottomPanel.add(btnTokens);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void analizarTokens() {
        String codigo = areaCodigo.getText();
        miEscaner scanner = new miEscaner(codigo);
        String token = scanner.getToken(true);
        while (!token.equals("EOF")) {
            areaTokens.append(token + "\n");
            token = scanner.getToken(true);
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
