package ra2;

public class Processador implements Runnable {
    private final MessageBuffer buffer;

    public Processador(MessageBuffer buffer) {
        this.buffer = buffer;
    }

    //Creem classe processador amb messagebuffer

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
    //El processador treu missatges del buffer i els processa amb un delay de 300ms entre cada missatge
}
