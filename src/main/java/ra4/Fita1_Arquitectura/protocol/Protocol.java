package ra4.Fita1_Arquitectura.protocol;

import ra4.Fita1_Arquitectura.handler.ClientHandler;
import ra4.Fita1_Arquitectura.server.SecureChatServer;
import ra4.Fita1_Arquitectura.storage.UserStorage;

public class Protocol {

    private UserStorage storage = UserStorage.getInstance();

    public void handle(String line, ClientHandler ch, SecureChatServer server) {

        String[] parts = line.split(" ", 2);
        String cmd  = parts[0].toUpperCase();
        String args = (parts.length > 1) ? parts[1] : null;

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
    System.out.println("DEBUG: S'ha cridat al mètode handleLogin amb l'usuari: " + username);
}
    private void handleMsg(String msg, ClientHandler ch, SecureChatServer server) {}
    private void handleList(ClientHandler ch) {}
    private void handleQuit(ClientHandler ch) {}

    
}
