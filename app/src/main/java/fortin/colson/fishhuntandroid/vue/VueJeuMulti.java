package fortin.colson.fishhuntandroid.vue;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.Toast;

import fortin.colson.fishhuntandroid.controleur.activite.ScoreActivity;
import fortin.colson.fishhuntandroid.controleur.multijoueur.ControleurPartieMulti;

public class VueJeuMulti extends VueJeu {

    private ControleurPartieMulti controleurPartieMulti;
    private Paint msgMultiJoueurPaint;

    /**
     * Construit la vue du jeu avec le contexte et les dimensions en pixels de l'écran.
     *
     * @param context               Le contexte de l'activité.
     * @param controleurPartieMulti Le contrôleur de la partie en mode multijouer.
     */
    public VueJeuMulti(Context context, ControleurPartieMulti controleurPartieMulti) {
        super(context, controleurPartieMulti);
        this.controleurPartieMulti = controleurPartieMulti;

        msgMultiJoueurPaint = new Paint();
        msgMultiJoueurPaint.setColor(Color.WHITE);
        msgMultiJoueurPaint.setTextSize(40);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(controleurPartieMulti.getErreurConnexion()) {

            Toast.makeText(getContext(), "Un erreur de connexion s'est produit.",
                    Toast.LENGTH_LONG).show();
            Intent versScoresIntent = new Intent(getContext(), ScoreActivity.class);
            versScoresIntent.putExtra(ScoreActivity.SCORE, controleurPartieMulti.getScore());
            getContext().startActivity(versScoresIntent);

        } else
            super.onDraw(canvas);

        String msg = controleurPartieMulti.getMsgMultijoueurAfficher();
        if(msg != null)
            canvas.drawText(msg, 10, canvas.getHeight() - 30, msgMultiJoueurPaint);
    }
}
