package ra5.Fase4.Fita2.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.SecretKey;

import ra5.Fase4.Fita2.security.Seguretat;

public class ClientSimple {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner sc = new Scanner(System.in);
            Seguretat seguretat = new Seguretat();
            SecretKey myAesKey = null;

            System.out.println("Connectant al servidor i negociant claus...");

            // --- HANDSHAKE ---
            String initMsg = in.readLine();
            if (initMsg != null && initMsg.startsWith("RSA_PUB ")) {
                // 1. Recibe la pública
                String pubKeyStr = initMsg.substring(8);
                PublicKey serverPubKey = seguretat.stringToPublicKey(pubKeyStr);

                // 2. Genera AES temporal y la cifra con RSA
                myAesKey = seguretat.generarClauAES();
                byte[] encAesKey = seguretat.xifrarRSA(myAesKey.getEncoded(), serverPubKey);

                // 3. Envía AES cifrada
                out.println("AES_KEY " + Base64.getEncoder().encodeToString(encAesKey));

                String handshakeStatus = in.readLine();
                if ("HANDSHAKE_OK".equals(handshakeStatus)) {
                    System.out.println("Connexió SEGURA establerta! Ja pots fer LOGIN.");
                } else {
                    System.out.println("Handshake fallit.");
                    System.exit(1);
                }
            } else {
                System.out.println("El servidor no suporta encriptació.");
                System.exit(1);
            }

            // Hacemos la clave final para poder usarla dentro de los hilos
            final SecretKey finalAesKey = myAesKey;

            // Fil de lectura
            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.startsWith("MSG ")) {
                            try {
                                String textDesxifrat = seguretat.desxifrarAES(line.substring(4), finalAesKey);
                                System.out.println(textDesxifrat);
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
                    System.out.println("Connexió tancada.");
                    System.exit(0);
                }
            }).start();

            // Fil d'escriptura
            while (true) {
                String input = sc.nextLine();

                if (input.toUpperCase().startsWith("MSG ")) {
                    String textAEscriure = input.substring(4);
                    try {
                        // EXCELENCIA: Añadimos el SHA-256 al mensaje antes de cifrar
                        String hash = seguretat.generarHashSHA256(textAEscriure);
                        String textAmbHash = textAEscriure + "|HASH:" + hash;

                        String textXifrat = seguretat.xifrarAES(textAmbHash, finalAesKey);
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
