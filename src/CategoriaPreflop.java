public enum CategoriaPreflop {
    DELUXE(5), FUERTE(4), DECENTE(3), MALA(2), PESIMA(1);
    private final int peso;
    CategoriaPreflop(int peso) { this.peso = peso; }
    public int peso() { return peso; }
}