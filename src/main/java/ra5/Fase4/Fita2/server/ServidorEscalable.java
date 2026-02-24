package ra5.Fase4.Fita2.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ra5.Fase4.Fita2.handler.ClientHandler;
import ra5.Fase4.Fita2.security.Seguretat;

public class ServidorEscalable {

    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private ExecutorService pool = Executors.newFixedThreadPool(50);

    // Claves RSA del Servidor
    public KeyPair rsaKeys;
    private Seguretat seguretat = new Seguretat();

    public ServidorEscalable() {
        try {
            System.out.println("Generant parell de claus RSA del Servidor...");
            this.rsaKeys = seguretat.generarParellClausRSA();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor Fase 4 (Fita 2 - RSA/AES) escoltant al port " + port);

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

    // El servidor hace broadcast del mensaje cifrándolo para CADA cliente con su
    // AES
    public void broadcast(String msgClar, ClientHandler sender) {
        for (ClientHandler ch : clients) {
            if (ch != sender && ch.username != null && ch.aesKey != null) {
                try {
                    String xifrat = seguretat.xifrarAES(msgClar, ch.aesKey);
                    ch.send("MSG " + xifrat);
                } catch (Exception e) {
                    System.err.println("Error enviant missatge xifrat a " + ch.username);
                }
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
