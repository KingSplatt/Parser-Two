public class miEscaner {
    private String tokens[];
    private final String reservadas[] = { "int", "string", "dou", "IF", "ELSE", "print" };
    private final String operadores[] = { "+", "=", "-", "*", "/" };
    private final String delimitador = ";";
    private final String corchetes[] = { "{", "}" };
    private int indice;
    private String tokenActual = "";
    private String tipoToken = "";

    public miEscaner(String codigo) {
        this.tokens = codigo.split("\\s+");
        this.indice = 0;
        this.tokenActual = tokens[indice];
    }

    public String getTipoToken() {
        return this.tipoToken;
    }

    public void setTipoToken(String tipo) {
        this.tipoToken = tipo;
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

    public String getToken(boolean avanza) {
        boolean tokenValido = false;
        if (indice >= tokens.length) {
            setTipoToken("EOF");
            return "EOF";
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
                break;
            }
        }
        // Verificar si es un operador
        if (!tokenValido) {
            for (String operador : operadores) {
                if (this.tokenActual.equals(operador)) {
                    tokenValido = true;
                    setTipoToken("operador");
                    break;
                }
            }
        }
        // Verificar si es un delimitador
        if (!tokenValido) {
            if (this.tokenActual.equals(delimitador)) {
                tokenValido = true;
                setTipoToken(delimitador);
            }
        }
        // Verificar si es un corchete
        if (!tokenValido) {
            for (String corchete : corchetes) {
                if (this.tokenActual.equals(corchete)) {
                    tokenValido = true;
                    setTipoToken("corchete");
                    break;
                }
            }
        }
        // Verificar si es un numero
        if (!tokenValido) {
            if (this.tokenActual.matches("[0-9]+")) {
                tokenValido = true;
                setTipoToken("num");
            }
        }
        // verifica si es un numero positivo
        if (!tokenValido) {
            if (this.tokenActual.matches("[1-9]+")) {
                tokenValido = true;
                setTipoToken("numP");
            }
        }
        // verifica si es un numero fraccionario
        if (!tokenValido) {
            if (this.tokenActual.matches("[0-9]+\\.[0-9]+")) {
                tokenValido = true;
                setTipoToken("FRACC");
            }
        }
        // Verificar si es un identificador
        if (!tokenValido) {
            if (this.tokenActual.matches("[a-z|A-Z][a-z|A-Z|0-9]*")) {
                tokenValido = true;
                setTipoToken("id");
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

    public void error(String token) {
        System.out.println("Token no valido para: " + token);
    }
}