
public class Compilador {

    public static void main(String[] args) throws Exception {
        String codigo = "a83 int ; b32 dou ; IF a83 == b32 {\r\n" + //
                "a83 = 15 + 2 \r\n" + //
                "} ELSE {\r\n" + //
                "IF a83 !== b32 {\r\n" + //
                "print a83\r\n" + //
                "}\r\n" + //
                "}";
        String codigo2 = "a83 string ; b32 int ; IF a83 == b32 {\r\n" + //
                "a83 = jairu\r\n" + //
                "} ELSE {\r\n" + //
                "print a83\r\n" + //
                "}";
        String codigo3 = "hola int ; pedro dou ; pedro = 12 * 5";
        String codigo4 = "hola int ; pedro string ; pedro = jairuvb";
        Parser parser = new Parser(codigo2);
        parser.P();
    }
}