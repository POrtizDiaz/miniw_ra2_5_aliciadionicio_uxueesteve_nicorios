package ra4.Fita2_Sessions.protocol;

import ra4.Fita2_Sessions.handler.ClientHandler; // <--- ASEGURA QUE ES FITA 2
import ra4.Fita2_Sessions.server.SecureChatServer;
import ra4.Fita2_Sessions.storage.UserStorage;

public class Protocol {

    private UserStorage storage = UserStorage.getInstance();

    // CORRECCIÓN 1: El tipo de ClientHandler debe ser el de Fita 2, no Fita 3
    public void handle(String line, ClientHandler ch, SecureChatServer server) {

        if (line == null || line.trim().isEmpty())
            return;

        String[] parts = line.split(" ", 2);
        String cmd = parts[0].toUpperCase();
        String args = (parts.length > 1) ? parts[1] : "";

        switch (cmd) {
            case "LOGIN":
                handleLogin(args, ch);
                break;
            case "MSG":
                // CORRECCIÓN 2: Cambiado 'clientHandler' por 'ch'
                handleMsg(args, ch, server);
                break;
            case "LIST":
                handleList(ch);
                break;
            case "QUIT":
                // CORRECCIÓN 3: Cambiado 'clientHandler' por 'ch'
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

    private void handleMsg(String msg, ClientHandler ch, SecureChatServer server) {
        if (ch.username == null) {
            ch.send("ERROR Cal fer LOGIN primer");
            return;
        }
        if (msg == null || msg.trim().isEmpty()) {
            ch.send("ERROR Missatge buit");
            return;
        }
        server.broadcast(ch.username + ": " + msg, ch);
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
        ch.disconnect(); // Esto requiere que tengas el método disconnect en ClientHandler
    }
}