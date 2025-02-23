import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Semantico extends IOException {
    private ArrayList<String> tokens, tokensNaturales;
    private HashMap<String, Variables> tablaSimbolos;

    public Semantico(ArrayList<String> tokensRecibidos, ArrayList<String> tokensNaturalesRecibidos) {
        this.tokens = tokensRecibidos;
        this.tokensNaturales = tokensNaturalesRecibidos;
        this.tablaSimbolos = new HashMap<>();
    }

    public void AnalizarTokens() throws Exception {
        try {
            for (int i = 0; i < tokens.size(); i++) {
                if (tokens.get(i).equals("ID")) {
                    String nombreVariable = tokensNaturales.get(i);
                    String tipodedato = "";
                    double valorFinal = 0.0;
                    boolean esOperacion = false;

                    // Verificar si el siguiente token es un tipo de dato
                    if (i + 1 < tokens.size()) {
                        String tipo = tokens.get(i + 1);
                        if (tipo.equals("int") || tipo.equals("dou") || tipo.equals("string")) {
                            tipodedato = tipo;
                        }
                    }

                    // Si la variable ya fue declarada, recuperar su tipo
                    if (tablaSimbolos.containsKey(nombreVariable)) {
                        tipodedato = tablaSimbolos.get(nombreVariable).getTipo();
                    } else {
                        if (tipodedato.equals("")) {
                            throw new Exception("Error: Variable no declarada: " + nombreVariable);
                        }
                    }

                    // Verificar si hay una asignación (ID = valor)
                    if (i + 2 < tokens.size() && tokens.get(i + 1).equals("=")) {
                        if (tipodedato.equals("string") && !tokens.get(i + 2).equals("ID")) {
                            throw new Exception("Error no puedes asignar a esta variable: " + nombreVariable
                                    + " de tipo: " + tipodedato + " un valor: " + tokensNaturales.get(i + 2));
                        }
                        if (tipodedato.equals("int") && tokens.get(i + 2).equals("ID")) {
                            throw new Exception("Error no puedes asignar a esta variable: " + nombreVariable
                                    + " de tipo: " + tipodedato + " un valor: " + tokensNaturales.get(i + 2));
                        }
                        if (tipodedato.equals("dou") && tokens.get(i + 2).equals("ID")) {
                            throw new Exception("Error no puedes asignar a esta variable: " + nombreVariable
                                    + " de tipo: " + tipodedato + " un valor: " + tokensNaturales.get(i + 2));
                        }
                        int j = i + 2; // Posición después del '='
                        valorFinal = procesarExpresion(j);
                        esOperacion = true;
                    }

                    // Si la variable ya existe, solo actualizar su valor
                    if (tablaSimbolos.containsKey(nombreVariable)) {
                        Variables variableExistente = tablaSimbolos.get(nombreVariable);
                        if (esOperacion) {
                            // verifica si el tipo de dato es int, dou o string
                            if (tipodedato.equals("int")) {
                                variableExistente.setValorInt((int) valorFinal);
                            } else if (tipodedato.equals("dou") || tipodedato.equals("FRACC")) {
                                variableExistente.setValorDouble(valorFinal);
                            } else {
                                System.out.println("llegue aqui");
                                variableExistente.setValorStr(tokensNaturales.get(i + 2));
                                tokensNaturales.remove(i + 2);
                                tokens.remove(i + 2);
                            }
                        }
                    } else {
                        // Crear la variable solo si no existe
                        Variables variable;
                        if (tipodedato.equals("int")) {
                            variable = new Variables(tipodedato, (int) valorFinal);
                        } else if (tipodedato.equals("dou")) {
                            variable = new Variables(tipodedato, valorFinal);
                        } else {
                            variable = new Variables(tipodedato, "");
                        }
                        tablaSimbolos.put(nombreVariable, variable);
                    }

                }
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private double procesarExpresion(int inicio) {
        ArrayList<Double> valores = new ArrayList<>();
        char operador = ' ';

        for (int i = inicio; i < tokens.size(); i++) {
            String token = tokens.get(i);
            String valor = tokensNaturales.get(i);
            // verificar si es un numero o una variable
            if (token.equals("Num") || token.equals("ID") || token.equals("dou")) {
                double numero;
                if (token.equals("ID")) {
                    numero = buscarValorVariableDouble(valor);
                } else {
                    numero = Double.parseDouble(valor);
                }
                valores.add(numero);
            } else if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
                operador = token.charAt(0);
            } else {
                break;
            }
        }

        double resultado = valores.get(0);
        for (int i = 1; i < valores.size(); i++) {
            double numero = valores.get(i);
            switch (operador) {
                case '+':
                    resultado += numero;
                    break;
                case '-':
                    resultado -= numero;
                    break;
                case '*':
                    resultado *= numero;
                    break;
                case '/':
                    if (numero == 0) {
                        throw new ArithmeticException("Error: División por cero");
                    }
                    resultado /= numero;
                    break;
            }
        }

        return resultado;
    }

    private double buscarValorVariableDouble(String nombreVariable) {
        if (tablaSimbolos.containsKey(nombreVariable)) {
            return tablaSimbolos.get(nombreVariable).getValorDouble();
        }
        return 0.0;
    }

    public void AnalizarValorTokens() {
        System.out.println("Tabla de símbolos:");
        tablaSimbolos.forEach((key, value) -> {
            System.out.println("Nombre: " + key + " Tipo: " + value.getTipo() +
                    " Valor (int): " + value.getValorInt() +
                    " Valor (double): " + value.getValorDouble() +
                    " Valor (string): " + value.getValorStr() +
                    " Hex: " + value.getValorHex() +
                    " Bin: " + value.getValorBin());
        });
    }

    public HashMap<String, Variables> getTablaSimbolos() {
        return tablaSimbolos;
    }
}
