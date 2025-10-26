import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Mesa {
    Scanner sc = new Scanner(System.in);
    private Ronda ronda;
    List<Jugador> jugadores = new ArrayList<>();
    Baraja baraja;
    int pozo, apuestaActual;
    private final Juez juez = new Juez();
    private final List<Carta> comunitarias = new ArrayList<>();

    public Mesa() {
        baraja = new Baraja();
        crearJugadores();
        repartoInicial();
        ronda = Ronda.PREFLOP;
        pozo = 0;
    }

    public void showdown() {
        if (ronda != Ronda.SHOWDOWN) return;
        ResultadoMano mejor = null;
        Jugador ganador = null;
        for (Jugador j : jugadores) {
            ResultadoMano r = juez.evaluarMejorMano(j.getMano(), comunitarias);
            if (ganador == null || juez.compararResultados(r, mejor) > 0) {
                ganador = j;
                mejor = r;
            }
        }
        assert ganador != null;
        System.out.println("Ganador: " + ganador.getNombre() + " con " + mejor.getTipo());
    }

    private void repartirComunitarias() {
        switch (ronda) {
            case FLOP:
                for (int i = 0; i < 3; i++) {
                    comunitarias.add(baraja.repartir());
                }
                break;
            case TURN, RIVER:
                comunitarias.add(baraja.repartir());
                break;
            default:
                break;
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

    public void imprimirMesa() {
        System.out.println("=== Ronda: " + ronda + " ===");
        for (Jugador j : jugadores) {
            System.out.println(j.getNombre());
            System.out.println("Tiene " + j.get + " ===");
            j.imprimirMano();
        }
        if (!comunitarias.isEmpty()) {
            System.out.println("Cartas comunitarias");
            for (Carta c : comunitarias) {
                c.imprimirCarta();
            }
        }
    }

    public void rondaDeApuestas() {
        boolean todosIgualados = true;
        apuestaActual = 0;
        for (Jugador j : jugadores) {
            j.apuestaEnRonda = 0;
        }
        do {
            for (Jugador j : jugadores) {
                if (!j.enRonda) continue;//se salta a los que se salieron

                if (apuestaActual == 0) {
                    boolean invalido;
                    do {
                        invalido = false;
                        System.out.println(j.getNombre() + ", tu movimiento:\n1->Pasar\n2->Apostar");
                        int eleccion = Integer.parseInt(sc.nextLine());
                        switch (eleccion) {
                            case 1:
                                break;//No hace nada
                            case 2:
                                int apuesta = j.apostar();
                                apuestaActual = j.apuestaEnRonda;
                                pozo += apuesta;
                                break;
                            default:
                                System.out.println("Opción no válida");
                                invalido = true;
                        }
                    } while (invalido);
                } else {
                    boolean invalido;
                    do {
                        invalido = false;
                        System.out.println(j.getNombre() + ", tu movimiento:\n1->Igualar\n2->Subir\n3->All-in\n4->Retirarse");
                        int eleccion = Integer.parseInt(sc.nextLine());
                        switch (eleccion) {
                            case 1: {//Igualar
                                int cantidad = j.call(apuestaActual);
                                pozo += cantidad;
                                break;
                            }
                            case 2: {//Subir
                                int diferencia = j.raise(apuestaActual);
                                if (diferencia != -1) {
                                    apuestaActual = j.apuestaEnRonda;
                                    pozo += diferencia;
                                } else {
                                    invalido = true;
                                }
                                break;
                            }
                            case 3: {//All-in
                                pozo += j.allIn();
                                //Esta es con cuidado porque hay varias condiciones
                                break;
                            }
                            case 4: {//Retirarse
                                j.fold();
                                break;
                            }
                            default:
                                System.out.println("Opción no válida, prueba otra vez");
                                invalido = true;
                        }
                    } while (invalido);
                }
            }
            jugadores.removeIf(jugador -> !jugador.enRonda);//se salen
            todosIgualados = true;
            for (Jugador j : jugadores) {
                if (j.enRonda && j.apuestaEnRonda < apuestaActual) {
                    todosIgualados = false;
                    break;
                }
            }
        } while (!todosIgualados && jugadores.size() > 1);
    }
}