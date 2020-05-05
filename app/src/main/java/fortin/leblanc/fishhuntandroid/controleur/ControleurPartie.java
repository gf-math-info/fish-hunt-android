package fortin.leblanc.fishhuntandroid.controleur;

import java.util.List;

import fortin.leblanc.fishhuntandroid.modele.Partie;
import fortin.leblanc.fishhuntandroid.modele.PlanJeu;
import fortin.leblanc.fishhuntandroid.modele.entite.Bulle;
import fortin.leblanc.fishhuntandroid.modele.entite.Projectile;
import fortin.leblanc.fishhuntandroid.modele.entite.poisson.Poisson;

/**
 * Cette classe est le contrôleur de jeu. La vue dit au controleur qu'elle
 * est prête à se mettre à jour, le contrôleur met à jour la partie et dicte
 * à la vue quoi dessiner. La vue ne fait affaire qu'avec cette classe.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class ControleurPartie {

    protected Partie partie;
    protected PlanJeu planJeu;

    protected boolean augmenteNiveau, perdPartie, partieTerminee;
    protected double deltaMessage;
    protected final double TEMPS_MESSAGE = 3;

    /**
     * Construit un contrôleur de jeu avec la largeur et la hauteur du plan de
     * jeu, ainsi que la classe dessinable.
     * @param largeur       La largeur du plan de jeu.
     * @param hauteur       La hauteur du plan de jeu.
     */
    public ControleurPartie(double largeur, double hauteur) {
        partie = new Partie(this);
        planJeu = new PlanJeu(largeur, hauteur, partie);

        augmenteNiveau = true;
    }

    /**
     * Actualise la partie. Le contrôleur dicte également à la
     * classe dessinable quoi dessiner.
     * @param deltaTemps    L'intervalle de temps depuis la dernière
     *                      actualisation.
     */
    public void actualiser(double deltaTemps) {

        if(augmenteNiveau) {
            deltaMessage += deltaTemps;
            if (deltaMessage >= TEMPS_MESSAGE) {
                deltaMessage = 0;
                augmenteNiveau = false;
            }
        } else if(perdPartie) {
            deltaMessage += deltaTemps;
            partieTerminee = deltaMessage >= TEMPS_MESSAGE;
        } else {
            planJeu.actualiser(deltaTemps);
        }

        perdPartie = partie.estPerdue();
    }

    /**
     * Méthode appelée par la partie pour signaler au contrôleur que le niveau de la partie vient
     * d'augmenter.
     */
    public void augmenteNiveau() {
        augmenteNiveau = true;
    }

    /**
     * Ajoute un projectile au plan de jeu.
     * @param x La position horizontale du projectile.
     * @param y La position verticale du projectile.
     */
    public void ajouterProjectile(double x, double y) {
        if(!augmenteNiveau && !partie.estPerdue())
            planJeu.getProjectiles().add(new Projectile(x, y));
    }


    //Accesseurs et mutateurs

    /**
     * Indique si la partie est en train d'augmenter de niveau. Un message devrait signaler au
     * joueur que la partie augmente de niveau.
     * @return  Vrai si la partie augmente de niveau, faux, sinon.
     */
    public boolean getAugmenteNiveau() {
        return augmenteNiveau;
    }

    /**
     * Indique si la partie est en train d'être perdue. Un message devrait signaler au joueur qu'il
     * a perdu la partie.
     * @return  Vrai si le joueur perd la partie, faux, sinon.
     */
    public boolean getPerdPartie() {
        return perdPartie;
    }

    /**
     * Indique si la partie est terminée. La vue peut changer d'activité.
     * @return  Vrai si la partie est terminée, faux, sinon.
     */
    public boolean getPartieTerminee() {
        return partieTerminee;
    }

    /**
     * Accesseur du niveau de la partie.
     * @return  Le niveau de la partie.
     */
    public int getNiveau() {
        return partie.getNiveau();
    }

    /**
     * Accesseur du score du joueur.
     * @return  Le score du joueur.
     */
    public int getScore() {
        return partie.getScore();
    }

    /**
     * Accesseur du nombre de vie restant du joueur.
     * @return  Le nombre de vie restant du joueur.
     */
    public int getNbVie() {
        return partie.getNbViesRestantes();
    }

    /**
     * Accesseur du nombre de un-projectile-un-mort (one shot kill).
     * @return  Le nombre de un-projectile-un-mort.
     */
    public int getNbUnProjectileUnMort() {
        return partie.getNbUnProjectileUnMort();
    }

    /**
     * Accesseur de la liste des poissons dans le plan de jeu.
     * @return  La liste des poissons du plan de jeu.
     */
    public List<Poisson> getPoissons() {
        return planJeu.getPoissons();
    }

    /**
     * Accesseur de la liste des bulles dans le plan de jeu.
     * @return  La liste des bulles du plan de jeu.
     */
    public List<Bulle> getBulles() {
        return planJeu.getBulles();
    }

    /**
     * Accesseur de la liste des projectiles dans le plan de jeu.
     * @return  La liste des projectiles du plan de jeu.
     */
    public List<Projectile> getProjectiles() {
        return planJeu.getProjectiles();
    }
}