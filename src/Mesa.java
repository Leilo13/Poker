import java.util.*;
import java.util.stream.Collectors;

public class Mesa {
    private int apuestaActual;
    private int dealerIndex = -1;
    private final int smallBlind = 10;
    private final int bigBlind = 2 * smallBlind;
    private final Baraja baraja = new Baraja();
    private Ronda ronda;
    private final List<Jugador> jugadores = new ArrayList<>();
    private final List<Carta> comunitarias = new ArrayList<>();
    private final List<Pozo> pozos = new ArrayList<>();

    public Mesa() {
        ronda = Ronda.PREFLOP;
    }

    public String aplicarMovimiento(Jugador j, Movimiento mov) {
        switch (mov.accion()) {
            case CHECK, CALL -> {
                return j.call(apuestaActual);
            }
            case RAISE -> {
                int nuevaApuesta = mov.cantidad();
                int diff = nuevaApuesta - j.getApuestaEnRonda();
                if (nuevaApuesta <= apuestaActual) return "Raise inválido";
                if (nuevaApuesta < apuestaActual + bigBlind) return "Raise demasiado pequeño";
                if (diff > j.getFichas()) return "No tienes suficientes fichas";
                String msg = j.raise(nuevaApuesta);
                apuestaActual = nuevaApuesta;
                return msg;
            }
            case ALL_IN -> {
                String msg = j.allIn();
                apuestaActual = Math.max(apuestaActual, j.getApuestaEnRonda());
                return msg;
            }
            case FOLD -> {
                return j.fold();
            }
            default -> {
                return "Acción inválida";
            }
        }
    }

    public ResultadoCiegas asignarCiegas(){
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
        return new ResultadoCiegas(smallBlindPlayer, sb, bigBlindPlayer, bb, apuestaActual);
    }

    public void avanzarRonda() {
        if (ronda == Ronda.SHOWDOWN) return;
        ronda = ronda.siguiente();
        apuestaActual = 0;
        jugadores.stream().filter(Jugador::isEnRonda).forEach(j -> j.setApuestaEnRonda(0));
        repartirComunitarias();
    }

    private int calcularStartIndex() {
        if (jugadores.size() == 2) return (ronda == Ronda.PREFLOP) ? dealerIndex : (dealerIndex + 1) % jugadores.size();
        return (ronda == Ronda.PREFLOP) ? (dealerIndex + 3) % jugadores.size() : (dealerIndex + 1) % jugadores.size();
    }

    private boolean condicionesDeCierre(boolean huboApuesta, Map<Jugador, Boolean> actuo) {
        if (!huboApuesta) {
            if (jugadores.stream()
                    .filter(Jugador::isEnRonda)
                    .allMatch(j -> actuo.getOrDefault(j, false))) {
                construirPozos();
                return true;
            }
        } else {
            if (estanTodosIgualados() && todosActuaron(actuo)) {
                construirPozos();
                return true;
            }
        }
        return false;
    }

    private void construirPozos() {
        int fichasRetirados = jugadores.stream()
                .filter(j -> !j.isEnRonda() && j.getApuestaEnRonda() > 0)
                .mapToInt(Jugador::getApuestaEnRonda)
                .sum();
        jugadores.forEach(j -> {
            if (!j.isEnRonda()) j.setApuestaEnRonda(0);
        });
        List<Jugador> conApuesta = jugadores.stream()
                .filter(j -> j.getApuestaEnRonda() > 0)
                .collect(Collectors.toList());
        while (!conApuesta.isEmpty()) {
            int minApuesta = conApuesta.stream()
                    .mapToInt(Jugador::getApuestaEnRonda)
                    .min()
                    .orElse(0);
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
                Pozo ultimo = pozos.getLast();
                if (ultimo.getParticipantes().containsAll(participantes) && participantes.containsAll(ultimo.getParticipantes())) {
                    ultimo.setCantidad(ultimo.getCantidad() + cantidadPozo);
                } else {
                    pozos.add(new Pozo(cantidadPozo, participantes));
                }
            }
            conApuesta = jugadores.stream().filter(j -> j.isEnRonda() && j.getApuestaEnRonda() > 0).collect(Collectors.toList());
        }
    }
    public void crearJugadores(List<String> nombres) {
        for (String nom : nombres){
            jugadores.add(new Jugador(nom));
        }
    }
    public EstadoMesa estadoMesa(Jugador jugadorEnTurno, boolean showdown) {
        List<EstadoJugador> estadoJugadores = new ArrayList<>();
        for (Jugador j : jugadores) {
            List<Carta> manoVisible = (showdown && j.isEnRonda() || j.equals(jugadorEnTurno) ? j.getMano() : List.of(new Carta(null, null), new Carta(null, null)));
            estadoJugadores.add(new EstadoJugador(j.getNombre(), j.getFichas(), j.getApuestaEnRonda(), j.isEnRonda(), j.isAllIn(), manoVisible));
        }
        return new EstadoMesa(ronda, apuestaActual, estadoJugadores, new ArrayList<>(comunitarias));
    }
    private boolean estanTodosIgualados() {
        return jugadores.stream()
                .filter(Jugador::isEnRonda)
                .allMatch(j -> j.isAllIn() || j.getApuestaEnRonda() == apuestaActual);
    }

    public Jugador ganadorFinal() {
        return getJugadores().stream()
                .filter(j -> j.getFichas() > 0)
                .findFirst()
                .orElse(null);
    }

    public int getApuestaActual() { return apuestaActual; }

    public int getBigBlind() { return bigBlind; }

    public List<Carta> getComunitarias() { return List.copyOf(comunitarias); }

    public List<Jugador> getJugadores() { return List.copyOf(jugadores); }

    public int getPozoTotal() {
        return pozos.stream()
                .mapToInt(Pozo::getCantidad)
                .sum();
    }

    public Ronda getRonda() { return ronda; }

    private Map<Jugador, Boolean> inicalizarMapaActuacion() {
        Map<Jugador, Boolean> actuo = new HashMap<>();
        jugadores.stream()
                .filter(Jugador::isEnRonda)
                .forEach(j -> actuo.put(j, false));
        jugadores.stream()
                .filter(Jugador::isAllIn)
                .forEach(j -> actuo.put(j, true));
        return actuo;
    }
    public ResultadoCiegas nuevaMano(){
        if (jugadores.size() <= 1)  return null;
        baraja.reiniciar();
        comunitarias.clear();
        pozos.clear();
        jugadores.forEach(Jugador::resetJugador);
        dealerIndex = (dealerIndex + 1) % jugadores.size();
        apuestaActual = 0;
        ResultadoCiegas resultadoCiegas = asignarCiegas();
        repartoInicial();
        ronda = Ronda.PREFLOP;
        return resultadoCiegas;
    }

    public List<Accion> operacionesDisponibles(Jugador j) {
        List<Accion> opciones = new ArrayList<>();
        int diff = apuestaActual - j.getApuestaEnRonda();
        int fichas = j.getFichas();
        if (diff == 0) {
            opciones.add(Accion.CHECK);
        } else {
            if (fichas >= diff) opciones.add(Accion.CALL);
            else opciones.add(Accion.FOLD);
        }
        int minNuevaApuesta = apuestaActual + bigBlind;
        if (j.getApuestaEnRonda() + fichas >= minNuevaApuesta) opciones.add(Accion.RAISE);
        if (fichas > 0) opciones.add(Accion.ALL_IN);
        return opciones;
    }

    public boolean partidaTerminada() { return jugadores.size() <= 1; }
    private void repartirComunitarias() {
        switch (ronda) {
            case FLOP -> { for (int i = 0; i < 3; i++) comunitarias.add(baraja.repartir()); }
            case TURN, RIVER -> comunitarias.add(baraja.repartir());
            default -> {}
        }
    }

    private void repartoInicial() {
        for (Jugador j : jugadores) {
            for (int i = 0; i < 2; i++) j.recibir(baraja.repartir());
        }
    }

    private ResultadoFold resolverGanadorPorFold() {
        Optional<Jugador> ganadorOpt = jugadores.stream().filter(Jugador::isEnRonda).findFirst();
        if (ganadorOpt.isPresent()) {
            Jugador ganador = ganadorOpt.get();
            int total = jugadores.stream().mapToInt(Jugador::getApuestaEnRonda).sum() + pozos.stream().mapToInt(Pozo::getCantidad).sum();
            ganador.ganarFichas(total);
            pozos.clear();
            jugadores.forEach(j -> j.setApuestaEnRonda(0));
            return new ResultadoFold(ganador, total);
        }
        return null;
    }

    public ResultadoApuesta rondaDeApuestas(Map<Jugador, InterfazJuego> interfaces) {
        if (jugadores.stream().filter(Jugador::isEnRonda).allMatch(Jugador::isAllIn)) {
            construirPozos();
            return null;
        }
        int startIndex = calcularStartIndex();
        Map<Jugador, Boolean> actuo = inicalizarMapaActuacion();
        boolean huboApuesta = (apuestaActual > 0);
        while (true) {
            for (int i = 0; i < jugadores.size(); i++) {
                Jugador j = jugadores.get((startIndex + i) % jugadores.size());
                if (!j.isEnRonda() || j.isAllIn()) continue;
                InterfazJuego ui = interfaces.get(j);
                Movimiento mov = ui.pedirMovimiento(this, j);
                int apuestaAntes = apuestaActual;
                String mensaje = aplicarMovimiento(j, mov);
                ui.mostrarAccion(mensaje);
                actuo.put(j, true);
                if (apuestaActual > apuestaAntes) {
                    huboApuesta = true;
                    actuo = inicalizarMapaActuacion();
                    actuo.put(j, true);
                }
                if (condicionesDeCierre(huboApuesta, actuo)) return null;
                if (soloQuedaUnJugador()) return resolverGanadorPorFold();
            }
        }
    }

    public List<ResultadoShowdown> showdown() {
        List<ResultadoShowdown> resultados = new ArrayList<>();
        if (ronda != Ronda.SHOWDOWN) return resultados;
        construirPozos();
        if (pozos.isEmpty()) return resultados;
        for (Pozo p : pozos) {
            ResultadoMano mejor = null;
            List<Jugador> ganadores = new ArrayList<>();
            for (Jugador j : p.getParticipantes()) {
                if (!j.isEnRonda()) continue;
                ResultadoMano r = Juez.evaluarMejorMano(j.getMano(), comunitarias);
                if (mejor == null || Juez.compararResultados(r, mejor) > 0) {
                    ganadores.clear();
                    ganadores.add(j);
                    mejor = r;
                } else if (Juez.compararResultados(r, mejor) == 0) {
                    ganadores.add(j);
                }
            }
            int premio = p.getCantidad() / ganadores.size();
            ganadores.forEach(g -> g.ganarFichas(premio));
            resultados.add(new ResultadoShowdown(p, List.copyOf(ganadores), mejor, premio));
        }
        pozos.clear();
        jugadores.removeIf(j -> j.getFichas() <= 0);
        return resultados;
    }

    private boolean soloQuedaUnJugador() {
        return jugadores.stream().filter(Jugador::isEnRonda).count() == 1;
    }

    private boolean todosActuaron(Map<Jugador, Boolean> yaActuo) { return jugadores.stream().filter(Jugador::isEnRonda).allMatch(j -> j.isAllIn() || yaActuo.getOrDefault(j, false)); }
}