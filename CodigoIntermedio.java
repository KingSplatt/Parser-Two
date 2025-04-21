import java.util.ArrayList;
import java.util.HashMap;

public class CodigoIntermedio {
    private ArrayList<String> tokens, tokensNaturales;
    private HashMap<String, Variables> PuntoCodeDatos, tablaSimbolos;

    private StringBuilder codigoIntermedio = new StringBuilder();
    private String formato = "%-15s\t%-15s\t%-25s%n";
    private int indiceComienzaEstatutos = 0;
    private Boolean acaboElse,hayElse;

    public CodigoIntermedio(Semantico semantico, ArrayList<String> tokens, ArrayList<String> tokensNaturales) {
        this.tablaSimbolos = semantico.getTablaSimbolos();
        this.indiceComienzaEstatutos = semantico.getIndiceEstatutos();
        this.tokens = tokens;
        this.tokensNaturales = tokensNaturales;
        this.PuntoCodeDatos = new HashMap<String, Variables>();
        this.acaboElse = false;
        this.hayElse = false;
    }

    public void CrearCodigoIntermedio() throws Exception {
        PuntoCode();
    }

    public String PuntoData() {
        codigoIntermedio.append(String.format(formato, "", ".MODEL", "SMALL"));
        codigoIntermedio.append(String.format(formato, "", ".STACK", ""));
        codigoIntermedio.append(String.format(formato, "", ".DATA", ""));
        codigoIntermedio.append(String.format(formato, "", ".386", ""));
        for (String key : tablaSimbolos.keySet()) {
            Variables variable = tablaSimbolos.get(key);
            String tipo = variable.getTipo();
            if (tipo.equals("string")) {
                PuntoCodeDatos.put(key, new Variables("DB", "'$'"));
                codigoIntermedio.append(String.format(formato, key, "DB", "256 DUP ('$')"));
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
        int completo = 0;
        try {
            while (indiceComienzaEstatutos < tokens.size()) {
                if(completo == 2){
                    break;
                }
                if(acaboElse){
                    indiceComienzaEstatutos = tokens.indexOf("IF") + 5;
                    completo += 1;
                    acaboElse = false;
                    codigoIntermedio.append(String.format(formato, "", "", ""));
                    codigoIntermedio.append(String.format(formato, "\tCONTINUA:", "", ""));
                }
                if (tokens.get(indiceComienzaEstatutos).equals("ID")
                        && tokens.get(indiceComienzaEstatutos + 1).equals("=")) {
                    String variable = tokensNaturales.get(indiceComienzaEstatutos);
                    String tipo = PuntoCodeDatos.get(variable).getTipo();
                    String valor = tokensNaturales.get(indiceComienzaEstatutos + 2);
                    AsignacionOperacion(variable, tipo, valor);
                }

                if (tokens.get(indiceComienzaEstatutos).equals("IF")) {
                    String variable1 = tokensNaturales.get(indiceComienzaEstatutos + 1);
                    String comparador = tokensNaturales.get(indiceComienzaEstatutos + 2);
                    String variable2 = tokensNaturales.get(indiceComienzaEstatutos + 3);
                    Comparacion(variable1, comparador, variable2);
                    completo += 1;
                    if(!tokens.stream().anyMatch(token -> token.equals("ELSE"))){
                        codigoIntermedio.append(String.format(formato, "\tCONTINUA:", "", ""));
                    }
                }

                if (tokens.get(indiceComienzaEstatutos).equals("ELSE")) {
                    if(acaboElse){
                        indiceComienzaEstatutos = tokens.size() - 1;
                    }
                    codigoIntermedio.append(String.format(formato, "JMP", "FINAL", ""));
                    codigoIntermedio.append(String.format(formato, "\tCONTINUA:", "", ""));
                    if(acaboElse){
                        indiceComienzaEstatutos = tokens.indexOf("IF") + 4;
                    }
                }

                if (tokens.get(indiceComienzaEstatutos).equals("print")) {
                    boolean printNON,printID;printNON = impresionNumeroOperadorNumero();printID = impresionIDB();

                    if(printNON){
                        String numero1 = tokensNaturales.get(indiceComienzaEstatutos + 1);
                        String operador = tokensNaturales.get(indiceComienzaEstatutos + 2);
                        String numero2 = tokensNaturales.get(indiceComienzaEstatutos + 3);
                        impresionNumeroOperadorNumero(numero1, operador, numero2);
                    }else if(printID){
                        String variable = tokensNaturales.get(indiceComienzaEstatutos + 1);
                        impresionID(variable);
                    }


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

                if(tokens.get(indiceComienzaEstatutos).equals("}") && hayElse){
                    acaboElse = true;
                    indiceComienzaEstatutos -= 1;
                    codigoIntermedio.append(String.format(formato, "JMP", "FINAL", ""));
                }

                indiceComienzaEstatutos++;
            }
            codigoIntermedio.append(String.format(formato, "\tFINAL:", "", ""));
            codigoIntermedio.append(String.format(formato, "", ".EXIT", ""));
            codigoIntermedio.append(String.format(formato, "MAIN", "ENDP", ""));
            codigoIntermedio.append(String.format(formato, "", "END", ""));
        } catch (Exception e) {
            System.out.println("Error al crear el código intermedio: " + e.getMessage());
        }
    }

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
        boolean n1,n2;n1 = esNumeroAL(numero1);n2 = esNumeroAL(numero2);
        boolean n3,n4;n3 = esNumeroAX(numero1);n4 = esNumeroAX(numero2);
        if (n1 && n2) {
            impresionAL(numero1, operador, numero2);
        } else if (n3 && n4) {
            impresionAX(numero1, operador, numero2);
        }
    }
    //pendiente
    private void impresionID(String variable){

        String tipoSimbolo = tablaSimbolos.get(variable).getTipo();

        if(tipoSimbolo.equals("int")){
            int valor = 0;
            codigoIntermedio.append(String.format(formato, "MOV", "AX,", valor));
            codigoIntermedio.append(String.format(formato, "OR", "AX,", "00110000B"));

            codigoIntermedio.append(String.format(formato, "MOV", "DL,", "AL"));
            codigoIntermedio.append(String.format(formato, "MOV", "AH,", "02H"));
            codigoIntermedio.append(String.format(formato, "INT", "21H", ""));

        }else if(tipoSimbolo.equals("dou")){
            //pendiente
            double valor = 0.0;
            codigoIntermedio.append(String.format(formato, "MOV", "AX,", valor));
            codigoIntermedio.append(String.format(formato, "OR", "AX,", "00110000B"));

            codigoIntermedio.append(String.format(formato, "MOV", "DL,", "AL"));
            codigoIntermedio.append(String.format(formato, "MOV", "AH,", "02H"));
            codigoIntermedio.append(String.format(formato, "INT", "21H", ""));
        }else if(tipoSimbolo.equals("string")){
            codigoIntermedio.append(String.format(formato, "MOV", "SI,", "OFFSET " + variable));
            codigoIntermedio.append(String.format(formato, "MOV", "AH,", "09H"));
            codigoIntermedio.append(String.format(formato, "INT", "21H", ""));
        }
        
        // if (tipo.equals("DB")) {
        //     codigoIntermedio.append(String.format(formato, "MOV", "SI,", "OFFSET " + variable));
        //     codigoIntermedio.append(String.format(formato, "MOV", "DL,", "BYTE PTR [SI]"));
        //     codigoIntermedio.append(String.format(formato, "MOV", "AH,", "02H"));
        //     codigoIntermedio.append(String.format(formato, "INT", "21H", ""));
        // } else if (tipo.equals("DW")) {
        //     codigoIntermedio.append(String.format(formato, "MOV", "AX,", variable));
        //     codigoIntermedio.append(String.format(formato, "CALL", "PRINTNUM", ""));
        // } else if (tipo.equals("DD")) {
        //     codigoIntermedio.append(String.format(formato, "MOV", "EAX,", variable));
        //     codigoIntermedio.append(String.format(formato, "CALL", "PRINTNUM", ""));
        // }
    }

    public void Comparacion(String variable1, String comparador, String variable2) {
        try {
            String tipo1 = PuntoCodeDatos.get(variable1).getTipo();
            String tipo2 = PuntoCodeDatos.get(variable2).getTipo();

            if (tipo1.equals("DW") && tipo2.equals("DW")) {
                codigoIntermedio.append(String.format(formato, "MOV", "AX,", variable1));
                codigoIntermedio.append(String.format(formato, "CMP", "AX,", variable2));
            } else if (tipo1.equals("DD") && tipo2.equals("DD")) {
                codigoIntermedio.append(String.format(formato, "MOV", "EAX,", variable1));
                codigoIntermedio.append(String.format(formato, "CMP", "EAX,", variable2));
            } else if ((tipo1.equals("DW") && tipo2.equals("DD")) || (tipo1.equals("DD") && tipo2.equals("DW"))) {
                if (tipo1.equals("DW")) {
                    codigoIntermedio.append(String.format(formato, "MOV", "AX,", variable1));
                    codigoIntermedio.append(String.format(formato, "MOVZX", "EAX,", "AX"));
                    codigoIntermedio.append(String.format(formato, "MOV", "EBX,", variable2));
                    codigoIntermedio.append(String.format(formato, "CMP", "EAX,", "EBX"));
                } else if (tipo1.equals("DD")) {
                    codigoIntermedio.append(String.format(formato, "MOV", "AX,", variable2));
                    codigoIntermedio.append(String.format(formato, "MOVZX", "EAX,", "AX"));
                    codigoIntermedio.append(String.format(formato, "MOV", "EBX,", variable1));
                    codigoIntermedio.append(String.format(formato, "CMP", "EAX,", "EBX"));

                }
            } else if (tipo1.equals("DB") && tipo2.equals("DB")) {
                codigoIntermedio.append(String.format(formato, "MOV", "AL,", variable1));
                codigoIntermedio.append(String.format(formato, "CMP", "AL,", variable2));
            } else {
                throw new ArithmeticException("Error: Tipos de datos incompatibles en la condición IF");
            }
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
                break;
            case "!==":
                codigoIntermedio.append(String.format(formato, "JNE", etiqueta, ""));
                break;
            case ">":
                codigoIntermedio.append(String.format(formato, "JG", etiqueta, ""));
                break;
            case ">=":
                codigoIntermedio.append(String.format(formato, "JGE", etiqueta, ""));
                break;
            case "<":
                codigoIntermedio.append(String.format(formato, "JL", etiqueta, ""));
                break;
            case "<=":
                codigoIntermedio.append(String.format(formato, "JLE", etiqueta, ""));
                break;
            default:
                codigoIntermedio.append(String.format(formato, "JMP", etiqueta, ""));
        }
        if(tokens.stream().anyMatch(token -> token.equals("ELSE"))){
            indiceComienzaEstatutos = tokens.indexOf("ELSE") + 1;
            hayElse = true;
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

    private void impresionAL(String numero1, String operador, String numero2) {
        if(operador.equals("*") || operador.equals("/")){
            codigoIntermedio.append(String.format(formato, "MOV", "AX,", numero1));
        }else{
            codigoIntermedio.append(String.format(formato, "MOV", "AL,", numero1));
        }
        if (operador.equals("+")) {
            codigoIntermedio.append(String.format(formato, "ADD", "AL,", numero2));
        } else if (operador.equals("-")) {
            codigoIntermedio.append(String.format(formato, "SUB", "AL,", numero2));
        } else if (operador.equals("*")) {
            codigoIntermedio.append(String.format(formato, "IMUL", "AX,", numero2));
        } else if (operador.equals("/")) {
            codigoIntermedio.append(String.format(formato, "MOV", "DL,", numero2));
            codigoIntermedio.append(String.format(formato, "IDIV", "DL", ""));
        }
        impresionResultado(numero1,operador, numero2);
    }

    private void impresionAX(String numero1, String operador, String numero2) {
        codigoIntermedio.append(String.format(formato, "MOV", "AX,", numero1));
        if (operador.equals("+")) {
            codigoIntermedio.append(String.format(formato, "ADD", "AX,", numero2));
        } else if (operador.equals("-")) {
            codigoIntermedio.append(String.format(formato, "SUB", "AX,", numero2));
        } else if (operador.equals("*")) {
            codigoIntermedio.append(String.format(formato, "IMUL", "AX,", numero2));
        } else if (operador.equals("/")) {
            codigoIntermedio.append(String.format(formato, "MOV", "DX,", "0"));
            codigoIntermedio.append(String.format(formato, "IDIV", "AX,", numero2));
        }
        impresionResultado(numero1,operador ,numero2);


    }
    private void impresionResultado(String operador1,String operador, String operador2) {
        int resultado = Integer.parseInt(operador1) * Integer.parseInt(operador2);
        String resultadoStr = String.valueOf(resultado);
        int tamañoDivisor = resultadoStr.length();
        String divisor = "1";
        for (int i = 1; i < tamañoDivisor; i++) {
            divisor += "0";
        }
        if(operador.equals("*")){
            for (int i = 0; i < tamañoDivisor; i++) {
                if(divisor.equals("0") || divisor.equals("1") || divisor.equals("10") ){
                    continue;
                }
                codigoIntermedio.append(String.format(formato, "MOV", "BL,", divisor));
                divisor = String.valueOf(Integer.parseInt(divisor) / 10);
                codigoIntermedio.append(String.format(formato, "", "DIV", "BL"));
                codigoIntermedio.append(String.format(formato, "MOV", "BL,", "AL"));
                codigoIntermedio.append(String.format(formato, "MOV", " CL,", "AH"));
    
                codigoIntermedio.append(String.format(formato, "OR", "BL,", "00110000B"));
    
                codigoIntermedio.append(String.format(formato, "MOV", "DL,", "BL"));
                codigoIntermedio.append(String.format(formato, "MOV", "AH,", "02H"));
                codigoIntermedio.append(String.format(formato, "INT", "21H", ""));
            }
            if(tamañoDivisor == 3){
                codigoIntermedio.append(String.format(formato, "MOV", "AL,", "CL"));
            }

        }else if(operador.equals("/")){
            // Convertir EAX a decimal y mostrarlo
            codigoIntermedio.append(String.format(formato, "MOV", "BL,", "AL"));
            codigoIntermedio.append(String.format(formato, "MOV", "CL,", "AH"));

            codigoIntermedio.append(String.format(formato, "OR", "BL,", "00110000B"));
            codigoIntermedio.append(String.format(formato, "OR", "CL,", "00110000B"));

            codigoIntermedio.append(String.format(formato, "MOV", "DL,", "BL"));
            codigoIntermedio.append(String.format(formato, "MOV", "AH,", "02H"));
            codigoIntermedio.append(String.format(formato, "INT", "21H", ""));
    
            codigoIntermedio.append(String.format(formato, "MOV", "DL,", "CL"));
            codigoIntermedio.append(String.format(formato, "MOV", "AH,", "02H"));
            codigoIntermedio.append(String.format(formato, "INT", "21H", ""));
            return;
        }

        codigoIntermedio.append(String.format(formato, "CBW", "", ""));
        codigoIntermedio.append(String.format(formato, "MOV", "BL,", "10"));
        codigoIntermedio.append(String.format(formato, "DIV", "BL", ""));

        codigoIntermedio.append(String.format(formato, "MOV", "BL,", "AL"));
        codigoIntermedio.append(String.format(formato, "MOV", "CL,", "AH"));

        codigoIntermedio.append(String.format(formato, "OR", "BL,", "00110000B"));
        codigoIntermedio.append(String.format(formato, "OR", "CL,", "00110000B"));

        codigoIntermedio.append(String.format(formato, "MOV", "DL,", "BL"));
        codigoIntermedio.append(String.format(formato, "MOV", "AH,", "02H"));
        codigoIntermedio.append(String.format(formato, "INT", "21H", ""));

        codigoIntermedio.append(String.format(formato, "MOV", "DL,", "CL"));
        codigoIntermedio.append(String.format(formato, "MOV", "AH,", "02H"));
        codigoIntermedio.append(String.format(formato, "INT", "21H", ""));


    }
    
    private boolean esNumeroAL(String numero) {
        int valor = Integer.parseInt(numero);
        return valor >= 0 && valor <= 255;
    }

    private boolean esNumeroAX(String numero) {
        int valor = Integer.parseInt(numero);
        return valor >= 0 && valor <= 65535;
    }

    private boolean impresionNumeroOperadorNumero(){
        if(tokens.get(indiceComienzaEstatutos+1).equals("Num")&& tokens.get(indiceComienzaEstatutos + 3).equals("Num")) {
            return true;
        }
        return false;
    }
    private boolean impresionIDB(){
        if(tokens.get(indiceComienzaEstatutos+1).equals("ID")){
            return true;
        }
        return false;
    }

    public HashMap<String, Variables> getPuntoCodeDatos() {
        return PuntoCodeDatos;
    }
}