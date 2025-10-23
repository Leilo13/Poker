public class Carta {
    private final Valor valor;
    private final Mazo mazo;
    public Carta(Valor valor, Mazo mazo){
        this.valor=valor;
        this.mazo=mazo;
    }
    public Valor getValor() {return valor;}
    public Mazo getMazo() {return mazo;}
    public void imprimirCarta() {
        System.out.println(valor.getEtiqueta()+ mazo.getSimbolo());
    }
}