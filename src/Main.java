import java.util.*;
public class Main {
    public static void main(String[] args) {
        Mesa mesa = new Mesa();
        ConsolaUI consola = new ConsolaUI();
        List<String> nombresHumanos = consola.pedirNombresJugadores();
        mesa.crearJugadores(nombresHumanos);    // Crea los jugadores humanos en la mesa
        int cantidadBots = consola.pedirCantidadBots(nombresHumanos.size());
        List<AgenteInteligente> agentes = new ArrayList<>();
        for (int i = 1; i <= cantidadBots; i++) {
            AgenteInteligente bot = new AgenteInteligente("Bot" + i);
            agentes.add(bot);
            mesa.crearJugadores(bot.pedirNombresJugadores());
        }
        Map<Jugador, InterfazJuego> interfaces = new HashMap<>();
        for (Jugador j : mesa.getJugadores()) {
            if (nombresHumanos.contains(j.getNombre())) {
                interfaces.put(j, consola);
            } else {
                for (AgenteInteligente ai : agentes) {
                    if (ai.pedirNombresJugadores().contains(j.getNombre())) interfaces.put(j, ai);
                }
            }
        }
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n=== Nueva mano ===");
            consola.mostrarResultadoCiegas(mesa.nuevaMano());
            ResultadoApuesta resultado;
            System.out.println("=== PREFLOP ===");
            resultado = mesa.rondaDeApuestas(interfaces);
            if (mostrarSiHayResultado(consola, resultado)) continue;
            consola.mostrarEstadoMesa(mesa.estadoMesa(null, false));
            mesa.avanzarRonda();
            System.out.println("=== FLOP ===");
            resultado = mesa.rondaDeApuestas(interfaces);
            if (mostrarSiHayResultado(consola, resultado)) continue;
            consola.mostrarEstadoMesa(mesa.estadoMesa(null, false));
            mesa.avanzarRonda();
            System.out.println("=== TURN ===");
            resultado = mesa.rondaDeApuestas(interfaces);
            if (mostrarSiHayResultado(consola, resultado)) continue;
            consola.mostrarEstadoMesa(mesa.estadoMesa(null, false));
            mesa.avanzarRonda();
            System.out.println("=== RIVER ===");
            resultado = mesa.rondaDeApuestas(interfaces);
            if (mostrarSiHayResultado(consola, resultado)) continue;
            consola.mostrarEstadoMesa(mesa.estadoMesa(null, false));
            mesa.avanzarRonda();
            System.out.println("=== SHOWDOWN ===");
            List<ResultadoShowdown> showdowns = mesa.showdown();
            consola.mostrarShowdown(showdowns);
            if (mesa.partidaTerminada()) {
                Jugador campeon = mesa.ganadorFinal();
                System.out.println("\nJuego terminado: " + campeon.getNombre() + " es el ganador con todas las fichas.");
                break;
            }
            System.out.println("¿Quieres jugar otra mano? (s/n)");
            String respuesta = consola.leerLinea(Set.of("s", "n"));
            continuar = respuesta.equals("s");
        }
        System.out.println("Gracias por jugar.");
    }
    private static boolean mostrarSiHayResultado(ConsolaUI consola, ResultadoApuesta resultado) {
        if (resultado != null) {
            String ganadores = resultado.ganadores().stream().map(Jugador::getNombre).reduce((a, b) -> a + ", " + b).orElse("");
            consola.mostrarAccion("[Resultado] " + ganadores + " ganan " + resultado.descripcion());
            return true;
        }
        return false;
    }
}