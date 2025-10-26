import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Mesa {
    private final Scanner sc = new Scanner(System.in);
    private Ronda ronda;
    private final List<Jugador> jugadores = new ArrayList<>();
    private Baraja baraja;
    private int pozo, apuestaActual;
    private int dealerIndex = 0;
    private final int smallBlind = 10;
    private final int bigBlind = 20;
    private final Juez juez = new Juez();
    private final List<Carta> comunitarias = new ArrayList<>();

    public Mesa() {
        baraja = new Baraja();
        crearJugadores();
        asignarCiegas();
        repartoInicial();
        ronda = Ronda.PREFLOP;
    }

    private void asignarCiegas(){
        int sbIndex = (dealerIndex + 1) % jugadores.size();
        int bbIndex = (dealerIndex + 2) % jugadores.size();

        Jugador smallBlindPlayer = jugadores.get(sbIndex);
        Jugador bigBlindPlayer = jugadores.get(bbIndex);

        int sb = smallBlindPlayer.pagarBlind(smallBlind);
        int bb = bigBlindPlayer.pagarBlind(bigBlind);

        pozo += (sb + bb);
        apuestaActual = Math.max(sb,bb);

        System.out.println(smallBlindPlayer.getNombre() + " paga la ciega pequeña: " + sb);
        System.out.println(bigBlindPlayer.getNombre() + " paga la ciega grande: " + bb);
    }

    public void showdown() {
        if (ronda != Ronda.SHOWDOWN) return;
        ResultadoMano mejor = null;
        List<Jugador> ganadores=new ArrayList<>();

        for (Jugador j : jugadores) {
            ResultadoMano r = juez.evaluarMejorMano(j.getMano(), comunitarias);
            if (mejor == null || juez.compararResultados(r, mejor) > 0) {
                ganadores.clear();
                ganadores.add(j);
                mejor=r;
            } else if (juez.compararResultados(r,mejor) == 0) {
                ganadores.add(j);

            }
        }

        int premio=pozo/ ganadores.size();
        for (Jugador g: ganadores) {
            g.ganarFichas(premio);
            System.out.println("Ganador: " + g.getNombre() + " con " + mejor);
        }
        pozo=0;
        dealerIndex = (dealerIndex + 1) % jugadores.size();
    }

    private void repartirComunitarias() {
        switch (ronda) {
            case FLOP -> {
                for (int i = 0; i < 3; i++) {
                    comunitarias.add(baraja.repartir());
                }
            }
            case TURN, RIVER -> comunitarias.add(baraja.repartir());
            default -> {}
        }
    }

    public void avanzarRonda() {
        ronda = ronda.siguiente();
        repartirComunitarias();
    }

    private void crearJugadores() {
        System.out.println("Cuántos van a jugar");
        byte nj = sc.nextByte();
        sc.nextLine();
        for (int i = 1; i <= nj; i++) {
            System.out.println("Nombre del jugador " + i);
            String nom = sc.nextLine();
            jugadores.add(new Jugador(nom));
        }
    }

    private void repartoInicial() {
        for (Jugador j : jugadores) {
            for (int i = 0; i < 2; i++) {
                j.recibir(baraja.repartir());
            }
        }
    }

    public void imprimirMesaHibrida(Jugador jugadorEnTurno) {
        System.out.println("=== Ronda: " + ronda + " ===\nPozo: " + pozo + "\n----------------------------");
        for (Jugador j : jugadores) {
            System.out.print(j.getNombre() + " (fichas: " + j.getFichas() + ", apuesta actual: " + j.getApuestaEnRonda() + (j.isEnRonda() ? "" : " - RETIRADO") + ") ");

            if (j.equals(jugadorEnTurno)) {
                // Mostrar la mano del jugador en turno
                System.out.print("Mano: ");
                j.imprimirMano();
            } else {
                // Ocultar las manos de los demás
                System.out.println("Mano: [??] [??]");
            }
        }

        if (!comunitarias.isEmpty()) {
            System.out.print("Cartas comunitarias: ");
            for (Carta c : comunitarias) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
        System.out.println();
    }


    /*public void imprimirMesa() {
        System.out.println("=== Ronda: " + ronda + " ===\nPozo: " + pozo + "\n----------------------------");
        for (Jugador j : jugadores) {
            System.out.println(j.getNombre() + " (fichas: " + j.getFichas()  + ", apuesta actual: " + j.getApuestaEnRonda() + ")");
            j.imprimirMano();
        }
        if (!comunitarias.isEmpty()) {
            System.out.print("Cartas comunitarias: ");
            for (Carta c : comunitarias) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }*/

    public void rondaDeApuestas() {
        boolean todosIgualados = true;
        int startIndex = (dealerIndex + 3) % jugadores.size();
        do {
            for (int i = 0; i < jugadores.size(); i++) {
                Jugador j=jugadores.get((startIndex+i) % jugadores.size());
                if (!j.isEnRonda()) continue;

                boolean invalido;
                do {
                    invalido = false;
                    imprimirMesaHibrida(j);
                    System.out.println(j.getNombre() + ", tu movimiento:\n1->Igualar\n2->Subir\n3->All-in\n4->Retirarse");
                    int eleccion = Integer.parseInt(sc.nextLine());
                    switch (eleccion) {
                        case 1 -> {//Igualar
                            int cantidad = j.call(apuestaActual);
                            pozo += cantidad;
                        }
                        case 2 -> {//Subir
                            System.out.println("¿A cuánto subes la apuesta? (mínimo " + (apuestaActual + 1) + ")");
                            int nuevaApuesta = Integer.parseInt(sc.nextLine());
                            int diferencia = j.raise(nuevaApuesta, apuestaActual);
                            if (diferencia != -1) {
                                apuestaActual = j.getApuestaEnRonda();
                                pozo += diferencia;
                            } else {
                                invalido = true;
                            }
                        }
                        case 3 -> pozo += j.allIn();
                        case 4 -> j.fold();
                        default -> {
                            System.out.println("Opción no válida, prueba otra vez");
                            invalido = true;
                        }
                    }
                } while (invalido);
            }

            jugadores.removeIf(jugador -> !jugador.isEnRonda());

            todosIgualados = true;
            for (Jugador j : jugadores) {
                if (j.isEnRonda() && j.getApuestaEnRonda() < apuestaActual) {
                    todosIgualados = false;
                    break;
                }
            }
        } while (!todosIgualados && jugadores.size() > 1);
    }

    public void prepararNuevaRonda(){
        apuestaActual = 0;
        for (Jugador j : jugadores) {
            j.resetApuesta();
        }
    }

    public void nuevaMano(){
        baraja.reiniciar();
        comunitarias.clear();
        for (Jugador j : jugadores){
            j.getMano().clear();
            j.resetApuesta();
        }
        asignarCiegas();
        repartoInicial();
        ronda = Ronda.PREFLOP;
        pozo=0;
    }
}