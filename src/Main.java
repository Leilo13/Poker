public class Main {
    public static void main(String[] args) {
        Mesa mesa = new Mesa();
        while (!mesa.partidaTerminada()) {
            while (mesa.getRonda() != Ronda.SHOWDOWN && !mesa.partidaTerminada()) {
                mesa.imprimirDebug();
                mesa.rondaDeApuestas();
                if (mesa.partidaTerminada()) break;
                mesa.avanzarRonda();
            }
            if (!mesa.partidaTerminada() && mesa.getRonda() == Ronda.SHOWDOWN) {
                mesa.avanzarRonda();
            }
            mesa.nuevaMano();
        }
        System.out.println("¡Ganador final: " + mesa.getGanadorFinal() + "!");
    }
}