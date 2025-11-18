import java.util.*;
public class ConsolaUI implements InterfazJuego{
    private final Scanner sc = new Scanner(System.in);
    public List<String> pedirNombresJugadores() {
        int nj = 0;
        while (nj < 1 || nj > 23) {
            System.out.println("¿Cuántos humanos van a jugar? (mínimo 1)");
            try {
                nj = Integer.parseInt(sc.nextLine());
                if (nj < 1) {
                    System.out.println("Debe haber al menos 1 jugador");
                } else if (nj > 23) {
                    System.out.println("No pueden jugar más de " + 23 + " humanos");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida, escribe un número entero");
            }
        }
        List<String> nombres = new ArrayList<>();
        for (int i = 1; i <= nj; i++){
            String nom = "";
            while (nom.isBlank()) {
                System.out.println("Nombre del jugador " + i + ":");
                nom = sc.nextLine().trim();
                if (nom.isBlank()) {
                    System.out.println("El nombre no puede estar vacio.");
                } else {
                    String finalNom = nom;
                    if (nombres.stream().anyMatch(n -> n.equalsIgnoreCase(finalNom))) {
                        System.out.println("Ese nombre ya está en uso, elige otro.");
                        nom = "";
                    }
                }
            }
            nombres.add((nom));
        }
        return nombres;
    }
    public Movimiento pedirMovimiento(Mesa mesa, Jugador j) {
        List<Accion> opciones = mesa.operacionesDisponibles(j);
        System.out.println("\nTurno de " + j.getNombre() + " (fichas: " + j.getFichas() + ")");
        System.out.println("Tu apuesta acctual: " + j.getApuestaEnRonda());
        System.out.println("Apuesta actual de la mesa: " + mesa.getApuestaActual());
        if (!mesa.getComunitarias().isEmpty()) System.out.println("Cartas comunitarias: " + mesa.getComunitarias());
        System.out.print("Tus cartas: ");
        j.getMano().forEach(c -> System.out.print(c + " "));
        System.out.println();
        System.out.println("Opciones disponibles: " + opciones);
        Accion accionElegida = null;
        while (accionElegida == null) {
            String entrada = sc.nextLine().trim().toUpperCase();
            try {
                accionElegida = Accion.valueOf(entrada);
                if (!opciones.contains(accionElegida)) {
                    System.out.println("Acción no válida, elige entre: " + opciones);
                    accionElegida = null;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Entrada inválida, escribe una acción válida: " + opciones);
            }
        }
        int cantidad = 0;
        if (accionElegida == Accion.RAISE) {
            int minRaiseTo = mesa.getApuestaActual() + mesa.getBigBlind();
            int maxRaiseTo = j.getApuestaEnRonda() + j.getFichas();
            System.out.println("¿Cuánto quieres subir (mínimo " + minRaiseTo + ", máximo " + maxRaiseTo + ")");
            while (true) {
                try {
                    cantidad = Integer.parseInt(sc.nextLine());
                    if (cantidad < minRaiseTo) {
                        System.out.println("La nueva apuesta debe ser al menos " + minRaiseTo);
                    } else if (cantidad > maxRaiseTo) {
                        System.out.println("No tienes suficientes fichas. Máximo " + maxRaiseTo);
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Número inválido. Intenta de nuevo.");
                }
            }
        }
        return new Movimiento(accionElegida, cantidad);
    }
    public void mostrarAccion(String mensaje) { System.out.println(mensaje); }
    public void mostrarEstadoMesa(EstadoMesa estado) {
        System.out.println("=== Ronda: " + estado.ronda() + " ===\nApuesta actual: " + estado.apuestaActual() + "----------------------------");
        for (EstadoJugador ej : estado.jugadores()) {
            String estadoStr = ej.enRonda() ? (ej.allIn() ? " - ALL-IN" : "") : " - Retirado";
            System.out.println(ej.nombre() + " (fichas: " + ej.fichas() + ", apuesta actual: " + ej.apuestaEnRonda() + estadoStr + ")");
            System.out.println("Mano: " + ej.manoComoTexto());
            System.out.println("----------------------------");
        }
        if (!estado.comunitarias().isEmpty()) {
            System.out.println("\nCartas comunitarias: " + estado.comunitarias());
        }
        System.out.println();
    }
    public void mostrarResultadoCiegas(ResultadoCiegas r) {
        System.out.println(r.smallBlindPlayer().getNombre() + " paga SB: " + r.smallBlindPagada());
        System.out.println(r.bigBlindPlayer().getNombre() + " paga BB: " + r.bigBlindPagada());
        System.out.println("Apuesta actual: " + r.apuestaActual() + "\n");
    }
    public void mostrarShowdown(List<ResultadoShowdown> resultados) {
        System.out.println("\n=== SHOWDOWN ===");
        for (ResultadoShowdown r : resultados) {
            String ganadores = r.ganadores().stream().map(Jugador::getNombre).reduce((a,b) -> a + ", " + b).orElse("");
            System.out.println("Ganadores del " + r.pozo() + " -> " + ganadores + " con " + r.mejorMano() + " (premio: " + r.premio() + ")");
        }
    }
    public String leerLinea(Set<String> opcionesValidas) {
        String entrada;
        do {
            entrada = sc.nextLine().trim().toLowerCase();
            if (!opcionesValidas.contains(entrada)) {
                System.out.println("Entrada inválida. Opciones válidas: " + opcionesValidas);
            }
        } while (!opcionesValidas.contains(entrada));
        return entrada;
    }

    public int pedirCantidadBots(int jugadoresHumanos) {
        int nb = -1;
        int maxBots = 23 - jugadoresHumanos;
        while (nb < 0 || nb > maxBots) {
            System.out.println("¿Cuántos bots van a jugar? (0 - " + maxBots + ")");
            try {
                nb = Integer.parseInt(sc.nextLine());
                if (nb < 0 || nb > maxBots) {
                    System.out.println("Número inválido. Debe estar entre 0 y " + maxBots);
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida, escribe un número entero");
            }
        }
        return nb;
    }
}
