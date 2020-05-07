package fortin.leblanc.fishhuntandroid.vue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;

import fortin.leblanc.fishhuntandroid.R;
import fortin.leblanc.fishhuntandroid.controleur.ControleurPartie;
import fortin.leblanc.fishhuntandroid.controleur.activite.JeuActivity;
import fortin.leblanc.fishhuntandroid.controleur.activite.ScoreActivity;
import fortin.leblanc.fishhuntandroid.modele.entite.Bulle;
import fortin.leblanc.fishhuntandroid.modele.entite.Projectile;
import fortin.leblanc.fishhuntandroid.modele.entite.poisson.Crabe;
import fortin.leblanc.fishhuntandroid.modele.entite.poisson.EtoileMer;
import fortin.leblanc.fishhuntandroid.modele.entite.poisson.Poisson;

/**
 * Cette classe représente la vue du jeu.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class VueJeu extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private AnimationJeu animationJeu;
    private ControleurPartie controleurPartie;
    private Object cadenasControleur;

    private int largeur, hauteur;
    private WeakHashMap<Poisson, Bitmap> poissonBitmaps;
    private ArrayBlockingQueue<Bitmap> filePoissonsAleatoires;
    private Random random;
    private Bitmap etoileMerBitmap, crabeBitmap, vieBitmap;
    private int[] imgIdPoissons, couleurPoissons;

    private Paint msgCentrePaint, bullePaint, poissonPaint, projectilePaint, scoreViePaint;

    /**
     * Construit la vue du jeu avec le contexte et les dimensions en pixels de l'écran.
     * @param context   Le contexte de l'activité.
     * @param largeur   La largeur de l'écran.
     * @param hauteur   La hauteur de l'écran.
     */
    public VueJeu(Context context, int largeur, int hauteur) {
        super(context);
        this.largeur = largeur;
        this.hauteur = hauteur;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        animationJeu = new AnimationJeu();
        controleurPartie = new ControleurPartie(largeur, hauteur);
        cadenasControleur = new Object();

        initOutilsDessin();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    controleurPartie.ajouterProjectile(event.getX(), event.getY());
                    return false;
                }
                return true;
            }
        });
    }

    /**
     * Lorsque la surface est créée, on débute le "thread" d'animation.
     * @param holder    Le "SurfaceHolder" du SurfaceView.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        animationJeu = new AnimationJeu();
        animationJeu.setJeuEnCours(true);
        animationJeu.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        /*L'écran n'est qu'en mode paysage. Voir le manifest.*/
    }

    /**
     * Lorsque la surface est détruite, on arrête le "thread" d'animation.
     * @param holder    Le "SurfaceHolder" du "SurfaceView".
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        finAnimationJeu();
    }

    /**
     * Méthode appelée par le "thread" d'animation pour redessiner la surface de jeu.
     * @param canvas    Le canvas du "SurfaceView".
     */
    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(getResources().getColor(R.color.fondMarin));

        if(controleurPartie.getPartieTerminee()) {

            animationJeu.setJeuEnCours(false);
            Intent versScoreActivity = new Intent(getContext(), ScoreActivity.class);
            versScoreActivity.putExtra(JeuActivity.SCORE, controleurPartie.getScore());
            getContext().startActivity(versScoreActivity);

        } else if(controleurPartie.getPerdPartie()) {

            String msg = "Game Over";
            Rect grandeur = new Rect();
            msgCentrePaint.getTextBounds(msg, 0, msg.length(), grandeur);

            canvas.drawText(msg, (canvas.getWidth() - grandeur.width()) / 2,
                    (canvas.getHeight() + grandeur.height()) / 2, msgCentrePaint);

        } else if(controleurPartie.getAugmenteNiveau()) {

            String msg = "Level " + controleurPartie.getNiveau();
            Rect grandeur = new Rect();
            msgCentrePaint.getTextBounds(msg, 0, msg.length(), grandeur);

            canvas.drawText(msg, (canvas.getWidth() - grandeur.width())  / 2,
                    (canvas.getHeight() + grandeur.height()) / 2, msgCentrePaint);

        } else {
            synchronized (cadenasControleur) {

                //On dessine les bulles.
                for(Bulle bulle : controleurPartie.getBulles()) {
                    canvas.drawCircle((float)bulle.getX(), (float)bulle.getY(),
                            (float)bulle.getDiametre() / 2, bullePaint);
                }

                //On dessine les poissons.
                for(Poisson poisson : controleurPartie.getPoissons()) {

                    if(!poissonBitmaps.containsKey(poisson)) {//Un nouveau poisson, alors...

                        if(poisson instanceof EtoileMer) {

                            poissonBitmaps.put(poisson, Bitmap.createScaledBitmap(etoileMerBitmap,
                                    (poisson.getVx() > 0 ? 1 : -1) *
                                            (int)poisson.getLargeur(), (int)poisson.getHauteur(),
                                    false));

                        } else if(poisson instanceof Crabe) {

                            poissonBitmaps.put(poisson, Bitmap.createScaledBitmap(crabeBitmap,
                                    (poisson.getVx() > 0 ? 1 : -1) *
                                            (int)poisson.getLargeur(), (int)poisson.getHauteur(),
                                    false));

                        } else {

                            int largeur = (int) poisson.getLargeur(),
                                    hauteur = (int) poisson.getHauteur();

                            Bitmap image = null;
                            try {
                                image = filePoissonsAleatoires.take();
                            } catch (InterruptedException e) {}

                            image = Bitmap.createScaledBitmap(image,
                                    (poisson.getVx() > 0 ? 1 : -1) *
                                            (int) poisson.getLargeur(), (int) poisson.getHauteur(),
                                    false);

                            poissonBitmaps.put(poisson, image);

                        }

                    }

                    canvas.drawBitmap(poissonBitmaps.get(poisson), (float)poisson.getX(),
                            (float)poisson.getY(), poissonPaint);

                }

                for(Projectile projectile : controleurPartie.getProjectiles()) {

                    canvas.drawCircle((float)projectile.getX(), (float)projectile.getY(),
                            (float)projectile.getDiametre() / 2, projectilePaint);

                }

                //On dessine le score.
                String msgScore = String.valueOf(controleurPartie.getScore());
                Rect grandeur = new Rect();
                scoreViePaint.getTextBounds(msgScore, 0, msgScore.length(), grandeur);
                canvas.drawText(msgScore, (canvas.getWidth() - grandeur.width()) / 2,
                        100, scoreViePaint);

                //On dessine les vies.
                int nbVie = controleurPartie.getNbVie();
                float x, y = 80 + grandeur.height(), espaceVie = 50;
                if(nbVie < 5) {
                    x = (canvas.getWidth() - vieBitmap.getWidth() * nbVie - espaceVie *
                            (nbVie > 0 ? nbVie - 1 : 0)) / 2;
                    canvas.drawBitmap(vieBitmap, x, y, scoreViePaint);
                    for(int i = 1; i < nbVie; i++)
                        canvas.drawBitmap(vieBitmap, x + i * (vieBitmap.getWidth() + espaceVie),
                                y, scoreViePaint);
                } else {
                    String msgVie = nbVie + " \u2715 ";
                    scoreViePaint.getTextBounds(msgVie, 0, msgVie.length(), grandeur);
                    x = (canvas.getWidth() - grandeur.width() - vieBitmap.getWidth()) / 2;
                    canvas.drawText(msgVie, x, y +
                            (vieBitmap.getHeight() + grandeur.height()) / 2, scoreViePaint);
                    canvas.drawBitmap(vieBitmap, x + grandeur.width() + espaceVie, y,
                            scoreViePaint);
                }

                //On dessine le nombre de un-projectile-un-mort.
                if(controleurPartie.getNbUnProjectileUnMort() > 0) {
                    String msgUnProjectileUnMort = "Tir parfait \u2715 " +
                            controleurPartie.getNbUnProjectileUnMort();
                    scoreViePaint.getTextBounds(msgUnProjectileUnMort, 0,
                            msgUnProjectileUnMort.length(), grandeur);

                    canvas.drawText(msgUnProjectileUnMort, canvas.getWidth() - grandeur.width() -
                            20, 100, scoreViePaint);
                }

            }
        }

        super.onDraw(canvas);
    }

    /**
     * On initialise les objects utiles pour le dessin.
     */
    private void initOutilsDessin() {

        poissonPaint = new Paint();

        projectilePaint = new Paint();
        projectilePaint.setColor(getResources().getColor(R.color.projectile));
        projectilePaint.setStyle(Paint.Style.FILL);

        bullePaint = new Paint();
        bullePaint.setColor(getResources().getColor(R.color.bulle));
        bullePaint.setAlpha(100);
        bullePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        msgCentrePaint = new Paint();
        msgCentrePaint.setColor(Color.WHITE);
        msgCentrePaint.setTextSize(250);

        scoreViePaint = new Paint();
        scoreViePaint.setColor(Color.WHITE);
        scoreViePaint.setTextSize(80);

        etoileMerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star);
        crabeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.crabe);
        vieBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.poisson00);
        vieBitmap = Bitmap.createScaledBitmap(vieBitmap, 100, 100, false);

        poissonBitmaps = new WeakHashMap<>();

        imgIdPoissons = new int[] {R.drawable.poisson00, R.drawable.poisson01,
                R.drawable.poisson02, R.drawable.poisson03, R.drawable.poisson04,
                R.drawable.poisson05, R.drawable.poisson06, R.drawable.poisson07};
        couleurPoissons = new int[] {Color.RED, Color.GREEN, Color.CYAN, Color.LTGRAY, Color.YELLOW};
        random = new Random();

        filePoissonsAleatoires = new ArrayBlockingQueue<>(1);

    }

    /**
     * Met fin à l'animation du jeu.
     */
    public void finAnimationJeu() {
        //On arrête le thread de jeu.
        animationJeu.setJeuEnCours(false);
        boolean joint = false;
        while(!joint) {
            try {
                animationJeu.join();
                joint = true;
            } catch(InterruptedException e) {}
        }
    }

    /**
     * Cette classe représente l'animation de jeu.
     */
    private class AnimationJeu extends Thread{

        private boolean jeuEnCours;
        private Object cadenasJeuEnCours;
        private long dernierMoment, maintenant;
        private double deltaTemps;

        private ConstructeurBitmapThread constructeurBitmapThread;

        /**
         * Construit l'animation de jeu.
         */
        public AnimationJeu() {
            constructeurBitmapThread = new ConstructeurBitmapThread();
            jeuEnCours = true;
            cadenasJeuEnCours = new Object();
            dernierMoment = System.nanoTime();
        }

        /**
         * Anime le jeu.
         */
        @SuppressLint("WrongCall")
        @Override
        public void run() {

            //On démarre le thread qui nous aide à faire les images de poissons de couleur.
            constructeurBitmapThread.start();

            while(getJeuEnCours()) {

                Canvas canvas = null;

                try {

                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        synchronized (cadenasControleur) {
                            maintenant = System.nanoTime();
                            deltaTemps = (maintenant - dernierMoment) * 1e-9;
                            dernierMoment = maintenant;
                            controleurPartie.actualiser(deltaTemps);
                        }
                        onDraw(canvas);
                    }

                } finally {

                    if(canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);

                }
            }

            //On met fin au thread qui aide à faire les images.
            while(!constructeurBitmapThread.isInterrupted())
                constructeurBitmapThread.interrupt();
        }

        /**
         * Accesseur thread-safe du drapeau signifiant que la partie est toujours en cours.
         * @return  Vrai si la partie est en cours, faux, sinon.
         */
        public boolean getJeuEnCours() {
            synchronized (cadenasJeuEnCours) {
                return jeuEnCours;
            }
        }

        /**
         * Mutateur thread-safe du drapeau signifiant que la partie est toujours en cours.
         * @param jeuEstEnCours Vrai si le drapeau est en cours, faux, sinon.
         */
        public void setJeuEnCours(boolean jeuEstEnCours) {
            synchronized (cadenasJeuEnCours) {
                this.jeuEnCours = jeuEstEnCours;
            }
        }
    }

    /**
     * Cette classe représente le thread qui aide le thread d'animation. Charger les images et les
     * colorier sont des exécutions demandantes, alors ce thread est toujours en cours tant que le
     * thread d'animation est également en cours. Il s'assure que la file "filePoissonsAleatoires"
     * est toujours pleine d'image de poisson aléatoire dessinée d'une couleur aléatoire.
     * Lorsque la file est pleine, alors le thread attend qu'un autre thread retire un élément.
     * Il ne reste qu'au thread d'animation d'agencer la grandeur des images au grandeur des
     * poissons.
     * @see ArrayBlockingQueue
     */
    private class ConstructeurBitmapThread extends Thread{

        /**
         * S'assure que la file d'images de poissons est toujours pleine.
         */
        @Override
        public void run() {

            while (true) {

                Bitmap image = BitmapFactory.decodeResource(getResources(),
                        imgIdPoissons[random.nextInt(imgIdPoissons.length)]);
                //Pour que le bitmap soit "mutable".
                image = image.copy(Bitmap.Config.ARGB_8888, true);
                //On choisit la couleur du poisson.
                int couleur = couleurPoissons[random.nextInt(couleurPoissons.length)];
                //On parcourt l'image et lorsque le pixel est blanc, on change la
                //couleur de ce pixel pour la couleur choisit.
                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {

                        int pixel = image.getPixel(x, y);
                        if (Color.red(pixel) == 255 &&
                                Color.green(pixel) == 255 &&
                                Color.blue(pixel) == 255) {
                            image.setPixel(x, y, couleur);
                        }

                    }
                }

                try {
                    filePoissonsAleatoires.put(image);
                } catch (InterruptedException e) {}

            }

        }
    }
}
