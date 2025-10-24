public enum Mazo {
    TREBOLES('C', "♣"), CORAZONES('H', "♥"),
    PICAS('S', "♠"), DIAMANTES('D', "♦");
    private final char codigo;
    private final String simbolo;
    Mazo(char codigo, String simbolo){
        this.codigo=codigo;
        this.simbolo=simbolo;
    }
    public String getSimbolo() {
        return simbolo;
    }
}