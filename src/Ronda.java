public enum Ronda {
    PREFLOP,
    FLOP,
    TURN,
    RIVER,
    SHOWDOWN;
    public Ronda siguiente(){
        switch(this){
            case PREFLOP:return FLOP;
            case FLOP:return TURN;
            case TURN:return RIVER;
            case RIVER:return SHOWDOWN;
            default:return SHOWDOWN;
        }
    }
}
