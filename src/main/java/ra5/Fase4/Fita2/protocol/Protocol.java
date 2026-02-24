package ra5.Fase4.Fita2.protocol;

import ra5.Fase4.Fita2.handler.ClientHandler;
import ra5.Fase4.Fita2.security.Seguretat;
import ra5.Fase4.Fita2.server.ServidorEscalable;
import ra5.Fase4.Fita2.storage.UserStorage;

public class Protocol {

    private UserStorage storage = UserStorage.getInstance();
    private Seguretat seguretat = new Seguretat();

    public void handle(String line, ClientHandler ch, ServidorEscalable server) {
        if (line == null || line.trim().isEmpty())
            return;

        String[] parts = line.split(" ", 2);
        String cmd = parts[0].toUpperCase();
        String args = (parts.length > 1) ? parts[1].trim() : "";

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
            ch.send("ERROR Ja estàs identificat");
            return;
        }
        // VALIDACIÓN: Solo letras y números, entre 3 y 15 chars
        if (!username.matches("^[a-zA-Z0-9]{3,15}$")) {
            ch.send("ERROR Nom d'usuari invàlid (només lletres/números, 3-15 caràcters)");
            return;
        }
        if (storage.exists(username)) {
            ch.send("ERROR L'usuari " + username + " ja existeix");
            return;
        }

        // EXCELENCIA (Rols): El primer en entrar es ADMIN
        if (storage.getUserList().isEmpty()) {
            ch.rol = "ADMIN";
        } else {
            ch.rol = "USER";
        }

        storage.addUser(username, ch);
        ch.username = username;
        ch.send("OK Benvingut " + username + " [Rol: " + ch.rol + "]");
    }

    private void handleMsg(String missatgeXifrat, ClientHandler ch, ServidorEscalable server) {
        if (ch.username == null) {
            ch.send("ERROR Cal fer LOGIN primer");
            return;
        }

        try {
            // 1. Desciframos con la clave AES DEL CLIENTE
            String desxifrat = seguretat.desxifrarAES(missatgeXifrat, ch.aesKey);

            // 2. Extraemos el Hash (formato: "texto|HASH:hashBase64")
            String[] parts = desxifrat.split("\\|HASH:");
            if (parts.length != 2)
                throw new Exception("Format de missatge corrupte");

            String textClar = parts[0];
            String hashRebut = parts[1];

            // 3. VALIDACIÓN DE INTEGRIDAD (SHA-256)
            String hashCalculat = seguretat.generarHashSHA256(textClar);
            if (!hashCalculat.equals(hashRebut)) {
                System.err.println("ALERTA: Integritat fallida de l'usuari " + ch.username);
                ch.send("ERROR El missatge ha estat modificat pel camí!");
                return;
            }

            System.out.println("MSG Segur (" + ch.username + "): " + textClar);

            // 4. Broadcast del mensaje verificado (el Servidor se encarga de cifrarlo para
            // el resto)
            String broadcastStr = ch.username + ": " + textClar;
            server.broadcast(broadcastStr, ch);

        } catch (Exception e) {
            ch.send("ERROR Validació o xifrat incorrectes");
            // Si envía datos muy corruptos, lo expulsamos por seguridad
            ch.disconnect();
        }
    }

    private void handleList(ClientHandler ch) {
        if (ch.username == null) {
            ch.send("ERROR Cal fer LOGIN primer");
            return;
        }
        // EXCELENCIA: Solo los ADMIN pueden ver la lista
        if (!ch.rol.equals("ADMIN")) {
            ch.send("ERROR Permís denegat. Només els ADMIN poden veure la llista.");
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
