package clientPackage;

import commonPackage.Operation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client_3_2 {

    static final int PORT = 1234;

    static final Pattern SYNTAXE = Pattern.compile(
            "^\\s*(-?\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(-?\\d+(?:\\.\\d+)?)\\s*$"
    );

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Adresse IP du serveur (Entrée = localhost) : ");
        String hote = sc.nextLine().trim();
        if (hote.isEmpty()) {
            hote = "localhost";
        }

        try (
                Socket socket = new Socket(hote, PORT);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            oos.flush();

            System.out.println("Je suis un client connecté.");

            // Recevoir et afficher le numéro attribué par le serveur
            System.out.println((String) ois.readObject());

            while (true) {
                System.out.print("Donnez une opération (ex : 55 * 25) ou quit : ");
                String saisie = sc.nextLine().trim();

                if (saisie.equalsIgnoreCase("quit")) {
                    oos.writeObject("quit");
                    oos.flush();
                    break;
                }

                Matcher m = SYNTAXE.matcher(saisie);

                if (!m.matches()) {
                    System.out.println("Syntaxe invalide, rien n'est envoyé.");
                    continue;
                }

                // Construire et envoyer l'objet Operation
                Operation op = new Operation(
                        Double.parseDouble(m.group(1)),
                        m.group(2).charAt(0),
                        Double.parseDouble(m.group(3))
                );
                oos.writeObject(op);
                oos.flush();

                // Recevoir l'objet Operation avec le résultat
                Operation reponse = (Operation) ois.readObject();

                if (reponse.getErreur() != null) {
                    System.out.println(reponse.getErreur());
                } else {
                    System.out.println("Résultat : " + reponse.getResultat());
                }
            }

            System.out.println("Fin de la communication.");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur côté client : " + e.getMessage());
        }
    }
}