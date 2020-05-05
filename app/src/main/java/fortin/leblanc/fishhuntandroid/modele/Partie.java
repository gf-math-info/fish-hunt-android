package fortin.leblanc.fishhuntandroid.modele;

import fortin.leblanc.fishhuntandroid.controleur.ControleurPartie;

/**
 * La partie en cours. La partie et le plan de jeu sont deux entités distinctes.
 * @see PlanJeu
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Partie {

    protected int score;
    protected int niveau;
    protected int nbPoissonsTouches;
    protected boolean perdue;
    protected int nbViesRestantes;
    protected int nbUnProjectileUnMort;

    protected ControleurPartie controleurPartie;

    // les 2 constantes suivantes sont sous forme d'attribut pour
    // faciliter la tâche d'un programmeur qui voudrait les
    // modifier.
    protected final int NB_VIES_INIT = 3;
    protected final int NB_POISSONS_NIVEAU = 5;

    /**
     * Contruit une partie.
     */
    public Partie(ControleurPartie controleurPartie) {
        this.niveau = 1;
        this.nbViesRestantes = NB_VIES_INIT;
        this.controleurPartie = controleurPartie;
    }

    /**
     * Incrémente le niveau de la partie.
     */
    public void incrementerNiveau() {
        this.niveau++;
        this.controleurPartie.augmenteNiveau();
    }

    /**
     * Incrémente le nombre de poissons touchés.
     * @param unProjectileUnMort    Vrai si le poisson a été touché d'un coup,
     *                              faux sinon.
     */
    public void incrementerNbPoissonsTouches(boolean unProjectileUnMort) {
        if(unProjectileUnMort) {
            switch (++nbUnProjectileUnMort) {
                case 1:
                    score += 3;
                    break;
                case 2:
                    score += 5;
                    break;
                case 3:
                    nbViesRestantes++;
                    break;
                default:
                    score += 3;
                    nbUnProjectileUnMort = 1;
            }
        } else
            incrementerScore();

        if (++nbPoissonsTouches % NB_POISSONS_NIVEAU == 0)
            incrementerNiveau();
    }

    /**
     * Incrémente le score de la partie.
     */
    public void incrementerScore() {
        score++;
    }

    /**
     * Incrémente le nombre de vies.
     */
    public void incrementerVie() {
        this.nbViesRestantes++;
    }

    /**
     * Décrémente le nombre de vies.
     */
    public void decrementerVie() {
        perdue = --nbViesRestantes == 0;
    }

    /**
     * Remet à 0 le nombre de un-projectile-un-mort.
     */
    public void initUnProjectileUnMort() {
        nbUnProjectileUnMort = 0;
    }

    /**
     * Mutateur de l'état de la partie.
     * @param estPerdue  L'état de la partie.
     */
    public void setPerdue(boolean estPerdue) {
        this.perdue = estPerdue;
    }

    /**
     * Accesseur de l'état de la partie.
     * @return  L'état de la partie.
     */
    public boolean estPerdue() {
        return perdue;
    }

    /**
     * Accesseur du niveau de la partie.
     * @return  Le niveau de la partie.
     */
    public int getNiveau() {
        return niveau;
    }

    /**
     * Accesseur du score de la partie.
     * @return  Le score de la partie.
     */
    public int getScore() {
        return score;
    }


    /**
     * Accesseur du nombres de vies restantes.
     * @return  Le nombre de vies restantes.
     */
    public int getNbViesRestantes() {
        return nbViesRestantes;
    }

    /**
     * Accesseur du nombre de tire un-projectile-un-mort.
     * @return  Le nombre de tire un-projectile-un-mort.
     */
    public int getNbUnProjectileUnMort() {
        return nbUnProjectileUnMort;
    }
}
