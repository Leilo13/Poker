import java.util.*;
public class AgenteInteligente implements InterfazJuego {
    private final String nombre;
    private final Random rng = new Random();
    public AgenteInteligente(String nombre) { this.nombre = nombre; }
    public List<String> pedirNombresJugadores() {
        return List.of(nombre);
    }
    @Override
    public Movimiento pedirMovimiento(Mesa mesa, Jugador j) {
        List<Accion> opciones = mesa.operacionesDisponibles(j);
        if (mesa.getRonda() == Ronda.PREFLOP) {
            return decidirPreflop(mesa, j, opciones);
        }
        Juez juez = new Juez();
        ResultadoMano mejorMano = juez.evaluarMejorMano(j.getMano(), mesa.getComunitarias());
        int jugadoresActivos = (int) mesa.getJugadores().stream().filter(Jugador::isEnRonda).count();
        int pozo = mesa.getPozoTotal();
        int stack = j.getFichas();
        switch (mejorMano.getTipo()) {
            case FLOR, POKER, FULL, COLOR:
                if (opciones.contains(Accion.ALL_IN) && (stack < pozo / 2 || rng.nextDouble() < 0.2)) {
                    return new Movimiento(Accion.ALL_IN, 0);
                } else if (opciones.contains(Accion.RAISE)) {
                int cantidad = mesa.getApuestaActual() == 0
                        ? mesa.getBigBlind() * (rng.nextBoolean() ? 2 : 3)
                        : mesa.getApuestaActual() + mesa.getBigBlind();
                return new Movimiento(Accion.RAISE, cantidad);
            } else {
                return new Movimiento(Accion.CALL, 0);
            }
            case ESCALERA, TERCIA, DOS_PARES:
                if (jugadoresActivos <= 2 && opciones.contains(Accion.RAISE) && pozo > mesa.getBigBlind() * 4) {
                    if (rng.nextDouble() < 0.3) {
                        return new Movimiento(Accion.RAISE, mesa.getApuestaActual() + mesa.getBigBlind());
                    }
                } else if (opciones.contains(Accion.CALL)) {
                    return new Movimiento(Accion.CALL, 0);
                } else if (opciones.contains(Accion.CHECK)) {
                    return new Movimiento(Accion.CHECK, 0);
                } else {
                    return new Movimiento(Accion.FOLD, 0);
                }
            default:
                if (opciones.contains(Accion.CHECK)) {
                    return new Movimiento(Accion.CHECK, 0);
                } else {
                    return new Movimiento(Accion.FOLD, 0);
                }
        }
    }
    private Movimiento decidirPreflop(Mesa mesa, Jugador j, List<Accion> opciones) {
        CategoriaPreflop categoria = clasificarManoPreflop(j.getMano());
        switch (categoria) {
            case DELUXE -> {
                if (opciones.contains(Accion.RAISE)) {
                    int cantidad = mesa.getApuestaActual() + mesa.getBigBlind() * (rng.nextBoolean() ? 2 : 3);
                    return new Movimiento(Accion.RAISE, cantidad);
                } else if (opciones.contains(Accion.ALL_IN) && rng.nextDouble() < 0.2) {
                    return new Movimiento(Accion.ALL_IN, 0);
                } else if (opciones.contains(Accion.CALL)){
                    return new Movimiento(Accion.CALL, 0);
                } else if (opciones.contains(Accion.CHECK)) {
                    return new Movimiento(Accion.CHECK, 0);
                } else {
                    return new Movimiento(Accion.FOLD, 0);
                }
            }
            case FUERTE -> {
                if (opciones.contains(Accion.RAISE) && rng.nextDouble() < 0.5) {
                    return new Movimiento(Accion.RAISE, mesa.getApuestaActual() + mesa.getBigBlind());
                } else if (opciones.contains(Accion.CALL)) {
                    return new Movimiento(Accion.CALL, 0);
                } else if (opciones.contains(Accion.CHECK)){
                    return new Movimiento(Accion.CHECK, 0);
                } else {
                    return new Movimiento(Accion.FOLD, 0);
                }
            }
            case DECENTE -> {
                if (opciones.contains(Accion.RAISE) && rng.nextDouble() < 0.3) {
                    return new Movimiento(Accion.RAISE, mesa.getApuestaActual() + mesa.getBigBlind());
                } else if (opciones.contains(Accion.CALL)) {
                    return new Movimiento(Accion.CALL, 0);
                } else if (opciones.contains(Accion.CHECK)) {
                    return new Movimiento(Accion.CHECK, 0);
                } else {
                    return new Movimiento(Accion.FOLD, 0);
                }
            }
            case MALA -> {
                if (opciones.contains(Accion.CALL) && rng.nextDouble() < 0.2) {
                    return new Movimiento(Accion.CALL, 0);
                } else if (opciones.contains(Accion.CHECK)) {
                    return new Movimiento(Accion.CHECK, 0);
                } else {
                    return new Movimiento(Accion.FOLD, 0);
                }
            }
            default -> {
                if (opciones.contains(Accion.CALL) && rng.nextDouble() < 0.05) {
                    return new Movimiento(Accion.CALL, 0);
                } else if (opciones.contains(Accion.CHECK)) {
                    return new Movimiento(Accion.CHECK, 0);
                } else {
                    return new Movimiento(Accion.FOLD, 0);
                }
            }
        }
    }
    @Override
    public void mostrarAccion(String mensaje) {
        System.out.println("[Agente] " + mensaje);
    }
    private CategoriaPreflop clasificarManoPreflop(List<Carta> mano) {
        Carta c1 = mano.get(0);
        Carta c2 = mano.get(1);
        int v1 = c1.getValor().ordinal();
        int v2 = c2.getValor().ordinal();
        boolean par = (c1.getValor() == c2.getValor());
        boolean suited = (c1.getMazo() == c2.getMazo());
        int gap = Math.abs(v1 - v2);
        if (par && v1 >= Valor.JOTA.ordinal()) return CategoriaPreflop.DELUXE;
        if (suited && ((c1.getValor() == Valor.AS && (c2.getValor() == Valor.REY || c2.getValor() == Valor.REINA)) || (c2.getValor() == Valor.AS && (c1.getValor() == Valor.REY || c1.getValor() == Valor.REINA)))) {
            return CategoriaPreflop.DELUXE;
        }
        if (par && v1 >= Valor.OCHO.ordinal()) return CategoriaPreflop.FUERTE;
        if ((c1.getValor() == Valor.AS && c2.getValor() == Valor.REY) || (c1.getValor() == Valor.REY && c2.getValor() == Valor.AS)) return CategoriaPreflop.FUERTE;
        if ((c1.getValor() == Valor.AS && c2.getValor() == Valor.REINA) || (c1.getValor() == Valor.REINA && c2.getValor() == Valor.AS)) return CategoriaPreflop.FUERTE;
        if (suited && ((c1.getValor() == Valor.AS && c2.getValor() == Valor.JOTA) || (c2.getValor() == Valor.AS && c1.getValor() == Valor.JOTA))) return CategoriaPreflop.FUERTE;
        if (suited && ((c1.getValor() == Valor.REY && c2.getValor() == Valor.REINA) || (c2.getValor() == Valor.REY && c1.getValor() == Valor.REINA))) return  CategoriaPreflop.FUERTE;
        if (par) return CategoriaPreflop.DECENTE;
        if (suited && gap == 1 && v1 >= Valor.SIETE.ordinal()) return CategoriaPreflop.DECENTE;
        if (suited && (c1.getValor() == Valor.AS || c2.getValor() == Valor.AS)) return CategoriaPreflop.DECENTE;
        if (gap == 1 && !suited && v1 >= Valor.NUEVE.ordinal() && v2 >= Valor.NUEVE.ordinal()) return CategoriaPreflop.MALA;
        return CategoriaPreflop.PESIMA;
    }
}
