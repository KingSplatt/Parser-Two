import java.util.HashMap;
import java.util.LinkedHashMap;

public class CodigoObjeto {
    private String codigoEnsamblador[];
    private StringBuilder codigoMaquinaCode, codigoMaquinaData;
    private String formatoData = "%-10s %-10s\t%n";
    private String tipo = "";
    private int contadorCode=0;
    private LinkedHashMap<String, String> etiquetas = new LinkedHashMap<>();
    private String[] registros = {
            "AX", "BX", "CX", "DX", "SI", "DI", "SP", "BP",
            "AL", "BL", "CL", "DL", "AH", "BH", "CH", "DH",
            "EAX", "EBX", "ECX", "EDX", "ESI", "EDI", "ESP", "EBP",
    };
    private String[] flags = {
        "JE", "JZ", "JNE", "JNZ", "JL", "JGE", "JLE", "JG"
    };
    private String[] ignorar = {
        ".MODEL", "SMALL", ".STACK ", ".DATA", ".386", ".CODE", "MAIN", "PROC",
        "END", "ENDP", "STARTUP", ".EXIT", "ENDP", "NEAR", "FAR", "OFFSET",
    };
    private HashMap <String, Variables> Var; 
    public CodigoObjeto(String codigoEnsamblador, HashMap<String, Variables> variables) {
        this.codigoEnsamblador = codigoEnsamblador.split("\\r?\\n");
        this.Var = variables;
        this.codigoMaquinaCode = new StringBuilder();
        this.codigoMaquinaData = new StringBuilder();
    }

    public void TraducirData() {
        int direccionActual = 0;
        for (Variables variable : Var.values()) {
            String tipo = variable.getTipo();
            String valor = "";
            int bytes = 0;
            if (tipo.equalsIgnoreCase("DB")) {
                valor = "0000 0000";
                bytes = 1;
            } else if (tipo.equalsIgnoreCase("DW")) {
                valor = "0000 0000 0000 0000";
                bytes = 2;
            } else if (tipo.equalsIgnoreCase("DD")) {
                valor = "0000 0000 0000 0000 0000 0000 0000 0000";
                bytes = 4;
            }
            String direccionHexActual = String.format("%04X", direccionActual);
            String direccionBinActual = String.format("%16s", Integer.toBinaryString(direccionActual)).replace(' ', '0');
            Var.put(variable.getNombre(), new Variables(variable.getTipo(), variable.getValorStr(), direccionHexActual, direccionBinActual, variable.getNombre()));
            codigoMaquinaData.append(String.format(formatoData, direccionHexActual, valor));
            direccionActual += bytes;
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
            if(linea.contains(":")) {
                String etiqueta = linea.split(":")[0].trim();
                if (!etiquetas.containsKey(etiqueta)) {
                    etiquetas.put(etiqueta, String.format("%04X", contadorCode));
                }
            }
            switch (instruccion) {
                case "MOV":
                    if (ops.length == 2) {
                        traducirMOV(ops[0], ops[1]);
                        codigoMaquinaCode.append("\n");
                    }
                    break;
                case "MOVZX":
                    if (ops.length == 2) {
                        traducirMOVZX(ops[0], ops[1]);
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
                case "IMUL":
                    if (ops.length == 2) {
                        traducirIMUL(ops[0], ops[1]);
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
                    break;
            }
        }
        for(String etiqueta : etiquetas.keySet()) {
            String direccionHex = etiquetas.get(etiqueta);
            String direccionBin = String.format("%16s", Integer.toBinaryString(Integer.parseInt(direccionHex, 16))).replace(' ', '0');
            Var.put(etiqueta, new Variables("ETIQUETA", "", direccionHex, direccionBin, etiqueta));
        }
        //ultima direccion
        codigoMaquinaCode.append(String.format(formatoData, String.format("%04X", contadorCode), ""));
        SegundaPasda();
    }

    private void traducirMOV(String destino, String origen) {
        try {
            int bytes = 0;
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
                bytes = 2;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1000 101"+w + " 00 "+rrr+" 110"));
            } else if (tipo1.equals("reg") && tipo2.equals("mem")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                String desplazamiento = Var.get(origen).getValorBin();
                bytes = 2 + Var.get(origen).getValorBin().length()/8; // 2 bytes for MOV + 1 byte for displacement
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1000 110"+w+ " 00 "+rrr+" 110 "+desplazamiento));
            } else if (tipo1.equals("mem") && tipo2.equals("reg")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                String desplazamiento = Var.get(destino).getValorBin();
                bytes = 2 + Var.get(destino).getValorBin().length()/8; 
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1000 110"+w+ " 00 "+rrr+" 110 "+desplazamiento));
            } else if(tipo1.equals("mem") && tipo2.equals("inm")) {
                String w = elegirWconVar(destino, origen);
                int valor = Integer.parseInt(origen);
                String datos = intToBin(valor);
                String desplazamiento = Var.get(destino).getValorBin();
                bytes = 2 + Var.get(destino).getValorBin().length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1100 011"+w + " 00 "+"000"+" 110 "+datos+" "+desplazamiento));
            } else if(tipo1.equals("reg") && tipo2.equals("inm")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                int valor = Integer.parseInt(origen);
                String datos = intToBin(valor);
                bytes = 2;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1011 "+rrr+w+" "+datos));
            } else {
                throw new Exception("Error en la instrucción MOV: operandos no válidos");
            }
            contadorCode += bytes;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void traducirMOVZX(String destino, String origen) {
        try {
            int bytes = 0;
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
                bytes = 3;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0000 1111 1011 011"+w + " 00 "+rrr+" 110"));
            } else if (tipo1.equals("reg") && tipo2.equals("mem")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                String desplazamiento = Var.get(origen).getValorBin();
                bytes = 3 + Var.get(origen).getValorBin().length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0000 1111 1011 011"+w+ " 00 "+rrr+" 110 "+desplazamiento));
            } else {
                throw new Exception("Error en la instrucción MOVZX: operandos no válidos");
            }
            contadorCode += bytes;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void traducirADD(String destino, String origen) {
        try {
            int bytes = 0;
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
                bytes = 2;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0000 001"+w+ " 11 "+rrr+" "+mmm));
            } else if (tipo1.equals("reg") && tipo2.equals("mem")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                String desplazamiento = Var.get(origen).getValorBin();
                bytes = 2 + Var.get(origen).getValorBin().length()/8; // 2 bytes for MOV + 1 byte for displacement
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0000 001"+w+ " 00 "+rrr+" 110 "+desplazamiento));
            } else if (tipo1.equals("mem") && tipo2.equals("reg")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                String desplazamiento = Var.get(destino).getValorBin();
                bytes = 2 + Var.get(destino).getValorBin().length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0000 001"+w+ " 00 "+rrr+" 110 "+desplazamiento));
            } else if(tipo1.equals("reg") && tipo2.equals("inm")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                int valor = Integer.parseInt(origen);
                String datos = intToBin(valor);
                bytes = 2 + datos.length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1000 000"+w + " 11 "+"101 "+rrr+" "+datos));
            } else{ 
                throw new Exception("Error en la instrucción ADD: operandos no válidos");
            }
            contadorCode += bytes;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void traducirSUB(String destino, String origen) {
        try {
            int bytes = 0;
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
                bytes = 2;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0001 011"+w+ " 11 "+rrr+" "+mmm));
            } else if (tipo1.equals("reg") && tipo2.equals("mem")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                String desplazamiento = Var.get(origen).getValorBin();
                bytes = 2 + Var.get(origen).getValorBin().length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0000 001"+w+ " 00 "+rrr+" 110 "+desplazamiento));
            } else if (tipo1.equals("mem") && tipo2.equals("reg")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                String desplazamiento = Var.get(destino).getValorBin();
                bytes = 2 + Var.get(destino).getValorBin().length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0000 001"+w+ " 00 "+rrr+" 110 "+desplazamiento));
            } else if(tipo1.equals("reg") && tipo2.equals("inm")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                int valor = Integer.parseInt(origen);
                String datos = intToBin(valor);
                bytes = 2 + datos.length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1000 000"+w + " 11 "+"101 "+rrr+" "+datos));
            } else if(tipo1.equals("mem") && tipo2.equals("inm")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                int valor = Integer.parseInt(origen);
                String datos = intToBin(valor);
                String desplazamiento = Var.get(destino).getValorBin();
                bytes = 2 + datos.length()/8 + desplazamiento.length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1000 000"+w + " 00 "+"101 "+rrr+" "+desplazamiento+" "+datos));
            } else {
                throw new Exception("Error en la instrucción SUB: operandos no válidos");
            }
            contadorCode += bytes;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void traducirMUL(String origen) {
        try {
            int bytes = 0;
            String tipo1 = "mem";
            for (String reg : registros) {
                if (origen.equalsIgnoreCase(reg)) {
                    tipo1 = "reg";
                }
            }
            if(tipo1.equals("reg")) {
                String w = elegirW(origen, origen);
                String rrr = elegirRRR(origen, origen);
                bytes = 2;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1111 011"+w+ " 11 "+"100"+" "+rrr));
            } else if(tipo1.equals("mem")) {
                String w = elegirW(origen, origen);
                String desplazamiento = Var.get(origen).getValorBin();
                bytes = 2 + Var.get(origen).getValorBin().length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1111 011"+w+ " 00 "+"100"+" "+"110"+" "+desplazamiento));
            } else {
                throw new Exception("Error en la instrucción MUL: operandos no válidos");
            }
            contadorCode += bytes;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void traducirIMUL(String destino, String origen){
        try {
            int bytes = 0;
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
                String rrr = elegirRRR(destino, origen);
                bytes = 3;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0000 1111 1010 1111 "+ " 11 "+rrr+" "+"000"));
            } else if (tipo1.equals("reg") && tipo2.equals("mem")) {
                String rrr = elegirRRR(destino, origen);
                String desplazamiento = Var.get(origen).getValorBin();
                bytes = 3 + Var.get(origen).getValorBin().length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0000 1111 1010 1111 "+ " 00 "+rrr+" "+"110 "+desplazamiento));
            } if(tipo1.equals("reg") && tipo2.equals("inm")) {
                String rrr = elegirRRR(destino, origen);
                int valor = Integer.parseInt(origen);
                String datos = intToBin(valor);
                bytes = 2 + datos.length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0110 1001 "+ " 11 "+rrr+" "+"110 "+datos));
            } else if(tipo1.equals("mem") && tipo2.equals("inm")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                int valor = Integer.parseInt(origen);
                String datos = intToBin(valor);
                String desplazamiento = Var.get(destino).getValorBin();
                bytes = 2 + datos.length()/8 + desplazamiento.length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1111 101"+w + " 00 "+"000"+" "+rrr+" "+desplazamiento+" "+datos));
            } else {
                throw new Exception("Error en la instrucción IMUL: operandos no válidos");
            }
            contadorCode += bytes;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void traducirDIV(String origen) {
        try {
            int bytes = 0;
            String tipo1 = "mem";
            for (String reg : registros) {
                if (origen.equalsIgnoreCase(reg)) {
                    tipo1 = "reg";
                }
            }
            if(tipo1.equals("reg")) {
                String w = elegirW(origen, origen);
                String rrr = elegirRRR(origen, origen);
                bytes = 2;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1111 011"+w+ " 11 "+"110"+" "+rrr));
            } else if(tipo1.equals("mem")) {
                String w = elegirW(origen, origen);
                String desplazamiento = Var.get(origen).getValorBin();
                bytes = 2 + Var.get(origen).getValorBin().length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1111 011"+w+ " 00 "+"110"+" "+"110"+" "+desplazamiento));
            } else {
                throw new Exception("Error en la instrucción DIV: operandos no válidos");
            }
            contadorCode += bytes;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void traducirCMP(String destino, String origen) {
        try {
            int bytes = 0;
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
                bytes = 2;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0011 101"+w+ " 11 "+rrr+" "+mmm));  
            } else if (tipo1.equals("reg") && tipo2.equals("mem")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                String desplazamiento = Var.get(origen).getValorBin();
                bytes = 2 + Var.get(origen).getValorBin().length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0011 101"+w+ " 00 "+rrr+" 110 "+desplazamiento));
            } else if (tipo1.equals("mem") && tipo2.equals("reg")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                String desplazamiento = Var.get(destino).getValorBin();
                bytes = 2 + Var.get(destino).getValorBin().length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "0011 101"+w+ " 00 "+rrr+" 110 "+desplazamiento));
            } else if(tipo1.equals("reg") && tipo2.equals("inm")) {
                String w = elegirW(destino, origen);
                String rrr = elegirRRR(destino, origen);
                int valor = Integer.parseInt(origen);
                String datos = intToBin(valor);
                bytes = 2 + datos.length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1000 000"+w + " 11 "+"111 "+rrr+" "+datos));
            } else if(tipo1.equals("mem") && tipo2.equals("inm")) {
                String w = elegirW(destino, origen);
                int valor = Integer.parseInt(origen);
                String datos = intToBin(valor);
                String desplazamiento = Var.get(destino).getValorBin();
                bytes = 2 + datos.length()/8 + desplazamiento.length()/8;
                String direccionHex = String.format("%04X", contadorCode);
                codigoMaquinaCode.append(String.format(formatoData, direccionHex, "1000 000"+w + " 00 "+"111 "+"110 "+desplazamiento+" "+datos));
            } else {
                throw new Exception("Error en la instrucción CMP: operandos no válidos");
            }
            contadorCode += bytes;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //JMP corto PENDIETEE
    private void traducirJMP(String destino) {
        codigoMaquinaCode.append(String.format(formatoData, "JMP", "1110 1011 "));
        //pendiente desplazamiento
    }

    private void traducirSalto(String salto, String destino) {
        String cccc = elegirCCCC(salto);
        codigoMaquinaCode.append(String.format(formatoData, salto, "0111 "+cccc+" "));
        //pendiente desplazamiento
    }
private void SegundaPasda() {
    StringBuilder nuevoCodigo = new StringBuilder();
    String[] lineas = codigoMaquinaCode.toString().split("\\r?\\n");

    for (String linea : lineas) {
        linea = linea.trim();
        if (linea.isEmpty()) continue;

        String[] partes = linea.split("\\s+");
        String instruccion = partes[0].toUpperCase();
        String DireccionBinario = "";
        // Verificar si es instrucción de salto
        if (instruccion.equals("JMP") || instruccion.equals("JE") || instruccion.equals("JZ") ||
            instruccion.equals("JNE") || instruccion.equals("JNZ") || instruccion.equals("JL") ||
            instruccion.equals("JGE") || instruccion.equals("JLE") || instruccion.equals("JG")) {

            if (partes.length > 1) {
                String direccion = "";
                for(String etiquetaKey : etiquetas.keySet()) {
                        direccion = etiquetas.get(etiquetaKey);
                        DireccionBinario = HextoBin(direccion);
                        etiquetas.remove(etiquetaKey);
                        break;
                }
                nuevoCodigo.append(linea).append(" ").append(DireccionBinario).append("\n\n");
            } else {
                nuevoCodigo.append(linea).append(" [ETIQUETA NO ENCONTRADA]\n");
            }

        } else {
            nuevoCodigo.append(linea).append("\n");
        }
    }
    codigoMaquinaCode = nuevoCodigo;

    // Opcional: mostrar resultado
    System.out.println("=== Código máquina actualizado ===");
    System.out.println(codigoMaquinaCode.toString());
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
        } else if( (destino.equalsIgnoreCase("BL") || destino.equalsIgnoreCase("BX") || destino.equalsIgnoreCase("EBX")) || (origen.equalsIgnoreCase("DL") || origen.equalsIgnoreCase("BX") || origen.equalsIgnoreCase("EBX"))  ) {
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

    public String intToBin(int valor) {
        return String.format("%16s", Integer.toBinaryString(valor)).replace(' ', '0');
    }

    public String HextoBin(String valor) {
        StringBuilder binario = new StringBuilder();
        for (char c : valor.toCharArray()) {
            int decimal = Character.digit(c, 16);
            String bin = Integer.toBinaryString(decimal);
            binario.append(String.format("%4s", bin).replace(' ', '0'));
        }
        return binario.toString();
    }
    
    public String getCodigoMaquinaCode() {
        return codigoMaquinaCode.toString();
    }

    public String getCodigoMaquinaData() {
        return codigoMaquinaData.toString();
    }
}
