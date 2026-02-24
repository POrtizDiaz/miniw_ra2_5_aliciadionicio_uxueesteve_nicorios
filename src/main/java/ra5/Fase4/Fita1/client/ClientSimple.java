package ra5.Fase4.Fita1.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import ra5.Fase4.Fita1.security.Seguretat;

public class ClientSimple {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            System.out.println("Connectat al servidor xifrat (Fita 1)!");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner sc = new Scanner(System.in);

            Seguretat seguretat = new Seguretat();

            // Fil de lectura
            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.startsWith("MSG ")) {
                            String dadesXifrades = line.substring(4);
                            try {
                                String textNet = seguretat.desxifrar(dadesXifrades);
                                System.out.println(textNet);
                            } catch (Exception e) {
                                System.out.println("[Error desxifrant un missatge]");
                            }
                        } else if (line.equals("BYE")) {
                            System.out.println("Tancant l'aplicació...");
                            System.exit(0);
                        } else {
                            System.out.println(line);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Connexió tancada per part del servidor.");
                    System.exit(0);
                }
            }).start();

            // Fil d'escriptura
            while (true) {
                String input = sc.nextLine();

                if (input.toUpperCase().startsWith("MSG ")) {
                    String textAEscriure = input.substring(4);
                    try {
                        String textXifrat = seguretat.xifrar(textAEscriure);
                        out.println("MSG " + textXifrat);
                    } catch (Exception e) {
                        System.out.println("Error a l'encriptar.");
                    }
                } else {
                    out.println(input);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
