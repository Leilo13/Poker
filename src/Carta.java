public class Carta {
    private final Valor valor;
    private final Mazo mazo;
    public Carta(Valor valor, Mazo mazo){
        this.valor=valor;
        this.mazo=mazo;
    }
    public Valor valor() {return valor;}
    public Mazo mazo() {return mazo;}
    @Override
    public String toString(){
        if (valor == null || mazo == null) return "[??]";
        String simbolo = mazo.simbolo();
        String color = (simbolo.equals("♥") || simbolo.equals("♦")) ? "\u001B[31m" : "\u001B[37m";
        return color + "[" + valor.etiqueta() + mazo.simbolo() + "]" + "\u001B[0m";
    }
}