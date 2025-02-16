import java.io.IOException;
import java.util.ArrayList;

public class Parser extends IOException {
    private String token;
    private miEscaner scanner;
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

    public Parser(String codigo) throws Exception {
        try {
            if (codigo.equals("")) {
                throw new RuntimeException();
            }
            scanner = new miEscaner(codigo);
            avanzar();
        } catch (Exception e) {
            throw new RuntimeException("No se puede leer un archivo vacio");
        }
    }

    public void avanzar() {
        this.token = scanner.getToken(true);
        if (scanner.getTipoToken().equals("ID")) {
            this.token = "ID";
            System.out.println("Token: " + this.token);
            tokens.add(this.token);
        } else if (scanner.getTipoToken().equals("Num")) {
            this.token = "Num";
            System.out.println("Token: " + this.token);
        } else if (scanner.getTipoToken().equals("FRACC")) {
            System.out.println("Token: " + this.token);
            tokens.add(this.token);
        } else {
            System.out.println("Token: " + this.token);
            tokens.add(this.token);
        }
    }

    public void comer(String tok) {
        if (this.token.equals(tok)) {
            avanzar();
        } else {
            error("Se esperaba " + tok + " pero se encontró " + this.token);
        }
    }

    public void error(String mensaje) {
        System.err.println("Error de sintaxis: " + mensaje);
        System.exit(1);
    }

    public void intorstringdou(String tok) {
        try {
            if (this.token.equals(M_int)) {
                comer(M_int);
            } else if (this.token.equals(M_string)) {
                comer(M_string);
            } else if (this.token.equals(M_dou)) {
                comer(M_dou);
            } else {
                error("Se esperaba un tipo de dato, pero se recibio: " + this.token);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void P() throws Exception {
        try {
            D();
            S();
            System.exit(0);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void D() {
        if (this.token.equals(M_id)) {
            comer(M_id);
            intorstringdou(this.token);
            comer(";");
            D();
        } else {
            return;
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
                throw new Exception("Error se esperaba algo en S (IF, ID, print, read)");
            }
        } catch (Exception e) {
            System.err.println(e);
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
                    throw new Exception("Error se esperaba una expresion");
                }
            }
        } catch (Exception e) {
            throw new Exception("Error se esperaba una " + e.getMessage());
        }
    }

    public void OPER() {
        if (this.token.equals(M_id) || this.token.equals(M_num) || this.token.equals(M_dou)) {
            TERMINO();
            while (esOperador(this.token)) {
                String operador = this.token;
                comer(operador);
                TERMINO();
            }
        } else {
            error("g");
        }
    }

    public void TERMINO() {
        if (scanner.getTipoToken().equals("Num") || scanner.getTipoToken().equals("FRACC")) {
            avanzar();
        } else if (this.token.equals("ID")) {
            comer("ID");
        } else {
            error("g");
        }
    }

    private boolean esOperador(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }
}
