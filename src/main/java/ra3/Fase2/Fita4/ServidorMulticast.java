package ra3.ProvaFase2.Fita4;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ServidorMulticast {
    public static void main(String[] args) {

        String grupo = "230.0.0.1";
        int puerto = 7000;


        try (MulticastSocket socket = new MulticastSocket()) {

            InetAddress direccionGrupo = InetAddress.getByName(grupo);
            
            System.out.println("Servidor Multicast iniciado en grupo " + grupo + ":" + puerto);
            System.out.println("Esperando clientes...");

            int contador = 0;
            while (true) {
                String mensaje = "Mensaje " + (++contador) + " del servidor multicast!";
                byte[] datos = mensaje.getBytes();

                DatagramPacket paquete =
                        new DatagramPacket(datos, datos.length, direccionGrupo, puerto);

                socket.send(paquete);

                System.out.println("[" + java.time.LocalTime.now() + "] " + mensaje);

                Thread.sleep(3000); // Enviar cada 3 segundos

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
