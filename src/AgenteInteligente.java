import java.util.*;
public class AgenteInteligente implements InterfazJuego {
    private final String nombre;
    private final Random rng = new Random();
    public AgenteInteligente(String nombre) { this.nombre = nombre; }
    private Movimiento accionPorDefecto(List<Accion> opciones) {
        if (opciones.contains(Accion.CALL)) return new Movimiento(Accion.CALL, 0);
        if (opciones.contains(Accion.CHECK)) return new Movimiento(Accion.CHECK, 0);
        return new Movimiento(Accion.FOLD, 0);
    }
    private CategoriaPreflop clasificarManoPreflop(List<Carta> mano) {
        Carta c1 = mano.get(0);
        Carta c2 = mano.get(1);
        int v1 = c1.valor().valor();
        int v2 = c2.valor().valor();
        boolean par = (c1.valor() == c2.valor());
        boolean suited = (c1.mazo() == c2.mazo());
        int gap = Math.abs(v1 - v2);
        if (par && v1 >= Valor.JOTA.valor()) return CategoriaPreflop.DELUXE;
        if (suited && ((c1.valor() == Valor.AS && (c2.valor() == Valor.REY || c2.valor() == Valor.REINA)) || (c2.valor() == Valor.AS && (c1.valor() == Valor.REY || c1.valor() == Valor.REINA)))) {
            return CategoriaPreflop.DELUXE;
        }
        if (par && v1 >= Valor.OCHO.valor()) return CategoriaPreflop.FUERTE;
        if ((c1.valor() == Valor.AS && c2.valor() == Valor.REY) || (c1.valor() == Valor.REY && c2.valor() == Valor.AS)) return CategoriaPreflop.FUERTE;
        if ((c1.valor() == Valor.AS && c2.valor() == Valor.REINA) || (c1.valor() == Valor.REINA && c2.valor() == Valor.AS)) return CategoriaPreflop.FUERTE;
        if (suited && ((c1.valor() == Valor.AS && c2.valor() == Valor.JOTA) || (c2.valor() == Valor.AS && c1.valor() == Valor.JOTA))) return CategoriaPreflop.FUERTE;
        if (suited && ((c1.valor() == Valor.REY && c2.valor() == Valor.REINA) || (c2.valor() == Valor.REY && c1.valor() == Valor.REINA))) return  CategoriaPreflop.FUERTE;
        if (par) return CategoriaPreflop.DECENTE;
        if (suited && gap == 1 && v1 >= Valor.SIETE.valor()) return CategoriaPreflop.DECENTE;
        if (suited && (c1.valor() == Valor.AS || c2.valor() == Valor.AS)) return CategoriaPreflop.DECENTE;
        if (gap == 1 && !suited && v1 >= Valor.NUEVE.valor() && v2 >= Valor.NUEVE.valor()) return CategoriaPreflop.MALA;
        return CategoriaPreflop.PESIMA;
    }
    private Movimiento decidirPreflop(Mesa mesa, Jugador j, List<Accion> opciones) {
        CategoriaPreflop categoria = clasificarManoPreflop(j.getMano());
        return switch (categoria) {
            case DELUXE -> {
                if (opciones.contains(Accion.RAISE)) {
                    int cantidad = mesa.getApuestaActual() + mesa.getBigBlind() * (rng.nextBoolean() ? 2 : 3);
                    yield new Movimiento(Accion.RAISE, cantidad);
                } else if (opciones.contains(Accion.ALL_IN) && rng.nextDouble() < 0.2) {
                    yield new Movimiento(Accion.ALL_IN, 0);
                } else {
                    yield accionPorDefecto(opciones);
                }
            }
            case FUERTE -> {
                if (opciones.contains(Accion.RAISE) && rng.nextDouble() < 0.5) {
                    yield new Movimiento(Accion.RAISE, mesa.getApuestaActual() + mesa.getBigBlind());
                } else {
                    yield accionPorDefecto(opciones);
                }
            }
            case DECENTE -> {
                if (opciones.contains(Accion.RAISE) && rng.nextDouble() < 0.3) {
                    yield new Movimiento(Accion.RAISE, mesa.getApuestaActual() + mesa.getBigBlind());
                } else {
                    yield accionPorDefecto(opciones);
                }
            }
            case MALA -> {
                if (opciones.contains(Accion.CALL) && rng.nextDouble() < 0.2) {
                    yield new Movimiento(Accion.CALL, 0);
                } else {
                    yield accionPorDefecto(opciones);
                }
            }
            default -> {
                if (opciones.contains(Accion.CALL) && rng.nextDouble() < 0.05) {
                    yield new Movimiento(Accion.CALL, 0);
                } else {
                    yield accionPorDefecto(opciones);
                }
            }
        };
    }
    public List<String> pedirNombresJugadores() {
        return List.of(nombre);
    }
    @Override
    public void mostrarAccion(String mensaje) {
        System.out.println("[Agente] " + mensaje);
    }
    @Override
    public Movimiento pedirMovimiento(Mesa mesa, Jugador j) {
        List<Accion> opciones = mesa.operacionesDisponibles(j);
        if (mesa.getRonda() == Ronda.PREFLOP) return decidirPreflop(mesa, j, opciones);
        Juez juez = new Juez();
        ResultadoMano mejorMano = juez.evaluarMejorMano(j.getMano(), mesa.getComunitarias());
        int jugadoresActivos = (int) mesa.getJugadores().stream().filter(Jugador::isEnRonda).count();
        int pozo = mesa.getPozoTotal();
        int stack = j.getFichas();
        return switch (mejorMano.tipo()) {
            case FLOR, POKER, FULL, COLOR -> {
                if (opciones.contains(Accion.ALL_IN) && (stack < pozo / 2 || rng.nextDouble() < 0.2)) {
                    yield new Movimiento(Accion.ALL_IN, 0);
                } else if (opciones.contains(Accion.RAISE)) {
                    int cantidad = mesa.getApuestaActual() == 0
                            ? mesa.getBigBlind() * (rng.nextBoolean() ? 2 : 3)
                            : mesa.getApuestaActual() + mesa.getBigBlind();
                    yield new Movimiento(Accion.RAISE, cantidad);
                } else {
                    yield accionPorDefecto(opciones);
                }
            }
            case ESCALERA, TERCIA, DOS_PARES -> {
                if (jugadoresActivos <= 2 && opciones.contains(Accion.RAISE) && pozo > mesa.getBigBlind() * 4 && rng.nextDouble() < 0.3) {
                    yield new Movimiento(Accion.RAISE, mesa.getApuestaActual() + mesa.getBigBlind());
                } else {
                    yield new Movimiento(Accion.FOLD, 0);
                }
            }
            default -> accionPorDefecto(opciones);
        };
    }
}
