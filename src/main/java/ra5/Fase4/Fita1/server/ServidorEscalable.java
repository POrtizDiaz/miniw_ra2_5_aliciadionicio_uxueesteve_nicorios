package ra5.Fase4.Fita1.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ra5.Fase4.Fita1.handler.ClientHandler;

public class ServidorEscalable {

    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    
    // Pool de 50 fils per escalabilitat
    private ExecutorService pool = Executors.newFixedThreadPool(50);

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor Fase 4 (Fita 1) escoltant al port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler ch = new ClientHandler(socket, this);
                clients.add(ch);
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
