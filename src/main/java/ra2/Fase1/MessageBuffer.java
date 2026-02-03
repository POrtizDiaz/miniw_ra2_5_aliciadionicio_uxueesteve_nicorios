package ra2;

public class MessageBuffer {
    private String dada = null;
    private boolean existeixDada = false;

    //Creem el buffer amb una dada i un boolean per controlar si hi ha dada o no

    public synchronized void posarMissatge(String novaDada) throws InterruptedException {
        while (existeixDada) {
            wait();
        }
        dada = novaDada;
        existeixDada = true;
        notifyAll();
    }
    //Metode per posar missatge al buffer i espera si ja hi ha dada
    public synchronized String treureMissatge() throws InterruptedException {
        while (!existeixDada) {
            wait();
        }
        String resultat = dada;
        existeixDada = false;
        notifyAll();
        return resultat;
    }
    //Metode per treure missatge del buffer i espera si no hi ha dada
}
