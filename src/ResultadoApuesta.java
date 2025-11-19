import java.util.List;
public sealed interface ResultadoApuesta permits ResultadoFold, ResultadoShowdown {
    List<Jugador> ganadores();
    String descripcion();
}