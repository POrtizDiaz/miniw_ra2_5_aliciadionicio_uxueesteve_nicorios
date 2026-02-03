package ra3.Fase2.Fita6;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServidorMinichat {
    private static final int PUERTO = 5000;
    private static ServerSocket serverSocket;
    private static final List<ClientHandler> clientes = Collections.synchronizedList(new ArrayList<>());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PUERTO);
            log("Servidor iniciat en port " + PUERTO);
            log("Esperant connexions...\n");

            while (true) {
                Socket socket = serverSocket.accept();
                String clientIP = socket.getInetAddress().getHostAddress();
                log("Nova connexió des de: " + clientIP);

                ClientHandler handler = new ClientHandler(socket);
                clientes.add(handler);
                new Thread(handler).start();

                log("Clients connectats: " + clientes.size() + "\n");
            }
        } catch (IOException e) {
            log("Error en servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retransmet un missatge a tots els clients excepte al remitent
     */
    public static void broadcast(String mensaje, ClientHandler remitente) {
        synchronized (clientes) {
            for (ClientHandler cliente : clientes) {
                if (cliente != remitente) {
                    cliente.enviarMensaje(mensaje);
                }
            }
        }
    }

    /**
     * Elimina un client de la llista quan es desconnecta
     */
    public static void desconectarCliente(ClientHandler cliente) {
        clientes.remove(cliente);
        log("Client desconnectat. Connectats: " + clientes.size() + "\n");
    }

    /**
     * Retorna la llista de clients (per LIST)
     */
    public static List<ClientHandler> getClients() {
        return clientes;
    }

    /**
     * Log amb timestamp
     */
    public static synchronized void log(String mensaje) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[" + timestamp + "] " + mensaje);
    }
}
