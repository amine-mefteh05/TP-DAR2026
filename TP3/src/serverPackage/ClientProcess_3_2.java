package serverPackage;

import commonPackage.Operation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientProcess_3_2 extends Thread {

    // Compteur global partagé par tous les threads (ressource critique)
    private static int compteurGlobal = 0;

    private Socket socket;
    private int numero;

    public ClientProcess_3_2(Socket socket, int numero) {
        this.socket = socket;
        this.numero = numero;
    }

    // Section critique : un seul thread à la fois peut incrémenter et afficher
    private static synchronized void enregistrerOperation(int numeroClient, Operation op) {
        compteurGlobal++;
        System.out.println("Client n°" + numeroClient + " : "
                + op.getOp1() + " " + op.getOperateur() + " " + op.getOp2()
                + " = " + op.getResultat()
                + " | Compteur global = " + compteurGlobal);
    }

    private void calculer(Operation op) {
        double a = op.getOp1();
        double b = op.getOp2();

        switch (op.getOperateur()) {
            case '+':
                op.setResultat(a + b);
                break;
            case '-':
                op.setResultat(a - b);
                break;
            case '*':
                op.setResultat(a * b);
                break;
            case '/':
                if (b == 0) {
                    op.setErreur("Erreur : division par zéro");
                    return;
                }
                op.setResultat(a / b);
                break;
            default:
                op.setErreur("Opérateur invalide");
                return;
        }

        // L'opération a été calculée : on met à jour le compteur global
        enregistrerOperation(numero, op);
    }

    @Override
    public void run() {

        System.out.println("Traitement du client n°" + numero);

        try (
                // OutputStream d'abord (et flush) pour éviter un blocage des en-têtes
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            oos.flush();

            // Envoyer le numéro attribué par le serveur
            oos.writeObject("Vous êtes le client n°" + numero);
            oos.flush();

            while (true) {
                Object recu = ois.readObject();

                // Fin de communication demandée par le client
                if (recu instanceof String && ((String) recu).equalsIgnoreCase("quit")) {
                    break;
                }

                if (recu instanceof Operation) {
                    Operation op = (Operation) recu;
                    calculer(op);
                    oos.writeObject(op); // renvoi de l'objet avec résultat ou erreur
                    oos.flush();
                }
            }

            System.out.println("Client n°" + numero + " déconnecté.");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur avec le client n°" + numero + " : " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Erreur lors de la fermeture de la socket.");
            }
        }
    }
}