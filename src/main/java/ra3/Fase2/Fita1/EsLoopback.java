package ra3.ProvaFase2.Fita1;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class EsLoopback {
    public static void main(String[] args) {
        try {
            // Obtenir la IP local (localhost)
            InetAddress localHost = InetAddress.getLocalHost();

            // Mostrar el nom i l’adreça IP
            System.out.println("Nom del host: " + localHost.getHostName());
            System.out.println("Adreça IP: " + localHost.getHostAddress());

            // Comprovar si és loopback
            if (localHost.isLoopbackAddress()) {
                System.out.println("És una adreça loopback.");
            } else {
                System.out.println("No és una adreça loopback.");
            }
        } catch (UnknownHostException e) {
            System.out.println("No s’ha pogut obtenir la IP local.");
        }
    }
}
