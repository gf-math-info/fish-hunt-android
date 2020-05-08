package fortin.colson.fishhuntandroid.vue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class VueJeuMulti extends VueJeu {

    private Paint msgMultiJoueur;

    /**
     * Construit la vue du jeu avec le contexte et les dimensions en pixels de l'écran.
     *
     * @param context Le contexte de l'activité.
     * @param largeur La largeur de l'écran.
     * @param hauteur La hauteur de l'écran.
     */
    public VueJeuMulti(Context context, int largeur, int hauteur) {
        super(context, largeur, hauteur);

        msgMultiJoueur = new Paint();
        msgMultiJoueur.setColor(Color.WHITE);
        msgMultiJoueur.setTextSize(30);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }
}
