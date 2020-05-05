package fortin.leblanc.fishhuntandroid.controleur.activite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import fortin.leblanc.fishhuntandroid.R;

public class MainActivity extends AppCompatActivity {

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
    }
}
