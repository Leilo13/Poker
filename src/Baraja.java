import java.util.List;
import java.util.ArrayList;
public class Baraja {
    private final List<Carta> cartas=new ArrayList<>();
    public Baraja(){
        for (Mazo m : Mazo.values()) {
            for (Valor v: Valor.values()){
                cartas.add(new Carta(v,m));
            }
        }
        barajar();
    }
    public void barajar(){
        if (cartas.isEmpty()||cartas.size()==1) return;
        int n= cartas.size();
            for (int i = n - 1; i > 0; i--) {
                int aux = (int) (Math.random() * (i + 1));
                Carta temp=cartas.get(i);
                cartas.set(i, cartas.get(aux));
                cartas.set(aux, temp);
            }
    }
    public Carta repartir(){
        return cartas.remove(cartas.size()-1);
    }
    public void imprimirBaraja(){
           for (Carta c:cartas){
                c.imprimirCarta();
            }
    }
}

