package fortin.leblanc.fishhuntandroid.modele.entite;

/**
 * Cette classe représente un entité mouvante dans le jeu.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public abstract class Entite {

    protected double largeur;
    protected double hauteur;
    protected double x;
    protected double y;
    protected double vx;
    protected double vy;

    /**
     * Construit une entité avec tous les paramètres d'une entité.
     * @param largeur   La largeur.
     * @param hauteur   La hauteur.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param vx        La vitesse horizontale.
     * @param vy        La vitesse verticale.
     */
    public Entite(double largeur, double hauteur, double x, double y,
                  double vx,double vy) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    /**
     * Actualise le déplacement de l'entité selon l'intervalle de temps depuis
     * la dernière actualisation.
     * @param deltaTemps    L'intervalle de temps.
     */
    public abstract void actualiser(double deltaTemps);

    /**
     * Accesseur de la largeur.
     * @return  La largeur.
     */
    public double getLargeur() {
        return largeur;
    }

    /**
     * Accesseur de la hauteur.
     * @return  La hauteur.
     */
    public double getHauteur() {
        return hauteur;
    }

    /**
     * Accesseur de la position horizontale.
     * @return  La position horizontale.
     */
    public double getX() {
        return x;
    }

    /**
     * Accesseur de la position verticale.
     * @return  La position verticale.
     */
    public double getY() {
        return y;
    }

    /**
     * Accesseur de la vitesse horizontale.
     * @return  La vitesse horizontale.
     */
    public double getVx() {
        return vx;
    }

    /**
     * Accesseur de la vitesse verticale.
     * @return  La vitesse verticale.
     */
    public double getVy() {
        return vy;
    }
}
