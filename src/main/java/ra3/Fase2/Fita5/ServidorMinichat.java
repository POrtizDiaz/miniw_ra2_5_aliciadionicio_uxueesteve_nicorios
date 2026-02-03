package ra3.ProvaFase2.Fita5;

import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servidor de Minichat multihilo
 * - Acepta múltiples clientes
 * - Retransmite mensajes a todos los conectados
 * - Gestiona conexiones/desconexiones
 */
public class ServidorMinichat {
    private static final int PUERTO = 5000;
    private static ServerSocket serverSocket;
    private static final List<ClientHandler> clientes = Collections.synchronizedList(new ArrayList<>());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PUERTO);
            log(" Servidor iniciado en puerto " + PUERTO);
            log("Esperando conexiones...\n");

            while (true) {
                Socket socket = serverSocket.accept();
                String clientIP = socket.getInetAddress().getHostAddress();
                log(" Nueva conexión desde: " + clientIP);

                // Crear hilo para manejar el cliente
                ClientHandler handler = new ClientHandler(socket);
                clientes.add(handler);
                new Thread(handler).start();

                log("  Clientes conectados: " + clientes.size() + "\n");
            }
        } catch (IOException e) {
            log(" Error en servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retransmite un mensaje a todos los clientes excepto al remitente
     */
    public static void broadcast(String mensaje, ClientHandler remitente) {
        for (ClientHandler cliente : clientes) {
            if (cliente != remitente) {
                cliente.enviarMensaje(mensaje);
            }
        }
    }

    /**
     * Elimina un cliente de la lista cuando se desconecta
     */
    public static void desconectarCliente(ClientHandler cliente) {
        clientes.remove(cliente);
        log(" Cliente desconectado. Conectados: " + clientes.size() + "\n");
    }

    /**
     * Obtiene la cantidad de clientes conectados
     */
    public static int getClientesConectados() {
        return clientes.size();
    }

    /**
     * Log con timestamp
     */
    public static synchronized void log(String mensaje) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[" + timestamp + "] " + mensaje);
    }
}
