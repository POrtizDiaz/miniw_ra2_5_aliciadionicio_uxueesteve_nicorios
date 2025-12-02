package ra2;

public class Processador implements Runnable {
    private final MessageBuffer buffer;

    public Processador(MessageBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String missatge = buffer.treureMissatge();
                System.out.println(java.time.LocalTime.now() + " [PROCESSADOR] Processant -> " + missatge);
                Thread.sleep(300);
            }
        } catch (InterruptedException e) {
            System.out.println("Processador interromput");
        }
    }
}
