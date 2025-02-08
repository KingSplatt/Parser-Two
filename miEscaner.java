import java.util.ArrayList;

public class miEscaner {
    private String tokens[];
    private final String reservadas[] = { "int", "string", "dou", "IF", "ELSE", "print" };
    private final String operadores[] = { "+", "=", "-", "*", "/" };
    private final String expresion[] = { "==", "!==", "<", ">", "<=", ">=" };
    private final String delimitador = ";";
    private final String corchetes[] = { "{", "}" };
    private final String parentesis[] = { "(", ")" };
    private int indice;
    private String tokenActual = "";
    private String tipoToken = "";
    private ArrayList<String> tiposTokens, naturalTokens;

    public miEscaner(String codigo) {
        this.tokens = codigo.split("\\s+");
        this.indice = 0;
        this.tokenActual = tokens[indice];
        this.tiposTokens = new ArrayList<>();
        this.naturalTokens = new ArrayList<>();
    }

    public String getTipoToken() {
        return this.tipoToken;
    }

    public void setTipoToken(String tipo) {
        this.tipoToken = tipo;
    }

    public String getToken(boolean avanza) {
        boolean tokenValido = false;
        if (indice >= tokens.length) {
            return "FinArchivo";
        }
        this.tokenActual = tokens[indice];
        if (avanza) {
            indice++;
        }

        // Verificar si es una palabra reservada
        for (String reservada : reservadas) {
            if (this.tokenActual.equals(reservada)) {
                tokenValido = true;
                setTipoToken("reservada");
                tiposTokens.add(tipoToken);
                naturalTokens.add(tokenActual);
                break;
            }
        }
        // Verificar si es un operador
        if (!tokenValido) {
            for (String operador : operadores) {
                if (this.tokenActual.equals(operador)) {
                    tokenValido = true;
                    setTipoToken("operador");
                    tiposTokens.add(tipoToken);
                    naturalTokens.add(tokenActual);
                    break;
                }
            }
        }
        // Verificar si es un delimitador
        if (!tokenValido) {
            if (this.tokenActual.equals(delimitador)) {
                tokenValido = true;
                setTipoToken("delimitador");
                tiposTokens.add(tipoToken);
                naturalTokens.add(tokenActual);
            }
        }
        // Verificar si es un corchete
        if (!tokenValido) {
            for (String corchete : corchetes) {
                if (this.tokenActual.equals(corchete)) {
                    tokenValido = true;
                    setTipoToken("corchete");
                    tiposTokens.add(tipoToken);
                    naturalTokens.add(tokenActual);
                    break;
                }
            }
        }
        // Verificar si es un parentesis
        if (!tokenValido) {
            for (String parentesi : parentesis) {
                if (this.tokenActual.equals(parentesi)) {
                    tokenValido = true;
                    setTipoToken("parentesis");
                    tiposTokens.add(tipoToken);
                    naturalTokens.add(tokenActual);
                    break;
                }
            }
        }
        // Verificar si es una expresion
        if (!tokenValido) {
            for (String expres : expresion) {
                if (this.tokenActual.equals(expres)) {
                    tokenValido = true;
                    setTipoToken("expresion");
                    tiposTokens.add(tipoToken);
                    naturalTokens.add(tokenActual);
                    break;
                }
            }
        }
        // Verificar si es un numero
        if (!tokenValido) {
            if (this.tokenActual.matches("[0-9]+")) {
                tokenValido = true;
                setTipoToken("Num");
                tiposTokens.add(tipoToken);
                naturalTokens.add(tokenActual);
            }
        }
        // verifica si es un numero fraccionario
        if (!tokenValido) {
            if (this.tokenActual.matches("[0-9]+\\.[0-9]+")) {
                tokenValido = true;
                setTipoToken("FRACC");
                tiposTokens.add(tipoToken);
                naturalTokens.add(tokenActual);
            }
        }
        // Verificar si es un identificador
        if (!tokenValido) {
            if (this.tokenActual.matches("[a-z|A-Z][a-z|A-Z|0-9]*")) {
                tokenValido = true;
                setTipoToken("ID");
                tiposTokens.add(tipoToken);
                naturalTokens.add(tokenActual);
            } else {
                tokenValido = false;
            }
        }

        if (tokenValido) {
            return this.tokenActual;

        } else {
            error(this.tokenActual);
            setTipoToken("Token invalido");
            return "Token invalido";
        }
    }

    public String goFront() {
        if (indice < tokens.length) {
            return tokens[indice + 1];
        } else {
            return "No hay mas tokens";
        }
    }

    public String goBack() {
        if (indice > 0) {
            return tokens[indice - 1];
        } else {
            return "No hay mas tokens";
        }
    }

    public void error(String token) {
        System.out.println("Token no valido para: " + token);
    }

    public ArrayList<String> getTokens() {
        return tiposTokens;
    }

    public ArrayList<String> getNaturalTokens() {
        return naturalTokens;
    }
}