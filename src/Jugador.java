import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Jugador {
    private final String nombre;
    private int fichas=500;
    private int apuestaEnRonda;
    private List<Carta> mano=new ArrayList<>();
    private boolean enRonda=true;

    public Jugador(String nombre){
        this.nombre=nombre;
    }

    //Acciones del jugador
    public void fold(){
        enRonda=false;
    }

    public int call(int apuestaAnterior){
        int cantidad = Math.min(apuestaAnterior - apuestaEnRonda, fichas);
        fichas -= cantidad;
        apuestaEnRonda += cantidad;
        return cantidad;
    }

    public int raise(int nuevaApuesta, int apuestaActual){//Se usa cuando alguien más apostó
        if (nuevaApuesta > fichas + apuestaEnRonda || nuevaApuesta <= apuestaActual){
            System.out.println("No puedes subir. Prueba igualar o ir all-in.");
            return -1;//Inválido
        }
        int cantidad=nuevaApuesta - apuestaEnRonda;
        fichas -= cantidad;
        apuestaEnRonda = nuevaApuesta;
        return cantidad;
    }
    public int allIn(){
        int cantidad = fichas;
        apuestaEnRonda += cantidad;
        fichas=0;
        return cantidad;
    }

    public int pagarBlind(int cantidad){
        int pago = Math.min(cantidad, fichas);
        fichas -= pago;
        apuestaEnRonda += pago;
        return pago;
    }

    public void ganarFichas(int fichas) {
        this.fichas += fichas;
    }

    //Utilidades
    public void resetApuesta(){
        apuestaEnRonda = 0;
        enRonda = true;
    }

    public void recibir(Carta carta){
        if (mano.size()<2){
            mano.add(carta);
        }
    }

    public void imprimirMano(){
        for (Carta c:mano){
            System.out.print(c + " ");
        }
        System.out.println();
    }

    //Getters
    public List<Carta> getMano() { return mano; }

    public int getFichas() { return fichas; }

    public String getNombre() { return nombre; }

    public boolean isEnRonda() { return enRonda; }

    public int getApuestaEnRonda() { return apuestaEnRonda; }

    public String toString() {
        return  nombre + " (fichas: " + fichas + ", apuesta: " + apuestaEnRonda + ", enRonda: " + enRonda + ")";
    }
}
