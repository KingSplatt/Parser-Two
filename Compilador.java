import java.util.ArrayList;
import java.util.HashMap;

public class Compilador {

    public static void main(String[] args) throws Exception {
        String codigo = "a83 string ; b32 dou ; pero dou ; IF pero == b32 {\r\n" + //
                "a83 = jairu\r\n" + //
                "} ELSE {\r\n" + //
                "print 12 + 1\r\n" + //
                "}\r\n" + //
                "";
        String codigo2 = "a83 string ; b32 dou ; pero int ; IF pero == b32 {\r\n" + //
                "a83 = jairu\r\n" + //
                "} ELSE {\r\n" + //
                "pero = 12 + 1\r\n" + //
                "}\r\n" + //
                "";
        String codigo3 = "hola int ; pedro int ; print 12 + 5";
        String codigo4 = "hola int ; pedro string ; pedro = jairuvb";
        Parser parser = new Parser(codigo3);
        parser.P();
        ArrayList<String> tiposTokens = parser.getTokens();
        ArrayList<String> naturalTokens = parser.getTokensNaturales();
        HashMap<String, Variables> tabla = new HashMap<>();
        Semantico semantico = new Semantico(tiposTokens, naturalTokens);
        semantico.AnalizarTokens();
        boolean error = semantico.getEntroCondicional();
        tabla = semantico.getTablaSimbolos();
        CodigoIntermedio CI = new CodigoIntermedio(semantico, tiposTokens, naturalTokens);
        String CII = CI.PuntoData();
        System.out.println(CII);
    }
}