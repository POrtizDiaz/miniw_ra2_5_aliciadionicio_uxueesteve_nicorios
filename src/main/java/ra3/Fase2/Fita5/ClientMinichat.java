package ra3.ProvaFase2.Fita5;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Cliente de Minichat
 * - Conecta al servidor
 * - Envía mensajes
 * - Recibe mensajes en paralelo (hilo receptor)
 */
public class ClientMinichat {
    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
    private String nombre;

    public ClientMinichat(String host, int puerto, String nombre) throws IOException {
        this.nombre = nombre;
        this.socket = new Socket(host, puerto);
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.salida = new PrintWriter(socket.getOutputStream(), true);

        System.out.println(" Conectado al servidor en " + host + ":" + puerto);
        System.out.println("Nombre: " + nombre);
        System.out.println("Escribe /salir para desconectarte\n");
    }

    /**
     * Inicia los hilos de lectura y escritura
     */
    public void iniciar() {
        // Hilo para recibir mensajes
        Thread hiloReceptor = new Thread(this::recibirMensajes);
        hiloReceptor.setDaemon(true);
        hiloReceptor.start();

        // Hilo para enviar mensajes (en main)
        enviarMensajes();
    }

    /**
     * Hilo que recibe mensajes del servidor continuamente
     */
    private void recibirMensajes() {
        try {
            String linea;
            while ((linea = entrada.readLine()) != null) {
                System.out.println(linea);
            }
        } catch (IOException e) {
            System.out.println(" Desconectado del servidor");
        }
    }

    /**
     * Envía mensajes al servidor (desde main)
     */
    private void enviarMensajes() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String mensaje = scanner.nextLine();

            if (mensaje.equalsIgnoreCase("/salir")) {
                salida.println("/salir");
                cerrar();
                break;
            }

            if (!mensaje.trim().isEmpty()) {
                salida.println("[" + nombre + "] " + mensaje);
            }
        }
    }

    /**
     * Cierra la conexión
     */
    private void cerrar() {
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println(" Desconexión completada");
        } catch (IOException e) {
            System.out.println(" Error al cerrar: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            String host = "localhost";
            int puerto = 5000;
            String nombre = (args.length > 0) ? args[0] : "Usuario";

            ClientMinichat cliente = new ClientMinichat(host, puerto, nombre);
            cliente.iniciar();

        } catch (ConnectException e) {
            System.out.println(" No se puede conectar al servidor. ¿Está ejecutándose?");
            System.out.println("  java ServidorMinichat");
        } catch (IOException e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }
}
