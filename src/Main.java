import org.w3c.dom.ls.LSOutput;

import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Mesa mesa = new Mesa();//Preflop

        boolean seguirJugando = true;
        while (seguirJugando) {//Preflop
            mesa.rondaDeApuestas();

            mesa.avanzarRonda();//Flop
            mesa.prepararNuevaRonda();
            mesa.rondaDeApuestas();

            mesa.avanzarRonda();//Turn
            mesa.prepararNuevaRonda();
            mesa.rondaDeApuestas();

            mesa.avanzarRonda();//River
            mesa.prepararNuevaRonda();
            mesa.rondaDeApuestas();

            mesa.avanzarRonda();//Showdown
            mesa.prepararNuevaRonda();
            mesa.showdown();

            System.out.println("¿Jugar otra mano? (s/n)");
            String respuesta = sc.nextLine().trim().toLowerCase();
            if (respuesta.equals("s")) {
                mesa.nuevaMano();
            } else {
                seguirJugando = false;
            }
        }
        System.out.println("¡Gracias por jugar!");
    }
}