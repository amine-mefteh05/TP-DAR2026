package client;


import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientUDPDiffusion {

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();

        InetAddress adresseServeur =
                InetAddress.getByName("localhost");
        int portServeur = 1234;

        Scanner clavier = new Scanner(System.in);

        System.out.print("Votre nom : ");
        String nom = clavier.nextLine();

        // Thread de reception des messages diffuses
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
                            StandardCharsets.UTF_8);

                    System.out.println("\n" + message);
                    System.out.print("> ");

                } catch (Exception e) {
                    break;
                }
            }
        });

        reception.start();

        System.out.println("Chat de diffusion demarre.");
        System.out.println("Envoyez un premier message pour etre enregistre.");
        System.out.println("Tapez /quit pour quitter.");

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
                        adresseServeur,
                        portServeur);

                socket.send(paquet);
            }
        } finally {
            socket.close();
            clavier.close();
        }
    }
}
