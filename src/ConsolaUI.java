import java.util.*;
public class ConsolaUI implements InterfazJuego{
    private final Scanner sc = new Scanner(System.in);
    private Accion leerAccionValida(List<Accion> opciones) {
        Accion accion = null;
        while (accion == null) {
            String entrada = sc.nextLine().trim().toUpperCase();
            try {
                accion = Accion.valueOf(entrada);
                if (!opciones.contains(accion)) {
                    System.out.println("Acción no válida, elige entre: " + opciones);
                    accion = null;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Entrada inválida, escribe una acción válida: " + opciones);
            }
        }
        return accion;
    }
    private int leerEnteroEnRango(String mensaje, int min, int max) {
        int valor = -1;
        while (valor < min || valor > max) {
            System.out.println(mensaje);
            try {
                valor = Integer.parseInt(sc.nextLine());
                if (valor < min || valor > max) {
                    System.out.println("Número inválido. Debe estar entre " + min + " y " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida, escribe un número entero");
            }
        }
        return  valor;
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
    public void mostrarEstadoMesa(EstadoMesa estado) {
        System.out.println("=== Ronda: " + estado.ronda() + " ===\nApuesta actual: " + estado.apuestaActual() + "----------------------------");
        for (EstadoJugador ej : estado.jugadores()) {
            String estadoStr = ej.enRonda() ? (ej.allIn() ? " - ALL-IN" : "") : " - Retirado";
            System.out.printf("%s (fichas: %d, apuesta actual: %d%s)%n", ej.nombre(), ej.fichas(), ej.apuestaEnRonda(), estadoStr);
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
            String ganadores = String.join(", ", r.ganadores().stream().map(Jugador::getNombre).toList());
            System.out.println("Ganadores del " + r.pozo() + " -> " + ganadores + " con " + r.mejorMano() + " (premio: " + r.premio() + ")");
        }
    }
    public int pedirCantidadBots(int jugadoresHumanos) {
        int maxBots = 23 - jugadoresHumanos;
        return leerEnteroEnRango("¿Cuántos bots van a jugar? (0 - " + maxBots + ")", 0, maxBots);
    }
    public List<String> pedirNombresJugadores() {
        int nj = leerEnteroEnRango("¿Cuántos humanos van a jugar? (mínimo 1, máximo 23)", 1, 23);
        List<String> nombres = new ArrayList<>();
        for (int i = 1; i <= nj; i++){
            String nom;
            do {
                System.out.println("Nombre del jugador " + i + ":");
                nom = sc.nextLine().trim();
                if (nom.isBlank()) {
                    System.out.println("El nombre no puede estar vacio.");
                } else {
                    String finalNom = nom;
                    if (nombres.stream().anyMatch(n -> n.equalsIgnoreCase(finalNom))){
                        System.out.println("Ese nombre ya está en uso, elige otro.");
                        nom = "";
                    }
                }
            } while (nom.isBlank());
            nombres.add((nom));
        }
        return nombres;
    }
    @Override
    public void mostrarAccion(String mensaje) { System.out.println(mensaje); }
    @Override
    public Movimiento pedirMovimiento(Mesa mesa, Jugador j) {
        List<Accion> opciones = mesa.operacionesDisponibles(j);
        System.out.printf("%nTurno de %s (fichas: %d)%n", j.getNombre(), j.getFichas());
        System.out.println("Tu apuesta acctual: " + j.getApuestaEnRonda());
        System.out.println("Apuesta actual de la mesa: " + mesa.getApuestaActual());
        if (!mesa.getComunitarias().isEmpty()) System.out.println("Cartas comunitarias: " + mesa.getComunitarias());
        System.out.print("Tus cartas: ");
        j.getMano().forEach(c -> System.out.print(c + " "));
        System.out.println();
        System.out.println("Opciones disponibles: " + opciones);
        Accion accionElegida = leerAccionValida(opciones);
        int cantidad = 0;
        if (accionElegida == Accion.RAISE) {
            int minRaiseTo = mesa.getApuestaActual() + mesa.getBigBlind();
            int maxRaiseTo = j.getApuestaEnRonda() + j.getFichas();
            cantidad = leerEnteroEnRango("¿Cuánto quieres subir? (mínimo " + minRaiseTo + ", máximo " + maxRaiseTo + ")", minRaiseTo, maxRaiseTo);
        }
        return new Movimiento(accionElegida, cantidad);
    }
}
