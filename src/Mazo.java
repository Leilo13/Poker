public enum Mazo {
    TREBOLES( "♣"), CORAZONES("♥"),
    PICAS( "♠"), DIAMANTES("♦");
    private final String simbolo;
    Mazo(String simbolo){
        this.simbolo=simbolo;
    }
    public String simbolo() {
        return simbolo;
    }
}