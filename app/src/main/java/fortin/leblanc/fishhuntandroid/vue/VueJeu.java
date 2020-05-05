package fortin.leblanc.fishhuntandroid.vue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.Random;
import java.util.WeakHashMap;

import fortin.leblanc.fishhuntandroid.R;
import fortin.leblanc.fishhuntandroid.controleur.ControleurPartie;
import fortin.leblanc.fishhuntandroid.modele.entite.Bulle;
import fortin.leblanc.fishhuntandroid.modele.entite.Projectile;
import fortin.leblanc.fishhuntandroid.modele.entite.poisson.Poisson;

public class VueJeu extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private AnimationJeu animationJeu;
    private ControleurPartie controleurPartie;
    private Object cadenasControleur;

    private int largeur, hauteur;
    private WeakHashMap<Poisson, Integer> poissonImages;
    private WeakHashMap<Poisson, Paint> poissonPaints;
    private WeakHashMap<Poisson, Boolean> poissonCotes;
    private Random random;
    private int[] imgIdPoissons;
    private Paint[] paintPoissons;

    private Paint msgCentrePaint, bullePaint;

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

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (cadenasControleur) {
                    controleurPartie.ajouterProjectile(v.getX(), v.getY());
                }
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

            Log.i("État de la partie", "Partie terminée");

            //TODO : Vers l'affchage de score.

        } else if(controleurPartie.getPerdPartie()) {

            Log.i("État de la partie", "Perd la partie");

            String msg = "Game Over";
            Rect grandeur = new Rect();
            msgCentrePaint.getTextBounds(msg, 0, msg.length(), grandeur);

            canvas.drawText(msg, (canvas.getWidth() - grandeur.width()) / 2f,
                    (canvas.getHeight() - grandeur.height()) / 2, msgCentrePaint);

        } else if(controleurPartie.getAugmenteNiveau()) {

            Log.i("État de la partie", "Augmente de niveau");

            String msg = "Level " + controleurPartie.getNiveau();
            Rect grandeur = new Rect();
            msgCentrePaint.getTextBounds(msg, 0, msg.length(), grandeur);

            canvas.drawText(msg, (canvas.getWidth() - grandeur.width())  / 2f,
                    (canvas.getHeight() + grandeur.height()) / 2f, msgCentrePaint);

        } else {
            synchronized (cadenasControleur) {

                Log.i("État de la partie", "Partie en cours");

                for(Bulle bulle : controleurPartie.getBulles()) {
                    canvas.drawCircle((float)bulle.getX(), (float)bulle.getY(),
                            (float)bulle.getDiametre() / 2, bullePaint);
                }

                for(Poisson poisson : controleurPartie.getPoissons()) {

                    if(!poissonPaints.containsKey(poisson)) {

                        poissonPaints.put(poisson,
                                paintPoissons[random.nextInt(paintPoissons.length)]);
                        poissonImages.put(poisson,
                                imgIdPoissons[random.nextInt(imgIdPoissons.length)]);
                        poissonCotes.put(poisson, poisson.getVx() > 0);

                    }

                    Bitmap image = BitmapFactory.decodeResource(getResources(),
                            poissonImages.get(poisson));
                    //On ajuste l'image à la taille des poissons. S'il se déplace vers la gauche,
                    //alors on retourne l'image.
                    image = Bitmap.createScaledBitmap(image,
                            (poissonCotes.get(poisson) ? 1 : -1) * (int)poisson.getLargeur(),
                            (int)poisson.getHauteur(), false);
                    canvas.drawBitmap(image, (float)poisson.getX(),
                            (float)poisson.getY(), poissonPaints.get(poisson));

                }

                for(Projectile projectile : controleurPartie.getProjectiles()) {

                    canvas.drawColor(Color.BLACK);
                    canvas.drawCircle((float)projectile.getX(), (float)projectile.getY(),
                            (float)projectile.getDiametre() / 2, new Paint());

                }

                //TODO : Dessiner le nombre de vies.

                //TODO : Dessiner le score.

                //TODO : Dessiner le nombre de one shot kill.

            }
        }





        super.onDraw(canvas);
    }

    private void initOutilsDessin() {

        bullePaint = new Paint();
        bullePaint.setColor(getResources().getColor(R.color.bulle));
        bullePaint.setStyle(Paint.Style.FILL);

        msgCentrePaint = new Paint();
        msgCentrePaint.setColor(Color.WHITE);
        msgCentrePaint.setTextSize(250);

        poissonPaints = new WeakHashMap<>();
        poissonImages = new WeakHashMap<>();
        poissonCotes = new WeakHashMap<>();

        imgIdPoissons = new int[] {R.drawable.poisson00, R.drawable.poisson01,
                R.drawable.poisson02, R.drawable.poisson03, R.drawable.poisson04,
                R.drawable.poisson05, R.drawable.poisson06, R.drawable.poisson07};
        paintPoissons = new Paint[] {new Paint(Color.RED), new Paint(Color.GREEN),
                new Paint(Color.CYAN), new Paint(Color.LTGRAY), new Paint(Color.YELLOW)};
        random = new Random();

    }

    private class AnimationJeu extends Thread{

        private boolean jeuEnCours;
        private Object cadenasJeuEnCours;
        private long deltaTemps;
        
        private final long FRAME_RATE = 30;

        public AnimationJeu() {
            jeuEnCours = true;
            cadenasJeuEnCours = new Object();
            deltaTemps = 1000 / FRAME_RATE;
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
                            controleurPartie.actualiser(deltaTemps * 1e-3);
                        }
                        onDraw(canvas);
                    }

                } finally {

                    if(canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);

                }
                
                try {
                    Thread.sleep(deltaTemps);
                } catch (InterruptedException e) {}

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
