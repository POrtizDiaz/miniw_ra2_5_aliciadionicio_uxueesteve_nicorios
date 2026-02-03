package ra2;

public class ExecutorServer {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("\n== SecureChat Fase 1. Provant motor ExecutorServer: ==\n");

        MessageBuffer buffer = new MessageBuffer();

        Thread processador = new Thread(new Processador(buffer));
        //declarem messagebuffer i el processador
        processador.start();
        //iniciem el processador
        Thread[] clients = new Thread[3];
        for (int i = 0; i < 3; i++) {
            clients[i] = new Thread(new Client(buffer, i + 1));
            clients[i].start();
        }
        //creem 3 clients i els iniciem

        for (Thread client : clients) {
            client.join();
        }
        //esperem que acabin els clients
        processador.interrupt(); // atura el processador quan els clients acaben

        System.out.println("\n== FI DEL MOTOR ==\n");
    }

}
