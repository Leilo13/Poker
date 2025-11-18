public interface InterfazJuego {
    Movimiento pedirMovimiento(Mesa mesa, Jugador j);
    void mostrarAccion(String mensaje);
}