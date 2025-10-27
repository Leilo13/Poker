import java.util.ArrayList;
import java.util.List;
public class Pozo {
    private int cantidad;
    private final List<Jugador> participantes = new ArrayList<>();

    public Pozo(int cantidad, List<Jugador> participantes) {
        this.cantidad=cantidad;
        this.participantes.addAll(participantes);
    }

    public int getCantidad() { return cantidad; }

    public List<Jugador> getParticipantes() { return participantes; }

    public void agregar(int fichas) { cantidad += fichas;}

    @Override
    public String toString() {
        return "Pozo: " + cantidad + " fichas, participantes: " + participantes.stream().map(Jugador::getNombre).toList();
    }
}
