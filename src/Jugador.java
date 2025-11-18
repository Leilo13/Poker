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
    //Acciones de juego
    public void recibir(Carta c) { mano.add(c); } //YA ESTÁ

    public String call(int apuestaActual) {
        int diff = apuestaActual - apuestaEnRonda;
        if (diff == 0) return nombre + " pasa.";
        if (diff >= fichas){
            apuestaEnRonda += fichas;
            fichas = 0;
            allIn = true;
            return nombre + " iguala con " + fichas + " y queda ALL-IN";
        }
            fichas -= diff;
            apuestaEnRonda += diff;
            return nombre + " iguala con " + diff;
    } //YA ESTÁ

    public String raise(int nuevaApuesta) {
        int diff = nuevaApuesta - apuestaEnRonda;
        fichas -= diff;
        apuestaEnRonda = nuevaApuesta;
        if (fichas == 0) {
            allIn = true;
            return nombre + " sube a " + nuevaApuesta + " y queda ALL-IN";
        } else {
            return nombre + " sube a " + nuevaApuesta;
        }
    }

    public String allIn() {
        if (fichas > 0) {
            apuestaEnRonda += fichas;
            fichas = 0;
            allIn = true;
           return nombre + " va ALL-IN con " + apuestaEnRonda;
        }
        return nombre + " ya estaba ALL-IN";
    }

    public String fold() {
        enRonda=false;
        return nombre + " se retira.";
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
