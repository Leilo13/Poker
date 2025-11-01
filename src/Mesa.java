import java.util.*;
import java.util.stream.Collectors;

public class Mesa {
    private int apuestaActual;
    private int dealerIndex = 0;
    private final int smallBlind = 10;
    private final int bigBlind = 20;
    private final Juez juez = new Juez();
    private Baraja baraja;
    private Ronda ronda;
    private final Scanner sc = new Scanner(System.in);
    private final List<Jugador> jugadores = new ArrayList<>();
    private final List<Carta> comunitarias = new ArrayList<>();
    private final List<Pozo> pozos = new ArrayList<>();

    public Mesa() {
        baraja = new Baraja();
        crearJugadores();
        asignarCiegas();
        repartoInicial();
        ronda = Ronda.PREFLOP;
    } //YA ESTÁ

    private void asignarCiegas(){
        int sbIndex = (dealerIndex + 1) % jugadores.size();
        int bbIndex = (dealerIndex + 2) % jugadores.size();
        Jugador smallBlindPlayer = jugadores.get(sbIndex);
        Jugador bigBlindPlayer = jugadores.get(bbIndex);
        int sb = smallBlindPlayer.pagarBlind(smallBlind);
        int bb = bigBlindPlayer.pagarBlind(bigBlind);
        apuestaActual = Math.max(sb,bb);
        System.out.println(smallBlindPlayer.getNombre() + " paga la ciega pequeña: " + sb);
        System.out.println(bigBlindPlayer.getNombre() + " paga la ciega grande: " + bb);
    } //YA ESTÁ

    private void construirPozos() {
        int fichasRetirados = jugadores.stream().filter(j -> !j.isEnRonda() && j.getApuestaEnRonda() > 0).mapToInt(Jugador::getApuestaEnRonda).sum();
        jugadores.forEach(j -> {
            if (!j.isEnRonda()) j.setApuestaEnRonda(0);
        });
        List<Jugador> conApuesta = jugadores.stream().filter(j -> j.getApuestaEnRonda() > 0).collect(Collectors.toList());
        while (!conApuesta.isEmpty()) {
            int minApuesta = conApuesta.stream().mapToInt(Jugador::getApuestaEnRonda).min().getAsInt();
            int cantidadPozo = 0;
            List<Jugador> participantes = new ArrayList<>();
            for (Jugador j : jugadores) {
                if (j.getApuestaEnRonda() > 0) {
                    cantidadPozo += minApuesta;
                    j.setApuestaEnRonda(j.getApuestaEnRonda() - minApuesta);
                    if (j.isEnRonda()) participantes.add(j);
                }
            }
            if (pozos.isEmpty()) {
                cantidadPozo += fichasRetirados;
                pozos.add(new Pozo(cantidadPozo, participantes));
            } else {
                Pozo ultimo = pozos.get(pozos.size() - 1);
                if (ultimo.getParticipantes().containsAll(participantes) && participantes.containsAll(ultimo.getParticipantes())) {
                    ultimo.setCantidad(ultimo.getCantidad() + cantidadPozo);
                } else {
                    pozos.add(new Pozo(cantidadPozo, participantes));
                }
            }
            conApuesta = jugadores.stream().filter(j -> j.isEnRonda() && j.getApuestaEnRonda() > 0).collect(Collectors.toList());
        }
    } //YA ESTÁ

    public void showdown() {
        if (ronda != Ronda.SHOWDOWN) return;
        construirPozos();
        System.out.println("\n=== SHOWDOWN ===");
        //imprimirMesa(null, true);
        for (Pozo p : pozos) {
            ResultadoMano mejor = null;
            List<Jugador> ganadores = new ArrayList<>();
            for (Jugador j : p.getParticipantes()) {
                ResultadoMano r = juez.evaluarMejorMano(j.getMano(), comunitarias);
                if (mejor == null || juez.compararResultados(r, mejor) > 0) {
                    ganadores.clear();
                    ganadores.add(j);
                    mejor = r;
                } else if (juez.compararResultados(r, mejor) == 0) {
                    ganadores.add(j);
                }
            }
            int premio = p.getCantidad() / ganadores.size();
            for (Jugador g : ganadores) {
                g.ganarFichas(premio);
                System.out.println("Ganador del: " + p + " -> " + g.getNombre() + " con " + mejor);
            }
        }
        pozos.clear();
    } //YA ESTÁ

    private void repartirComunitarias() {
        switch (ronda) {
            case FLOP -> {
                for (int i = 0; i < 3; i++) comunitarias.add(baraja.repartir());
            }
            case TURN, RIVER -> comunitarias.add(baraja.repartir());
            default -> {}
        }
    } //YA ESTÁ

    public void avanzarRonda() {
        ronda = ronda.siguiente();
        apuestaActual = 0;
        for (Jugador j : jugadores) {
            if (j.getFichas() > 0) {
                j.setApuestaEnRonda(0);
            }
        }
        repartirComunitarias();
    } //YA ESTÁ

    private void crearJugadores() {
        int nj = 0;
        while (nj < 2) {
            System.out.println("¿Cuántos van a jugar? (mínimo 2)");
            try {
                nj = Integer.parseInt(sc.nextLine());
                if (nj < 2) System.out.println("Debe haber al menos 2 jugadores");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida, escribe un número entero");
            }
        }
        for (int i = 1; i <= nj; i++){
            String nom = "";
            while (nom.isBlank()) {
                System.out.println("Nombre del jugador " + i + ":");
                nom = sc.nextLine().trim();
                if (nom.isBlank()) System.out.println("El nombre no puede estar vacio.");
                String finalNom = nom;
                if (jugadores.stream().anyMatch(j -> j.getNombre().equalsIgnoreCase(finalNom))) {
                    System.out.println("Ese nombre ya está en uso, elige otro.");
                    nom = "";
                }
            }
            jugadores.add(new Jugador(nom));
        }
    } //YA ESTÁ

    private void repartoInicial() {
        for (Jugador j : jugadores) {
            for (int i = 0; i < 2; i++) j.recibir(baraja.repartir());
        }
    } //YA ESTÁ

    public void imprimirMesa(Jugador jugadorEnTurno, boolean showdown) {
        System.out.println("=== Ronda: " + ronda + " ===" + "\nApuesta actual: " + apuestaActual + "\n----------------------------");
        for (Jugador j : jugadores) {
            String estado = j.isEnRonda()
                    ? (j.isAllIn() ? " - ALL-IN" : "")
                    : " - Retirado";
            System.out.println(j.getNombre() + " (fichas: " + j.getFichas() + ", apuesta actual: " + j.getApuestaEnRonda() + estado + ") ");
            if (showdown && j.isEnRonda()) {
                System.out.println("Mano: ");
                j.imprimirMano();
            } else if (j.equals(jugadorEnTurno)) {
                System.out.println("Mano: ");
                j.imprimirMano();
            } else {
                System.out.println("Mano: [??] [??]");
            }
        }
        if (!comunitarias.isEmpty()) {
            System.out.println("\nCartas comunitarias:");
            for (Carta c : comunitarias) {
                System.out.println("\u001B[1m" + c + "\u001B[0m ");
            }
            System.out.println();
        }
        System.out.println();
    } //EN REVISIÓN

    public void rondaDeApuestas() {
        boolean todosIgualados = true;
        int startIndex = (dealerIndex + 3) % jugadores.size();
        do {
            for (int i = 0; i < jugadores.size(); i++) {
                Jugador j = jugadores.get((startIndex + i) % jugadores.size());
                if (!j.isEnRonda() || j.isAllIn()) continue;
                boolean invalido;
                //imprimirMesa(j, false);
                do {
                    invalido = false;
                    List<Integer> opcionesDisponibles = new ArrayList<>();
                    System.out.println("Turno de " + j.getNombre() + " (fichas: " + j.getFichas() + ")\nApuesta actual en mesa: " + apuestaActual + "\nTu apuesta actual: " + j.getApuestaEnRonda() + "\nElige tu acción:");
                    if (apuestaActual == 0) {
                        System.out.println("1 -> Check");
                        opcionesDisponibles.add(1);
                        int minimoApuesta = 1;
                        System.out.println("2 -> Apostar (mínimo " + minimoApuesta + ")");
                        opcionesDisponibles.add(2);
                        if (j.getFichas() > 0) {
                            System.out.println("3 -> All-in (" + j .getFichas() + ")");
                            opcionesDisponibles.add(3);
                        }
                        System.out.println("4 -> Retirarse");
                        opcionesDisponibles.add(4);
                    } else {
                        if (j.getFichas() + j.getApuestaEnRonda() >= apuestaActual) {
                            int cantidad = apuestaActual - j.getApuestaEnRonda();
                            System.out.println("1 -> Igualar (" + (apuestaActual - j.getApuestaEnRonda() + ")"));
                            opcionesDisponibles.add(1);
                        }
                        if (j.getFichas() + j.getApuestaEnRonda() > apuestaActual) {
                            System.out.println("2 -> Subir (mínimo " + (apuestaActual + 1) + ")");
                            opcionesDisponibles.add(2);
                        }
                        if (j.getFichas() > 0) {
                            System.out.println("3 -> All-in (" + j.getFichas() + ")");
                            opcionesDisponibles.add(3);
                        }
                        System.out.println("4 -> Retirarse");
                        opcionesDisponibles.add(4);
                    }
                    int eleccion = -1;
                    try {
                        eleccion = Integer.parseInt(sc.nextLine());
                        if (!opcionesDisponibles.contains(eleccion)) {
                            System.out.println("Opción no válida, elige una de las mostradas.");
                            invalido = true;
                            continue;
                        }
                    } catch (NumberFormatException e) {
                            System.out.println("Entrada inválida, escribe un número válido");
                            invalido = true;
                            continue;
                    }
                    if (apuestaActual == 0) {
                        switch (eleccion) {
                            case 1 -> {}
                            case 2 -> {
                                int minimoApuesta = 1;
                                boolean subidaValida = false;
                                while (!subidaValida) {
                                    System.out.println("¿Cuánto apuestas? (mínimo " + minimoApuesta + ")");
                                    try {
                                        int apuesta = Integer.parseInt(sc.nextLine());
                                        if (apuesta < minimoApuesta || apuesta > j.getFichas()) {
                                            System.out.println("Apuesta inválida, intenta de nuevo.");
                                            continue;
                                        }
                                        j.raise(apuesta, apuestaActual);
                                        apuestaActual = apuesta;
                                        subidaValida = true;
                                    } catch (NumberFormatException e) {
                                        System.out.println("Entrada inválida, escribe un número válido");
                                    }
                                }
                            }
                            case 3 -> j.allIn();
                            case 4 -> j.fold();
                        }
                    } else {
                        switch (eleccion) {
                            case 1 -> j.call(apuestaActual);
                            case 2 -> {
                                boolean subidaValida = false;
                                while (!subidaValida) {
                                    System.out.println("¿A cuánto subes la apuesta? (mínimo " + (apuestaActual + 1) + ")");
                                    try {
                                        int nuevaApuesta = Integer.parseInt(sc.nextLine());
                                        int diferencia = j.raise(nuevaApuesta, apuestaActual);
                                        if (diferencia != -1) {
                                            apuestaActual = j.getApuestaEnRonda();
                                            subidaValida = true;
                                        } else {
                                            System.out.println("Apuesta inválida, intenta de nuevo.");
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.println("Entrada inválida, escribe un número válido");
                                    }
                                }
                            }
                            case 3 -> j.allIn();
                            case 4 -> j.fold();
                        }
                    }
                    long activos = jugadores.stream().filter(Jugador::isEnRonda).count();
                    if (activos == 1){
                        Jugador ganador = jugadores.stream().filter(Jugador::isEnRonda).findFirst().get();
                        int total = 0;
                        for (Jugador jug : jugadores){
                            total += jug.getApuestaEnRonda();
                            jug.setApuestaEnRonda(0);
                        }
                        for (Pozo p : pozos) {
                            total += p.getCantidad();
                        }
                        ganador.ganarFichas(total);
                        System.out.println("Todos se retiraron. " + ganador.getNombre() + " gana automáticamente " + total + " fichas");
                        System.out.println(ganador.getFichas());
                        pozos.clear();
                        return;
                    }
                } while (invalido);
            }
            todosIgualados = true;
            for (Jugador j : jugadores) {
                if (j.isEnRonda() && !j.isAllIn() && j.getApuestaEnRonda() < apuestaActual) {
                    todosIgualados = false;
                    break;
                }
            }
            boolean todosAllIn = jugadores.stream().filter(Jugador::isEnRonda).allMatch(Jugador::isAllIn);
            if (todosAllIn) {
                System.out.println("Todos los jugadores están ALL-IN. Se revelan las cartas restantes...");
                while (ronda != Ronda.SHOWDOWN) {
                    avanzarRonda();
                }
                showdown();
                return;
            }
        } while (!todosIgualados && jugadores.stream().filter(Jugador::isEnRonda).count() > 1);
        construirPozos();
    } //YA ESTÁ

    public void nuevaMano(){
        jugadores.removeIf(jugador -> !jugador.isEnRonda());
        jugadores.removeIf(j -> j.getFichas() <= 0);
        if (jugadores.size() <= 1) {
            return;
        }
        baraja.reiniciar();
        comunitarias.clear();
        pozos.clear();
        for (Jugador j : jugadores){
            j.getMano().clear();
            j.resetApuesta();
            j.setEnRonda(true);
        }
        dealerIndex = (dealerIndex + 1) % jugadores.size();
        asignarCiegas();
        repartoInicial();
        ronda = Ronda.PREFLOP;
    } //YA ESTÁ

    public boolean partidaTerminada() {
        return jugadores.stream().filter(Jugador::isEnRonda).count() <= 1;
    } //YA ESTÁ

    public String getGanadorFinal() {
        if (jugadores.size() == 1) {
            return jugadores.get(0).getNombre();
        }
        return null;
    } //YA ESTÁ

    public Ronda getRonda() { return ronda; } //YA ESTÁ

    public void imprimirDebug() {
        System.out.println("\n=== DEBUG MESA ===");
        System.out.println("Ronda: " + ronda);
        System.out.println("Apuesta actual: " + apuestaActual);
        System.out.println("Dealer index: " + dealerIndex);
        System.out.println("Pozos: " + pozos); // asumiendo que Pozo tiene toString()

        // Jugadores
        for (Jugador j : jugadores) {
            System.out.print(j.getNombre() +
                    " | fichas: " + j.getFichas() +
                    " | apuesta: " + j.getApuestaEnRonda() +
                    " | enRonda: " + j.isEnRonda() +
                    " | allIn: " + j.isAllIn() +
                    " | mano: ");
            j.imprimirMano(); // aquí sí mostramos siempre la mano
        }

        // Comunitarias
        if (!comunitarias.isEmpty()) {
            System.out.print("Comunitarias: ");
            for (Carta c : comunitarias) {
                System.out.print(c + " ");
            }
            System.out.println();
        }

        System.out.println("==================\n");
    }

}