import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Semantico extends IOException {
    private ArrayList<String> tokens, tokensNaturales;
    private HashMap<String, Variables> tablaSimbolos;
    private boolean entroCondicional = false;
    private int indiceAnalizar, indiceEstatutos = 0;

    public Semantico(ArrayList<String> tokensRecibidos, ArrayList<String> tokensNaturalesRecibidos) {
        this.tokens = tokensRecibidos;
        this.tokensNaturales = tokensNaturalesRecibidos;
        this.tablaSimbolos = new HashMap<>();
    }

    public void AnalizarTokens() throws Exception {
        try {
            for (indiceAnalizar = 0; indiceAnalizar < tokens.size(); indiceAnalizar++) {
                if (tokens.get(indiceAnalizar).equals("ID")) {
                    String nombreVariable = tokensNaturales.get(indiceAnalizar);
                    String tipodedato = "";
                    double valorFinal = 0.0;
                    boolean esOperacion = false;

                    // Verificar si el siguiente token es un tipo de dato
                    if (indiceAnalizar + 1 < tokens.size()) {
                        String tipo = tokens.get(indiceAnalizar + 1);
                        if (tipo.equals("int") || tipo.equals("dou") || tipo.equals("string")) {
                            Variables variable = new Variables(tipo, "");
                            tablaSimbolos.put(nombreVariable, variable);
                            tipodedato = tipo;

                            indiceAnalizar += 2;
                        }
                    }

                    if (indiceAnalizar + 2 < tokens.size() && tokens.get(indiceAnalizar + 1).equals("=")) {
                        if (tablaSimbolos.containsKey(nombreVariable)) {
                            tipodedato = tablaSimbolos.get(nombreVariable).getTipo();
                        } else {
                            throw new Exception("Error: Variable no declarada: " + nombreVariable);
                        }

                        if (tipodedato.equals("string")) {
                            if (tokens.get(indiceAnalizar + 2).equals("ID")) {
                                String idAsignado = tokensNaturales.get(indiceAnalizar + 2);
                                if (tablaSimbolos.containsKey(idAsignado)) {
                                    String tipoAsignado = tablaSimbolos.get(idAsignado).getTipo();
                                    if (!tipoAsignado.equals("string")) {
                                        throw new Exception("Error: No puedes asignar a esta variable: "
                                                + nombreVariable
                                                + " de tipo: " + tipodedato + " un ID de tipo: " + tipoAsignado);
                                    }
                                    // Asignar el valor del ID actual al ID asignado
                                    String valorAsignado = tablaSimbolos.get(idAsignado).getValorStr();
                                    tablaSimbolos.get(nombreVariable).setValorStr(valorAsignado);
                                    esOperacion = false;
                                    indiceAnalizar += 2;
                                } else {
                                    String valorAsignado = tokensNaturales.get(indiceAnalizar + 2);
                                    tablaSimbolos.get(nombreVariable).setValorStr(valorAsignado);
                                    esOperacion = false;
                                    indiceAnalizar += 2;
                                }
                            } else {
                                throw new Exception("Error: No puedes asignar a esta variable: " + nombreVariable
                                        + " de tipo: " + tipodedato + " un valor: "
                                        + tokensNaturales.get(indiceAnalizar + 2));
                            }
                        } else if (tipodedato.equals("int") || tipodedato.equals("dou")) {
                            if (tokens.get(indiceAnalizar + 2).equals("ID")) {
                                String idAsignado = tokensNaturales.get(indiceAnalizar + 2);
                                if (tablaSimbolos.containsKey(idAsignado)) {
                                    String tipoAsignado = tablaSimbolos.get(idAsignado).getTipo();
                                    if (!tipoAsignado.equals(tipodedato)) {
                                        throw new Exception("Error: No puedes asignar a esta variable: "
                                                + nombreVariable
                                                + " de tipo: " + tipodedato + " un ID de tipo: " + tipoAsignado);
                                    }
                                    valorFinal = buscarValorVariableDouble(idAsignado);
                                    esOperacion = true;
                                    indiceAnalizar += 2;
                                } else {
                                    throw new Exception("Error: Variable no declarada: " + idAsignado);
                                }
                            } else {
                                int j = indiceAnalizar + 2; // Posición después del '='
                                valorFinal = procesarExpresion(j);
                                esOperacion = true;
                                String tipoDato = tablaSimbolos.get(nombreVariable).getTipo();
                                if (tipoDato.equals("int")) {
                                    tablaSimbolos.get(nombreVariable).setValorInt((int) valorFinal);
                                } else {
                                    tablaSimbolos.get(nombreVariable).setValorDouble(valorFinal);
                                }
                                indiceAnalizar += 4;
                            }
                        }
                    }

                }
                if (tokens.get(indiceAnalizar).equals("IF")) {
                    indiceEstatutos = indiceAnalizar;
                    double valorFinal = 0.0;
                    int j = indiceAnalizar + 1;
                    boolean resultado = procesarCondicional(j);
                    if (!resultado) {
                        int indiceElse = tokens.indexOf("ELSE");
                        int nuevoIndice = indiceElse + 1;
                        String tipo = tablaSimbolos.get(tokensNaturales.get(indiceAnalizar + 1)).getTipo();
                        indiceAnalizar = nuevoIndice;
                        String token = tokens.get(nuevoIndice + 1);
                        if (token.equals("ID")) {
                            valorFinal = procesarExpresion(nuevoIndice + 3);
                            System.out.println("Resultado: " + valorFinal);
                            if (tipo.equals("int")) {
                                tablaSimbolos.get(tokensNaturales.get(nuevoIndice + 1)).setValorInt((int) valorFinal);
                                indiceAnalizar += 3;

                            } else {
                                tablaSimbolos.get(tokensNaturales.get(nuevoIndice + 1)).setValorDouble(valorFinal);
                                indiceAnalizar += 3;

                            }
                            entroCondicional = false;
                        } else if (token.equals("print")) {
                            valorFinal = procesarExpresion(nuevoIndice + 2);
                            System.out.println("Resultado: " + valorFinal);
                            entroCondicional = false;
                        } else if (token.equals("read")) {
                            indiceAnalizar += 3;
                            entroCondicional = false;
                        }
                    } else {
                        indiceAnalizar += 4;
                        String tipoToken = tablaSimbolos.get(tokensNaturales.get(indiceAnalizar + 1)).getTipo();
                        String tipo = tablaSimbolos.get(tokensNaturales.get(indiceAnalizar + 1)).getTipo();

                        if (tipoToken.equals("ID")) {
                            System.out.println("Resultado: " + valorFinal);
                            valorFinal = procesarExpresion(indiceAnalizar + 3);
                            if (tipo.equals("int")) {
                                tablaSimbolos.get(tokensNaturales.get(indiceAnalizar + 1))
                                        .setValorInt((int) valorFinal);
                                indiceAnalizar += 3;

                            } else {
                                tablaSimbolos.get(tokensNaturales.get(indiceAnalizar + 1)).setValorDouble(valorFinal);
                                indiceAnalizar += 3;

                            }
                            entroCondicional = true;
                        } else if (tipoToken.equals("print")) {
                            valorFinal = procesarExpresion(indiceAnalizar + 2);
                            System.out.println("Resultado: " + valorFinal);
                            entroCondicional = true;
                        } else if (tipoToken.equals("read")) {
                            indiceAnalizar += 3;
                            entroCondicional = true;
                        }
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

    private boolean procesarCondicional(int inicio) {
        boolean resultado = false;
        char operador = ' ';
        ArrayList<Double> valores = new ArrayList<>();
        try {
            for (int i = inicio; i < tokens.size(); i++) {
                String valor = tokensNaturales.get(i);
                String valor2 = tokensNaturales.get(i + 2);
                String token = tokens.get(i);
                if (token.equals("ID")) {
                    String tipo1 = tablaSimbolos.get(valor).getTipo();
                    String tipo2 = tablaSimbolos.get(valor2).getTipo();

                    if (tipo1.equals("string") && tipo2.equals("string")) {
                        if (valor.equals(valor2)) {
                            return true;
                        }
                        return false;

                    }

                    if (tipo1.equals("int") && tipo2.equals("string")) {
                        throw new Exception("Error: No puedes comparar un int con un string");
                    }
                    if (tipo1.equals(tipo2)) {
                        return true;
                    }
                    if (tipo1.equals("int") && tipo2.equals("dou") || tipo1.equals("dou") && tipo2.equals("int")) {
                        return false;
                    }
                    double numero;
                    if (token.equals("ID")) {
                        numero = buscarValorVariableDouble(valor);
                    } else {
                        numero = Double.parseDouble(valor);
                    }
                    valores.add(numero);
                } else if (token.equals("<") || token.equals(">") || token.equals("==") || token.equals("!=")) {
                    operador = token.charAt(0);
                } else {
                    break;
                }
            }

            double numero1 = valores.get(0);
            double numero2 = valores.get(1);

            switch (operador) {
                case '<':
                    resultado = numero1 < numero2;
                    break;
                case '>':
                    resultado = numero1 > numero2;
                    break;
                case '=':
                    resultado = numero1 == numero2;
                    break;
                case '!':
                    resultado = numero1 != numero2;
                    break;
            }

            return resultado;

        } catch (Exception e) {
            throw new ArithmeticException("Error: " + e.getMessage());
        }
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

    public Boolean getEntroCondicional() {
        return entroCondicional;
    }

    public Integer getIndiceEstatutos() {
        return indiceEstatutos;
    }
}
