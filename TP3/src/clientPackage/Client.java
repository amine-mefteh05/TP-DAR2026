
package clientPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {

    static final Pattern SYNTAXE = Pattern.compile(
            "^\\s*(-?\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(-?\\d+(?:\\.\\d+)?)\\s*$"
    );

    public static void main(String[] args) {

        try (
                Socket socket = new Socket("localhost", 1234);
                Scanner sc = new Scanner(System.in);
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter pw = new PrintWriter(
                        socket.getOutputStream(), true
                )
        ) {
            System.out.println("Je suis un client connecté.");

            // Recevoir et afficher le numéro attribué par le serveur
            System.out.println(br.readLine());

            while (true) {
                System.out.print(
                        "Donnez une opération (ex : 55 * 25) ou quit : "
                );

                String saisie = sc.nextLine().trim();

                if (saisie.equalsIgnoreCase("quit")) {
                    pw.println("quit");
                    break;
                }

                Matcher m = SYNTAXE.matcher(saisie);

                if (!m.matches()) {
                    System.out.println(
                            "Syntaxe invalide, rien n'est envoyé."
                    );
                    continue;
                }

                // Envoyer les trois éléments au serveur
                pw.println(m.group(1));
                pw.println(m.group(2));
                pw.println(m.group(3));

                // Recevoir le résultat
                String reponse = br.readLine();

                if (reponse == null) {
                    System.out.println("Le serveur a fermé la connexion.");
                    break;
                }

                System.out.println("Résultat : " + reponse);
            }

            System.out.println("Fin de la communication.");

        } catch (IOException e) {
            System.out.println(
                    "Erreur côté client : " + e.getMessage()
            );
        }
    }
}
