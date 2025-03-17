import java.util.ArrayList;
import java.util.HashMap;

public class CodigoIntermedio {
    private ArrayList<String> tokens, tokensNaturales;
    private HashMap<String, Variables> tablaSimbolos;

    public CodigoIntermedio(ArrayList<String> tokensRecibidos, ArrayList<String> tokensNaturalesRecibidos) {
        this.tokens = tokensRecibidos;
        this.tokensNaturales = tokensNaturalesRecibidos;
        this.tablaSimbolos = new HashMap<>();
    }

    public void CrearCodigoIntermedio() throws Exception {
        try {
            for (int i = 0; i < tokens.size(); i++) {
                if (tokens.get(i).equals("ID")) {
                    String nombreVariable = tokensNaturales.get(i);
                    String tipodedato = "";
                    String valorFinal = "?";

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

                    Variables variable = new Variables(tipodedato, valorFinal);
                    tablaSimbolos.put(nombreVariable, variable);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error al crear el código intermedio: " + e.getMessage());
        }
    }

    public String GenerarCodigoIntermedio() {
        String codigoIntermedio = ".DATA\n";
        for (String key : tablaSimbolos.keySet()) {
            Variables variable = tablaSimbolos.get(key);
            String tipo = variable.getTipo();
            String valor = variable.getValorStr();
            System.out.println("Variable: " + key + " Tipo: " + tipo + " Valor: " + valor);
            if (tipo.equals("string")) {
                if (valor.equals("?")) {
                    valor = "$";
                }
                codigoIntermedio += key + " DB " + " 256 DUP (0DH ," + "'" + valor + "'" + ")" + "\n";
            } else if (tipo.equals("dou")) {
                codigoIntermedio += key + " DD " + valor + "\n";
            } else if (tipo.equals("int")) {
                codigoIntermedio += key + " DW " + valor + "\n";
            }
        }
        return codigoIntermedio;
    }

    public HashMap<String, Variables> getTablaSimbolos() {
        return tablaSimbolos;
    }
}
