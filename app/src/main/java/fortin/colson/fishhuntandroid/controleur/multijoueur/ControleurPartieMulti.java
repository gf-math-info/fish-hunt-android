package fortin.colson.fishhuntandroid.controleur.multijoueur;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import fortin.colson.fishhuntandroid.controleur.ControleurPartie;
import fortin.colson.fishhuntandroid.modele.PartieMulti;
import fortin.colson.fishhuntandroid.modele.PlanJeu;
import fortin.colson.fishhuntandroid.modele.Record;
import fortin.colson.fishhuntandroid.modele.entite.Bulle;
import fortin.colson.fishhuntandroid.modele.entite.Projectile;
import fortin.colson.fishhuntandroid.modele.entite.poisson.Poisson;

/**
 * Cette classe représente le contrôleur d'une partie en mode multijoueur.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class ControleurPartieMulti extends ControleurPartie {

    private final int ATTAQUE_POISSON_NORMAL_ENVOIE = 50;
    private final int ATTAQUE_POISSON_SPECIAL_ENVOIE = 51;
    private final int MISE_A_JOUR_SCORE_ENVOIE = 60;

    private final Object cadenasDonneesAffichage;
    private ArrayList<Record> scores;
    private ConnexionServeur connexion;
    private PrintWriter output;
    private Receveur receveur;

    /*
    Variables utilisées pour l'affichage des messages en mode multijoueur.
    */
    private boolean erreurConnexion, attaqueEnCours, deconnexionEnCours, connexionEnCours,
            attaqueSpeciale, lancementAttaque;
    private double deltaMessage;

    //Variables pour afficher le score des joueurs.
    private int indexScores;
    private double deltaScores;

    private String nomAttaquant, nomDeconnexion, nomConnexion, msgMultijoueurAfficher;

    private final int TEMPS_MESSAGE_AUTRE = 2;
    private final int TEMPS_MESSAGE_SCORES = 1;

    /**
     * Construit un contrôleur de jeu avec la largeur et la hauteur du plan de
     * jeu, ainsi que la classe dessinable.
     */
    public ControleurPartieMulti(double largeur, double hauteur) {
        super(largeur, hauteur);
        partie = new PartieMulti(this);
        planJeu.setPartie(partie);
        cadenasDonneesAffichage = new Object();

        scores = new ArrayList<>();
        indexScores = 0;

        receveur = new Receveur(this);

        Thread scoreServeurThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    connexion = ConnexionServeur.getInstance();
                    output = connexion.getOutput();

                    //On récupère le score des joueurs en ligne.
                    int nombreJoueurs = connexion.getInput().read();
                    if(nombreJoueurs == -1)
                        throw new IOException();

                    String pseudoJoueur;
                    int scoreJoueur;
                    for(int i = 0; i < nombreJoueurs; i++) {
                        pseudoJoueur = connexion.getInput().readLine();
                        if(pseudoJoueur == null)
                            throw new IOException();

                        scoreJoueur = connexion.getInput().read();
                        if(scoreJoueur == -1)
                            throw new IOException();

                        scores.add(new Record(pseudoJoueur, scoreJoueur));
                    }

                    Collections.sort(scores);

                    new Thread(receveur).start();

                } catch (IOException ioException) {
                    afficherErreur();
                }
            }
        });

        scoreServeurThread.start();
        try {
            scoreServeurThread.join();//C'est voulu.
        } catch (InterruptedException e) {}
    }

    @Override
    public void actualiser(double deltaTemps) {

        super.actualiser(deltaTemps);

        synchronized (cadenasDonneesAffichage) {

            if(attaqueEnCours) {

                deltaMessage += deltaTemps;
                if(deltaMessage >= TEMPS_MESSAGE_AUTRE) {
                    attaqueEnCours = false;
                } else {
                    msgMultijoueurAfficher = nomAttaquant + " vient de vous envoyer un poisson " +
                            (attaqueSpeciale ? "spécial." : "normal.");
                }

            } else if(lancementAttaque) {

                deltaMessage += deltaTemps;
                if(deltaMessage >= TEMPS_MESSAGE_AUTRE) {
                    lancementAttaque = false;
                } else {
                    msgMultijoueurAfficher = "Vous avez envoyer un poisson " +
                            (attaqueSpeciale ? "spécial." : "normal.");
                }

            } else if(connexionEnCours) {

                deltaMessage += deltaTemps;
                if(deltaMessage >= TEMPS_MESSAGE_AUTRE) {
                    connexionEnCours = false;
                } else {
                    msgMultijoueurAfficher = nomConnexion + " vient de se connecter.";
                }

            } else if(deconnexionEnCours) {

                deltaMessage += deltaTemps;
                if(deltaMessage >= TEMPS_MESSAGE_AUTRE) {
                    deconnexionEnCours = false;
                } else {
                    msgMultijoueurAfficher = nomDeconnexion + " vient de se déconnecter.";
                }

            } else {

                deltaScores += deltaTemps;
                if(deltaScores >= TEMPS_MESSAGE_SCORES) {
                    indexScores++;
                    indexScores %= scores.size();
                }

                msgMultijoueurAfficher = (indexScores + 1) + ". " + scores.get(indexScores);

            }
        }
    }

    /**
     * Envoie au serveur un signal signifiant que le joueur "attaque" les autres joueurs avec un poisson normal.
     */
    public void attaquePoissonNormal() {
        synchronized (cadenasDonneesAffichage) {
            lancementAttaque = true;
            attaqueSpeciale = false;
            attaqueEnCours = false;
            connexionEnCours = false;
            deconnexionEnCours = false;
            deltaMessage = 0;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                output.write(ATTAQUE_POISSON_NORMAL_ENVOIE);
                output.flush();
            }
        }).start();
    }

    /**
     * Envoie au serveur un signal signifiant que le joueur "attque" les autres joueurs avec un poisson spécial.
     */
    public void attaquePoissonSpecial() {
        synchronized (cadenasDonneesAffichage) {
            lancementAttaque = true;
            attaqueSpeciale = true;
            attaqueEnCours = false;
            connexionEnCours = false;
            deconnexionEnCours = false;
            deltaMessage = 0;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                output.write(ATTAQUE_POISSON_SPECIAL_ENVOIE);
                output.flush();
            }
        }).start();
    }

    /**
     * Envoie un signal au serveur signifiant que le score du joueur a changé.
     */
    public void miseAJourScore() {
        final int scoreAEnvoyer = partie.getScore();

        new Thread(new Runnable() {
            @Override
            public void run() {
                output.write(MISE_A_JOUR_SCORE_ENVOIE);
                output.write(scoreAEnvoyer);
                output.flush();
            }
        }).start();
    }

    /**
     * Signal au contrôleur de la partie que le joueur se fait "attaquer" par un autre joueur avec un poisson normal.
     * @param pseudoAttaquant   Le pseudo de l'attaquant.
     */
    public void attaquePoissonNormal(String pseudoAttaquant) {
        synchronized (cadenasDonneesAffichage) {
            attaqueEnCours = true;
            attaqueSpeciale = false;
            lancementAttaque = false;
            connexionEnCours = false;
            deconnexionEnCours = false;
            nomAttaquant = pseudoAttaquant;
            deltaMessage = 0;
        }

        getPlanJeu().ajouterPoissonNormal();
    }

    /**
     * Signal au contrôleur de la partie que le joueur se fait "attaquer" par un autre joueur avec un poisson spécial.
     * @param pseudoAttaquant   Le pseudo de l'attaquant.
     */
    public void attaquePoissonSpecial(String pseudoAttaquant) {
        synchronized (cadenasDonneesAffichage) {
            attaqueEnCours = true;
            attaqueSpeciale = true;
            lancementAttaque = false;
            connexionEnCours = false;
            deconnexionEnCours = false;
            nomAttaquant = pseudoAttaquant;
            deltaMessage = 0;
        }

        getPlanJeu().ajouterPoissonSpecial();
    }

    /**
     * Signal au contrôleur de la partie que le score d'un joueur a changé.
     * @param pseudo    Le joueur pour lequel le score a changé.
     * @param score     Le nouveau score du joueur.
     */
    public void miseAJourScore(String pseudo, int score) {
        synchronized (cadenasDonneesAffichage) {
            for(Record nScore : scores) {
                if(nScore.getNom().equals(pseudo)) {
                    nScore.setScore(score);
                    return;
                }
            }
            Collections.sort(scores);
        }
    }

    /**
     * Signal au contrôleur de la partie qu'un joueur vient de se déconnecter.
     * @param pseudo    Le pseudo du joueur qui vient de se déconnecter.
     */
    public void deconnexionJoueur(String pseudo) {
        synchronized (cadenasDonneesAffichage) {
            deconnexionEnCours = true;
            attaqueEnCours = false;
            lancementAttaque = false;
            connexionEnCours = false;
            nomDeconnexion = pseudo;
            deltaMessage = 0;
            Record scoreARetirer = null;
            for(Record score : scores) {
                if(score.getNom().equals(pseudo)) {
                    scoreARetirer = score;
                    break;
                }
            }
            scores.remove(scoreARetirer);
            indexScores = 0;
            Collections.sort(scores);
        }
    }

    /**
     * Signal au contrôleur de la partie qu'un joueur vient de se connecter.
     * @param pseudo    Le pseudo du joueur qui vient de se connecter.
     */
    public void connexionJoueur(String pseudo) {
        synchronized (cadenasDonneesAffichage) {
            connexionEnCours = true;
            deconnexionEnCours = false;
            attaqueEnCours = false;
            lancementAttaque = false;
            nomConnexion = pseudo;
            deltaMessage = 0;
            scores.add(new Record(pseudo, 0));
            indexScores = 0;
            Collections.sort(scores);
        }
    }

    /**
     * Accesseur du plan de jeu. Cette méthode est "thread-safe" pour permettre le partage sur plan
     * de jeu entre ThreadUI et Receveur.
     * @return  Le plan de jeu.
     */
    public synchronized PlanJeu getPlanJeu() {
        return planJeu;
    }

    /**
     * Affiche une boite modale signifiant une erreur et met fin à la partie.
     */
    public void afficherErreur() {
        synchronized (cadenasDonneesAffichage) {
            erreurConnexion = true;
            receveur.setPartieEnCours(false);
            connexion.ferme();
        }
    }

    /**
     * Accesseur de la liste des poissons dans le plan de jeu.
     * @return  La liste des poissons du plan de jeu.
     */
    @Override
    public List<Poisson> getPoissons() {
        return getPlanJeu().getPoissons();
    }

    /**
     * Accesseur de la liste des bulles dans le plan de jeu.
     * @return  La liste des bulles du plan de jeu.
     */
    @Override
    public List<Bulle> getBulles() {
        return getPlanJeu().getBulles();
    }

    /**
     * Accesseur de la liste des projectiles dans le plan de jeu.
     * @return  La liste des projectiles du plan de jeu.
     */
    @Override
    public List<Projectile> getProjectiles() {
        return getPlanJeu().getProjectiles();
    }

    /**
     * Accesseur du drapeau signifiant qu'un erreur de connexion vient de se produire.
     * @return  Vrai si un erreur de connexion vient de se produire, faux, sinon.
     */
    public boolean getErreurConnexion() {
        synchronized (cadenasDonneesAffichage) {
            return erreurConnexion;
        }
    }

    /**
     * Accesseur du message à afficher dans la zone de texte réservée pour les messages du mode
     * multijoueur.
     * @return  Le message à afficher.
     */
    public String getMsgMultijoueurAfficher() {
        return msgMultijoueurAfficher;
    }
}
