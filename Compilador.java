
public class Compilador {

    public static void main(String[] args) throws Exception {
        String codigo = "a83 int ; b32 int ; IF a83 == b32 {\r\n" + //
                "a83 = 15 + 2 \r\n" + //
                "} ELSE {\r\n" + //
                "IF a83 !== b32 {\r\n" + //
                "print a83\r\n" + //
                "}\r\n" + //
                "}";
        String codigo2 = "hola dou ; pedro int ; IF hola == pedro { print 11 + 41 } ELSE { IF hola == pedro { print 12 / 41 } }";
        String codigo3 = "hola dou ; hola int ; hola = 24 + 12.4";
        Parser parser = new Parser(codigo);
        parser.P();
    }
}