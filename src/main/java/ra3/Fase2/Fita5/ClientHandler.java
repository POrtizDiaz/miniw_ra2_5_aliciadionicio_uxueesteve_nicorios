package ra3.ProvaFase2.Fita5;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manejador de cliente en el servidor
 * - Ejecuta en su propio hilo
 * - Escucha mensajes del cliente
 * - Coordina broadcast con otros clientes
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private String idCliente;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.idCliente = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    @Override
    public void run() {
        try {
            // Configurar flujos
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            // Enviar mensaje de bienvenida
            salida.println("[SERVIDOR] Bienvenido al Minichat. Hay " + ServidorMinichat.getClientesConectados() + " clientes conectados.");
            ServidorMinichat.log("  Flujos configurados para: " + idCliente);

            String linea;
            while ((linea = entrada.readLine()) != null) {
                if (linea.equalsIgnoreCase("/salir")) {
                    ServidorMinichat.log("  " + idCliente + " solicita desconexión");
                    break;
                }

                // Registrar mensaje recibido
                String mensajeFormatado = "[" + LocalDateTime.now().format(formatter) + "] " + idCliente + ": " + linea;
                ServidorMinichat.log("  Mensaje recibido: " + mensajeFormatado);

                // Retransmitir a otros clientes
                ServidorMinichat.broadcast(mensajeFormatado, this);
            }

        } catch (IOException e) {
            ServidorMinichat.log(" Error en cliente " + idCliente + ": " + e.getMessage());
        } finally {
            cerrar();
        }
    }

    /**
     * Envía un mensaje a este cliente
     */
    public synchronized void enviarMensaje(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
        }
    }

    /**
     * Cierra conexión y recursos
     */
    private void cerrar() {
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null && !socket.isClosed()) socket.close();
            ServidorMinichat.desconectarCliente(this);
        } catch (IOException e) {
            ServidorMinichat.log(" Error al cerrar cliente " + idCliente);
        }
    }
}
