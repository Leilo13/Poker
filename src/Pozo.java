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

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pozo[")
                .append(cantidad)
                .append(", participantes=");
        for (int i = 0; i < participantes.size(); i++) {
            sb.append(participantes.get(i).getNombre());
            if (i < participantes.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

}
