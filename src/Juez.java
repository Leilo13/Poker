import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
public final class Juez {
    private static boolean esColor(int[] conteoPalo) {
        for (int count : conteoPalo) if (count == 5) return true;
        return false;
    }
    public static int compararResultados(ResultadoMano a, ResultadoMano b) {
        int cmpTipo = Integer.compare(a.tipo().peso(),b.tipo().peso());
        if (cmpTipo != 0) return cmpTipo;
        List<Integer> da = a.desempate();
        List<Integer> db = b.desempate();
        int len = Math.min(da.size(), db.size());
        for (int i = 0; i < len; i++){
            int cmp = Integer.compare(da.get(i),db.get(i));
            if (cmp != 0) return cmp;
        }
        return Integer.compare(da.size(), db.size());
    }
    private static int mapPalo(Mazo m) {
        return switch (m) {
            case TREBOLES -> 0;
            case CORAZONES -> 1;
            case PICAS -> 2;
            case DIAMANTES -> 3;
        };
    }
    private static int primerKicker(List<Integer> valoresDesc, List<Integer> excluidos) {
        for (int v : valoresDesc) if (!excluidos.contains(v)) return v;
        return 0;
    }
    private static Integer valorEscalera(List<Integer> valoresDesc, int[] conteoValor) {
        List<Integer> uniq = new ArrayList<>();
        Integer prev = null;
        for (Integer v : valoresDesc) {
            if (!v.equals(prev)) {
                uniq.add(v);
                prev = v;
            }
        }
        for (int i = 0; i + 4 < uniq.size(); i++) {
            int a = uniq.get(i);
            if (uniq.get(i + 1) == a - 1 && uniq.get(i + 2) == a - 2 && uniq.get(i + 3) == a - 3 && uniq.get(i + 4) == a - 4) {
                return a;
            }
        }
        boolean as = conteoValor[14] > 0, dos = conteoValor[2] > 0, tres = conteoValor[3] > 0, cuatro = conteoValor[4] > 0, cinco = conteoValor[5] > 0;
        if (as && dos && tres && cuatro && cinco) return 5;
        return null;
    }
    private static List<List<Carta>> combinar5(List<Carta> cartas) {
        List<List<Carta>> res = new ArrayList<>();
        int n = cartas.size();
        for (int a = 0; a < n; a++)
            for (int b = a + 1; b < n; b++)
                for (int c = b + 1; c < n; c++)
                    for (int d = c + 1; d < n; d++)
                        for (int e = d + 1; e < n; e++) {
                            List<Carta> combo = new ArrayList<>();
                            combo.add(cartas.get(a));
                            combo.add(cartas.get(b));
                            combo.add(cartas.get(c));
                            combo.add(cartas.get(d));
                            combo.add(cartas.get(e));
                            res.add(combo);
                        }
        return res;
    }
    private static List<Integer> kickers(List<Integer> valoresDesc, List<Integer> excluidos, int cuantos) {
        List<Integer> ks = new ArrayList<>();
        for (int v : valoresDesc) {
            if (excluidos.contains(v)) continue;
            if (ks.size()<cuantos)ks.add(v);
        }
        return ks;
    }
    private static ResultadoMano evaluar5(List<Carta> mano5) {
        int[] conteoValor = new int[15];
        int[] conteoPalo = new int[4];
        List<Integer> valores = new ArrayList<>();
        for (Carta c:mano5){
            int v = c.valor().valor();
            valores.add(v);
            conteoValor[v]++;
            int p = mapPalo(c.mazo());
            conteoPalo[p]++;
        }
        valores.sort(Comparator.reverseOrder());
        boolean esColor=esColor(conteoPalo);
        Integer topEscalera=valorEscalera(valores, conteoValor);
        int cuatroIguales =- 1, tresIguales =- 1;
        List<Integer> pares = new ArrayList<>();
        for (int v = 14; v >= 2; v--){
            if (conteoValor[v] == 4) cuatroIguales = v;
            else if (conteoValor[v] == 3) tresIguales = v;
            else if(conteoValor[v] == 2) pares.add(v);
        }
        if (esColor && topEscalera != null) {
            boolean esImperial = (topEscalera == 14);
            return esImperial
                    ? new ResultadoMano(RankingMano.FLOR_IMPERIAL, List.of(14), mano5)
                    : new ResultadoMano(RankingMano.FLOR, List.of(topEscalera), mano5);
        }
        if (cuatroIguales != -1) {
            int kicker = primerKicker(valores, List.of(cuatroIguales));
            return new ResultadoMano(RankingMano.POKER, List.of(cuatroIguales, kicker), mano5);
        }
        if (tresIguales !=-1 && !pares.isEmpty()) {
            return new ResultadoMano(RankingMano.FULL,List.of(tresIguales, pares.getFirst()), mano5);
        }
        if (esColor) {
            return new ResultadoMano(RankingMano.COLOR, new ArrayList<>(valores), mano5);
        }
        if (topEscalera != null) {
            return new ResultadoMano(RankingMano.ESCALERA, List.of(topEscalera), mano5);
        }
        if (tresIguales != -1) {
            List<Integer> ks = kickers(valores, List.of(tresIguales),2);
            List<Integer> d = new ArrayList<>();
            d.add(tresIguales);
            d.addAll(ks);
            return new ResultadoMano(RankingMano.TERCIA, d, mano5);
        }
        if (pares.size() >= 2) {
            pares.sort(Comparator.reverseOrder());
            int k=primerKicker(valores, List.of(pares.get(0), pares.get(1)));
            return new ResultadoMano(RankingMano.DOS_PARES,List.of(pares.get(0), pares.get(1),k), mano5);
        }
        if (pares.size() == 1) {
            List<Integer> ks = kickers(valores, List.of(pares.getFirst()),3);
            List<Integer> d = new ArrayList<>();
            d.add(pares.getFirst());
            d.addAll(ks);
            return new ResultadoMano(RankingMano.PAR, d, mano5);
        }
        return new ResultadoMano(RankingMano.CARTA_ALTA, new ArrayList<>(valores), mano5);
    }
    public static ResultadoMano evaluarMejorMano(List<Carta> privadas, List<Carta> comunitarias) {
        List<Carta> todas = new ArrayList<>(privadas);
        todas.addAll(comunitarias);
        List<List<Carta>> combinaciones = combinar5(todas);
        ResultadoMano mejor = null;
        for (List<Carta> combo : combinaciones){
            ResultadoMano r = evaluar5(combo);
            if (mejor == null || compararResultados(r,mejor) > 0){
                mejor = r;
            }
        }
        return mejor;
    }
}