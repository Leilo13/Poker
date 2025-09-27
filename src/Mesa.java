import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Mesa {
    Scanner sc=new Scanner(System.in);
    private Ronda ronda;
    List<Jugador> jugadores=new ArrayList<>();
    Baraja bar;
    public Mesa(){
        //Algo con la ronda
        bar=new Baraja();
        crearJugadores();
        repartoInicial();
        ronda=Ronda.PREFLOP;
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
                j.recibir(bar.repartir());
            }
        }
    }
    public void imprimirMesa(){
        for (Jugador j:jugadores){
            System.out.println(j.getNombre());
            j.imprimirMano();
        }
    }
}
