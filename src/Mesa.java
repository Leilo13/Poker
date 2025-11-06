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
        int sbIndex, bbIndex;
        if (jugadores.size() == 2) {
            sbIndex = dealerIndex;
            bbIndex =(dealerIndex + 1) % jugadores.size();
        } else {
            sbIndex = (dealerIndex +1) % jugadores.size();
            bbIndex = (dealerIndex + 2) % jugadores.size();
        }
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
        if (pozos.isEmpty()) return;
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
                System.out.println(g.getNombre() + " " + g.getFichas());
            }
        }
        pozos.clear();
        jugadores.removeIf(j -> j.getFichas() <= 0);
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
        if (ronda == Ronda.SHOWDOWN) {
            return;
        }
        ronda = ronda.siguiente();
        apuestaActual = 0;
        for (Jugador j : jugadores) {
            if (j.isEnRonda()) {
                j.setApuestaEnRonda(0);
            }
        }
        repartirComunitarias();
        imprimirDebug();
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
        int startIndex;
        if (jugadores.size() == 2) {
            if (ronda == Ronda.PREFLOP) {
                startIndex = dealerIndex;
            } else {
                startIndex = (dealerIndex + 1) % jugadores.size();
            }
        } else {
            startIndex = (dealerIndex + 3) % jugadores.size();
        }
        Map<Jugador, Boolean> actuo = new HashMap<>();
        jugadores.stream().filter(Jugador::isEnRonda).forEach(j -> actuo.put(j, false));
        jugadores.stream().filter(Jugador::isAllIn).forEach(j -> actuo.put(j, true));
        boolean huboApuesta = (apuestaActual > 0);
        int ultimoAgresorIndex = -1;
        outer:
        do {
            for (int i = 0; i < jugadores.size(); i++) {
                Jugador j = jugadores.get((startIndex + i) % jugadores.size());
                if (!j.isEnRonda() || j.isAllIn()) continue;
                boolean invalido;
                do {
                    invalido = false;
                    List<Integer> opcionesDisponibles = new ArrayList<>();
                    System.out.println("Turno de " + j.getNombre() + " (fichas: " + j.getFichas() + ")\nApuesta actual en mesa: " + apuestaActual + "\nTu apuesta actual: " + j.getApuestaEnRonda() + "\nElige tu acción:");
                    if (apuestaActual == 0) {
                        System.out.println("1 -> Check");
                        opcionesDisponibles.add(1);
                        if (j.getFichas() > 0) {
                            System.out.println("2 -> Apostar (mínimo 1)");
                            opcionesDisponibles.add(2);
                            System.out.println("3 -> All-in (" + j .getFichas() + ")");
                            opcionesDisponibles.add(3);
                        }
                        System.out.println("4 -> Retirarse");
                        opcionesDisponibles.add(4);
                    } else {
                        int diff = apuestaActual - j.getApuestaEnRonda();
                        if (diff == 0) {
                            System.out.println("1 -> Check");
                            opcionesDisponibles.add(1);
                        } else if (diff > 0 && diff <= j.getFichas()){
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
                            case 1 -> actuo.put(j, true);
                            case 2 -> {
                                int apuesta = pedirCantidad(j, 1, j.getFichas());
                                j.raise(apuesta, apuestaActual);
                                apuestaActual = apuesta;
                                huboApuesta = true;
                                ultimoAgresorIndex = (startIndex + i) % jugadores.size();
                                actuo.put(j, true);
                            }
                            case 3 -> {
                                j.allIn();
                                if (j.getApuestaEnRonda() > apuestaActual) {
                                    apuestaActual = j.getApuestaEnRonda();
                                    huboApuesta = true;
                                    ultimoAgresorIndex = (startIndex + i) % jugadores.size();
                                    actuo.put(j, true);
                                }
                            }
                            case 4 -> {
                                j.fold();
                                actuo.put(j, true);
                            }
                        }
                    } else {
                        switch (eleccion) {
                            case 1 -> {
                                j.call(apuestaActual);
                                actuo.put(j, true);
                            }
                            case 2 -> {
                                int apuesta = pedirCantidad(j, (apuestaActual + 1), j.getFichas());
                                j.raise(apuesta, apuestaActual);
                                apuestaActual = apuesta;
                                huboApuesta = true;
                                ultimoAgresorIndex = (startIndex + i) % jugadores.size();
                                actuo.put(j, true);
                            }
                            case 3 -> {
                                j.allIn();
                                if (j.getApuestaEnRonda() > apuestaActual) {
                                    apuestaActual = j.getApuestaEnRonda();
                                    ultimoAgresorIndex = (startIndex + i) % jugadores.size();
                                    actuo.put(j, true);
                                }
                            }
                            case 4 -> {
                                j.fold();
                                actuo.put(j, true);
                            }
                        }
                    }
                    if (!huboApuesta) {
                        if (actuo.entrySet().stream().filter(e -> e.getKey().isEnRonda()).allMatch(Map.Entry::getValue)) {
                            construirPozos();
                            break outer;
                        }
                    } else {
                        if (estanTodosIgualados() && todosActuaron(actuo)) {
                            construirPozos();
                            if (noQuedanAcciones()) {
                                System.out.println("No queda acción posible. Avanzando directo al showdown...");
                                while (ronda != Ronda.SHOWDOWN) avanzarRonda();
                                showdown();
                            }
                            break outer;
                        }
                    }
                    long activos = jugadores.stream().filter(Jugador::isEnRonda).count();
                    if (activos == 1){
                        Jugador ganador = jugadores.stream().filter(Jugador::isEnRonda).findFirst().get();
                        int total = jugadores.stream().mapToInt(Jugador::getApuestaEnRonda).sum() + pozos.stream().mapToInt(Pozo::getCantidad).sum();
                        ganador.ganarFichas(total);
                        System.out.println("Todos se retiraron. " + ganador.getNombre() + " gana " + total);
                        pozos.clear();
                        break outer;
                        }
                } while (invalido);
            }
        } while (true);
    } //YA ESTÁ

    public void nuevaMano(){
        if (jugadores.size() <= 1) {
            return;
        }
        baraja.reiniciar();
        comunitarias.clear();
        pozos.clear();
        for (Jugador j : jugadores){
            j.resetJugador();
        }
        dealerIndex = (dealerIndex + 1) % jugadores.size();
        asignarCiegas();
        repartoInicial();
        ronda = Ronda.PREFLOP;
        apuestaActual = 0;
    } //YA ESTÁ

    public boolean partidaTerminada() {
        return jugadores.size() <= 1;
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

    private boolean estanTodosIgualados() {
        return jugadores.stream()
                .filter(Jugador::isEnRonda)
                .allMatch(j -> j.isAllIn() || j.getApuestaEnRonda() == apuestaActual);

    }

    private boolean noQuedanAcciones() {
        long activosNoAllIn = jugadores.stream().filter(Jugador::isEnRonda).filter(j -> !j.isAllIn()).count();
        return activosNoAllIn <= 1;
    }

    private int pedirCantidad(Jugador j, int minimo, int maximo) {
        while (true) {
            System.out.println("¿Cuánto apuesta " + j.getNombre() + "? (mínimo " + minimo + ", máximo " + maximo + ")");
            try {
                int cantidad = Integer.parseInt(sc.nextLine());
                if (cantidad < minimo || cantidad > maximo) {
                    System.out.println("Cantidad inválida, intenta de nuevo.");
                } else {
                    return cantidad;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida, escribe un número válido.");
            }
        }
    }

    private boolean todosActuaron(Map<Jugador, Boolean> yaActuo) {
        return jugadores.stream().filter(Jugador::isEnRonda).allMatch(j -> j.isAllIn() || yaActuo.getOrDefault(j, false));
    }
}

