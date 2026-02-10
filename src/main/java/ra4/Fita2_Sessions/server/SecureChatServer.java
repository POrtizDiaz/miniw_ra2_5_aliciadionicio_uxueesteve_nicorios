package ra4.Fita2_Sessions.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ra4.Fita2_Sessions.handler.ClientHandler;

public class SecureChatServer {

    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor Fita 2 escoltant al port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler ch = new ClientHandler(socket, this);
                clients.add(ch);
                ch.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        SecureChatServer server = new SecureChatServer();
        server.start(5000);
    }
}
