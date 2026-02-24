package ra5.Fase4.Fita1.protocol;

import ra5.Fase4.Fita1.handler.ClientHandler;
import ra5.Fase4.Fita1.server.ServidorEscalable;
import ra5.Fase4.Fita1.storage.UserStorage;
import ra5.Fase4.Fita1.security.Seguretat;

public class Protocol {

    private UserStorage storage = UserStorage.getInstance();

    public void handle(String line, ClientHandler ch, ServidorEscalable server) {
        if (line == null || line.trim().isEmpty()) return;

        String[] parts = line.split(" ", 2);
        String cmd  = parts[0].toUpperCase();
        String args = (parts.length > 1) ? parts[1] : "";

        switch (cmd) {
            case "LOGIN":
                handleLogin(args, ch);
                break;
            case "MSG":
                handleMsg(args, ch, server);
                break;
            case "LIST":
                handleList(ch);
                break;
            case "QUIT":
                handleQuit(ch);
                break;
            default:
                ch.send("ERROR Comanda desconeguda");
        }
    }

    private void handleLogin(String username, ClientHandler ch) {
        if (ch.username != null) {
            ch.send("ERROR Ja estàs identificat com a " + ch.username);
            return;
        }
        if (username == null || username.trim().isEmpty()) {
            ch.send("ERROR Nom d'usuari invàlid");
            return;
        }
        if (storage.exists(username)) {
            ch.send("ERROR L'usuari " + username + " ja existeix");
            return;
        }

        storage.addUser(username, ch);
        ch.username = username;
        ch.send("OK Benvingut " + username);
        System.out.println("Login correcte: " + username);
    }

    private void handleMsg(String missatgeXifrat, ClientHandler ch, ServidorEscalable server) {
        if (ch.username == null) {
            ch.send("ERROR Cal fer LOGIN primer");
            return;
        }
        if (missatgeXifrat == null || missatgeXifrat.trim().isEmpty()) {
            ch.send("ERROR Missatge buit");
            return;
        }

        try {
            Seguretat seguretat = new Seguretat();
            
            // 1. DESXIFREM el que envia el client
            String msgClar = seguretat.desxifrar(missatgeXifrat);
            
            // Mostrem per consola el que s'ha desxifrat (Per la captura de pantalla!)
            System.out.println("MSG Desxifrat al servidor -> " + ch.username + ": " + msgClar);
            
            // 2. Preparem el format de sortida
            String broadcastStr = ch.username + ": " + msgClar;
            
            // 3. TORNEM A XIFRAR
            String encryptedBroadcast = seguretat.xifrar(broadcastStr);
            
            // 4. Enviem a tothom amb la comanda MSG
            server.broadcast("MSG " + encryptedBroadcast, ch);
            
        } catch (Exception e) {
            System.err.println("Error de desxifrat amb l'usuari " + ch.username);
            ch.send("ERROR Clau incorrecta o missatge corrupte");
        }
    }

    private void handleList(ClientHandler ch) {
        if (ch.username == null) {
            ch.send("ERROR Cal fer LOGIN primer");
            return;
        }
        var users = storage.getUserList();
        ch.send("OK Llista d'usuaris: " + users.toString());
    }

    private void handleQuit(ClientHandler ch) {
        ch.send("BYE");
        ch.disconnect();
    }
}
