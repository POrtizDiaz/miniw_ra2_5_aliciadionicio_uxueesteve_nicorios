package ra4.Fita3_Carrega.handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import ra4.Fita3_Carrega.protocol.Protocol;
import ra4.Fita3_Carrega.server.ServidorEscalable;
import ra4.Fita3_Carrega.storage.UserStorage;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Protocol protocol;
    private ServidorEscalable server;

    public String username = null;

    public ClientHandler(Socket socket, ServidorEscalable server) {
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
            // Client desconnectat
        } finally {
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
        if (out != null)
            out.println(msg);
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (Exception e) {
        }
    }
}