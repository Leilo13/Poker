public class Jugador {
    private final String nombre;
    private int fichas;
    private Carta[] mano;
    boolean enRonda=true;
    public Jugador(String nombre, int fichas, Carta[] mano){
        this.nombre=nombre;
        this.fichas=fichas;
        this.mano=mano;
    }
    void salir(){
        enRonda=false;
    }
    boolean apostar(int apuesta){
        if (apuesta>fichas){
            System.out.println("Apuesta inválida");
            return false;
        }else {
            fichas-=apuesta;
        }return true;
    }
}
