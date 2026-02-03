package ra3.ProvaFase2.Fita2;

import java.io.*;
import java.net.*;

public class ClientTCP {
    public static void main(String[] args) {
        String host = "localhost"; // IP o nom del servidor
        int port = 5000;

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connectat al servidor " + host + ":" + port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            // enviem un missatge
            pw.println("Hola servidor!");
            // rebem la resposta
            String resposta = in.readLine();
            System.out.println("Resposta del servidor: " + resposta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
