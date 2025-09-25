import java.util.ArrayList;
import java.util.List;
public class Jugador {
    private final String nombre;
    private int fichas;
    private List<Carta> mano=new ArrayList<>();
    boolean enRonda=true;
    public Jugador(String nombre, int fichas){
        this.nombre=nombre;
        this.fichas=fichas;
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
    void recibir(Carta carta){
        if (mano.size()<2){
            mano.add(carta);
        }
    }
    void imprimir(){
        mano.get(0).imprimirCarta();
        mano.get(1).imprimirCarta();
    }
}
