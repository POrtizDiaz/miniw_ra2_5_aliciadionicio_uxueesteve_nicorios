package ra4.Fita1_Arquitectura.handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import ra4.Fita1_Arquitectura.protocol.Protocol;
import ra4.Fita1_Arquitectura.server.SecureChatServer;

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
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                protocol.handle(line, this, server);
            }

        } catch (Exception e) {
            System.out.println("Client desconnectat");
        } finally {
            server.removeClient(this);
        }
    }

    public void send(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
}
