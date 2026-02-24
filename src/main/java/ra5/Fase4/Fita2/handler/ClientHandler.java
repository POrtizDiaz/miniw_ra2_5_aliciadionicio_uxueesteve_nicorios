package ra5.Fase4.Fita2.handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import ra5.Fase4.Fita2.protocol.Protocol;
import ra5.Fase4.Fita2.server.ServidorEscalable;
import ra5.Fase4.Fita2.storage.UserStorage;
import ra5.Fase4.Fita2.security.Seguretat;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Protocol protocol;
    private ServidorEscalable server;
    private Seguretat seguretat = new Seguretat();

    public String username = null;
    public String rol = "USER"; // Por defecto
    public SecretKey aesKey = null; // Clave AES única de este cliente

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

            // --- HANDSHAKE RSA/AES ---
            // 1. Enviamos clave pública RSA al cliente
            String pubKeyStr = seguretat.publicKeyToString(server.rsaKeys.getPublic());
            out.println("RSA_PUB " + pubKeyStr);

            // 2. Esperamos la clave AES cifrada del cliente
            String resposta = in.readLine();
            if (resposta != null && resposta.startsWith("AES_KEY ")) {
                String clauXifradaB64 = resposta.substring(8);
                byte[] clauXifrada = Base64.getDecoder().decode(clauXifradaB64);

                // Desencriptamos la clave AES con la privada del servidor
                byte[] clauAESDescifrada = seguretat.desxifrarRSA(clauXifrada, server.rsaKeys.getPrivate());
                this.aesKey = new SecretKeySpec(clauAESDescifrada, "AES");

                out.println("HANDSHAKE_OK");
                System.out.println("Clau AES negociada amb èxit per un nou client.");
            } else {
                out.println("ERROR Handshake fallit");
                disconnect();
                return;
            }

            // --- BUCLE NORMAL DE CHAT ---
            String line;
            while ((line = in.readLine()) != null) {
                protocol.handle(line, this, server);
            }

        } catch (Exception e) {
            System.err.println("Client desconnectat o error de xarxa: " + e.getMessage());
        } finally {
            if (this.username != null) {
                UserStorage.getInstance().removeUser(this.username);
                System.out.println("Neteja: Usuari " + this.username + " eliminat.");
            }
            server.removeClient(this);
            disconnect();
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
