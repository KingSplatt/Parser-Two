import java.util.HashMap;

public class CodigoObjeto {
    private String codigoEnsamblador[];
    private StringBuilder codigoMaquinaCode, codigoMaquinaData;
    private String nombreArchivo;

    private String formatoData = "%-10s %-10s\t%n";
    private String tipo = "";
    private int contadorData=0, contadorCode=0;

    private String[] registros = {
            "AX", "BX", "CX", "DX", "SI", "DI", "SP", "BP",
            "AL", "BL", "CL", "DL", "AH", "BH", "CH", "DH"
    };
    private String[] flags = {
        "JE", "JZ", "JNE", "JNZ", "JL", "JGE", "JLE", "JG"
    };

    private String[] ignorar = {
        ".MODEL", "SMALL", ".STACK ", ".DATA", ".386", ".CODE", "MAIN", "PROC",
        "END", "ENDP", "STARTUP", ".EXIT", "ENDP", "NEAR", "FAR", "OFFSET",
    };
    private HashMap <String, Variables> Var; 

    // private String[] instrucciones = {
    //         "MOV", "ADD", "SUB", "MUL", "DIV", "CMP", "JMP", "JE", "JNE"
    // };
    // private String[] oo = {
    //     "00", "01", "10", "11"
    // };
    // private String[] mmm = {
    //     "000", "001", "010", "011", "100", "101", "110", "111"
    // };
    // private String[] rrr = {
    //     "000", "001", "010", "011", "100", "101", "110", "111"
    // };


    public CodigoObjeto(String codigoEnsamblador, HashMap<String, Variables> variables) {
        this.codigoEnsamblador = codigoEnsamblador.split("\\r?\\n");
        this.Var = variables;
        this.codigoMaquinaCode = new StringBuilder();
        this.codigoMaquinaData = new StringBuilder();
    }

    public void TraducirData() {
        System.out.println("Holaaaaa");
        for(Variables variables : Var.values()) {
            String tipo = variables.getTipo(); 
            String valor = ""; 
            System.out.println(tipo);      
            if(tipo.equalsIgnoreCase("DB")) {
                valor = "0000 0000";
                contadorData += 1;
            } else if(tipo.equalsIgnoreCase("DW")) {
                valor = "0000 0000 0000 0000";
                contadorData += 2;
            } else if(tipo.equalsIgnoreCase("DD")) {
                valor = "0000 0000 0000 0000 0000 0000 0000 0000";
                contadorData += 4;
            }
            System.out.println(contadorData);
            String direccion = intToHex(contadorData);
            codigoMaquinaData.append(String.format(formatoData, direccion,valor));
        }
    }

    public void TraducirCode() {

        System.out.println(codigoEnsamblador.length);

        for(String linea : codigoEnsamblador) {
            linea = linea.trim();
            if (linea.isEmpty() || linea.startsWith(";")) {
                continue;
            }
            String[] partes = linea.split("\\s+",2);
            String instruccion = partes[0].toUpperCase();
            for (String ignora : ignorar) {
                if(instruccion.equalsIgnoreCase(ignora)) {
                    tipo = "ignorar";
                    continue;
                }
            }
            String salto = "";
            for(String saltos : flags) {
                if(instruccion.equalsIgnoreCase(saltos)) {
                    tipo = "flag";
                    salto = saltos;
                    break;
                }
            }
            String operandos = (partes.length > 1) ? partes[1].replaceAll("\\s+", "") : "";

            String[] ops = operandos.split(",");
            System.out.println("Instruccion: " + instruccion);
            System.out.println("Operandos: " + operandos);
            
            switch (instruccion) {
                case "MOV":
                    if (ops.length == 2) {
                        traducirMOV(ops[0], ops[1]);
                        codigoMaquinaCode.append("\n");
                    }
                    break;
                case "ADD":
                    if (ops.length == 2) {
                        traducirADD(ops[0], ops[1]);
                        codigoMaquinaCode.append("\n");
                    }
                    break;
                case "SUB":
                    if (ops.length == 2) {
                        traducirSUB(ops[0], ops[1]);
                        codigoMaquinaCode.append("\n");
                    }
                    break;
                case "MUL":
                    if (ops.length == 1) {
                        traducirMUL(ops[0]);
                        codigoMaquinaCode.append("\n");
                    }
                    break;
                case "DIV":
                    if (ops.length == 1) {
                        traducirDIV(ops[0]);
                        codigoMaquinaCode.append("\n");
                    }
                    break;
                case "CMP":
                    if (ops.length == 2) {
                        traducirCMP(ops[0], ops[1]);
                        codigoMaquinaCode.append("\n");
                    }
                    break;
                case "JMP":
                    if (ops.length == 1) {
                        traducirJMP(ops[0]);
                        codigoMaquinaCode.append("\n");
                    }
                    break;
                case "JE":case "JZ":case "JNE":case "JNZ":case "JL":case "JGE":case "JLE":case "JG":
                    if (ops.length == 1) {
                        traducirSalto(salto, ops[0]);
                        codigoMaquinaCode.append("\n");
                    }
                    break;
                default:
                    codigoMaquinaCode.append("; Instrucción no reconocida: ").append(linea).append("\n");
                    break;
            }
        }
    }

    private void traducirMOV(String destino, String origen) {
        try {
            String tipo1 = "mem", tipo2 = "mem";
            Boolean esNumero = esNumeroDecimal(origen);
            Boolean esHex = esNumeroHexadecimal(origen);
            Boolean esBin = esNumeroBinario(origen);

            if (esNumero) {
                tipo2 = "inm";
            } else if (esHex) {
                tipo2 = "inm";
            } else if (esBin) {
                tipo2 = "inm";
            }

            
            for (String reg : registros) {
                if (destino.equalsIgnoreCase(reg)) {
                    tipo1 = "reg";
                }
                if (origen.equalsIgnoreCase(reg)) {
                    tipo2 = "reg";
                }
            }


            if(tipo1.equals("reg") && tipo2.equals("reg")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "MOV", "1000 101"+w + " 00 "+rrr+" 110"));
                //pendiente desplazamiento
            } else if (tipo1.equals("reg") && tipo2.equals("mem")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "MOV", "1000 110"+w+ " 00 "+rrr+" 110 "));
                //pendiente desplazamiento
            } else if (tipo1.equals("mem") && tipo2.equals("reg")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "MOV", "1000 110"+w+ " 00 "+rrr+" 110 "));
                //pendiente desplazamiento
            } else if(tipo1.equals("mem") && tipo2.equals("inm")) {
                String w = elegirWconVar(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "MOV", "1100 011"+w + " 00 "+"000"+" 110 "));
                //pendiente datos
                //pendiente desplazamiento
            } else if(tipo1.equals("reg") && tipo2.equals("inm")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "MOV", "1011 "+rrr+w));
                //pendiente datos
            } else {
                throw new Exception("Error en la instrucción MOV: operandos no válidos");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void traducirADD(String destino, String origen) {
        try {
            String tipo1 = "mem", tipo2 = "mem";
            Boolean esNumero = esNumeroDecimal(origen);
            Boolean esHex = esNumeroHexadecimal(origen);
            Boolean esBin = esNumeroBinario(origen);

            if (esNumero) {
                tipo2 = "inm";
            } else if (esHex) {
                tipo2 = "inm";
            } else if (esBin) {
                tipo2 = "inm";
            }

            
            for (String reg : registros) {
                if (destino.equalsIgnoreCase(reg)) {
                    tipo1 = "reg";
                }
                if (origen.equalsIgnoreCase(reg)) {
                    tipo2 = "reg";
                }
            }
            if(tipo1.equals("reg") && tipo2.equals("reg")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                String mmm = elegirMMM(origen);
                codigoMaquinaCode.append(String.format(formatoData, "ADD", "0000 001"+w+ " 11 "+rrr+" "+mmm));
                //pendiente desplazamiento
            } else if (tipo1.equals("reg") && tipo2.equals("mem")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "ADD", "0000 001"+w+ " 00 "+rrr+" 110 "));
                //pendiente desplazamiento
            } else if (tipo1.equals("mem") && tipo2.equals("reg")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "ADD", "0000 001"+w+ " 00 "+rrr+" 110 "));
                //pendiente desplazamiento
            } else if(tipo1.equals("reg") && tipo2.equals("inm")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "ADD", "1000 000"+w + " 11 "+"000 "+rrr+" "));
                //pendiente desplazamiento
                //pendiente datos
            } else {
                throw new Exception("Error en la instrucción ADD: operandos no válidos");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void traducirSUB(String destino, String origen) {
        try {
            String tipo1 = "mem", tipo2 = "mem";
            Boolean esNumero = esNumeroDecimal(origen);
            Boolean esHex = esNumeroHexadecimal(origen);
            Boolean esBin = esNumeroBinario(origen);

            if (esNumero) {
                tipo2 = "inm";
            } else if (esHex) {
                tipo2 = "inm";
            } else if (esBin) {
                tipo2 = "inm";
            }

            
            for (String reg : registros) {
                if (destino.equalsIgnoreCase(reg)) {
                    tipo1 = "reg";
                }
                if (origen.equalsIgnoreCase(reg)) {
                    tipo2 = "reg";
                }
            }
            if(tipo1.equals("reg") && tipo2.equals("reg")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                String mmm = elegirMMM(origen);
                codigoMaquinaCode.append(String.format(formatoData, "SUB", "0001 011"+w+ " 11 "+rrr+" "+mmm));
                //pendiente desplazamiento
            } else if (tipo1.equals("reg") && tipo2.equals("mem")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "SUB", "0000 001"+w+ " 00 "+rrr+" 110 "));
                //pendiente desplazamiento
            } else if (tipo1.equals("mem") && tipo2.equals("reg")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "SUB", "0000 001"+w+ " 00 "+rrr+" 110 "));
                //pendiente desplazamiento
            } else if(tipo1.equals("reg") && tipo2.equals("inm")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "SUB", "1000 000"+w + " 11 "+"101 "+rrr+" "));
                //pendiente desplazamiento
                //pendiente datos
            } else if(tipo1.equals("mem") && tipo2.equals("inm")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "SUB", "1000 000"+w + " 00 "+"101 "+rrr+" "));
                //pendiente desplazamiento
                //pendiente datos
            } else {
                throw new Exception("Error en la instrucción SUB: operandos no válidos");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void traducirMUL(String origen) {
        try {
            String tipo1 = "mem";
            
            for (String reg : registros) {
                if (origen.equalsIgnoreCase(reg)) {
                    tipo1 = "reg";
                }
            }

            if(tipo1.equals("reg")) {
                String w = elegirW(origen, origen);
                String rrr = elegirRRR(origen, origen);
                codigoMaquinaCode.append(String.format(formatoData, "MUL", "1111 011"+w+ " 11 "+"100"+" "+rrr));
                //pendiente desplazamiento
            } else if(tipo1.equals("mem")) {
                String w = elegirW(origen, origen);
                codigoMaquinaCode.append(String.format(formatoData, "MUL", "1111 011"+w+ " 00 "+"100"+" "+"110"));
                //pendiente desplazamiento
            } else {
                throw new Exception("Error en la instrucción MUL: operandos no válidos");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void traducirDIV(String origen) {
        try {
            String tipo1 = "mem";
            
            for (String reg : registros) {
                if (origen.equalsIgnoreCase(reg)) {
                    tipo1 = "reg";
                }
            }

            if(tipo1.equals("reg")) {
                String w = elegirW(origen, origen);
                String rrr = elegirRRR(origen, origen);
                codigoMaquinaCode.append(String.format(formatoData, "DIV", "1111 011"+w+ " 11 "+"110"+" "+rrr));
                //pendiente desplazamiento
            } else if(tipo1.equals("mem")) {
                String w = elegirW(origen, origen);
                codigoMaquinaCode.append(String.format(formatoData, "DIV", "1111 011"+w+ " 00 "+"110"+" "+"110"));
                //pendiente desplazamiento
            } else {
                throw new Exception("Error en la instrucción DIV: operandos no válidos");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void traducirCMP(String destino, String origen) {
        try {
            String tipo1 = "mem", tipo2 = "mem";
            Boolean esNumero = esNumeroDecimal(origen);
            Boolean esHex = esNumeroHexadecimal(origen);
            Boolean esBin = esNumeroBinario(origen);

            if (esNumero) {
                tipo2 = "inm";
            } else if (esHex) {
                tipo2 = "inm";
            } else if (esBin) {
                tipo2 = "inm";
            }

            
            for (String reg : registros) {
                if (destino.equalsIgnoreCase(reg)) {
                    tipo1 = "reg";
                }
                if (origen.equalsIgnoreCase(reg)) {
                    tipo2 = "reg";
                }
            }
            if(tipo1.equals("reg") && tipo2.equals("reg")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                String mmm = elegirMMM(origen);
                codigoMaquinaCode.append(String.format(formatoData, "CMP", "0011 101"+w+ " 11 "+rrr+" "+mmm));  
                //pendiente desplazamiento
            } else if (tipo1.equals("reg") && tipo2.equals("mem")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "CMP", "0010 011"+w+ " 00 "+rrr+" 110 "));
                //pendiente desplazamiento
            } else if (tipo1.equals("mem") && tipo2.equals("reg")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "CMP", "0010 011"+w+ " 00 "+rrr+" 110 "));
                //pendiente desplazamiento
            } else if(tipo1.equals("reg") && tipo2.equals("inm")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "CMP", "1000 000"+w + " 11 "+"111 "+rrr+" "));
                //pendiente desplazamiento
                //pendiente datos
            } else if(tipo1.equals("mem") && tipo2.equals("inm")) {
                String w = elegirW(destino, origen);
                //String rrr = elegirRRR(destino, origen);
                codigoMaquinaCode.append(String.format(formatoData, "CMP", "1000 000"+w + " 00 "+"111 "+"110 "));
                //pendiente desplazamiento
                //pendiente datos
            } else {
                throw new Exception("Error en la instrucción CMP: operandos no válidos");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //JMP corto
    private void traducirJMP(String destino) {
        codigoMaquinaCode.append(String.format(formatoData, "JMP", "1110 1011 "));
        //pendiente desplazamiento
    }

    private void traducirSalto(String salto, String destino) {
        String cccc = elegirCCCC(salto);
        codigoMaquinaCode.append(String.format(formatoData, salto, "0111 "+cccc+" "));
        //pendiente desplazamiento
    }

    public String elegirW(String destino, String origen) {
        String regex = "(AX|BX|CX|DX)";
        if (destino.matches("(?i).*" + regex + ".*") || origen.matches("(?i).*" + regex + ".*")) {
            return "1";
        } else {
            return "0";
        }
    }

    public String elegirWconVar(String destino, String origen) {
        // Verifica si destino está en el HashMap de variables
        if (Var.containsKey(destino)) {
            String tipo = Var.get(destino).getTipo();
            if ("DW".equalsIgnoreCase(tipo) || "DD".equalsIgnoreCase(tipo)) {
                return "1";
            }
        }
        // Verifica si origen está en el HashMap de variables
        if (Var.containsKey(origen)) {
            String tipo = Var.get(origen).getTipo();
            if ("DW".equalsIgnoreCase(tipo) || "DD".equalsIgnoreCase(tipo)) {
                return "1";
            }
        }
        return "0";
    }

    public String elegirRRR(String destino, String origen) {
        String rrr = "";
        if((destino.equalsIgnoreCase("AL") || destino.equalsIgnoreCase("AX") || destino.equalsIgnoreCase("EAX")) || (destino.equalsIgnoreCase("AL") || destino.equalsIgnoreCase("AX") || destino.equalsIgnoreCase("EAX")) ) {
            rrr = "000";
        } else if ( (destino.equalsIgnoreCase("CL") || destino.equalsIgnoreCase("CX") || destino.equalsIgnoreCase("ECX")) || (origen.equalsIgnoreCase("CL") || origen.equalsIgnoreCase("CX") || origen.equalsIgnoreCase("ECX")) ) {
            rrr = "001";
        } else if ((destino.equalsIgnoreCase("DL") || destino.equalsIgnoreCase("DX") || destino.equalsIgnoreCase("EDX")) || (origen.equalsIgnoreCase("DL") || origen.equalsIgnoreCase("DX") || origen.equalsIgnoreCase("EDX")) ) {
            rrr = "010";
        } else if( (destino.equalsIgnoreCase("BL") || destino.equalsIgnoreCase("DX") || destino.equalsIgnoreCase("EDX")) || (origen.equalsIgnoreCase("DL") || origen.equalsIgnoreCase("DX") || origen.equalsIgnoreCase("EDX"))  ) {
            rrr = "011";
        } else if( (destino.equalsIgnoreCase("AH") || destino.equalsIgnoreCase("SP") || destino.equalsIgnoreCase("ESP")) || (origen.equalsIgnoreCase("AH") || origen.equalsIgnoreCase("SP") || origen.equalsIgnoreCase("ESP")) ) {
            rrr = "100";
        } else if( (destino.equalsIgnoreCase("CH") || destino.equalsIgnoreCase("BP") || destino.equalsIgnoreCase("EBP")) || (origen.equalsIgnoreCase("CH") || origen.equalsIgnoreCase("BP") || origen.equalsIgnoreCase("EBP")) ) {
            rrr = "101";
        } else if( (destino.equalsIgnoreCase("DH") || destino.equalsIgnoreCase("SI") || destino.equalsIgnoreCase("ESI")) || (origen.equalsIgnoreCase("DH") || origen.equalsIgnoreCase("SI") || origen.equalsIgnoreCase("ESI")) ) {
            rrr = "110";
        } else if( (destino.equalsIgnoreCase("BH") || destino.equalsIgnoreCase("DI") || destino.equalsIgnoreCase("EDI")) || (origen.equalsIgnoreCase("BH") || origen.equalsIgnoreCase("DI") || origen.equalsIgnoreCase("EDI")) ) {
            rrr = "111";
        }
        return rrr;
    }

    public String elegirMMM(String origen) {
        String rrr = "";
        if(origen.equalsIgnoreCase("AL") || origen.equalsIgnoreCase("AX") || origen.equalsIgnoreCase("EAX")) {
            rrr = "000";
        } else if (origen.equalsIgnoreCase("CL") || origen.equalsIgnoreCase("CX") || origen.equalsIgnoreCase("ECX")) {
            rrr = "001";
        } else if (origen.equalsIgnoreCase("DL") || origen.equalsIgnoreCase("DX") || origen.equalsIgnoreCase("EDX")) {
            rrr = "010";
        } else if (origen.equalsIgnoreCase("BL") || origen.equalsIgnoreCase("BX") || origen.equalsIgnoreCase("EBX")) {
            rrr = "011";
        } else if (origen.equalsIgnoreCase("AH") || origen.equalsIgnoreCase("SP") || origen.equalsIgnoreCase("ESP")) {
            rrr = "100";
        } else if (origen.equalsIgnoreCase("CH") || origen.equalsIgnoreCase("BP") || origen.equalsIgnoreCase("EBP")) {
            rrr = "101";
        } else if (origen.equalsIgnoreCase("DH") || origen.equalsIgnoreCase("SI") || origen.equalsIgnoreCase("ESI")) {
            rrr = "110";
        } else if (origen.equalsIgnoreCase("BH") || origen.equalsIgnoreCase("DI") || origen.equalsIgnoreCase("EDI")) {
            rrr = "111";
        }
        return rrr;
    }

    public String elegirCCCC(String salto){
        String cccc = "";
        if(salto.equalsIgnoreCase("JE")) {
            cccc = "0100";
        } else if (salto.equalsIgnoreCase("JZ")) {
            cccc = "0100";
        } else if (salto.equalsIgnoreCase("JNE")) {
            cccc = "0101";
        } else if (salto.equalsIgnoreCase("JNZ")) {
            cccc = "0101";
        } else if (salto.equalsIgnoreCase("JL")) {
            cccc = "1100";
        } else if (salto.equalsIgnoreCase("JGE")) {
            cccc = "1101";
        } else if (salto.equalsIgnoreCase("JLE")) {
            cccc = "1110";
        } else if (salto.equalsIgnoreCase("JG")) {
            cccc = "1111";
        }
        return cccc;
    }

    public Boolean esNumeroDecimal(String valor) {
        return valor.matches("-?\\d+");
    }

    public Boolean esNumeroHexadecimal(String valor) {
        return valor.matches("0[xX][0-9a-fA-F]+");
    }

    public Boolean esNumeroBinario(String valor) {
        return valor.matches("0[bB][01]+");
    }

    public String intToHex(int valor) {
        return String.format("%04X", valor);
    }
    
    public String getCodigoMaquinaCode() {
        return codigoMaquinaCode.toString();
    }

    public String getCodigoMaquinaData() {
        return codigoMaquinaData.toString();
    }
}
