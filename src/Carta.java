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
        return "[" + valor.getEtiqueta() + mazo.getSimbolo() + "]";
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