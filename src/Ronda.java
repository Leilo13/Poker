public enum Ronda {
    PREFLOP,
    FLOP,
    TURN,
    RIVER,
    SHOWDOWN;

    public Ronda siguiente(){
        return switch (this) {
            case PREFLOP -> FLOP;
            case FLOP -> TURN;
            case TURN -> RIVER;
            case RIVER -> SHOWDOWN;
            case SHOWDOWN -> PREFLOP;
        };
    }
}