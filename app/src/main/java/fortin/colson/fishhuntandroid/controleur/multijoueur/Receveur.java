package fortin.colson.fishhuntandroid.controleur.multijoueur;

import java.io.BufferedReader;
import java.io.IOException;

import fortin.colson.fishhuntandroid.modele.Partie;

/**
 * Cette classe reçoit les commandes du serveur et met à jour le controleur de la partie.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Receveur implements Runnable{

    private final int ATTAQUE_POISSON_NORMAL_RECU = 150;
    private final int ATTAQUE_POISSON_SPECIAL_RECU = 151;
    private final int MISE_A_JOUR_SCORE_RECU = 160;
    private final int DECONNEXION_JOUEUR_RECU = 190;
    private final int CONNEXION_JOUEUR_RECU = 191;

    private ControleurPartieMulti controleur;
    private Object cadenasDrapeau;
    private ConnexionServeur connexion;
    private BufferedReader input;

    //Drapeau signifiant si le thread doit continuer à écouter le serveur.
    private boolean partieEnCours;

    /**
     * Construit le Receveur de commande avec le controleur de jeu.
     */
    public Receveur(ControleurPartieMulti controleur) {
        this.controleur = controleur;
        partieEnCours = true;
        cadenasDrapeau = new Object();

        try {
            connexion = ConnexionServeur.getInstance();
            input = connexion.getInput();
        } catch (IOException exception) {
            controleur.afficherErreur();
        }
    }

    /**
     * Accesseur du drapeau signifiant si la partie est toujours en cours.
     * @return  Vrai si la partie est en cours, faux, sinon.
     */
    public boolean estPartieEnCours() {
        boolean retour;
        synchronized (cadenasDrapeau) {
            retour = partieEnCours;
        }
        return retour;
    }

    /**
     * Mutateur du drapeau signifiant si la partie est toujours en cours.
     * @param partieEnCours Vrai si la partie est en cours, faux, sinon.
     */
    public void setPartieEnCours(boolean partieEnCours) {
        synchronized (cadenasDrapeau) {
            this.partieEnCours = partieEnCours;
        }
    }

    /**
     * Méthode redéfinie qui s'éxécute lorsque "Thread start".
     */
    @Override
    public void run() {

        try {
            while (estPartieEnCours()) {

                int code = input.read();
                if(code == -1)
                    throw new IOException();

                switch (input.read()) {

                    case ATTAQUE_POISSON_NORMAL_RECU:

                        String attaquantNormal = input.readLine();
                        if (attaquantNormal == null)
                            throw new IOException();

                        controleur.attaquePoissonNormal(attaquantNormal);

                        break;

                    case ATTAQUE_POISSON_SPECIAL_RECU:

                        String attaquantSpecial = input.readLine();
                        if (attaquantSpecial == null)
                            throw new IOException();

                        controleur.attaquePoissonSpecial(attaquantSpecial);

                        break;

                    case MISE_A_JOUR_SCORE_RECU:

                        String nomScore = input.readLine();
                        if (nomScore == null)
                            throw new IOException();

                        int score = input.read();
                        if (score == -1)
                            throw new IOException();

                        controleur.miseAJourScore(nomScore, score);

                        break;

                    case DECONNEXION_JOUEUR_RECU:

                        String nomJoueurDeconnexion = input.readLine();
                        if (nomJoueurDeconnexion == null)
                            throw new IOException();

                        controleur.deconnexionJoueur(nomJoueurDeconnexion);

                        break;

                    case CONNEXION_JOUEUR_RECU:

                        String pseudoNouveauJoueur = input.readLine();
                        if(pseudoNouveauJoueur == null)
                            throw new IOException();

                        controleur.connexionJoueur(pseudoNouveauJoueur);

                        break;
                }
            }

        } catch (IOException ioException) {
            controleur.afficherErreur();
        }
    }
}
