public enum Mazo {
    TREBOLES('C', "♣"), CORAZONES('H', "♥"),
    PICAS('S', "♠"), DIAMANTES('D', "♦");
    private final char codigo;
    private final String simbolo;
    Mazo(char codigo, String simbolo){
        this.codigo=codigo;
        this.simbolo=simbolo;
    }
    public char getCodigo() {
        return codigo;
    }
    public String getSimbolo() {
        return simbolo;
    }
    /*public static Mazo darCodigo(char c) {
        switch (Character.toUpperCase(c)) {
            case 'C': return TREBOLES;
            case 'H': return CORAZONES;
            case 'S': return PICAS;
            case 'D': return DIAMANTES;
            default: throw new IllegalArgumentException("Valor de mazo invalido "+c);
        }
    }*/
}