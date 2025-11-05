import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Mesa mesa = new Mesa(); // crea jugadores, asigna ciegas, reparte cartas
        Scanner sc = new Scanner(System.in);
        // Bucle de manos
        while (true) {
            System.out.println("\n=== Nueva mano ===");
            mesa.imprimirDebug(); // estado inicial de la mano
            while (mesa.getRonda() != Ronda.SHOWDOWN) {
                mesa.rondaDeApuestas();
                mesa.avanzarRonda();
            }
            mesa.showdown();
            if (mesa.partidaTerminada()) {
                System.out.println("El juego ha terminado.");
                break;
            }
            String respuesta;
            do {
                System.out.println("¿Quieres jugar otra mano? s/n");
                respuesta = sc.nextLine().trim().toLowerCase();
                if (!respuesta.equals("s") && !respuesta.equals("n")) {
                    System.out.println("Opción no válida. Por favor escribe 's' o 'n'.");
                }
            } while (!respuesta.equals("s") && !respuesta.equals("n"));
            if (respuesta.equals("n")) {
                System.out.println("El juego ha terminado");
                break;
            }
            mesa.nuevaMano();
        }
        sc.close();
    }
}