public class Variables {
    private String nombre;
    private String tipo;
    private String valorStr;
    private int valorInt;
    private double valorDouble;
    private String valorHex;
    private String valorBin;

    // Constructor para enteros
    public Variables(String tipo, int valor, String nombre) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valorInt = valor;
        this.valorDouble = valor;
        this.valorStr = String.valueOf(valor);
        generarRepresentaciones(valor);
    }

    // Constructor para doubles
    public Variables(String tipo, double valor, String nombre) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valorDouble = valor;
        this.valorInt = (int) valor; // Truncamos el double a int
        this.valorStr = String.valueOf(valor);
        generarRepresentaciones(this.valorInt); // Hex y bin basado en el entero
    }

    // Constructor para strings
    public Variables(String tipo, String valor, String nombre) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valorStr = valor;
        // procesarValor(valor);
    }

    // Constructor para strings con representación hex/bin
    public Variables(String tipo, String valor, String valorHex, String valorBin, String nombre) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valorStr = valor;
        this.valorHex = valorHex;
        this.valorBin = valorBin;
        //procesarValor(valor);
    }

    // Genera las representaciones hex y bin
    private void generarRepresentaciones(int valor) {
        this.valorHex = "0x" + Integer.toHexString(valor).toUpperCase();
        this.valorBin = "0b" + Integer.toBinaryString(valor);
    }

    // Getters
    public String getTipo() {
        return tipo;
    }

    public String getValorStr() {
        return valorStr;
    }

    public int getValorInt() {
        return valorInt;
    }

    public double getValorDouble() {
        return valorDouble;
    }

    public String getValorHex() {
        return valorHex;
    }

    public String getValorBin() {
        return valorBin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setValorStr(String valorStr) {
        this.valorStr = valorStr;
    }

    public void setValorInt(int valorInt) {
        this.valorInt = valorInt;
    }

    public void setValorDouble(double valorDouble) {
        this.valorDouble = valorDouble;
    }

    public void setValorHex(String valorHex) {
        this.valorHex = valorHex;
    }

    public void setValorBin(String valorBin) {
        this.valorBin = valorBin;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    @Override
    public String toString() {
        return "Variables{" +
                "nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", valorStr='" + valorStr + '\'' +
                ", valorInt=" + valorInt +
                ", valorDouble=" + valorDouble +
                ", valorHex='" + valorHex + '\'' +
                ", valorBin='" + valorBin + '\'' +
                '}';
    }
    
}
