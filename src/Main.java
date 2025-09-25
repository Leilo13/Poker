public class Main {
    public static void main(String[] args) {
        Baraja bar=new Baraja();
        Jugador p1=new Jugador("Leonardo", 100);
        Jugador p2=new Jugador("Marlon",100);
        Jugador p3=new Jugador("Felix",100);
        Jugador p4=new Jugador("Alan", 100);
        for (int i=0; i<2;i++){
            p1.recibir(bar.repartir());
            p2.recibir(bar.repartir());
            p3.recibir(bar.repartir());
            p4.recibir(bar.repartir());
        }
        System.out.println(p1.getNombre());
        p1.imprimir();
        System.out.println(p2.getNombre());
        p2.imprimir();
        System.out.println(p3.getNombre());
        p3.imprimir();
        System.out.println(p4.getNombre());
        p4.imprimir();
    }
}