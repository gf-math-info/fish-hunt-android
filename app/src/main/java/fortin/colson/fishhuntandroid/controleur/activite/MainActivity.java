package fortin.colson.fishhuntandroid.controleur.activite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import fortin.colson.fishhuntandroid.R;

/**
 * L'activité principale. Elle affiche la page d'accueil.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Crée l'activité représentant la page d'accueil.
     * @param savedInstanceState    Le "bundle" gardant les données de l'activité.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Pour que le menu s'affiche correctement.
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_linearlayout);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            mainLayout.setOrientation(LinearLayout.VERTICAL);
        }

        Button nouvellePartieButton = (Button) findViewById(R.id.nouvelle_partie);
        Button meilleursScoresButton = (Button) findViewById(R.id.meilleurs_scores);
        Button multijoueursButton = (Button) findViewById(R.id.multijoueurs);

        nouvellePartieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent versNouvellePartieIntent = new Intent(MainActivity.this, JeuActivity.class);
                startActivity(versNouvellePartieIntent);
            }
        });

        meilleursScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent versMeilleursScoresIntent = new Intent(MainActivity.this, fortin.colson.fishhuntandroid.controleur.activite.ScoreActivity.class);
                startActivity(versMeilleursScoresIntent);
            }
        });

        multijoueursButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
    }
}
