package ra4.Fase3.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    // De momento el value es Object (Paso 3 lo cambiamos a ClientSession)
    private final ConcurrentHashMap<String, Object> sessionsByUser = new ConcurrentHashMap<>();

    /**
     * Intenta registrar un usuario.
     * @return true si se ha podido registrar, false si el nombre ya estaba en uso.
     */
    public boolean login(String username, Object session) {
        if (username == null) return false;

        String clean = username.trim();
        if (clean.isEmpty()) return false;

        // putIfAbsent es atómico: evita que dos hilos metan el mismo username a la vez
        return sessionsByUser.putIfAbsent(clean, session) == null;
    }

    /**
     * Elimina un usuario (por QUIT o desconexión).
     */
    public void logout(String username) {
        if (username == null) return;
        sessionsByUser.remove(username.trim());
    }

    /**
     * Devuelve lista de usuarios conectados (snapshot).
     */
    public List<String> listUsers() {
        return new ArrayList<>(sessionsByUser.keySet());
    }

    // Devuelve las "sesiones" conectadas (snapshot).
    public List<Object> sessions() {
        return new ArrayList<>(sessionsByUser.values());
    }

    //Comprueba si un usuario existe.
    public boolean isLogged(String username) {
        if (username == null) return false;
        return sessionsByUser.containsKey(username.trim());
    }
}
