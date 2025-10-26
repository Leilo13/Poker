public class Main {
    public static void main(String[] args) {
        Mesa mesa=new Mesa();//Preflop
        mesa.imprimirMesa();
        mesa.rondaDeApuestas();

        mesa.avanzarRonda();//Flop
        mesa.imprimirMesa();
        mesa.rondaDeApuestas();

        mesa.avanzarRonda();//Turn
        mesa.imprimirMesa();
        mesa.rondaDeApuestas();

        mesa.avanzarRonda();//River
        mesa.imprimirMesa();
        mesa.rondaDeApuestas();

        mesa.avanzarRonda();//Showdown
        mesa.imprimirMesa();
        mesa.showdown();
    }
}