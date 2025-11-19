public enum RankingMano {
    CARTA_ALTA(1), PAR(2),
    DOS_PARES(3), TERCIA(4),
    ESCALERA(5), COLOR(6),
    FULL(7), POKER(8),
    FLOR(9), FLOR_IMPERIAL(10);
    private final int peso;
    RankingMano(int peso) { this.peso = peso; }
    public int peso() { return peso; }
}