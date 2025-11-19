public enum Valor {
    DOS(2, "2"), TRES(3, "3"),
    CUATRO(4, "4"), CINCO(5, "5"),
    SEIS(6, "6"), SIETE(7, "7"),
    OCHO(8, "8"), NUEVE(9, "9"),
    DIEZ(10, "10"), JOTA(11, "J"),
    REINA(12, "Q"), REY(13, "K"),
    AS(14, "A");
    private final int valor;
    private final String etiqueta;
    Valor(int valor, String etiqueta){
        this.valor=valor;
        this.etiqueta=etiqueta;
    }
    public String etiqueta() {
        return etiqueta;
    }
    public int valor() {
        return valor;
    }
}
