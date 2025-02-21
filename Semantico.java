import java.util.ArrayList;
import java.util.HashMap;

public class Semantico {
    Parser parser;
    ArrayList<String> tokens, tokensNaturales;
    HashMap<String, Variables> tablaSimbolos = new HashMap<String, Variables>();

    public Semantico(ArrayList<String> tokensRecibidos, ArrayList<String> tokensNaturalesRecibidos) {
        this.tokens = tokensRecibidos;
        this.tokensNaturales = tokensNaturalesRecibidos;
    }

    public void AnalizarTokens() {
        for (String tok : tokens) {
            System.out.println(tok);
        }
        for (int i = 0; i < tokens.size(); i++) {
            String tipodedato = "";
            String nombreVariable = "";
            String valorVariable = "";
            if (tokens.get(i).equals("ID")) {
                nombreVariable = tokensNaturales.get(i);
                if (tokens.get(i + 1).equals("int") || tokens.get(i + 1).equals("dou")
                        || tokens.get(i + 1).equals("string")) {
                    tipodedato = tokens.get(i + 1);
                }
                if (tokens.get(i + 1).equals("=")) {
                    if (tokens.get(i + 2).equals("ID")) {
                        valorVariable = buscarValorVariable(tokensNaturales.get(i + 2));
                    }
                    valorVariable = tokensNaturales.get(i + 2);
                }
            }
        }
    }

    public String buscarValorVariable(String nombreVariable) {
        tablaSimbolos.forEach((k, v) -> {
            if (k.equals(nombreVariable)) {
                return v.getValor();
            }
        });
        return nombreVariable;
    }

    public void AnalizarEstatutos() {
        for (String token : tokens) {
            if (token.equals("IF")) {
                // Analizar IF
            } else if (token.equals("ELSE")) {
                // Analizar ELSE
            } else if (token.equals("print")) {
                // Analizar print
            } else if (token.equals("read")) {
                // Analizar read
            }
        }
    }

    public void AnalizarValorTokens() {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals("ID")) {
                if (tokens.get(i + 1).equals("INT") || tokens.get(i + 1).equals("dou")
                        || tokens.get(i + 1).equals("string")) {
                    if (tokens.get(i + 2).equals("=")) {
                        if (tokens.get(i + 3).equals("Num")) {
                            tablaSimbolos.add(new Variables(tokensNaturales.get(i), tokensNaturales.get(i + 1),
                                    tokensNaturales.get(i + 3)));
                        } else if (tokens.get(i + 3).equals("FRACC")) {
                            tablaSimbolos.add(new Variables(tokensNaturales.get(i), tokensNaturales.get(i + 1),
                                    tokensNaturales.get(i + 3)));
                        } else if (tokens.get(i + 3).equals("ID")) {
                            for (Variables variable : tablaSimbolos) {
                                if (variable.getNombre().equals(tokensNaturales.get(i + 3))) {
                                    tablaSimbolos.add(new Variables(tokensNaturales.get(i), tokensNaturales.get(i + 1),
                                            variable.getValor()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
