import java.util.ArrayList;
import java.util.HashMap;

public class CodigoIntermedio {
    private ArrayList<String> tokens, tokensNaturales;
    private HashMap<String, Variables> PuntoCodeDatos, tablaSimbolos;

    private StringBuilder codigoIntermedio = new StringBuilder();
    private String formato = "%-15s\t%-15s\t%-25s%n";
    // Formato de columnas: Nombre - Tipo - Valor
    private int indiceComienzaEstatutos = 0;
    private Boolean entroCondicional;

    public CodigoIntermedio(Semantico semantico, ArrayList<String> tokens, ArrayList<String> tokensNaturales) {
        this.tablaSimbolos = semantico.getTablaSimbolos();
        this.entroCondicional = semantico.getEntroCondicional();
        this.indiceComienzaEstatutos = semantico.getIndiceEstatutos();
        this.tokens = tokens;
        this.tokensNaturales = tokensNaturales;
        this.PuntoCodeDatos = new HashMap<String, Variables>();
    }

    public void CrearCodigoIntermedio() throws Exception {
        PuntoCode();
    }

    public String PuntoData() {
        codigoIntermedio.append(String.format(formato, "", ".MODEL", "SMALL"));
        codigoIntermedio.append(String.format(formato, "", ".STACK", ""));
        codigoIntermedio.append(String.format(formato, "", ".DATA", ""));
        for (String key : tablaSimbolos.keySet()) {
            Variables variable = tablaSimbolos.get(key);
            String tipo = variable.getTipo();
            if (tipo.equals("string")) {
                PuntoCodeDatos.put(key, new Variables("DB", "'$'"));
                codigoIntermedio.append(String.format(formato, key, "DB", "'$'"));
            } else if (tipo.equals("int")) {
                PuntoCodeDatos.put(key, new Variables("DW", "?"));
                codigoIntermedio.append(String.format(formato, key, "DW", "?"));
            } else if (tipo.equals("dou")) {
                PuntoCodeDatos.put(key, new Variables("DD", "?"));
                codigoIntermedio.append(String.format(formato, key, "DD", "?"));
            }
        }

        PuntoCode();
        return codigoIntermedio.toString();
    }

    public void PuntoCode() {
        codigoIntermedio.append(String.format(formato, "", ".CODE", ""));
        codigoIntermedio.append(String.format(formato, "MAIN", "PROC", "FAR"));
        codigoIntermedio.append(String.format(formato, "", ".STARTUP", ""));
        codigoIntermedio.append(String.format(formato, "", "", ""));
        try {
            while (indiceComienzaEstatutos < tokens.size()) {
                // vericar si es una asignacion ID = Num + Num
                if (tokens.get(indiceComienzaEstatutos).equals("ID")
                        && tokens.get(indiceComienzaEstatutos + 1).equals("=")) {
                    String variable = tokensNaturales.get(indiceComienzaEstatutos);
                    String tipo = PuntoCodeDatos.get(variable).getTipo();
                    String valor = tokensNaturales.get(indiceComienzaEstatutos + 2);
                    AsignacionOperacion(variable, tipo, valor);

                }

                // TOKEN: IF
                if (tokens.get(indiceComienzaEstatutos).equals("IF")) {
                    String variable1 = tokensNaturales.get(indiceComienzaEstatutos + 1);
                    String comparador = tokensNaturales.get(indiceComienzaEstatutos + 2);
                    String variable2 = tokensNaturales.get(indiceComienzaEstatutos + 3);
                    Comparacion(variable1, comparador, variable2);
                }
                if (tokens.get(indiceComienzaEstatutos).equals("ELSE")) {
                    codigoIntermedio.append(String.format(formato, "\tCONTINUA:", "", ""));
                }
                if (tokens.get(indiceComienzaEstatutos).equals("print")) {
                    String numero1 = tokensNaturales.get(indiceComienzaEstatutos + 1);
                    String operador = tokensNaturales.get(indiceComienzaEstatutos + 2);
                    String numero2 = tokensNaturales.get(indiceComienzaEstatutos + 3);
                    impresionNumeroOperadorNumero(numero1, operador, numero2);

                }
                if (tokens.get(indiceComienzaEstatutos).equals("read")) {

                    String variable = tokensNaturales.get(indiceComienzaEstatutos + 1);
                    String tipo = PuntoCodeDatos.get(variable).getTipo();

                    if (tipo.equals("int")) {
                        codigoIntermedio.append(String.format(formato, "MOV", "AH,", "01"));
                        codigoIntermedio.append(String.format(formato, "INT", "21H"));
                        codigoIntermedio.append(String.format(formato, "MOV", variable, "AL"));
                    } else if (tipo.equals("dou")) {
                        codigoIntermedio.append(String.format(formato, "MOV", "AH,", "01"));
                        codigoIntermedio.append(String.format(formato, "INT", "21H"));
                        codigoIntermedio.append(String.format(formato, "MOV", variable + ",", "AL"));
                    } else if (tipo.equals("string")) {
                        codigoIntermedio.append(String.format(formato, "MOV", "AH,", "01"));
                        codigoIntermedio.append(String.format(formato, "INT", "21H"));
                        codigoIntermedio.append(String.format(formato, "MOV", variable + ",", "AL"));
                    }
                }
                indiceComienzaEstatutos++;
            }
            codigoIntermedio.append(String.format(formato, "", "", ""));
            codigoIntermedio.append(String.format(formato, "", ".EXIT", ""));
            codigoIntermedio.append(String.format(formato, "MAIN", "ENDP", ""));
            codigoIntermedio.append(String.format(formato, "", "END", ""));
        } catch (Exception e) {
            System.out.println("Error al crear el código intermedio: " + e.getMessage());
        }
    }

    // Elaboracion de plantillas para el codigo intermedio, asignacion de valores a
    // las variables, para
    // condicion IF y ELSE , para leer datos y para imprimir datos

    public void AsignacionOperacion(String nombreVariable, String tipo, String valor) {
        String primerNumero = tokensNaturales.get(indiceComienzaEstatutos + 2);
        String operador = tokensNaturales.get(indiceComienzaEstatutos + 3);
        String segundoNumero = tokensNaturales.get(indiceComienzaEstatutos + 4);
        if (tipo.equals("DW")) {
            asignacionDW(primerNumero, operador, segundoNumero, valor, nombreVariable);
            indiceComienzaEstatutos += 4;
            return;
        }
        if (tipo.equals("DD")) {
            asignacionDD(primerNumero, operador, segundoNumero, valor, nombreVariable);
            indiceComienzaEstatutos += 4;
            return;
        }
        if (tipo.equals("DB")) {
            asignacionDB(nombreVariable, valor);
            indiceComienzaEstatutos += 2;
            return;
        }
    }

    public void impresionNumeroOperadorNumero(String numero1, String operador, String numero2) {
        codigoIntermedio.append(String.format(formato, "MOV", "Al,", numero1));
        if (operador.equals("+")) {
            codigoIntermedio.append(String.format(formato, "ADD", "Al,", numero2));
        } else if (operador.equals("-")) {
            codigoIntermedio.append(String.format(formato, "SUB", "Al,", numero2));
        } else if (operador.equals("*")) {
            codigoIntermedio.append(String.format(formato, "IMUL", "Al,", numero2));
        } else if (operador.equals("/")) {
            codigoIntermedio.append(String.format(formato, "MOV", "DX,", "0"));
            codigoIntermedio.append(String.format(formato, "IDIV", "Al,", numero2));
        }

        codigoIntermedio.append(String.format(formato, "MOV", "AH,", "0"));
        codigoIntermedio.append(String.format(formato, "MOV", "BL,", "10"));
        codigoIntermedio.append(String.format(formato, "DIV", "BL", ""));
        codigoIntermedio.append(String.format(formato, "MOV", "BH,", "AH"));
        codigoIntermedio.append(String.format(formato, "MOV", "DL,", "AL"));
        codigoIntermedio.append(String.format(formato, "ADD", "DL,", "30H"));
        codigoIntermedio.append(String.format(formato, "MOV", "AH,", "02H"));
        codigoIntermedio.append(String.format(formato, "INT", "21H", ""));

        codigoIntermedio.append(String.format(formato, "MOV", "DL,", "BH"));
        codigoIntermedio.append(String.format(formato, "ADD", "DL,", "30H"));
        codigoIntermedio.append(String.format(formato, "INT", "21H", ""));
    }

    public void Comparacion(String variable1, String comparador, String variable2) {
        try {
            String tipo1 = PuntoCodeDatos.get(variable1).getTipo();
            String tipo2 = PuntoCodeDatos.get(variable2).getTipo();

            if (tipo1.equals("DW") && tipo2.equals("DW")) {
                // Both variables are 16-bit integers
                codigoIntermedio.append(String.format(formato, "MOV", "AX,", variable1));
                codigoIntermedio.append(String.format(formato, "CMP", "AX,", variable2));
            } else if (tipo1.equals("DD") && tipo2.equals("DD")) {
                // Both variables are 32-bit integers
                codigoIntermedio.append(String.format(formato, "MOV", "EAX,", variable1));
                codigoIntermedio.append(String.format(formato, "CMP", "EAX,", variable2));
            } else if ((tipo1.equals("DW") && tipo2.equals("DD")) || (tipo1.equals("DD") && tipo2.equals("DW"))) {
                // Handle type conversion between 16-bit and 32-bit integers
                if (tipo1.equals("DW")) {
                    codigoIntermedio.append(String.format(formato, "MOV", "AX,", variable1));
                    codigoIntermedio.append(String.format(formato, "CWD", "", ""));
                    codigoIntermedio.append(String.format(formato, "MOV", "BX,", "WORD PTR " + variable2));
                    codigoIntermedio.append(String.format(formato, "MOV", "CX,", "WORD PTR " + variable2 + "+2"));
                    codigoIntermedio.append(String.format(formato, "CMP", "DX,", "CX"));
                } else if (tipo1.equals("DD")) {
                    codigoIntermedio.append(String.format(formato, "MOV", "AX,", variable2));
                    codigoIntermedio.append(String.format(formato, "CWD", "", ""));
                    codigoIntermedio.append(String.format(formato, "MOV", "BX,", "WORD PTR " + variable1));
                    codigoIntermedio.append(String.format(formato, "MOV", "CX,", "WORD PTR " + variable1 + "+2"));
                    codigoIntermedio.append(String.format(formato, "CMP", "CX,", "DX"));

                }
            } else if (tipo1.equals("DB") && tipo2.equals("DB")) {
                // Both variables are 8-bit values
                codigoIntermedio.append(String.format(formato, "MOV", "AL,", variable1));
                codigoIntermedio.append(String.format(formato, "CMP", "AL,", variable2));
            } else {
                throw new ArithmeticException("Error: Tipos de datos incompatibles en la condición IF");
            }
            // Add the appropriate jump instruction based on the comparator
            saltoComparacion(comparador, "CONTINUA");
            codigoIntermedio.append(String.format(formato, "", "", ""));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void saltoComparacion(String comparador, String etiqueta) {
        switch (comparador) {
            case "==":
                codigoIntermedio.append(String.format(formato, "JE", etiqueta, ""));
                indiceComienzaEstatutos += 4;
                break;
            case "!==":
                codigoIntermedio.append(String.format(formato, "JNE", etiqueta, ""));
                indiceComienzaEstatutos += 4;
                break;
            case ">":
                codigoIntermedio.append(String.format(formato, "JG", etiqueta, ""));
                indiceComienzaEstatutos += 4;
                break;
            case ">=":
                codigoIntermedio.append(String.format(formato, "JGE", etiqueta, ""));
                indiceComienzaEstatutos += 4;
                break;
            case "<":
                codigoIntermedio.append(String.format(formato, "JL", etiqueta, ""));
                indiceComienzaEstatutos += 4;
                break;
            case "<=":
                codigoIntermedio.append(String.format(formato, "JLE", etiqueta, ""));
                indiceComienzaEstatutos += 4;
                break;
            default:
                codigoIntermedio.append(String.format(formato, "JMP", etiqueta, ""));
                indiceComienzaEstatutos += 4;
        }
    }

    public void asignacionDW(String primerNumero, String operador, String segundoNumero, String valor,
            String nombreVariable) {
        if (operador.equals("+")) {
            agregarOperacion("MOV", "AX,", primerNumero);
            agregarOperacion("ADD", "AX,", segundoNumero);
            agregarOperacion("MOV", nombreVariable + ",", "AX");
        } else if (operador.equals("-")) {
            agregarOperacion("MOV", "AX,", primerNumero);
            agregarOperacion("SUB", "AX,", segundoNumero);
            agregarOperacion("MOV", nombreVariable + ",", "AX");
        } else if (operador.equals("*")) {
            agregarOperacion("MOV", "AX,", primerNumero);
            agregarOperacion("IMUL", "AX,", segundoNumero);
            agregarOperacion("MOV", nombreVariable + ",", "AX");
        } else if (operador.equals("/")) {
            if (segundoNumero.equals("0")) {
                throw new ArithmeticException("Error: División por cero no permitida.");
            }
            agregarOperacion("MOV", "AX,", primerNumero);
            agregarOperacion("MOV", "DX,", "0");
            agregarOperacion("IDIV", "AX,", segundoNumero);
            agregarOperacion("MOV", nombreVariable + ",", "AX");
        }
    }

    public void asignacionDD(String primerNumero, String operador, String segundoNumero, String valor,
            String nombreVariable) {
        if (operador.equals("+")) {
            agregarOperacion("MOV", "EAX,", primerNumero);
            agregarOperacion("ADD", "EAX,", segundoNumero);
            agregarOperacion("MOV", nombreVariable + ",", "EAX");
        } else if (operador.equals("-")) {
            agregarOperacion("MOV", "EAX,", primerNumero);
            agregarOperacion("SUB", "EAX,", segundoNumero);
            agregarOperacion("MOV", nombreVariable + ",", "EAX");
        } else if (operador.equals("*")) {
            agregarOperacion("MOV", "EAX,", primerNumero);
            agregarOperacion("IMUL", "EAX,", segundoNumero);
            agregarOperacion("MOV", nombreVariable + ",", "EAX");
        } else if (operador.equals("/")) {
            if (segundoNumero.equals("0")) {
                throw new ArithmeticException("Error: División por cero no permitida.");
            }
            agregarOperacion("MOV", "EAX,", primerNumero);
            agregarOperacion("MOV", "EDX,", "0");
            agregarOperacion("IDIV", "EAX,", segundoNumero);
            agregarOperacion("MOV", nombreVariable + ",", "EAX");
        }
    }

    // pendiente
    public void asignacionDB(String nombreVariable, String valor) {
        codigoIntermedio.append(String.format(formato, "MOV", "SI,", "OFFSET " + nombreVariable));
        for (int i = 0; i < valor.length(); i++) {
            codigoIntermedio
                    .append(String.format(formato, "MOV", "BYTE PTR [SI+" + i + "],", "'" + valor.charAt(i) + "'"));
        }
        codigoIntermedio.append(String.format(formato, "MOV", "BYTE PTR [SI+" + valor.length() + "],", "'$'"));
    }

    private void agregarOperacion(String instruction, String operand1, String operand2) {
        codigoIntermedio.append(String.format(formato, instruction, operand1, operand2));
    }

    public HashMap<String, Variables> getPuntoCodeDatos() {
        return PuntoCodeDatos;
    }
}
