import java.util.List;

// Guarda lo que tiene una mano
public record ResultadoMano (
    RankingMano tipo,   // Valor jerárquico de su jugada
    List<Integer> desempate,    // Cartas que puedan marcar el desempate
    List<Carta> cartasGanadoras // Cartas con las que gana
    ) {
    @Override
    public String toString() {
        return tipo + " " + cartasGanadoras;
    }
}