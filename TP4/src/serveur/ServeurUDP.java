package serveur;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

public class ServeurUDP {

    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket(null);
        socket.bind(new InetSocketAddress(1234));

        Set<SocketAddress> clients = new LinkedHashSet<>();
        byte[] buffer = new byte[2048];

        System.out.println("Serveur UDP lance sur le port 1234");

        while (true) {
            DatagramPacket paquet =
                    new DatagramPacket(buffer, buffer.length);

            socket.receive(paquet);

            SocketAddress expediteur = paquet.getSocketAddress();

            String message = new String(
                    paquet.getData(),
                    paquet.getOffset(),
                    paquet.getLength(),
                    StandardCharsets.UTF_8
            );

            // Ajouter le client s'il n'existe pas deja
            clients.add(expediteur);

            // Afficher l'adresse et le message
            System.out.println(
                    "Expediteur : " + expediteur
                            + " | Message : " + message
            );

            // Diffuser le message aux autres clients
            byte[] donnees =
                    message.getBytes(StandardCharsets.UTF_8);

            for (SocketAddress client : clients) {
                if (!client.equals(expediteur)) {

                    DatagramPacket diffusion =
                            new DatagramPacket(
                                    donnees,
                                    donnees.length,
                                    client
                            );

                    socket.send(diffusion);
                }
            }
        }
    }
}
