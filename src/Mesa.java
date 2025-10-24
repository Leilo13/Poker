import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Mesa {
    Scanner sc=new Scanner(System.in);
    private Ronda ronda;
    List<Jugador> jugadores=new ArrayList<>();
    Baraja baraja;
    private final Juez juez=new Juez();
    private final List<Carta> comunitarias=new ArrayList<>();
    public Mesa(){
        baraja =new Baraja();
        crearJugadores();
        repartoInicial();
        ronda=Ronda.PREFLOP;
    }
    public void showdown(){
        if(ronda!=Ronda.SHOWDOWN) return;
        ResultadoMano mejor=null;
        Jugador ganador=null;
        for(Jugador j:jugadores){
            ResultadoMano r=juez.evaluarMejorMano(j.getMano(),comunitarias);
            if(ganador==null||juez.compararResultados(r,mejor)>0){
                ganador=j;
                mejor=r;
            }
        }
        System.out.println("Ganador: "+ganador.getNombre()+" con "+mejor.getTipo());
    }
    private void repartirComunitarias(){
        switch (ronda){
            case FLOP:
                for (int i=0;i<3;i++){
                    comunitarias.add(baraja.repartir());                }
                break;
            case TURN:
                comunitarias.add(baraja.repartir());
                break;
            case RIVER:
                comunitarias.add(baraja.repartir());
                break;
            default:break;
        }
    }
    public void avanzarRonda(){
        ronda=ronda.siguiente();
        repartirComunitarias();
    }
    private void crearJugadores(){
        System.out.println("Cuántos van a jugar");
        byte nj=sc.nextByte();
        sc.nextLine();
        for (int i=1;i<=nj;i++){
            System.out.println("Nombre del jugador "+i);
            String nom=sc.nextLine();
            System.out.println("Con cuántas fichas inicia "+nom);
            int fichas=sc.nextInt();
            sc.nextLine();
            jugadores.add(new Jugador(nom,fichas));
        }
    }
    private void repartoInicial(){
        for (Jugador j:jugadores){
            for (int i=0;i<2;i++){
                j.recibir(baraja.repartir());
            }
        }
    }
    public void imprimirMesa(){
        System.out.println("=== Ronda: " + ronda + " ===");
        for (Jugador j:jugadores){
            System.out.println(j.getNombre());
            j.imprimirMano();
        }
        if (!comunitarias.isEmpty()){
            System.out.println("Cartas comunitarias");
            for (Carta c:comunitarias){
                c.imprimirCarta();
            }
        }
    }
}
