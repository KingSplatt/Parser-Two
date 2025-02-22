import java.util.ArrayList;
import java.util.HashMap;

public class Semantico {
    private ArrayList<String> tokens, tokensNaturales;
    private HashMap<String, Variables> tablaSimbolos;

    public Semantico(ArrayList<String> tokensRecibidos, ArrayList<String> tokensNaturalesRecibidos) {
        this.tokens = tokensRecibidos;
        this.tokensNaturales = tokensNaturalesRecibidos;
        this.tablaSimbolos = new HashMap<>();
    }

    public void AnalizarTokens() {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals("ID")) {
                String nombreVariable = tokensNaturales.get(i);
                String tipodedato = "";
                double valorFinal = 0.0;
                boolean esOperacion = false;

                // Verificar si el siguiente token es un tipo de dato
                if (i + 1 < tokens.size()) {
                    String tipo = tokens.get(i + 1);
                    if (tipo.equals("int") || tipo.equals("dou") || tipo.equals("FRACC") || tipo.equals("string")) {
                        tipodedato = tipo;
                    }
                }

                // Si la variable ya fue declarada, recuperar su tipo
                if (tablaSimbolos.containsKey(nombreVariable)) {
                    tipodedato = tablaSimbolos.get(nombreVariable).getTipo();
                }

                // Verificar si hay una asignación (ID = valor)
                if (i + 2 < tokens.size() && tokens.get(i + 1).equals("=")) {
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
                    } else if (tipodedato.equals("dou") || tipodedato.equals("FRACC")) {
                        variable = new Variables(tipodedato, valorFinal);
                    } else {
                        variable = new Variables(tipodedato, "");
                    }
                    tablaSimbolos.put(nombreVariable, variable);
                }

            }
        }
    }

    private double procesarExpresion(int inicio) {
        ArrayList<Double> valores = new ArrayList<>();
        char operador = ' ';

        for (int i = inicio; i < tokens.size(); i++) {
            String token = tokens.get(i);
            String valor = tokensNaturales.get(i);
            // verificar si es un numero o una variable
            if (token.equals("Num") || token.equals("ID") || token.equals("dou") || token.equals("FRACC")) {
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

    public void AnalizarEstatutos() {
        for (String token : tokens) {
            switch (token) {
                case "IF":
                    // Analizar IF
                    break;
                case "ELSE":
                    // Analizar ELSE
                    break;
                case "print":
                    // Analizar print
                    break;
                case "read":
                    // Analizar read
                    break;
            }
        }
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
