import java.util.ArrayList;
import java.util.List;
    private int cantidad;
    private final List<Jugador> participantes;

    public Pozo(int cantidad, List<Jugador> participantes) {
        this.cantidad = cantidad;
        this.participantes = new ArrayList<>(participantes);
    }

    // Getter de cantidad dentro del pozo
    public int getCantidad() { return cantidad; }

    // Getter de participantes dentro del pozo
    public List<Jugador> getParticipantes() { return List.copyOf(participantes); }

    // Establece la cantidad del pozo
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    @Override
    public String toString() {
        String nombres = participantes.stream().map(Jugador::getNombre).reduce((a, b) -> a + "," + b).orElse("");
        return "Pozo[" + cantidad + ", participantes=" + nombres + " ]";
    }
}