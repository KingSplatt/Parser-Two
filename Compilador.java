
public class Compilador {

    public static void main(String[] args) throws Exception {
        String codigo = "hola int ; pedro int ; IF hola == pedro { print 12 / 41 }";
        String codigo2 = "hola int ; pedro int ; IF hola == pedro { print 11 + 41 } ELSE { IF hola == pedro { print 12 / 41 } }";
        Parser parser = new Parser(codigo2);
        parser.P();
    }
}