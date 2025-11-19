import java.util.List;
public record ResultadoFold (Jugador ganador, int cantidad) implements ResultadoApuesta {
    @Override
    public List<Jugador> ganadores() { return List.of(ganador); }
    @Override
    public String descripcion() { return "Gana por fold"; }
    }