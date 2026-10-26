package serveur;


import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class ServeurUDPBidirectionnel {

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(
                new InetSocketAddress(1234));

        AtomicReference<SocketAddress> dernierClient =
                new AtomicReference<>();

        System.out.println("Serveur bidirectionnel lance sur le port 1234");

        // Thread pour envoyer les reponses depuis le serveur
        Thread emission = new Thread(() -> {
            Scanner clavier = new Scanner(System.in);

            while (!socket.isClosed()) {
                String texte = clavier.nextLine();
                SocketAddress destinataire = dernierClient.get();

                if (destinataire == null) {
                    System.out.println("Aucun client n'a encore envoye de message.");
                    continue;
                }

                byte[] donnees = ("[Serveur] : " + texte)
                        .getBytes(StandardCharsets.UTF_8);

                try {
                    DatagramPacket reponse = new DatagramPacket(
                            donnees, donnees.length, destinataire);
                    socket.send(reponse);
                } catch (Exception e) {
                    System.out.println("Erreur d'envoi : " + e.getMessage());
                }
            }
        });

        emission.setDaemon(true);
        emission.start();

        // Reception des messages des clients
        byte[] buffer = new byte[2048];

        while (true) {
            DatagramPacket paquet =
                    new DatagramPacket(buffer, buffer.length);

            socket.receive(paquet);

            dernierClient.set(paquet.getSocketAddress());

            String message = new String(
                    paquet.getData(),
                    paquet.getOffset(),
                    paquet.getLength(),
                    StandardCharsets.UTF_8);

            System.out.println(
                    "Message de " + paquet.getSocketAddress()
                            + " : " + message);
        }
    }
}
