import java.util.List;
public record ResultadoFold (Jugador ganador, int cantidad) implements ResultadoApuesta {
    @Override
    public List<Jugador> getGanadores() { return List.of(ganador); }
    @Override
    public int getCantidad() { return cantidad; }
    @Override
    public String getDescripcion() { return "Gana por fold"; }
    }
