package ra3.ProvaFase2.Fita3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ServidorUDP {
    public static void main(String[] args) {

        int port = 7001; // puerto donde escucha el servidor

        try (DatagramSocket socket = new DatagramSocket(port)) {

            System.out.println("Servidor UDP escuchando en el puerto " + port);

            // buffer donde se guardan los datos que llegan
            byte[] buffer = new byte[1024];

            while (true) {

                // paquete donde se guardará lo recibido
                DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);

                // ESPERA (bloquea) hasta que llegue un paquete
                socket.receive(paqueteRecibido);

                // convertimos los bytes a texto
                String mensaje = new String(
                        paqueteRecibido.getData(),
                        0,
                        paqueteRecibido.getLength());

                System.out.println("Mensaje recibido: " + mensaje);

                // respuesta tipo ECO
                String respuesta = "Eco UDP: " + mensaje;
                byte[] datosRespuesta = respuesta.getBytes();

                DatagramPacket paqueteRespuesta = new DatagramPacket(
                        datosRespuesta,
                        datosRespuesta.length,
                        paqueteRecibido.getAddress(),
                        paqueteRecibido.getPort());

                socket.send(paqueteRespuesta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
