import java.util.*;
public class Main {
    public static void main(String[] args) {
        Mesa mesa = new Mesa();
        ConsolaUI consola = new ConsolaUI();
        List<String> nombresHumanos = consola.pedirNombresJugadores();
        mesa.crearJugadores(nombresHumanos);
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
        while (true) {
            System.out.println("\n=== Nueva mano ===");
            mesa.nuevaMano();
            ResultadoApuesta resultado;
            resultado = mesa.rondaDeApuestas(interfaces);
            if (mostrarSiHayResultado(resultado)) continue;
            mesa.avanzarRonda();
            resultado = mesa.rondaDeApuestas(interfaces);
            if (mostrarSiHayResultado(resultado)) continue;
            mesa.avanzarRonda();
            resultado = mesa.rondaDeApuestas(interfaces);
            if (mostrarSiHayResultado(resultado)) continue;
            mesa.avanzarRonda();
            resultado = mesa.rondaDeApuestas(interfaces);
            if (mostrarSiHayResultado(resultado)) continue;
            mesa.avanzarRonda();
            List<ResultadoShowdown> showdowns = mesa.showdown();
            consola.mostrarShowdown(showdowns);
        }
    }
    private static boolean mostrarSiHayResultado(ResultadoApuesta resultado) {
        if (resultado != null) {
            String ganadores = resultado.getGanadores().stream().map(Jugador::getNombre).reduce((a, b) -> a + ", " + b).orElse("");
            System.out.println("[Resultado] " + ganadores + " ganan " + resultado.getDescripcion() + ")");
            return true;
        }
        return false;
    }
}