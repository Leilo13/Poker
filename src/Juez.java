import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class Juez {

    public ResultadoMano evaluarMejorMano(List<Carta> privadas, List<Carta> comunitarias){
        List<Carta> todas=new ArrayList<>();
        todas.addAll(privadas);
        todas.addAll(comunitarias);
        List<List<Carta>> combinaciones=combinar5(todas);//Se generan todas las combinaciones
        ResultadoMano mejor=null;
        for (List<Carta> combo:combinaciones){
            ResultadoMano r = evaluar5(combo);
            if(mejor==null||compararResultados(r,mejor)>0){
                mejor=r;
            }
        }
        return mejor;
    }

    //Método para generar todas las combinaciones de 5 cartas usando una lista
    private List<List<Carta>> combinar5(List<Carta> cartas){
        List<List<Carta>> res=new ArrayList<>();
        int n= cartas.size();
        for(int a=0;a<n;a++)
            for(int b=a+1;b<n;b++)
                for(int c=b+1;c<n;c++)
                    for(int d=c+1;d<n;d++)
                        for(int e=d+1;e<n;e++){
                            List<Carta> combo=new ArrayList<>();
                            combo.add(cartas.get(a));
                            combo.add(cartas.get(b));
                            combo.add(cartas.get(c));
                            combo.add(cartas.get(d));
                            combo.add(cartas.get(e));
                            res.add(combo);
                        }
        return res;
    }

    //Comparar 2 resultados. >0 es un mejor resultado, <0 es uno peor y 0 es el mismo valor
    public int compararResultados(ResultadoMano a, ResultadoMano b){
        int cmpTipo=Integer.compare(a.getTipo().ordinal(),b.getTipo().ordinal());
        if (cmpTipo!=0) return cmpTipo;//No hay necesidad de desempate, una es mejor que la otra y punto
        List<Integer> da=a.getDesempate();
        List<Integer> db=b.getDesempate();
        int len=Math.min(da.size(), db.size());
        for (int i=0;i<len;i++){
            int cmp=Integer.compare(da.get(i),db.get(i));
            if (cmp!=0) return cmp;
        }
        return Integer.compare(da.size(), db.size());//Esto NUNCA pasa, pero por si acaso
    }

    //Método para comparar una mano de exactamente 5 cartas y devolver su ResultadoMano

    private ResultadoMano evaluar5(List<Carta> mano5){
        int[] conteoValor=new int[15];
        int[] conteoPalo=new int[4];
        List<Integer> valores=new ArrayList<>();

        for (Carta c:mano5){
            int v=c.getValor().getValor();
            valores.add(v);
            conteoValor[v]++;
            int p=mapPalo(c.getMazo()); //Hacer este helper
            conteoPalo[p]++;
        }

        valores.sort(Comparator.reverseOrder());
        boolean esColor=esColor(conteoPalo);
        Integer topEscalera=valorEscalera(valores, conteoValor);//Devuelve la mayor carta de la escalera o null

        //Método para jugadas de cartas repetidas
        int cuatroIguales=-1, tresIguales=-1;
        List<Integer> pares=new ArrayList<>();
        for (int v=14;v>=2;v--){
            if (conteoValor[v]==4) cuatroIguales=v;
            else if (conteoValor[v]==3) tresIguales=v;
            else if(conteoValor[v]==2) pares.add(v);
        }

        //Método para diferentes tipos de Flor
        if (esColor&&topEscalera!=null){
            boolean esImperial=(topEscalera==14); //Si estoy aquí adentro ya sé que tiene color corrido, y si su mejor carta es el as, es una flor imperial
            if (esImperial) return new ResultadoMano(TipoJugada.FLOR_IMPERIAL, List.of(14), mano5);
            return new ResultadoMano(TipoJugada.FLOR, List.of(topEscalera), mano5);
        }

        if (cuatroIguales!=-1){ //Tiene Póker
            int kicker=primerKicker(valores, List.of(cuatroIguales));
            return new ResultadoMano(TipoJugada.POKER, List.of(cuatroIguales, kicker), mano5);
        }

        if (tresIguales!=-1&&!pares.isEmpty()){//Tiene Full
            return new ResultadoMano(TipoJugada.FULL,List.of(tresIguales, pares.get(0)), mano5);
        }

        if (esColor){//Es color, duh
            return new ResultadoMano(TipoJugada.COLOR, new ArrayList<>(valores), mano5);
        }

        if (topEscalera!=null){//Tiene escalera, pero no flor
            return new ResultadoMano(TipoJugada.ESCALERA, List.of(topEscalera), mano5);
        }

        if (tresIguales!=-1){//Tiene tercia pero no par
            List<Integer> ks=kickers(valores, List.of(tresIguales),2);
            List<Integer> d=new ArrayList<>();
            d.add(tresIguales); d.addAll(ks);
            return new ResultadoMano(TipoJugada.TERCIA, d, mano5);
        }

        if (pares.size()>=2){//Tiene dos pares
            pares.sort(Comparator.reverseOrder());
            int k=primerKicker(valores, List.of(pares.get(0), pares.get(1)));
            return new ResultadoMano(TipoJugada.DOS_PARES,List.of(pares.get(0), pares.get(1),k), mano5);
        }

        if (pares.size()==1) {//Tiene solo un par
            List<Integer> ks=kickers(valores, List.of(pares.get(0)),3);
            List<Integer> d=new ArrayList<>();
            d.add(pares.get(0)); d.addAll(ks);
            return new ResultadoMano(TipoJugada.PAR, d, mano5);
        }

        //Si llegaste hasta acá tienes carta alta y de cabrones deberías dejar de jugar
        return new ResultadoMano(TipoJugada.CARTA_ALTA, new ArrayList<>(valores), mano5);
    }

    private int mapPalo(Mazo m){
        return switch (m) {
            case TREBOLES -> 0;
            case CORAZONES -> 1;
            case PICAS -> 2;
            case DIAMANTES -> 3;
            default -> -1;
        };
    }

    private boolean esColor(int[] conteoPalo){
        for (int count:conteoPalo) if (count == 5) return true;
        return false;
    }

    private Integer valorEscalera(List<Integer> valoresDesc, int[] conteoValor) {
        //Necesitamos manejar A como 14 y como 0, le damos prioridad al 14 por ser el mejor juego
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

        //Escalera de A-5
        boolean a = conteoValor[14] > 0;
        boolean dos = conteoValor[2] > 0, tres = conteoValor[3] > 0, cuatro = conteoValor[4] > 0, cinco = conteoValor[5] > 0;
        if (a && dos && tres && cuatro && cinco) return 5;
        return null;
    }

    private int primerKicker(List<Integer> valoresDesc, List<Integer> excluidos){
        for(int v:valoresDesc){
            if(!excluidos.contains(v)) return v;
        }
        return 0;
    }

    private List<Integer> kickers(List<Integer> valoresDesc, List<Integer> excluidos, int cuantos){
        List<Integer> ks=new ArrayList<>();
        for(int v:valoresDesc){
            if(excluidos.contains(v)) continue;
            if (ks.size()<cuantos)ks.add(v);
        }
        return ks;
    }
}