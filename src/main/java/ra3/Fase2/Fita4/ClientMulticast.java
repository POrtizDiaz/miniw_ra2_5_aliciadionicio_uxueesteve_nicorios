package ra3.ProvaFase2.Fita4;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ClientMulticast {
    public static void main(String[] args) {

        String grupo = "230.0.0.1";
        int puerto = 7000;

        try (MulticastSocket socket = new MulticastSocket(puerto)) {

            InetAddress direccionGrupo = InetAddress.getByName(grupo);

            socket.joinGroup(direccionGrupo);

            System.out.println("Cliente unido al grupo multicast " + grupo + ":" + puerto);
            System.out.println("Esperando mensajes del servidor...");

            byte[] buffer = new byte[1024];

            while (true) {

                DatagramPacket paquete =
                        new DatagramPacket(buffer, buffer.length);

                socket.receive(paquete);

                String mensaje = new String(
                        paquete.getData(),
                        0,
                        paquete.getLength()
                );

                System.out.println("[" + java.time.LocalTime.now() + "] Mensaje recibido: " + mensaje);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
