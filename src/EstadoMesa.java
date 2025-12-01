import java.util.List;
// Guarda ronda, la apuesta actual, los jugadores en juego y las comunitatias mostradas
public record EstadoMesa (
        Ronda ronda,
        int apuestaActual,
        List<EstadoJugador> jugadores,
        List<Carta> comunitarias
) {}