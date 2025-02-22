import java.util.ArrayList;
import java.util.HashMap;

public class Semantico {
    private ArrayList<String> tokens, tokensNaturales;
    private HashMap<String, Variables> tablaSimbolos = new HashMap<>();

    public Semantico(ArrayList<String> tokensRecibidos, ArrayList<String> tokensNaturalesRecibidos) {
        this.tokens = tokensRecibidos;
        this.tokensNaturales = tokensNaturalesRecibidos;
    }

    public void AnalizarTokens() {
        System.out.println("Tokens recibidos:");
        tokens.forEach(System.out::println);

        System.out.println("Tokens naturales recibidos:");
        tokensNaturales.forEach(System.out::println);

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

                // Verificar si hay una asignación (ID = valor)
                if (i + 2 < tokens.size() && tokens.get(i + 1).equals("=")) {
                    int j = i + 2; // Posición después del '='
                    valorFinal = procesarExpresion(j);
                    esOperacion = true;
                    tipodedato = "dou";
                    if (valorFinal % 1 == 0) {
                        tipodedato = "int";
                    }

                }

                // Crear la variable con el valor correcto
                Variables variable;
                if (tipodedato.equals("int")) {
                    variable = new Variables(tipodedato, (int) valorFinal);
                } else if (tipodedato.equals("dou") || tipodedato.equals("FRACC")) {
                    variable = new Variables(tipodedato, valorFinal);
                } else {
                    variable = new Variables(tipodedato, String.valueOf(valorFinal));
                }

                // Agregar a la tabla de símbolos
                if (!tipodedato.isEmpty() || esOperacion) {
                    tablaSimbolos.put(nombreVariable, variable);
                }
            }
        }
    }

    private double procesarExpresion(int inicio) {
        ArrayList<Double> valores = new ArrayList<>();
        ArrayList<Character> operadores = new ArrayList<>();

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

                // Si hay una multiplicación o división pendiente, resolverla antes
                if (!operadores.isEmpty() && (operadores.get(operadores.size() - 1) == '*'
                        || operadores.get(operadores.size() - 1) == '/')) {
                    char operador = operadores.remove(operadores.size() - 1);
                    double previo = valores.remove(valores.size() - 1);
                    numero = (operador == '*') ? (previo * numero) : (previo / numero);
                }

                valores.add(numero);
            } else if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
                operadores.add(token.charAt(0));
            } else {
                break; // Fin de expresión
            }
        }

        // Resolver sumas y restas
        double resultado = valores.get(0);
        int index = 1;
        for (char operador : operadores) {
            if (operador == '+')
                resultado += valores.get(index);
            else if (operador == '-')
                resultado -= valores.get(index);
            index++;
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
        tablaSimbolos.forEach((key, value) -> {
            System.out.println("Nombre: " + key + " Tipo: " + value.getTipo() +
                    " Valor (int): " + value.getValorInt() +
                    " Valor (double): " + value.getValorDouble() +
                    " Valor (string): " + value.getValorStr() +
                    " Hex: " + value.getValorHex() +
                    " Bin: " + value.getValorBin());
        });
    }
}
