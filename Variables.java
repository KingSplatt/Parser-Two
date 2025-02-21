public class Variables {
    private String tipo;
    private String valor;
    // valor en hexadecimal
    private String valorHex;
    private String valorBin;

    public Variables(String nombre, String tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public Variables(String nombre, String tipo, String valor, String valorHex, String valorBin) {
        this.tipo = tipo;
        this.valor = valor;
        this.valorHex = valorHex;
        this.valorBin = valorBin;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getValorHex() {
        return valorHex;
    }

    public void setValorHex(String valorHex) {
        this.valorHex = valorHex;
    }

    public String getValorBin() {
        return valorBin;
    }

    public void setValorBin(String valorBin) {
        this.valorBin = valorBin;
    }

    @Override
    public String toString() {
        return "Variables [tipo=" + tipo + ", valor=" + valor + ", valorHex=" + valorHex + ", valorBin=" + valorBin
                + "]";
    }
}
