package fortin.colson.fishhuntandroid.modele.entite.poisson;

/**
 * Cette classe représente une étoile de mer.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class EtoileMer extends Poisson {

    private double yInit;
    private double tempsDepuisCreation;

    private final static double AMPLITUDE = 100;

    /**
     * Construit une étoile de mer carrée avec tous les paramètres.
     * @param cote      La longueur du côté de l'étoile.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param vx        La vitesse horizontale.
     */
    public EtoileMer(double cote, double x, double y,
                     double vx) {
        super(cote, cote, x, y, vx, 0);
        yInit = y;
        ay = 0;
        tempsDepuisCreation = 0;
    }

    /**
     * Actualise le déplacement de l'étoile de mer selon l'intervalle de temps
     * depuis la dernière actualisation.
     * @param deltaTemps    L'intervalle de temps.
     */
    @Override
    public void actualiser(double deltaTemps) {
        tempsDepuisCreation += deltaTemps;
        x += vx * deltaTemps;
        y = AMPLITUDE * Math.sin(2 * Math.PI * tempsDepuisCreation) + yInit;
    }

}
