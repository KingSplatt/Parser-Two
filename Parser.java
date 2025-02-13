import java.io.IOException;
import java.util.ArrayList;

public class Parser extends IOException {
    private String token;
    private miEscaner scanner;
    private final String M_id = "ID",
            M_int = "int",
            M_string = "string",
            M_IF = "IF",
            M_ELSE = "ELSE",
            M_print = "print",
            M_read = "read",
            M_num = "Num",
            M_dou = "FRACC";
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
        if (scanner.getTipoToken().equals("id")) {
            this.token = "id";
            System.out.println("Token: " + this.token);
            tokens.add(this.token);
        } else {
            System.out.println("Token: " + this.token);
            tokens.add(this.token);
        }
    }

    public void comer(String tok) {
        try {
            if (this.token.equals(tok)) {
                avanzar();
            } else {
                throw new Exception("error en " + tok);
            }

        } catch (Exception e) {
            System.err.println(e);

        }

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
                throw new Exception("Error se esperaba un tipo de dato");
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
        if (this.token == M_id) {
            comer(M_id);
        } else if (this.token == SUMA(M_num) || this.token == RESTA(M_num) || this.token == MULM(M_num)
                || this.token == DIVM(M_num)) {
            comer(this.token);
        } else if (this.token == SUMA(M_dou) || this.token == RESTA(M_dou) || this.token == MULM(M_dou)
                || this.token == DIVM(M_dou)) {
            comer(this.token);
        }
    }

    public String SUMA(String token) {
        if (token.equals(M_num)) {
            comer(M_num);
            if (this.token.equals(M_Operadores[0])) {
                comer(M_Operadores[0]);
                if (this.token.equals(M_num)) {
                    comer(M_num);
                } else if (this.token.equals(M_dou)) {
                    comer(M_dou);
                }
            }
        } else if (token.equals(M_dou)) {
            comer(M_dou);
            if (this.token.equals(M_Operadores[0])) {
                comer(M_Operadores[0]);
                if (this.token.equals(M_num)) {
                    comer(M_num);
                } else if (this.token.equals(M_dou)) {
                    comer(M_dou);
                }
            }
        }
        return token;
    }

    public String RESTA(String token) {
        if (token.equals(M_num)) {
            comer(M_num);
            if (this.token.equals(M_Operadores[1])) {
                comer(M_Operadores[1]);
                if (this.token.equals(M_num)) {
                    comer(M_num);
                } else if (this.token.equals(M_dou)) {
                    comer(M_dou);
                }
            }
        } else if (token.equals(M_dou)) {
            comer(M_dou);
            if (this.token.equals(M_Operadores[1])) {
                comer(M_Operadores[1]);
                if (this.token.equals(M_num)) {
                    comer(M_num);
                } else if (this.token.equals(M_dou)) {
                    comer(M_dou);
                }
            }
        }
        return token;
    }

    public String MULM(String token) {
        if (token.equals(M_num)) {
            comer(M_num);
            if (this.token.equals(M_Operadores[2])) {
                comer(M_Operadores[2]);
                if (this.token.equals(M_num)) {
                    comer(M_num);
                } else if (this.token.equals(M_dou)) {
                    comer(M_dou);
                }
            }
        } else if (token.equals(M_dou)) {
            comer(M_dou);
            if (this.token.equals(M_Operadores[2])) {
                comer(M_Operadores[2]);
                if (this.token.equals(M_num)) {
                    comer(M_num);
                } else if (this.token.equals(M_dou)) {
                    comer(M_dou);
                }
            }
        }
        return token;
    }

    public String DIVM(String token) {
        if (token.equals(M_num)) {
            comer(M_num);
            if (this.token.equals(M_Operadores[3])) {
                comer(M_Operadores[3]);
                if (this.token.equals(M_num)) {
                    comer(M_num);
                } else if (this.token.equals(M_dou)) {
                    comer(M_dou);
                }
            }
        } else if (token.equals(M_dou)) {
            comer(M_dou);
            if (this.token.equals(M_Operadores[3])) {
                comer(M_Operadores[3]);
                if (this.token.equals(M_num)) {
                    comer(M_num);
                } else if (this.token.equals(M_dou)) {
                    comer(M_dou);
                }
            }
        }
        return token;
    }

    public void error() {
        System.out.println("Error de sintaxis");
        System.exit(0);
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }
}
