import java.util.ArrayList;
import java.util.HashMap;

public class CodigoIntermedio {
    private ArrayList<String> tokens, tokensNaturales;
    private HashMap<String, Variables> PuntoCodeDatos;
    private StringBuilder codigoIntermedio = new StringBuilder();
    // private String M_expresiones[] = { "==", "!==", ">", ">=", "<", "<=", };
    private String formato = "%-10s %-10s %-20s%n"; // Formato de columnas: Nombre - Tipo - Valor
    private int indiceComienzaEstatutos = 0;

    public CodigoIntermedio(ArrayList<String> tokensRecibidos, ArrayList<String> tokensNaturalesRecibidos) {
        this.tokens = tokensRecibidos;
        this.tokensNaturales = tokensNaturalesRecibidos;
        this.PuntoCodeDatos = new HashMap<>();
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
                    if (tokens.get(i).equals("IF") || (tokens.get(i).equals("ID") && tokens.get(i + 1).equals("="))) {
                        indiceComienzaEstatutos = i;
                    }

                    // Si la variable ya fue declarada, recuperar su tipo
                    if (PuntoCodeDatos.containsKey(nombreVariable)) {
                        tipodedato = PuntoCodeDatos.get(nombreVariable).getTipo();
                    } else {
                        if (tipodedato.equals("")) {
                            throw new Exception("Error: Variable no declarada: " + nombreVariable);
                        }
                    }

                    Variables variable = new Variables(tipodedato, valorFinal);
                    PuntoCodeDatos.put(nombreVariable, variable);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error al crear el código intermedio: " + e.getMessage());
        }
    }

    public String PuntoData() {
        codigoIntermedio.append(String.format(formato, "", ".MODEL", ""));
        codigoIntermedio.append(String.format(formato, "", ".STACK", ""));
        codigoIntermedio.append(String.format(formato, "", ".DATA", ""));
        for (String key : PuntoCodeDatos.keySet()) {
            Variables variable = PuntoCodeDatos.get(key);
            String tipo = variable.getTipo();
            String valor = variable.getValorStr();

            if (tipo.equals("string")) {
                if (valor.equals("?")) {
                    valor = "$";
                }
                codigoIntermedio.append(String.format(formato, key, "DB", "256 ('" + valor + "')"));
            } else if (tipo.equals("dou")) {
                codigoIntermedio.append(String.format(formato, key, "DD", valor));
            } else if (tipo.equals("int")) {
                codigoIntermedio.append(String.format(formato, key, "DW", valor));
            }
        }

        PuntoCode();
        return codigoIntermedio.toString();
    }

    public void PuntoCode() {
        codigoIntermedio.append(String.format(formato, "", ".CODE", ""));
        codigoIntermedio.append(String.format(formato, "MAIN", "PROC", "FAR"));
        try {
            for (int i = indiceComienzaEstatutos; i < tokens.size(); i++) {
                // TOKEN: ID
                if (tokens.get(i).equals("ID")) {
                    String nombreVariable = tokensNaturales.get(i);
                    String tipo = PuntoCodeDatos.get(nombreVariable).getTipo();
                    String valor = PuntoCodeDatos.get(nombreVariable).getValorStr();
                    System.out.println("Nombre: " + nombreVariable + " Tipo: " + tipo + " Valor: " + valor);
                    if (tipo.equals("DW")) {
                        AsignacionOperacion(nombreVariable, tipo, valor);
                    } else if (tipo.equals("DD")) {
                        AsignacionOperacion(nombreVariable, tipo, valor);
                    } else if (tipo.equals("DB")) {
                        AsignacionOperacion(nombreVariable, tipo, valor);
                    }
                }
                // TOKEN: IF
                if (tokens.get(i).equals("IF")) {
                    String variable1 = tokensNaturales.get(i + 1);
                    String comparador = tokensNaturales.get(i + 2);
                    String variable2 = tokensNaturales.get(i + 3);
                    Comparacion(variable1, comparador, variable2);
                }
                if (tokens.get(i).equals("ELSE")) {
                    codigoIntermedio.append(String.format(formato, "ELSE", "", ""));
                }
                if (tokens.get(i).equals("print")) {
                    String variable = tokensNaturales.get(i + 1);
                    String tipo = PuntoCodeDatos.get(variable).getTipo();

                    if (tipo.equals("int")) {
                        codigoIntermedio.append(String.format(formato, "MOV", "DX", variable));
                        codigoIntermedio.append(String.format(formato, "MOV", "AH", "09"));
                        codigoIntermedio.append(String.format(formato, "INT", "21H"));
                    } else if (tipo.equals("dou")) {
                        codigoIntermedio.append(String.format(formato, "MOV", "DX", variable));
                        codigoIntermedio.append(String.format(formato, "MOV", "AH", "09"));
                        codigoIntermedio.append(String.format(formato, "INT", "21H"));
                    } else if (tipo.equals("string")) {
                        codigoIntermedio.append(String.format(formato, "MOV", "DX", variable));
                        codigoIntermedio.append(String.format(formato, "MOV", "AH", "09"));
                        codigoIntermedio.append(String.format(formato, "INT", "21H"));
                    }

                }
                if (tokens.get(i).equals("read")) {

                    String variable = tokensNaturales.get(i + 1);
                    String tipo = PuntoCodeDatos.get(variable).getTipo();

                    if (tipo.equals("int")) {
                        codigoIntermedio.append(String.format(formato, "MOV", "AH", "01"));
                        codigoIntermedio.append(String.format(formato, "INT", "21H"));
                        codigoIntermedio.append(String.format(formato, "MOV", variable, "AL"));
                    } else if (tipo.equals("dou")) {
                        codigoIntermedio.append(String.format(formato, "MOV", "AH", "01"));
                        codigoIntermedio.append(String.format(formato, "INT", "21H"));
                        codigoIntermedio.append(String.format(formato, "MOV", variable, "AL"));
                    } else if (tipo.equals("string")) {
                        codigoIntermedio.append(String.format(formato, "MOV", "AH", "01"));
                        codigoIntermedio.append(String.format(formato, "INT", "21H"));
                        codigoIntermedio.append(String.format(formato, "MOV", variable, "AL"));
                    }
                } else {
                    throw new Exception("Error: Token no reconocido: " + tokens.get(i));
                }
            }
            codigoIntermedio.append(String.format(formato, "END", "MAIN", ""));
            codigoIntermedio.append(String.format(formato, "", "END", "PROC"));
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

    public void Comparacion(String variable1, String comparador, String variable2) {
        try {
            String tipo1 = PuntoCodeDatos.get(variable1).getTipo();
            String tipo2 = PuntoCodeDatos.get(variable2).getTipo();

            if (tipo1.equals("DW") && tipo2.equals("DW")) {
                // Both variables are 16-bit integers
                codigoIntermedio.append(String.format(formato, "MOV", "AX", variable1));
                codigoIntermedio.append(String.format(formato, "CMP", "AX", variable2));
            } else if (tipo1.equals("DD") && tipo2.equals("DD")) {
                // Both variables are 32-bit integers
                codigoIntermedio.append(String.format(formato, "MOV", "EAX", variable1));
                codigoIntermedio.append(String.format(formato, "CMP", "EAX", variable2));
            } else if ((tipo1.equals("DW") && tipo2.equals("DD")) || (tipo1.equals("DD") && tipo2.equals("DW"))) {
                // Handle type conversion between 16-bit and 32-bit integers
                if (tipo1.equals("DW")) {
                    codigoIntermedio.append(String.format(formato, "MOV", "AX", variable1));
                    codigoIntermedio.append(String.format(formato, "CWD", "", "")); // Sign-extend AX to DX:AX
                    codigoIntermedio.append(String.format(formato, "CMP", "DX:AX", variable2));
                } else {
                    codigoIntermedio.append(String.format(formato, "MOV", "EAX", variable2));
                    codigoIntermedio.append(String.format(formato, "CMP", variable1, "EAX"));
                }
            } else if (tipo1.equals("DB") && tipo2.equals("DB")) {
                // Both variables are 8-bit values
                codigoIntermedio.append(String.format(formato, "MOV", "AL", variable1));
                codigoIntermedio.append(String.format(formato, "CMP", "AL", variable2));
            } else {
                throw new ArithmeticException("Error: Tipos de datos incompatibles en la condición IF");
            }
            // Add the appropriate jump instruction based on the comparator
            saltoComparacion(comparador, "ELSE");

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
            agregarOperacion("MOV", "AX", primerNumero);
            agregarOperacion("ADD", "AX", segundoNumero);
            agregarOperacion("MOV", nombreVariable, "AX");
        } else if (operador.equals("-")) {
            agregarOperacion("MOV", "AX", primerNumero);
            agregarOperacion("SUB", "AX", segundoNumero);
            agregarOperacion("MOV", nombreVariable, "AX");
        } else if (operador.equals("*")) {
            agregarOperacion("MOV", "AX", primerNumero);
            agregarOperacion("IMUL", "AX", segundoNumero);
            agregarOperacion("MOV", nombreVariable, "AX");
        } else if (operador.equals("/")) {
            if (segundoNumero.equals("0")) {
                throw new ArithmeticException("Error: División por cero no permitida.");
            }
            agregarOperacion("MOV", "AX", primerNumero);
            agregarOperacion("MOV", "DX", "0");
            agregarOperacion("IDIV", "AX", segundoNumero);
            agregarOperacion("MOV", nombreVariable, "AX");
        }
    }

    public void asignacionDD(String primerNumero, String operador, String segundoNumero, String valor,
            String nombreVariable) {
        if (operador.equals("+")) {
            agregarOperacion("MOV", "EAX", primerNumero);
            agregarOperacion("ADD", "EAX", segundoNumero);
            agregarOperacion("MOV", nombreVariable, "EAX");
        } else if (operador.equals("-")) {
            agregarOperacion("MOV", "EAX", primerNumero);
            agregarOperacion("SUB", "EAX", segundoNumero);
            agregarOperacion("MOV", nombreVariable, "EAX");
        } else if (operador.equals("*")) {
            agregarOperacion("MOV", "EAX", primerNumero);
            agregarOperacion("IMUL", "EAX", segundoNumero);
            agregarOperacion("MOV", nombreVariable, "EAX");
        } else if (operador.equals("/")) {
            if (segundoNumero.equals("0")) {
                throw new ArithmeticException("Error: División por cero no permitida.");
            }
            agregarOperacion("MOV", "EAX", primerNumero);
            agregarOperacion("MOV", "EDX", "0");
            agregarOperacion("IDIV", "EAX", segundoNumero);
            agregarOperacion("MOV", nombreVariable, "EAX");
        }
    }

    // pendiente
    public void asignacionDB(String nombreVariable, String valor) {

    }

    private void agregarOperacion(String instruction, String operand1, String operand2) {
        codigoIntermedio.append(String.format(formato, instruction, operand1, operand2));
    }

    public HashMap<String, Variables> getPuntoCodeDatos() {
        return PuntoCodeDatos;
    }
}
