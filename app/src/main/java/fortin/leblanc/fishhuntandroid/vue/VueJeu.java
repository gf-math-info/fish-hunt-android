package fortin.leblanc.fishhuntandroid.vue;

import android.annotation.SuppressLint;
import android.content.Context;
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

import fortin.leblanc.fishhuntandroid.R;
import fortin.leblanc.fishhuntandroid.controleur.ControleurPartie;
import fortin.leblanc.fishhuntandroid.modele.entite.Bulle;
import fortin.leblanc.fishhuntandroid.modele.entite.Projectile;
import fortin.leblanc.fishhuntandroid.modele.entite.poisson.Crabe;
import fortin.leblanc.fishhuntandroid.modele.entite.poisson.EtoileMer;
import fortin.leblanc.fishhuntandroid.modele.entite.poisson.Poisson;

public class VueJeu extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private AnimationJeu animationJeu;
    private ControleurPartie controleurPartie;
    private Object cadenasControleur;

    private int largeur, hauteur;
    private WeakHashMap<Poisson, Bitmap> poissonBitmaps;
    private Random random;
    private Bitmap etoileMerBitmap, crabeBitmap, vieBitmap;
    private int[] imgIdPoissons, couleurPoissons;

    private Paint msgCentrePaint, bullePaint, poissonPaint, projectilePaint, scoreViePaint;

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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        animationJeu.setJeuEnCours(true);
        animationJeu.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //TODO
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
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

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(getResources().getColor(R.color.fondMarin));

        if(controleurPartie.getPartieTerminee()) {

            //TODO : Vers l'affchage de score.

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

                for(Bulle bulle : controleurPartie.getBulles()) {
                    canvas.drawCircle((float)bulle.getX(), (float)bulle.getY(),
                            (float)bulle.getDiametre() / 2, bullePaint);
                }

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
                            //On choisit la couleur du poisson.
                            int couleur = couleurPoissons[random.nextInt(couleurPoissons.length)];

                            Bitmap image = BitmapFactory.decodeResource(getResources(),
                                    imgIdPoissons[random.nextInt(imgIdPoissons.length)]);
                            image = Bitmap.createScaledBitmap(image,
                                    (poisson.getVx() > 0 ? 1 : -1) *
                                            (int) poisson.getLargeur(), (int) poisson.getHauteur(),
                                    false);

                            //On parcourt l'image et lorsque le pixel est blanc, on change la
                            //couleur de ce pixel pour la couleur choisit.
                            for (int x = 0; x < largeur; x++) {
                                for (int y = 0; y < hauteur; y++) {

                                    int pixel = image.getPixel(x, y);
                                    if (Color.red(pixel) == 255 &&
                                            Color.green(pixel) == 255 &&
                                            Color.blue(pixel) == 255) {
                                        image.setPixel(x, y, couleur);
                                    }

                                }
                            }
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

                //On dessie le score.
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

    }

    private class AnimationJeu extends Thread{

        private boolean jeuEnCours;
        private Object cadenasJeuEnCours;
        private long dernierMoment, maintenant;
        private double deltaTemps;

        public AnimationJeu() {
            jeuEnCours = true;
            cadenasJeuEnCours = new Object();
            dernierMoment = System.nanoTime();
        }

        @SuppressLint("WrongCall")
        @Override
        public void run() {

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

        }

        public boolean getJeuEnCours() {
            synchronized (cadenasJeuEnCours) {
                return jeuEnCours;
            }
        }

        public void setJeuEnCours(boolean jeuEstEnCours) {
            synchronized (cadenasJeuEnCours) {
                this.jeuEnCours = jeuEstEnCours;
            }
        }
    }
}
