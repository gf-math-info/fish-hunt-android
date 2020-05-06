package fortin.leblanc.fishhuntandroid.controleur.activite;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import fortin.leblanc.fishhuntandroid.R;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_linearlayout);
        ListView listScore = (ListView) findViewById(R.id.list_scores);
        Button menuButton = (Button) findViewById(R.id.menu_button);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            mainLayout.setOrientation(LinearLayout.VERTICAL);
        }

        Intent intent = getIntent();
        int score = intent.getIntExtra(JeuActivity.SCORE, -1);


        //TODO : Chargement des données.

        menuButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent versAccueilIntent = new Intent(ScoreActivity.this, MainActivity.class);
                startActivity(versAccueilIntent);
            }

        });

        if(score == -1) {

            LinearLayout ajoutLayout = (LinearLayout) findViewById(R.id.ajout_layout);
            mainLayout.removeView(ajoutLayout);

        } else {

            TextView ajoutScoreTextview = (TextView) findViewById(R.id.ajout_score_textview);
            final EditText ajoutPseudoEditText = (EditText)
                    findViewById(R.id.ajout_pseudo_editText);
            Button ajoutButton = (Button) findViewById(R.id.ajout_button);

            ajoutScoreTextview.setText("Vous avez fait " + score + " point" +
                    (score > 1 ? "s." : "."));

            ajoutButton.setEnabled(false);
            ajoutPseudoEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ajoutPseudoEditText.setEnabled(s.toString().trim().length() > 0);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            ajoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO : ajout et sauvegarde des données.
                }
            });

        }
    }
}
