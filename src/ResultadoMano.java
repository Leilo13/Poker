import java.util.List;
public class ResultadoMano {
    private final RankingMano tipo;
    private final List<Integer> desempate;
    private final List<Carta> cartasGanadoras;

    public ResultadoMano(RankingMano tipo, List<Integer> desempate, List<Carta> cartasGanadoras){
        this.tipo=tipo;
        this.desempate=desempate;
        this.cartasGanadoras = cartasGanadoras;
    }

    public RankingMano getTipo() {return tipo;}

    public List<Integer> getDesempate() {return desempate;}

    public List<Carta> getCartasGanadoras() {return cartasGanadoras;}

    @Override
    public String toString() {
        return tipo + " " + cartasGanadoras;
    }
    /*@Override
    public String toString(){
        StringBuilder sb = new StringBuilder(tipo.name()).append(" -> ");
        for (Carta c : cartasGanadoras){
            sb.append(c).append(" ");
        }
        return sb.toString().trim();
    }*/
}