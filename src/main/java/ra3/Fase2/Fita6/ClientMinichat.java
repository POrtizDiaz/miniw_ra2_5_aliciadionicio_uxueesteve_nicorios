package ra3.Fase2.Fita6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client de Minichat amb protocol
 * - Conecta al servidor
 * - Envia comandes: LOGIN, MSG, LIST, QUIT
 * - Rep missatges en paral·lel
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

        System.out.println("Connectat al servidor en " + host + ":" + puerto);
        System.out.println("Nom local: " + nombre);
        System.out.println("Recorda: primer LOGIN " + nombre);
        System.out.println("Comandes: LOGIN <nom>, MSG <text>, LIST, QUIT\n");
    }

    public void iniciar() {
        Thread hiloReceptor = new Thread(this::recibirMensajes);
        hiloReceptor.setDaemon(true);
        hiloReceptor.start();

        enviarMensajes();
    }

    private void recibirMensajes() {
        try {
            String linea;
            while ((linea = entrada.readLine()) != null) {
                System.out.println(linea);
            }
        } catch (IOException e) {
            System.out.println("Desconnectat del servidor");
        }
    }

    private void enviarMensajes() {
        Scanner scanner = new Scanner(System.in);

        // Opcional: enviar LOGIN automàtic al principi
        if (nombre != null && !nombre.isBlank()) {
            salida.println("LOGIN " + nombre);
        }

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("QUIT")) {
                salida.println("QUIT");
                cerrar();
                break;
            }

            // Si l'usuari escriu només text, el convertim en MSG <text>
            if (!input.trim().isEmpty()
                    && !input.toUpperCase().startsWith("LOGIN")
                    && !input.toUpperCase().startsWith("MSG")
                    && !input.toUpperCase().startsWith("LIST")
                    && !input.toUpperCase().startsWith("QUIT")) {

                salida.println("MSG " + input);
            } else {
                salida.println(input);
            }
        }
    }

    private void cerrar() {
        try {
            if (entrada != null)
                entrada.close();
            if (salida != null)
                salida.close();
            if (socket != null && !socket.isClosed())
                socket.close();
            System.out.println("Desconnexió completada");
        } catch (IOException e) {
            System.out.println("Error al tancar: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Introdueix el teu nom: ");
            String nombre = sc.nextLine();

            String host = "localhost";
            int puerto = 5000;

            ClientMinichat cliente = new ClientMinichat(host, puerto, nombre);
            cliente.iniciar();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}