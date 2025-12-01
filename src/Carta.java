// Una carta almacena el valor de su simbolo y el mazo al que pertenece
public record Carta (Valor valor, Mazo mazo){
    @Override
    public String toString() {
        if (valor == null || mazo == null) return "[??]";
        String simbolo = mazo.simbolo();
        String color = (simbolo.equals("♥") || simbolo.equals("♦")) ? "\u001B[31m" : "\u001B[37m";
        return String.format("%s[%s%s]%s", color, valor.etiqueta(), simbolo, "\u001B[0m");
    }
}
