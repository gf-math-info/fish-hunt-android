package fortin.colson.fishhuntandroid.controleur.activite;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import fortin.colson.fishhuntandroid.controleur.RecordDAO;
import fortin.colson.fishhuntandroid.modele.Record;
import fortin.colson.fishhuntandroid.R;

/**
 * Cette classe représente l'activité de la page des meilleurs scores.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class ScoreActivity extends AppCompatActivity {

    public final static String SCORE = "scoreActivity.SCORE";

    /**
     * Crée l'activité de la page des meilleurs scores.
     * @param savedInstanceState    Le "bundle" contenant les informations de l'activité.
     */
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

        final RecordDAO recordDAO = new RecordDAO(this);
        final List<Record> records = recordDAO.getListe();
        final ArrayAdapter<Record> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, records);
        listScore.setAdapter(arrayAdapter);

        Intent intent = getIntent();
        final int score = intent.getIntExtra(ScoreActivity.SCORE, -1);

        menuButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent versAccueilIntent = new Intent(ScoreActivity.this,
                        MainActivity.class);
                startActivity(versAccueilIntent);
            }

        });

        /*Si, dans l'"intent", il n'y a pas de score ou s'il y a 10 scores et que le score dans
        l'"intent" est inférieur au plus petit score dans la liste des meilleurs score, alors on
        retire la section permettant au joueur d'ajouter son score.*/
        if(score == -1 || (records.size() == 10 && records.get(9).getScore() >= score)) {

            retraitSectionAjout();

        } else {

            TextView ajoutScoreTextview = (TextView) findViewById(R.id.ajout_score_textview);
            final EditText ajoutPseudoEditText = (EditText)
                    findViewById(R.id.ajout_pseudo_editText);
            final Button ajoutButton = (Button) findViewById(R.id.ajout_button);

            ajoutScoreTextview.setText("Vous avez fait " + score + " point" +
                    (score > 1 ? "s." : "."));

            ajoutButton.setEnabled(false);
            ajoutPseudoEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ajoutButton.setEnabled(s.toString().trim().length() > 0);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            ajoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(records.size() == 10)
                        recordDAO.supprimer(records.get(9));

                    recordDAO.ajout(new Record(ajoutPseudoEditText.getText().toString(), score));
                    arrayAdapter.clear();
                    arrayAdapter.addAll(recordDAO.getListe());

                    retraitSectionAjout();
                }
            });

        }
    }

    /**
     * Retire la section d'ajout de scores.
     */
    private void retraitSectionAjout() {
        LinearLayout ajoutButtonsLayout = (LinearLayout)
            findViewById(R.id.ajout_buttons_layout);
        LinearLayout ajoutLayout = (LinearLayout) findViewById(R.id.ajout_layout);
        ajoutButtonsLayout.removeView(ajoutLayout);
    }
}
