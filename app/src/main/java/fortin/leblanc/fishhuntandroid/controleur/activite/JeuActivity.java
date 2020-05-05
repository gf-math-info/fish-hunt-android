package fortin.leblanc.fishhuntandroid.controleur.activite;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;

import fortin.leblanc.fishhuntandroid.vue.VueJeu;

public class JeuActivity extends AppCompatActivity {

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

        setContentView(new VueJeu(this, largeur, hauteur));
    }
}
