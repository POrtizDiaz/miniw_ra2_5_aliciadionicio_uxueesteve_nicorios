package ra4.Fita3_Carrega.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ra4.Fita3_Carrega.handler.ClientHandler;

public class ServidorEscalable {

    private ServerSocket serverSocket;
    // Llista per fer broadcast
    private List<ClientHandler> clients = new ArrayList<>();

    // FITA 3: Pool de fils (Límit de 50 connexions simultànies)
    private ExecutorService pool = Executors.newFixedThreadPool(50);

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor ESCALABLE (Fita 3) escoltant al port " + port);

            while (true) {
                Socket socket = serverSocket.accept();

                // Creem el handler
                ClientHandler ch = new ClientHandler(socket, this);
                clients.add(ch);

                // IMPORTANT FITA 3: No fem ch.start(), ho enviem al pool
                pool.execute(ch);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }

    public void broadcast(String msg, ClientHandler sender) {
        for (ClientHandler ch : clients) {
            if (ch != sender) {
                ch.send(msg);
            }
        }
    }

    public void removeClient(ClientHandler ch) {
        clients.remove(ch);
    }

    public static void main(String[] args) {
        ServidorEscalable server = new ServidorEscalable();
        server.start(5000);
    }
}