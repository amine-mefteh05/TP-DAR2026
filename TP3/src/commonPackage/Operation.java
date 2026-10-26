package commonPackage;

import java.io.Serializable;

public class Operation implements Serializable {

    private static final long serialVersionUID = 1L;

    private double op1;
    private double op2;
    private char operateur;
    private double resultat;
    private String erreur; // null si le calcul s'est bien passé

    public Operation(double op1, char operateur, double op2) {
        this.op1 = op1;
        this.operateur = operateur;
        this.op2 = op2;
    }

    public double getOp1() { return op1; }
    public double getOp2() { return op2; }
    public char getOperateur() { return operateur; }
    public double getResultat() { return resultat; }
    public String getErreur() { return erreur; }

    public void setResultat(double resultat) { this.resultat = resultat; }
    public void setErreur(String erreur) { this.erreur = erreur; }
}