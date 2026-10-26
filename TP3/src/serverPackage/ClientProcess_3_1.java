
package serverPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientProcess_3_1 extends Thread {

    private Socket socket;
    private int numero;

    public ClientProcess_3_1(Socket socket, int numero) {
        this.socket = socket;
        this.numero = numero;
    }

    @Override
    public void run() {

        System.out.println("Traitement du client n°" + numero);

        try (
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter pw = new PrintWriter(
                        socket.getOutputStream(), true
                )
        ) {
            // Envoyer le numéro attribué par le serveur
            pw.println("Vous êtes le client n°" + numero);

            String ligne;

            while ((ligne = br.readLine()) != null) {

                // Le client demande la fin de la communication
                if (ligne.equalsIgnoreCase("quit")) {
                    break;
                }

                // Lire les deux opérandes et l'opérateur
                String operande1 = ligne;
                String operateur = br.readLine();
                String operande2 = br.readLine();

                if (operateur == null || operande2 == null) {
                    break;
                }

                try {
                    double a = Double.parseDouble(operande1);
                    double b = Double.parseDouble(operande2);
                    double resultat;

                    switch (operateur) {
                        case "+":
                            resultat = a + b;
                            break;
                        case "-":
                            resultat = a - b;
                            break;
                        case "*":
                            resultat = a * b;
                            break;
                        case "/":
                            if (b == 0) {
                                pw.println("Erreur : division par zéro");
                                continue;
                            }
                            resultat = a / b;
                            break;
                        default:
                            pw.println("Opérateur invalide");
                            continue;
                    }

                    pw.println(resultat);

                } catch (NumberFormatException e) {
                    pw.println("Erreur : opérandes invalides");
                }
            }

            System.out.println("Client n°" + numero + " déconnecté.");

        } catch (IOException e) {
            System.out.println(
                    "Erreur avec le client n°" + numero + " : "
                            + e.getMessage()
            );
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Erreur lors de la fermeture de la socket.");
            }
        }
    }
}
