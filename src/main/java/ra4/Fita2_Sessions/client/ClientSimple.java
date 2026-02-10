package ra4.Fita2_Sessions.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientSimple {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            System.out.println("Connectat al servidor Fita 2!");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner sc = new Scanner(System.in);

            // Fil per rebre missatges
            // Fil per rebre missatges
            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                        // Si rebem BYE, sortim
                        if (line.equals("BYE")) {
                            System.out.println("Tancant l'aplicació...");
                            System.exit(0);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Servidor tancat.");
                    System.exit(0);
                }
            }).start();

            // Enviar missatges
            while (true) {
                String msg = sc.nextLine();
                out.println(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}