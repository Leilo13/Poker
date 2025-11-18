import java.util.List;
public record ResultadoShowdown (
        Pozo pozo,
        List<Jugador> ganadores,
        ResultadoMano mejorMano,
        int premio
) implements ResultadoApuesta {
    @Override
    public List<Jugador> getGanadores() { return ganadores; }
    @Override
    public int getCantidad() { return premio; }
    @Override
    public String getDescripcion() {
        return ganadores.size() > 1
            ? "Empate con " + mejorMano.getTipo()
            : "Gana con " + mejorMano.getTipo();
    }
}