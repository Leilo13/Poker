import java.util.Objects;
public class Carta {
    private final Valor valor;
    private final Mazo mazo;

    public Carta(Valor valor, Mazo mazo){
        this.valor=valor;
        this.mazo=mazo;
    }

    public Valor getValor() {return valor;}

    public Mazo getMazo() {return mazo;}

    @Override
    public String toString(){
        if (valor == null || mazo == null) return "[??]";
        String simbolo = mazo.getSimbolo();
        String color = (simbolo.equals("♥") || simbolo.equals("♦")) ? "\u001B[31m" : "\u001B[37m";
        return color + "[" + valor.getEtiqueta() + mazo.getSimbolo() + "]" + "\u001B[0m";
    }

    /*@Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Carta)) return false;
        Carta carta = (Carta) o;
        return valor == carta.valor && mazo == carta.mazo;
    }

    @Override
    public int hashCode(){
        return Objects.hash(valor,mazo);
    }*/
}