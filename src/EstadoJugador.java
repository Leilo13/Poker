import java.util.List;
import java.util.stream.Collectors;
public record EstadoJugador(
    String nombre,
    int fichas,
    int apuestaEnRonda,
    boolean enRonda,
    boolean allIn,
    List<Carta> manoVisible
){
    public String manoComoTexto() {
        return manoVisible.stream().map(Carta::toString).collect(Collectors.joining(" "));
    }
}
