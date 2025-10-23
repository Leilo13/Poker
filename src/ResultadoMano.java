import java.util.List;
import java.util.ArrayList;
public class ResultadoMano {
    private final TipoJugada tipo;
    private final List<Integer> desempate;
    public ResultadoMano(TipoJugada tipo, List<Integer> desempate){
        this.tipo=tipo;
        this.desempate=desempate;
    }
    public TipoJugada getTipo() {return tipo;}
    public List<Integer> getDesempate() {return desempate;}
}