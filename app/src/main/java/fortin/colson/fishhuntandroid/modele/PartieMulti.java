package fortin.colson.fishhuntandroid.modele;

import fortin.colson.fishhuntandroid.controleur.multijoueur.ControleurPartieMulti;

/**
 * Cette classe représente une partie en mode multijoueur.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class PartieMulti extends Partie {

    private ControleurPartieMulti controleurPartieMulti;

    /**
     * Construit une partie en mode multijoueur avec le contrôleur de la partie.
     * @param controleurPartieMulti Le contrôleur de la partie.
     */
    public PartieMulti(ControleurPartieMulti controleurPartieMulti) {
        super(controleurPartieMulti);
        this.controleurPartieMulti = controleurPartieMulti;
    }

    /**
     * Incrémente le nombre de poissons touchés.
     * @param unProjectileUnMort    Vrai si le poisson a été touché d'un coup,
     *                              faux sinon.
     */
    @Override
    public void incrementerNbPoissonsTouches(boolean unProjectileUnMort) {
        if(unProjectileUnMort) {

            switch (++nbUnProjectileUnMort) {

                case 1:
                    score += 3;
                    controleurPartieMulti.miseAJourScore();
                    break;

                case 2:
                    controleurPartieMulti.attaquePoissonNormal();
                    incrementerScore();
                    break;

                case 3:
                    nbViesRestantes++;
                    incrementerScore();
                    break;

                case 4:
                    controleurPartieMulti.attaquePoissonSpecial();
                    incrementerScore();
                    break;

                default:
                    nbUnProjectileUnMort = 1;
                    score += 3;
                    controleurPartieMulti.miseAJourScore();
            }

        } else
            incrementerScore();

        if(++nbPoissonsTouches % NB_POISSONS_NIVEAU == 0)
            incrementerNiveau();

    }

    /**
     * Incrémente le score de la partie.
     */
    @Override
    public void incrementerScore() {
        super.incrementerScore();
        controleurPartieMulti.miseAJourScore();
    }
}
