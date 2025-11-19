import java.util.List;
public record ResultadoMano (
    RankingMano tipo,
    List<Integer> desempate,
    List<Carta> cartasGanadoras
    ) {
    @Override
    public String toString() {
        return tipo + " " + cartasGanadoras;
    }
}