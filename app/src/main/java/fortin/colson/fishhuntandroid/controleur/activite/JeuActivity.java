package fortin.colson.fishhuntandroid.controleur.activite;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;

import fortin.colson.fishhuntandroid.vue.VueJeu;

/**
 * Cette classe représente l'activité du jeu.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class JeuActivity extends AppCompatActivity {

    public final static String SCORE = "scoreActivity.SCORE";

    private VueJeu vueJeu;

    /**
     * Crée l'activité du jeu.
     * @param savedInstanceState    Le "bundle" contenant les données de l'activité.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //On cherche la grandeur du téléphone.
        /*
        Ce code est tiré de StackOverFlow :
        https://stackoverflow.com/questions/1016896/how-to-get-screen-dimensions-as-pixels-in-android
        */
        Display display = getWindowManager().getDefaultDisplay();
        Point grandeur = new Point();
        display.getSize(grandeur);
        int largeur = grandeur.x;
        int hauteur = grandeur.y;

        vueJeu = new VueJeu(this, largeur, hauteur);
        setContentView(vueJeu);
    }

    /**
     * Met fin à l'animation du jeu lorsque l'activité se met en pause.
     */
    @Override
    protected void onPause() {
        super.onPause();
        vueJeu.finAnimationJeu();
    }
}
