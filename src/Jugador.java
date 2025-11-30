import java.util.ArrayList;
import java.util.List;
public class Jugador {
    private boolean enRonda;
    private boolean allIn;
    private int fichas;
    private int apuestaEnRonda;
    private final List<Carta> mano;
    private final String nombre;
    public Jugador(String nombre) {
        this.nombre = nombre;
        this.fichas = 500;
        this.mano = new ArrayList<>();
        this.enRonda = true;
        this.allIn = false;
        this.apuestaEnRonda = 0;
    }
    public boolean isAllIn() { return allIn; }
    public boolean isEnRonda() { return enRonda; }
    public int getApuestaEnRonda() { return apuestaEnRonda; }
    public int getFichas() { return fichas; }
    public int pagarBlind(int cantidad) {
        int pagado = Math.min(cantidad, fichas);
        fichas -= pagado;
        apuestaEnRonda += pagado;
        if (fichas == 0) allIn = true;
        return pagado;
    }
    public List<Carta> getMano() { return List.copyOf(mano); }
    public String allIn() {
        if (fichas > 0) {
            apuestaEnRonda += fichas;
            fichas = 0;
            allIn = true;
            return nombre + " va ALL-IN con " + apuestaEnRonda;
        }
        return nombre + " ya estaba ALL-IN";
    }
    public String call(int apuestaActual) {
        int diff = apuestaActual - apuestaEnRonda;
        if (diff == 0) return nombre + " pasa.";
        if (diff >= fichas){
            apuestaEnRonda += fichas;
            fichas = 0;
            allIn = true;
            return nombre + " iguala con toddas sus fichas (" + fichas + ") y queda ALL-IN";
        }
        fichas -= diff;
        apuestaEnRonda += diff;
        return nombre + " iguala con " + diff;
    }
    public String fold() {
        enRonda=false;
        return nombre + " se retira.";
    }
    public String getNombre() { return nombre; }
    public String raise(int nuevaApuesta) {
        int diff = nuevaApuesta - apuestaEnRonda;
        if (diff > fichas) return nombre + " no tiene suficientes fichas para subir";
        fichas -= diff;
        apuestaEnRonda = nuevaApuesta;
        if (fichas == 0) {
            allIn = true;
            return nombre + " sube a " + nuevaApuesta + " y queda ALL-IN";
        } else {
            return nombre + " sube a " + nuevaApuesta;
        }
    }
    public void ganarFichas(int cantidad) { fichas += cantidad; }
    public void recibir(Carta c) { mano.add(c); }
    public void resetJugador(){
        apuestaEnRonda = 0;
        enRonda = true;
        allIn = false;
        mano.clear();
    }
    public void setApuestaEnRonda(int apuestaEnRonda) { this.apuestaEnRonda = apuestaEnRonda; }
}