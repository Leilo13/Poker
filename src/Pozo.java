import java.util.ArrayList;
import java.util.List;
public class Pozo {
    private int cantidad;
    private final List<Jugador> participantes;
    public Pozo(int cantidad, List<Jugador> participantes) {
        this.cantidad = cantidad;
        this.participantes = new ArrayList<>(participantes);
    }
    public int getCantidad() { return cantidad; }
    public List<Jugador> getParticipantes() { return List.copyOf(participantes); }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    @Override
    public String toString() {
        String nombres = participantes.stream().map(Jugador::getNombre).reduce((a, b) -> a + "," + b).orElse("");
        return "Pozo[" + cantidad + ", participantes=" + nombres + " ]";
    }
}