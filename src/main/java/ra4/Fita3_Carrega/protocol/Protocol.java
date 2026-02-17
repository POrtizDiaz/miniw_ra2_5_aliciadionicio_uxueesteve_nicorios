package ra4.Fita3_Carrega.protocol;

import ra4.Fita3_Carrega.handler.ClientHandler;
import ra4.Fita3_Carrega.server.ServidorEscalable;
import ra4.Fita3_Carrega.storage.UserStorage;

public class Protocol {

    private UserStorage storage = UserStorage.getInstance();

    public void handle(String line, ClientHandler ch, ServidorEscalable server) {

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

    private void handleMsg(String msg, ClientHandler ch, ServidorEscalable server) {
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
        ch.disconnect();
    }
}