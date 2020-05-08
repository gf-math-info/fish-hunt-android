package fortin.colson.fishhuntandroid.controleur;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fortin.colson.fishhuntandroid.modele.Record;

/**
 * Cette classe est implémentée selon le patron "data access object" pour aider à faire le lien entre
 * la base de données et le reste du programme.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class RecordDAO {

    private final int VERSION = 1;
    private final String NOM = "score.db";
    private GestionnaireRecordBDD gestionnaireRecordBDD;

    /**
     * Construit le "data access object".
     * @param context   Le contexte de l'activité.
     */
    public RecordDAO(Context context) {
        gestionnaireRecordBDD = new GestionnaireRecordBDD(context, NOM, null, VERSION);
    }

    /**
     * Ajoute le record passé en paramètre à la base de données.
     * @param record    Le record à ajouter.
     */
    public void ajout(Record record) {
        SQLiteDatabase bdd = gestionnaireRecordBDD.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(GestionnaireRecordBDD.NOM, record.getNom());
        contentValues.put(GestionnaireRecordBDD.SCORE, record.getScore());

        bdd.insert(GestionnaireRecordBDD.NOM_TABLE, null, contentValues);
        bdd.close();
    }

    /**
     * Supprime le record passé en paramètre de la base de donnée. Si plusieurs records dans la base
     * de données ont le même nom et le même score que le record passé en paramètre, alors un seul
     * record sera supprimé de la base de données.
     * @param record    Le record à supprimer.
     */
    public void supprimer(Record record) {
        SQLiteDatabase bdd = gestionnaireRecordBDD.getWritableDatabase();

        Cursor cursor = bdd.query(GestionnaireRecordBDD.NOM_TABLE,
                new String[] {GestionnaireRecordBDD.CLE}, null, null,
                null, null, GestionnaireRecordBDD.SCORE + " ASC", "1");
        cursor.moveToFirst();
        int cle = cursor.getInt(0);

        bdd.delete(GestionnaireRecordBDD.NOM_TABLE,
                GestionnaireRecordBDD.CLE + " = " + cle, null);

        bdd.close();
    }

    /**
     * Retourne la liste de tous les records dans la base de données.
     * @return  La liste de tous les records dans la base de données.
     */
    public List<Record> getListe() {
        List<Record> records = new ArrayList<>();
        SQLiteDatabase bdd = gestionnaireRecordBDD.getReadableDatabase();

        Cursor cursor = bdd.query(GestionnaireRecordBDD.NOM_TABLE,
                new String[] {GestionnaireRecordBDD.NOM, GestionnaireRecordBDD.SCORE},
                null, null, null, null,
                GestionnaireRecordBDD.SCORE + " DESC", null);

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            records.add(new Record(cursor.getString(0), cursor.getInt(1)));
        }

        cursor.close();
        bdd.close();
        return records;
    }
}
