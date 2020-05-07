package fortin.colson.fishhuntandroid.modele.entite;

import fortin.colson.fishhuntandroid.modele.entite.poisson.Poisson;

/**
 * Cette classe représente un projectile à lancer.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Projectile extends Entite {

    private final static double VITESSE_ELOIGNEMENT = 900;
    private final static double GRANDEUR_DEFAUT = 150;

    /**
     * Construit un projectile selon une position.
     * @param x La position horizontale.
     * @param y La position verticale.
     */
    public Projectile(double x, double y) {
        super(GRANDEUR_DEFAUT, GRANDEUR_DEFAUT, x, y, 0, 0);
    }

    //TODO : Implémenter les tests de Projectile.intersecte(Poisson)
    /**
     * Vérifie si le projectile est en contact avec le poisson.
     * @param poisson   Le poisson à évaluer.
     * @return          Vrai si le projectile est en contact avec le poisson,
     *                  faux sinon.
     */
    public boolean intersect(Poisson poisson) {
        return poisson.getX() <= x &&
                x <= poisson.getX() + poisson.getLargeur() &&
                y >= poisson.getY() &&
                y <= poisson.getY() + poisson.getHauteur();
    }

    /**
     * Actualise le déplacement du projectile selon l'intervalle de temps depuis
     * la dernière actualisation.
     * @param deltaTemps    L'intervalle de temps.
     */
    @Override
    public void actualiser(double deltaTemps) {
        largeur -= VITESSE_ELOIGNEMENT * deltaTemps;
        hauteur -= VITESSE_ELOIGNEMENT * deltaTemps;

        if(largeur <= 0) {
            largeur = hauteur = 0;
        }
    }

    /**
     * Accesseur du diamètre du projectile.
     * @return  Le diamètre du projectile.
     */
    public double getDiametre() {
        return largeur;
    }
}
