package serveur;


import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ServeurUDPDiffusion {

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(
                new InetSocketAddress(1234));

        Set<SocketAddress> clients = new CopyOnWriteArraySet<>();
        byte[] buffer = new byte[2048];

        System.out.println("Serveur de diffusion lance sur le port 1234");

        while (true) {
            DatagramPacket paquet =
                    new DatagramPacket(buffer, buffer.length);

            socket.receive(paquet);

            SocketAddress expediteur = paquet.getSocketAddress();

            // Enregistrer le client sans doublon
            clients.add(expediteur);

            String message = new String(
                    paquet.getData(),
                    paquet.getOffset(),
                    paquet.getLength(),
                    StandardCharsets.UTF_8);

            System.out.println(
                    "Message de " + expediteur + " : " + message);

            byte[] donnees = message.getBytes(StandardCharsets.UTF_8);

            // Diffuser aux autres clients
            for (SocketAddress client : clients) {
                if (!client.equals(expediteur)) {
                    DatagramPacket paquetDiffusion =
                            new DatagramPacket(
                                    donnees,
                                    donnees.length,
                                    client);

                    socket.send(paquetDiffusion);
                }
            }
        }
    }
}
