package fortin.leblanc.fishhuntandroid.controleur;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Cette classe aide à la création et à la gestion de la base de données.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class GestionnaireRecordBDD extends SQLiteOpenHelper {

    public static final String NOM_TABLE = "Record";
    public static final String CLE = "id";
    public static final String NOM = "nom";
    public static final String SCORE = "score";

    /**
     * Construit un gestionnaire de la base de données des records.
     * @param context       Le contexte de l'application.
     * @param nom           Le nom de la table.
     * @param cursorFactory Un "CursorFactory" pour simplifier les requêtes.
     * @param version       La version de la base de données.
     */
    public GestionnaireRecordBDD(Context context, String nom,
                                  SQLiteDatabase.CursorFactory cursorFactory, int version) {
        super(context, nom, cursorFactory, version);
    }

    /**
     * Crée la base de données.
     * @param db    L'outil utilisé pour envoyer les requêtes.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NOM_TABLE + " (" +
                CLE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOM + " TEXT NOT NULL, " +
                SCORE + " INTEGER);");
    }

    /**
     * Efface la base de données de l'ancienne version.
     * @param db            L'outil utilisé pour envoyer les requêtes.
     * @param oldVersion    Le numéro de l'ancienne version.
     * @param newVersion    Le numéro de la nouvelle version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOM_TABLE + ";");
        onCreate(db);
    }
}
