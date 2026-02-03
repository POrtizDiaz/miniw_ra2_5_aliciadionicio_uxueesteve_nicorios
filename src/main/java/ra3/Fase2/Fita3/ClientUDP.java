package ra3.ProvaFase2.Fita3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientUDP {
    public static void main(String[] args) {

        String host = "localhost";
        int port = 7001;

        try (DatagramSocket socket = new DatagramSocket()) {

            String mensaje = "Hola servidor UDP!";
            byte[] datos = mensaje.getBytes();

            InetAddress direccion = InetAddress.getByName(host);

            // creamos paquete a enviar
            DatagramPacket paquete = new DatagramPacket(datos, datos.length, direccion, port);

            // enviamos
            socket.send(paquete);

            System.out.println("Mensaje enviado al servidor UDP.");

            // ahora esperamos respuesta
            byte[] buffer = new byte[1024];

            DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);

            socket.receive(respuesta);

            String textoRespuesta = new String(
                    respuesta.getData(),
                    0,
                    respuesta.getLength());

            System.out.println("Respuesta del servidor: " + textoRespuesta);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
