package ra4.Fita2_Sessions.handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import ra4.Fita2_Sessions.protocol.Protocol;
import ra4.Fita2_Sessions.server.SecureChatServer;
import ra4.Fita2_Sessions.storage.UserStorage;

public class ClientHandler extends Thread {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Protocol protocol;
    private SecureChatServer server;

    public String username = null;

    public ClientHandler(Socket socket, SecureChatServer server) {
        this.socket = socket;
        this.server = server;
        this.protocol = new Protocol();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                protocol.handle(line, this, server);
            }

        } catch (Exception e) {
            // Client desconnectat abruptament
        } finally {
            // Neteja de sessió (Fita 2)
            if (this.username != null) {
                UserStorage.getInstance().removeUser(this.username);
                System.out.println("Neteja: Usuari " + this.username + " eliminat.");
            }
            server.removeClient(this);
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
    }

    public void send(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    // Mètode per forçar la desconnexió des del protocol
    public void disconnect() {
        try {
            socket.close(); // Això trenca el bucle while del run()
        } catch (Exception e) {
            // Ignorem errors de tancament
        }
    }
}

