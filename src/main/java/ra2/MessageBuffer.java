package ra2;

public class MessageBuffer {
    private String dada = null;
    private boolean existeixDada = false;

    public synchronized void posarMissatge(String novaDada) throws InterruptedException {
        while (existeixDada) {
            wait();
        }
        dada = novaDada;
        existeixDada = true;
        notifyAll();
    }

    public synchronized String treureMissatge() throws InterruptedException {
        while (!existeixDada) {
            wait();
        }
        String resultat = dada;
        existeixDada = false;
        notifyAll();
        return resultat;
    }
}
