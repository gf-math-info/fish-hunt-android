package fortin.leblanc.fishhuntandroid.modele;

import java.util.ArrayList;
import java.util.Random;

import fortin.leblanc.fishhuntandroid.modele.entite.Bulle;
import fortin.leblanc.fishhuntandroid.modele.entite.Projectile;
import fortin.leblanc.fishhuntandroid.modele.entite.poisson.Crabe;
import fortin.leblanc.fishhuntandroid.modele.entite.poisson.EtoileMer;
import fortin.leblanc.fishhuntandroid.modele.entite.poisson.Poisson;

/**
 * Cette classe représente le plan du jeu. Elle contient tous les éléments du
 * jeu.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class PlanJeu {

    private double largeur, hauteur;

    private Partie partie;
    private ArrayList<Bulle> bulles;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Poisson> poissons;

    private Random random;
    private boolean pretUnProjectileUnMort;
    private double deltaBulle, deltaPoisson, deltaPoissonSpecial;

    // ici, nous réunissons toutes les constantes décrivant chaque poisson
    // afin de faciliter la coordination des modifications au jeu.
    private final double DELAIS_BULLE = 3;
    private final int NB_GROUPES_BULLES = 3;
    private final double DIST_GROUPE_BULLE = 100;
    private final int NB_BULLES = 5;
    private final double DELAIS_POISSON = 3;
    private final double DELAIS_POISSON_SPECIAL = 5;
    private final double VITESSE_CRABE = 1.3;

    /**
     * Construit un plan de jeu selon certaines dimensions
     * avec une partie initiée sur ce plan de jeu.
     * @param largeur   La largeur du plan de jeu.
     * @param hauteur   La hauteur du plan de jeu.
     * @param partie    La partie de jeu.
     */
    public PlanJeu(double largeur, double hauteur, Partie partie) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.partie = partie;

        random = new Random();
        pretUnProjectileUnMort = true;
        deltaBulle = 0;
        deltaPoisson = 0;
        deltaPoissonSpecial = 0;

        bulles = new ArrayList<>();
        projectiles = new ArrayList<>();
        poissons = new ArrayList<>();
    }

    /**
     * Actualise tous les éléments dans le jeu.
     * @param deltaTemps    L'intervalle de temps depuis la dernière
     *                      actualisation.
     */
    public void actualiser(double deltaTemps) {
        /*On actualise la position des bulles, retire les bulles à l'extérieur
        du plan et on rajoute de nouvelles bulles lorsque le moment est venu.*/
        Bulle bulle;
        for(int i = 0; i < bulles.size();) {
            bulle = bulles.get(i);
            bulle.actualiser(deltaTemps);

            if(bulle.getY() + bulle.getHauteur() < 0)
                //Si la bulle est à l'extérieur du plan de jeu, alors...
                bulles.remove(i);
            else
                i++;
        }

        Poisson poisson;
        for(int i = 0; i < poissons.size();) {
            poisson = poissons.get(i);
            poisson.actualiser(deltaTemps);

            if((poisson.getVx() < 0 &&
                    poisson.getX() + poisson.getLargeur() < 0) ||
                    (poisson.getVx() > 0 && poisson.getX() > largeur)) {
                //Si le poisson s'est échappé, alors...
                poissons.remove(i);
                partie.decrementerVie();
            } else
                i++;
        }

        //On met à jour les contacts des projectiles avec les poissons.
        Projectile projectile;
        boolean touche;
        for(int i = 0; i < projectiles.size();) {
            projectile = projectiles.get(i);
            projectile.actualiser(deltaTemps);

            touche = false;//Le projectile n'a pas encore touché de poisson.

            if(projectile.getDiametre() == 0) {
                //Alors le projectile est au niveau des poissons...
                for(int j = 0; j < poissons.size();) {
                    poisson = poissons.get(j);
                    if(projectile.intersect(poisson)) {

                        /*On augmente le score selon que le tire est un
                        premier tire ou que le poisson a nécessité plusieurs
                        tires pour être touché.*/
                        partie.incrementerNbPoissonsTouches(
                                pretUnProjectileUnMort);
                        if(!pretUnProjectileUnMort)
                            pretUnProjectileUnMort = true;

                        //L'animation de bulle lorsqu'un poisson est touché.
                        ajouterBullePoisson(poisson.getX(), poisson.getY(),
                                poisson.getLargeur(), poisson.getHauteur());
                        touche = true;
                        poissons.remove(j);

                    } else
                        j++;
                }

                if(!touche) {
                    /* Si le projectile n'a pas touché de cible, alors le nombre
                    de poisson touché d'un coup est réinitialisé.
                     */
                    partie.initUnProjectileUnMort();
                    pretUnProjectileUnMort = false;
                }

                projectiles.remove(i);
            } else
                i++;
        }

        //On ajoute les poissons après un certain délai.
        deltaPoisson += deltaTemps;
        if(deltaPoisson >= DELAIS_POISSON) {
            deltaPoisson = 0;
            ajouterPoissonNormal();
        }

        //On ajoute les poissons spéciaux après un certain délai.
        if(partie.getNiveau() > 1) {
            deltaPoissonSpecial += deltaTemps;
            if(deltaPoissonSpecial >= DELAIS_POISSON_SPECIAL) {
                deltaPoissonSpecial = 0;
                ajouterPoissonSpecial();
            }
        }

        deltaBulle += deltaTemps;
        if(deltaBulle >= DELAIS_BULLE) {
            deltaBulle = 0;
            ajouterBulles();
        }
    }

    /**
     * Mutateur de la partie.
     * @param partie    La partie du jeu.
     */
    public void setPartie(Partie partie) {
        this.partie = partie;
    }

    /**
     * Accesseur des bulles.
     * @return  La liste de bulles que le plan de jeu contient.
     */
    public ArrayList<Bulle> getBulles() {
        return bulles;
    }

    /**
     * Accesseur des projectiles.
     * @return  La liste de projectiles que le plan de jeu contient.
     */
    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    /**
     * Accesseur des poissons que le plan de jeu contient.
     * @return  La liste de poissons que le plan de jeu contient.
     */
    public ArrayList<Poisson> getPoissons() {
        return poissons;
    }

    /**
     * Ajoute une bulle dans le plan jeu. Les attributs de la bulles sont
     * choisis aléatoirement.
     */
    private void ajouterBulles() {
        double posGroupe, posBulle, diametreBulle, vitesseBulle;
        for(int i = 0; i < NB_GROUPES_BULLES; i++) {

            posGroupe = random.nextDouble() * largeur;

            for(int j = 0; j < NB_BULLES; j++) {

                posBulle = random.nextDouble() * 2 * DIST_GROUPE_BULLE -
                        DIST_GROUPE_BULLE + posGroupe;
                diametreBulle = random.nextDouble() *
                        (Bulle.BULLE_RAYON_MAX - Bulle.BULLE_RAYON_MIN) +
                        Bulle.BULLE_RAYON_MIN;
                vitesseBulle = random.nextDouble() *
                        (Bulle.BULLE_VITESSE_MAX - Bulle.BULLE_VITESSE_MIN) +
                        Bulle.BULLE_VITESSE_MIN;
                bulles.add(new Bulle(diametreBulle,
                        posBulle, hauteur + diametreBulle, vitesseBulle));

            }
        }
    }

    /**
     * Ajoute un poisson normal au plan de jeu. Les attributs du poisson sont
     * choisis alatoirement.
     */
    public void ajouterPoissonNormal() {
        boolean versDroite = random.nextBoolean();
        double hauteurPoisson = random.nextDouble() *
                (Poisson.POISSON_GRANDEUR_MAX - Poisson.POISSON_GRANDEUR_MIN) +
                Poisson.POISSON_GRANDEUR_MIN;
        double vy = random.nextDouble() *
                (Poisson.POISSON_VITESSE_MAX - Poisson.POISSON_VITESSE_MIN) +
                Poisson.POISSON_VITESSE_MIN;
        double vx = vitesseLevel(partie.getNiveau());
        double y = random.nextDouble() *
                (Poisson.POISSON_Y_MAX_RATIO - Poisson.POISSON_Y_MIN_RATIO) *
                (hauteur - hauteurPoisson) +
                Poisson.POISSON_Y_MIN_RATIO * hauteur;
        double x;
        if(!versDroite) {
            vx = -vx;
            x = largeur;
        } else
            x = -hauteurPoisson;

        poissons.add(new Poisson(hauteurPoisson, hauteurPoisson, x, y, vx, vy));
    }

    /**
     * Ajoute un poisson spécial au plan de jeu. Les attributs du poisson sont
     * choisis aléatoirement.
     */
    public void ajouterPoissonSpecial() {
        Poisson poisson;
        boolean versDroite = random.nextBoolean();
        double largeurPoisson, hauteurPoisson, x, y, vx;

        largeurPoisson = random.nextDouble() *
                (Poisson.POISSON_GRANDEUR_MAX - Poisson.POISSON_GRANDEUR_MIN) +
                Poisson.POISSON_GRANDEUR_MIN;
        if(!versDroite)
            x = largeur;
        else
            x = - largeurPoisson;

        if(random.nextBoolean()) {//Un crabe...

            hauteurPoisson = Crabe.RATIO_CRABE_HAUTEUR_LARGEUR *
                    largeurPoisson;
            y = random.nextDouble() *
                    (Poisson.POISSON_Y_MAX_RATIO - Poisson.POISSON_Y_MIN_RATIO) *
                    (hauteur - hauteurPoisson) +
                    Poisson.POISSON_Y_MIN_RATIO * hauteur;
            vx = VITESSE_CRABE * vitesseLevel(partie.getNiveau());

            if(!versDroite)
                vx = -vx;
            poisson = new Crabe(largeurPoisson, x, y, vx);

        } else {//... ou une étoile de mer.

            y = random.nextDouble() *
                    (Poisson.POISSON_Y_MAX_RATIO - Poisson.POISSON_Y_MIN_RATIO)*
                    (hauteur - largeurPoisson) +
                    Poisson.POISSON_Y_MIN_RATIO * hauteur;
            vx = vitesseLevel(partie.getNiveau());

            if(!versDroite)
                vx = -vx;
            poisson = new EtoileMer(largeurPoisson, x, y, vx);

        }

        poissons.add(poisson);
    }

    /**
     * Ajoute des bulle dans une région donné lorsqu'un poisson est touché.
     * @param x         La position horizontale du poisson.
     * @param y         La position verticale du poisson.
     * @param largeur   La larguer du poisson.
     * @param hauteur   La hauteur du poisson.
     */
    private void ajouterBullePoisson(double x, double y,
                                     double largeur, double hauteur) {
        for(int i = 0; i < 12; i++) {
            bulles.add(new Bulle(random.nextDouble() * 30 + 50,
                    random.nextDouble() * largeur + x,
                    random.nextDouble() * hauteur + y,
                    random.nextDouble() *
                            (Bulle.BULLE_VITESSE_MAX - Bulle.BULLE_VITESSE_MIN)
                            + Bulle.BULLE_VITESSE_MIN));
        }
    }

    /**
     * Calcul la vitesse des poissons en fonction du niveau.
     * @param niveau    Le niveau de la partie.
     * @return  La vitesse des poissons selon le niveau de la partie.
     */
    private double vitesseLevel(int niveau) {
        return 200 * Math.pow(niveau, 1/3.0) + 200;
    }
}
