import java.util.ArrayList;
import java.util.List;

public class Jugador {
    private final String nombre;
    private int fichas;
    private int apuestaEnRonda;
    private List<Carta> mano;
    private boolean enRonda;
    private boolean allIn;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.fichas = 100;
        this.mano = new ArrayList<>();
        this.enRonda = true;
        this.allIn = false;
        this.apuestaEnRonda = 0;
    }
    public Jugador(String nombre, int fichas) {//Constructor de debugeo
        this.nombre = nombre;
        this.fichas = fichas;
        this.mano = new ArrayList<>();
        this.enRonda = true;
        this.allIn = false;
        this.apuestaEnRonda = 0;
    }
    //Acciones de juego
    public void recibir(Carta c) { mano.add(c); } //YA ESTÁ

    public void call(int apuestaActual) {
        int diff = apuestaActual - apuestaEnRonda;
        if (diff >= fichas){
            apuestaEnRonda += fichas;
            System.out.println(nombre + " iguala con " + fichas + " y queda ALL-IN");
            fichas = 0;
            allIn = true;
        } else if (diff == 0) {
            System.out.println(nombre + " pasa.");
        }else{
            fichas -= diff;
            apuestaEnRonda += diff;
            System.out.println(nombre + " iguala con " + diff);
        }
    } //YA ESTÁ

    public int raise(int nuevaApuesta, int apuestaActual) {
        int diff = nuevaApuesta - apuestaEnRonda;
        if (nuevaApuesta <= apuestaActual || diff > fichas) return -1;//Inválido
        fichas -= diff;
        apuestaEnRonda = nuevaApuesta;
        if (fichas == 0) {
            allIn = true;
            System.out.println(nombre + " sube a " + nuevaApuesta + " y queda ALL-IN");
        } else {
            System.out.println(nombre + " sube a " + nuevaApuesta);
        }
        return diff;
    }

    public void allIn() {
        if (fichas > 0) {
            apuestaEnRonda += fichas;
            fichas = 0;
            allIn = true;
            System.out.println(nombre + " va ALL-IN con " + apuestaEnRonda);
        }
    }

    public void fold() {
        enRonda=false;
        System.out.println(nombre + " se retira.");
    }

    public int pagarBlind(int cantidad) {
        int pagado = Math.min(cantidad, fichas);
        fichas -= pagado;
        apuestaEnRonda += pagado;
        if (fichas == 0) allIn = true;
        return pagado;
    }

    public void ganarFichas(int cantidad) { fichas += cantidad; }

    //Utilidades
    public void resetJugador(){
        apuestaEnRonda = 0;
        enRonda = true;
        allIn = false;
        mano.clear();
    }

    public void imprimirMano(){
        for (Carta c:mano){
            System.out.print(c + " ");
        }
        System.out.println();
    }

    //Getters y setters
    public String getNombre() { return nombre; }
    public int getFichas() { return fichas; }
    public int getApuestaEnRonda() { return apuestaEnRonda; }
    public List<Carta> getMano() { return mano; }
    public boolean isEnRonda() { return enRonda; }
    public boolean isAllIn() { return allIn; }
    public void setEnRonda(boolean enRonda) { this.enRonda = enRonda; }
    public void setApuestaEnRonda(int apuestaEnRonda) { this.apuestaEnRonda = apuestaEnRonda; }
}
