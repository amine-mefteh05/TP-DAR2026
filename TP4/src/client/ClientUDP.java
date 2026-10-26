package client;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientUDP {

    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket();

        InetAddress serveur = InetAddress.getByName("localhost");
        int portServeur = 1234;

        Scanner clavier = new Scanner(System.in);

        System.out.print("Entrez votre nom : ");
        String nom = clavier.nextLine();

        // Thread de reception des messages
        Thread reception = new Thread(() -> {

            byte[] buffer = new byte[2048];

            while (!socket.isClosed()) {
                try {
                    DatagramPacket paquet =
                            new DatagramPacket(buffer, buffer.length);

                    socket.receive(paquet);

                    String message = new String(
                            paquet.getData(),
                            paquet.getOffset(),
                            paquet.getLength(),
                            StandardCharsets.UTF_8
                    );

                    System.out.println("\n" + message);
                    System.out.print("> ");

                } catch (SocketException e) {
                    break;
                } catch (Exception e) {
                    System.out.println(
                            "Erreur de reception : " + e.getMessage()
                    );
                }
            }
        });

        reception.start();

        System.out.println(
                "Chat demarre. Tapez /quit pour quitter."
        );

        try {
            while (true) {
                String texte = clavier.nextLine();

                if (texte.equalsIgnoreCase("/quit")) {
                    break;
                }

                String message = "[" + nom + "] : " + texte;

                byte[] donnees =
                        message.getBytes(StandardCharsets.UTF_8);

                DatagramPacket paquet = new DatagramPacket(
                        donnees,
                        donnees.length,
                        serveur,
                        portServeur
                );

                socket.send(paquet);
            }
        } finally {
            socket.close();
            clavier.close();
        }
    }
}
