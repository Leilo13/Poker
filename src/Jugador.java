import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Jugador {
    private Scanner sc=new Scanner(System.in);
    private final String nombre;
    private int fichas=500;
    public int apuestaEnRonda;
    private List<Carta> mano=new ArrayList<>();
    boolean enRonda=true;
    public Jugador(String nombre){
        this.nombre=nombre;
    }
    public void fold(){
        enRonda=false;
    }
    public int apostar(){//Se usa si no hay apuesta anterior
        System.out.println("¿Cuánto vas a apostar?");
        int apuesta = Integer.parseInt(sc.nextLine());
        while (apuesta > fichas|| apuesta <= 0){
            System.out.println("Apuesta inválida. Ingresa una cantidad válida (max "+fichas+")");
            apuesta = Integer.parseInt(sc.nextLine());
        }
        fichas -= apuesta;
        apuestaEnRonda=apuesta;
        return apuesta;
    }
    public int call(int apuestaAnterior){//se usa cuando alguien más apostó
        int cantidad=Math.min(apuestaAnterior-apuestaEnRonda,fichas);//Si tiene más fichas que apuesta, simplemente iguala, sino, hace un all-in
        fichas-= cantidad;
        apuestaEnRonda+=cantidad;
        return cantidad;
    }
    public int raise(int apuestaActual){//Se usa cuando alguien más apostó
        if (fichas + apuestaEnRonda <= apuestaActual){
            System.out.println("No puedes subir. Prueba igualar o ir all-in.");
            return -1;
        }
        System.out.println("¿A cuánto subes la apuesta? (mínimo " + (apuestaActual + 1) + ")");
        int nuevaApuesta = Integer.parseInt(sc.nextLine());
        while (nuevaApuesta >fichas+apuestaEnRonda || nuevaApuesta <= apuestaActual){
            System.out.println("Apuesta inválida, ingresa una cantidad mayor a "+ apuestaActual+ " y no mayor a "+(fichas+apuestaEnRonda));
            nuevaApuesta =Integer.parseInt(sc.nextLine());
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
    public List<Carta> getMano() {
        return mano;
    }

    void recibir(Carta carta){
        if (mano.size()<2){
            mano.add(carta);
        }
    }
    void imprimirMano(){
        for (Carta c:mano){
            c.imprimirCarta();
        }
    }
    public String getNombre() {
        return nombre;
    }
}
