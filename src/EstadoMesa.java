import java.util.List;
public record EstadoMesa (
        Ronda ronda,
        int apuestaActual,
        List<EstadoJugador> jugadores,
        List<Carta> comunitarias
) {}