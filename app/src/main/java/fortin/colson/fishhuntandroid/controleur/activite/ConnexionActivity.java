package fortin.colson.fishhuntandroid.controleur.activite;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import fortin.colson.fishhuntandroid.R;
import fortin.colson.fishhuntandroid.controleur.ConnexionServeur;

public class ConnexionActivity extends AppCompatActivity {

    private ConnexionServeur connexionServeur;

    private final int PSEUDO_ACCEPTE = 110;
    private final int PSEUDO_REFUSE = 111;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);

        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        final TextView informationsTextView = (TextView) findViewById(R.id.informations_textview);
        final EditText adresseEditText = (EditText) findViewById(R.id.adresse_edittext);
        final EditText portEditText = (EditText) findViewById(R.id.port_edittext);
        final Button connexionButton = (Button) findViewById(R.id.conexion_button);
        final LinearLayout pseudoLayout = (LinearLayout) findViewById(R.id.pseudo_layout);
        final EditText pseudoEditText = (EditText) findViewById(R.id.pseudo_edittext);
        final Button validerButton = (Button) findViewById(R.id.valider_button);
        final Button menuButton = (Button) findViewById(R.id.menu_button);

        mainLayout.removeView(pseudoLayout);

        adresseEditText.setText(ConnexionServeur.getAdresse());
        portEditText.setText(String.valueOf(ConnexionServeur.getPort()));

        //Initialisation des listeners.
        connexionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String adresse = adresseEditText.getText().toString(),
                        portString = portEditText.getText().toString();
                int port;

                try {
                    port = Integer.parseInt(portString);
                } catch (NumberFormatException e) {

                    Toast.makeText(ConnexionActivity.this, R.string.toast_port_connexion,
                            Toast.LENGTH_LONG).show();
                    return;

                }

                ConnexionServeur.setAdresse(adresse);
                ConnexionServeur.setPort(port);

                connexionButton.setEnabled(false);
                informationsTextView.setText(R.string.informations_connexion_textview_connexion);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            //Le premier appel à l'instance lance la connexion.
                            connexionServeur = ConnexionServeur.getInstance();
                            ConnexionActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    informationsTextView.setText(
                                            R.string.informations_connecte_textview_connexion);
                                    String pseudo = pseudoEditText.getText().toString();
                                    validerButton.setEnabled(0 < pseudo.length() &&
                                            pseudo.length() <= 10);
                                    mainLayout.addView(pseudoLayout, 2);
                                }
                            });

                        } catch (IOException e) {

                            ConnexionActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    informationsTextView.setText(
                                        R.string.informations_erreur_textview_connexion);
                                    connexionButton.setEnabled(true);//On permet un nouvel essai.
                                }
                            });

                        }

                    }
                }).start();
            }

        });

        pseudoEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validerButton.setEnabled(0 < s.length() && s.length() <= 10);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        validerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validerButton.setEnabled(false);
                menuButton.setEnabled(false);
                final String pseudo = pseudoEditText.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            PrintWriter output = connexionServeur.getOutput();
                            BufferedReader input = connexionServeur.getInput();

                            output.println(pseudo);
                            int reponse = input.read();

                            switch (reponse) {
                                case PSEUDO_ACCEPTE:
                                    ConnexionActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            informationsTextView.setText(R.string.informations_pseudo_accepte_textview_connexion);
                                            //TODO : Intent vers la partie multijoueur.
                                        }
                                    });
                                    break;

                                case PSEUDO_REFUSE:
                                    ConnexionActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            informationsTextView.setText(R.string.informations_pseudo_refuse_textview_connexion);
                                            validerButton.setEnabled(true);
                                            menuButton.setEnabled(true);
                                        }
                                    });
                                    break;

                                default:
                                    throw new IOException();
                            }

                        } catch (IOException e) {

                            connexionServeur.ferme();
                            ConnexionActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    informationsTextView.setText(
                                            R.string.informations_erreur_textview_connexion);
                                    mainLayout.removeView(pseudoLayout);
                                    connexionButton.setEnabled(true);
                                    menuButton.setEnabled(true);

                                }
                            });

                        }

                    }
                }).start();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connexionServeur != null)
                    connexionServeur.ferme();
                Intent versAccueilIntent = new Intent(ConnexionActivity.this,
                        MainActivity.class);
                startActivity(versAccueilIntent);
            }
        });
    }
}
