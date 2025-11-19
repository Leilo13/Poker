import java.util.List;
public record ResultadoShowdown (
        Pozo pozo,
        List<Jugador> ganadores,
        ResultadoMano mejorMano,
        int premio
) implements ResultadoApuesta {
    @Override
    public List<Jugador> ganadores() { return ganadores; }
    @Override
    public String descripcion() {
        return ganadores.size() > 1 ? "Empate con " + mejorMano.tipo() : "Gana con " + mejorMano.tipo();
    }
}