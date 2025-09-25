public class Main {
    public static void main(String[] args) {
        Baraja bar=new Baraja();
        Jugador p1=new Jugador("Leonardo", 100);
        for (int i=0; i<2;i++){
            p1.recibir(bar.repartir());
            p1.imprimir();
        }

    }
}