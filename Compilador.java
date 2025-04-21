import java.util.ArrayList;
import java.util.HashMap;

public class Compilador {

    public static void main(String[] args) throws Exception {
        // String codigo = "edad int ; altura dou ; IF edad !== altura {\r\n" + //
        //                 "print 12 * 8\r\n" + //
        //                 "}";
        String codigo2 = "edad string ; altura string ; IF edad == altura {\r\n" + //
                        "print edad\r\n" + //
                        "}";
        // String codigo3 = "edad int ; altura dou ; IF edad == altura {\r\n" + //
        //                 "altura = 12 + 12\r\n" + //
        //                 "} ELSE {\r\n" + //
        //                 "print edad\r\n" + //
        //                 "}\r\n" + //
        //                 "";
        //String codigo4 = "hola int ; pedro string ; pedro = jairuvb";
        Parser parser = new Parser(codigo2);
        parser.P();
        ArrayList<String> tiposTokens = parser.getTokens();
        ArrayList<String> naturalTokens = parser.getTokensNaturales();
        Semantico semantico = new Semantico(tiposTokens, naturalTokens);
        semantico.AnalizarTokens();
        CodigoIntermedio CI = new CodigoIntermedio(semantico, tiposTokens, naturalTokens);
        String CII = CI.PuntoData();
        System.out.println(CII);
    }
}