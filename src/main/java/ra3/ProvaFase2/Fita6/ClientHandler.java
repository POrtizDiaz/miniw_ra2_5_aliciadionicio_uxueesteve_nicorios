package ra3.ProvaFase2.Fita6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manejador de client en el servidor
 * - Executa en el seu propi fil
 * - Implementa protocol: LOGIN, MSG, LIST, QUIT
 * - Valida peticions bàsiques
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private String idCliente;
    private String username; // nom d'usuari després de LOGIN
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.idCliente = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        this.username = null;
    }

    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            salida.println("[SERVIDOR] Benvingut al Minichat amb protocol.");
            salida.println("[SERVIDOR] Comandes: LOGIN <nom>, MSG <text>, LIST, QUIT");
            ServidorMinichat.log("Flujos configurats per: " + idCliente);

            String linea;
            while ((linea = entrada.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue;
                }

                ServidorMinichat.log("Rebut de " + idCliente + ": " + linea);
                processarComanda(linea);
            }

        } catch (IOException e) {
            ServidorMinichat.log("Error en client " + idCliente + ": " + e.getMessage());
        } finally {
            cerrar();
        }
    }

    private void processarComanda(String linea) {
        String[] parts = linea.split(" ", 2);
        String cmd = parts[0].toUpperCase();
        String args = (parts.length > 1) ? parts[1].trim() : null;

        switch (cmd) {
            case "LOGIN":
                manejarLogin(args);
                break;
            case "MSG":
                manejarMsg(args);
                break;
            case "LIST":
                manejarList();
                break;
            case "QUIT":
                manejarQuit();
                break;
            default:
                salida.println("ERROR Comanda desconeguda");
        }
    }

    private void manejarLogin(String args) {
        if (username != null) {
            salida.println("ERROR Ja estàs loguejat com " + username);
            return;
        }
        if (args == null || args.isBlank()) {
            salida.println("ERROR Falta nom d'usuari. Ús: LOGIN <nom>");
            return;
        }
        username = args.trim();
        salida.println("OK LOGIN " + username);
        ServidorMinichat.log("Usuari loguejat: " + username + " (" + idCliente + ")");
    }

    private void manejarMsg(String args) {
        if (username == null) {
            salida.println("ERROR Has de fer LOGIN abans de MSG");
            return;
        }
        if (args == null || args.isBlank()) {
            salida.println("ERROR Missatge buit. Ús: MSG <text>");
            return;
        }

        String mensajeFormatado = "[" + LocalDateTime.now().format(formatter) + "] "
                + username + ": " + args;
        ServidorMinichat.log("Broadcast: " + mensajeFormatado);
        ServidorMinichat.broadcast(mensajeFormatado, this);
    }

    private void manejarList() {
        if (username == null) {
            salida.println("ERROR Has de fer LOGIN abans de LIST");
            return;
        }

        StringBuilder sb = new StringBuilder("USERS");
        for (ClientHandler c : ServidorMinichat.getClients()) {
            if (c.username != null) {
                sb.append(" ").append(c.username);
            }
        }
        salida.println(sb.toString());
    }

    private void manejarQuit() {
        salida.println("OK QUIT");
        ServidorMinichat.log("Client " + idCliente + " (user=" + username + ") demana QUIT");
        cerrar();
    }

    /**
     * Envia un missatge a aquest client
     */
    public synchronized void enviarMensaje(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
        }
    }

    /**
     * Tanca connexió i recursos
     */
    private void cerrar() {
        try {
            if (entrada != null)
                entrada.close();
            if (salida != null)
                salida.close();
            if (socket != null && !socket.isClosed())
                socket.close();
            ServidorMinichat.desconectarCliente(this);
        } catch (IOException e) {
            ServidorMinichat.log("Error al tancar client " + idCliente);
        }
    }
}
