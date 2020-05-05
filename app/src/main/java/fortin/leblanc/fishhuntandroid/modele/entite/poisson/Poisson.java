package fortin.leblanc.fishhuntandroid.modele.entite.poisson;

import fortin.leblanc.fishhuntandroid.modele.entite.Entite;

/**
 * Cette classe représente un poisson dit normal.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Poisson extends Entite {

    public static final double POISSON_VITESSE_MIN = -300;
    public static final double POISSON_VITESSE_MAX = -400;
    public static final double POISSON_GRANDEUR_MIN = 150;
    public static final double POISSON_GRANDEUR_MAX = 250;
    public static final double POISSON_Y_MAX_RATIO = 0.8;
    public static final double POISSON_Y_MIN_RATIO = 0.2;
    private final double ACCELERATION_VERTICALE_DEFAUT = 200;

    protected double ay;

    /**
     * Construit un poisson carré.
     * @param cote  La longueur du côté du paramètre.
     * @param x     La position horizontale.
     * @param y     La position verticale.
     * @param vx    La vitesse horizontale.
     * @param vy    La vitesse verticale.
     */
    public Poisson(double cote, double x, double y, double vx, double vy) {
        this(cote, cote, x, y, vx, vy);
    }

    /**
     * Construit un poisson avec tous les paramètres.
     * @param largeur   La largeur.
     * @param hauteur   La hauteur.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param vx        La vitesse horizontale.
     * @param vy        La vitesse verticale.
     */
    public Poisson(double largeur, double hauteur, double x, double y,
                   double vx, double vy) {
        super(largeur, hauteur, x, y, vx, vy);
        ay = ACCELERATION_VERTICALE_DEFAUT;
    }


    /**
     * Actualise le déplacement du poisson selon l'intervalle de temps depuis
     * la dernière actualisation.
     * @param deltaTemps    L'intervalle de temps.
     */
    @Override
    public void actualiser(double deltaTemps) {
        vy += ay * deltaTemps;
        x += vx * deltaTemps;
        y += vy * deltaTemps;
    }
}
