import java.io.IOException;
import java.util.ArrayList;

public class Parser extends IOException {
    private String token;
    private miEscaner scanner;
    private Semantico semantico;
    private final String M_id = "ID",
            M_int = "int",
            M_string = "string",
            M_dou = "dou",
            M_IF = "IF",
            M_ELSE = "ELSE",
            M_print = "print",
            M_read = "read",
            M_num = "Num";
    private String M_corchetes[] = { "{", "}" };
    private String M_Operadores[] = { "+", "-", "*", "/", "=" };
    private String M_expresiones[] = { "==", "!==", ">", ">=", "<", "<=", };
    private ArrayList<String> tokens = new ArrayList<String>();
    private ArrayList<String> tokensNaturales = new ArrayList<String>();

    public Parser(String codigo) throws Exception {
        try {
            scanner = new miEscaner(codigo);
            avanzar();
        } catch (Exception e) {
            throw new RuntimeException("Error en el analisis lexico: " + e.getMessage());
        }
    }

    public void avanzar() throws Exception {
        this.token = scanner.getToken(true);
        tokensNaturales.add(this.token);
        if (scanner.getTipoToken().equals("ID")) {
            this.token = "ID";
            System.out.println("Token: " + this.token);
            tokens.add(this.token);
        } else if (scanner.getTipoToken().equals("Num")) {
            this.token = "Num";
            System.out.println("Token: " + this.token);
            tokens.add(this.token);
        } else if (scanner.getTipoToken().equals("dou")) {
            this.token = "FRACC";
            System.out.println("Token: " + this.token);
            tokens.add(this.token);
        } else {
            System.out.println("Token: " + this.token);
            tokens.add(this.token);
        }
    }

    public void comer(String tok) throws Exception {
        try {
            if (this.token.equals(tok)) {
                avanzar();
            } else {
                throw new Exception("Se esperaba " + tok);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void intorstringdou(String tok) throws Exception {
        try {
            if (this.token.equals(M_int)) {
                comer(M_int);
            } else if (this.token.equals(M_string)) {
                comer(M_string);
            } else if (this.token.equals(M_dou)) {
                comer(M_dou);
            } else {
                throw new Exception("Se esperaba un tipo de dato");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void P() throws Exception {
        try {
            D();
            S();
            tokens.remove(tokens.size() - 1);
            tokensNaturales.remove(tokensNaturales.size() - 1);
            semantico = new Semantico(tokens, tokensNaturales);
            semantico.AnalizarTokens();
            semantico.AnalizarValorTokens();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void D() throws Exception {
        try {
            if (this.token.equals(M_id)) {
                // checar el siguiente token
                String aux = scanner.getToken(false);
                if (aux.equals(M_Operadores[4])) {
                    return;
                }
                comer(M_id);
                if (this.token.equals(M_expresiones[0]) ||
                        this.token.equals(M_expresiones[1])
                        || this.token.equals(M_expresiones[2]) || this.token.equals(M_expresiones[3])
                        || this.token.equals(M_expresiones[4]) ||
                        this.token.equals(M_expresiones[5])) {
                    throw new Exception("Se esperaba un IF");
                }
                intorstringdou(this.token);
                comer(";");
                D();
            } else {
                return;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public void S() throws Exception {
        try {
            if (this.token.equals(M_IF)) {
                comer(M_IF);
                E();
                comer(M_corchetes[0]);
                S();
                comer(M_corchetes[1]);
                if (this.token.equals(M_ELSE)) {
                    comer(M_ELSE);
                    comer(M_corchetes[0]);
                    S();
                    comer(M_corchetes[1]);
                }
            } else if (this.token.equals(M_id)) { // ME QUEDE AQUI PARA PRODUCCION DE UN ID = OPER
                comer(M_id);
                comer(M_Operadores[4]);
                OPER();
            } else if (this.token.equals(M_print)) {
                comer(M_print);
                OPER();
            } else if (this.token.equals(M_read)) {
                comer(M_read);
                comer(M_id);
            } else {
                throw new Exception("Se esperaba un estatuto (IF, ID, print, read)");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage() + " en cambio se recibio: " + this.token);
        }
    }

    public void E() throws Exception {
        try {
            if (!this.token.equals(M_id)) {
                throw new Exception("Expresion");
            }
            if (this.token.equals(M_id)) {
                comer(M_id);
                if (this.token.equals(M_expresiones[0])) {
                    comer(M_expresiones[0]);
                    comer(M_id);
                } else if (this.token.equals(M_expresiones[1])) {
                    comer(M_expresiones[1]);
                    comer(M_id);
                } else if (this.token.equals(M_expresiones[2])) {
                    comer(M_expresiones[2]);
                    comer(M_id);
                } else if (this.token.equals(M_expresiones[3])) {
                    comer(M_expresiones[3]);
                    comer(M_id);
                } else if (this.token.equals(M_expresiones[4])) {
                    comer(M_expresiones[4]);
                    comer(M_id);
                } else if (this.token.equals(M_expresiones[5])) {
                    comer(M_expresiones[5]);
                    comer(M_id);
                } else {
                    throw new Exception("expresion");
                }
            }
        } catch (Exception e) {
            throw new Exception("Error se esperaba una " + e.getMessage());
        }
    }

    public void OPER() throws Exception {
        try {
            if (this.token.equals(M_num) || this.token.equals(M_dou)) {
                TERMINO();
                if (esOperador(this.token)) {
                    String operador = this.token;
                    comer(operador);
                    TERMINO();
                } else {
                    throw new Exception("Se esperaba un operador");
                }
            } else if (this.token.equals(M_id)) {
                comer(M_id);
            } else {
                throw new Exception("Se esperaba un Numero, Fraccion o un ID");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void TERMINO() throws Exception {
        try {
            if (scanner.getTipoToken().equals("Num") || scanner.getTipoToken().equals("FRACC")) {
                avanzar();
            } else if (this.token.equals("ID")) {
                comer("ID");
            } else {
                throw new Exception("Se esperaba un Numero, Fraccion o un ID");
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private boolean esOperador(String token) {
        if (token.equals(M_Operadores[0]) || token.equals(M_Operadores[1]) || token.equals(M_Operadores[2])
                || token.equals(M_Operadores[3])) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public ArrayList<String> getTokensNaturales() {
        return tokensNaturales;
    }
}
