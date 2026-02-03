package ra3.Fase2.Fita2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTCP {
    public static void main(String[] args) {
        int port = 5000; // port fix on escoltarà el servidor
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor escoltant al port " + port);

            // accepta connexions de clients
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());

            // canals d’entrada i sortida
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String missatge;
            while ((missatge = in.readLine()) != null) {
                System.out.println("Rebut: " + missatge);
                // retorna el mateix missatge (eco)
                out.println("Eco: " + missatge);
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
