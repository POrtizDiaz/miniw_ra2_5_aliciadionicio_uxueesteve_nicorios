package ra4.Fita3_Carrega.client;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class LoadTest {

    private static final int NUM_BOTS = 100; // Llancem 100 bots

    public static void main(String[] args) {
        System.out.println("Iniciant prova de càrrega amb " + NUM_BOTS + " clients...");

        for (int i = 0; i < NUM_BOTS; i++) {
            final int botId = i;
            new Thread(() -> {
                try {
                    // Petit retard per simular arribada escalonada
                    Thread.sleep(new Random().nextInt(1000));

                    Socket socket = new Socket("localhost", 5000);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    // Protocol del Bot
                    String name = "Bot-" + botId;
                    out.println("LOGIN " + name);
                    Thread.sleep(200);
                    out.println("MSG Hola, soc un bot d'estrès!");
                    Thread.sleep(1000); // Es queda 1 segon connectat
                    out.println("QUIT");

                    socket.close();
                    System.out.println(name + " ha acabat.");

                } catch (Exception e) {
                    System.err.println("Error en Bot-" + botId);
                }
            }).start();
        }
    }
}