package ra2;
import java.util.LinkedList;
import java.util.Queue;

public class MessageBuffer {

    private final Queue<String> buffer = new LinkedList<>();

    // Método para añadir mensajes al buffer
    public synchronized void put(String msg) {
        buffer.add(msg);
        notifyAll(); // Avisamos a los hilos que estén esperando
    }

    // Método para extraer mensajes del buffer
    public synchronized String take() throws InterruptedException {
        while (buffer.isEmpty()) {
            wait(); // Esperamos hasta que alguien ponga un mensaje
        }
        return buffer.poll();
    }
}
