package ra2;

public class Client implements Runnable {
    private final MessageBuffer buffer;
    private final int id;

    public Client(MessageBuffer buffer, int id) {
        this.buffer = buffer;
        this.id = id;
    }

    //Creem classe client amb messagebuffer i id

    @Override
    public void run() {
        try {
            for (int j = 1; j <= 5; j++) {
                String missatge = "Missatge " + j + " de Client " + id;
                System.out.println(java.time.LocalTime.now() + " [CLIENT " + id + "] Enviant -> " + missatge);
                buffer.posarMissatge(missatge);
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            System.out.println("Client " + id + " interromput");
        }
    }
    //El client envia 5 missatges al buffer amb un delay de 500ms entre cada missatge
}
