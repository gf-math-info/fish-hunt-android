package fortin.leblanc.fishhuntandroid.controleur;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fortin.leblanc.fishhuntandroid.modele.Record;

public class RecordDAO {

    private final int VERSION = 1;
    private final String NOM = "score.db";
    private GestionnaireRecordBDD gestionnaireRecordBDD;

    public RecordDAO(Context context) {
        gestionnaireRecordBDD = new GestionnaireRecordBDD(context, NOM, null, VERSION);
    }

    public void ajout(Record record) {
        SQLiteDatabase bdd = gestionnaireRecordBDD.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(GestionnaireRecordBDD.NOM, record.getNom());
        contentValues.put(GestionnaireRecordBDD.SCORE, record.getScore());

        bdd.insert(GestionnaireRecordBDD.NOM_TABLE, null, contentValues);
        bdd.close();
    }

    public void supprimer(Record record) {
        SQLiteDatabase bdd = gestionnaireRecordBDD.getWritableDatabase();

        bdd.execSQL("DELETE FROM " + GestionnaireRecordBDD.NOM_TABLE + " WHERE " +
                GestionnaireRecordBDD.NOM + " = " + record.getNom() + ", " +
                GestionnaireRecordBDD.SCORE + " = " + record.getScore() + " LIMIT 1");

        bdd.close();
    }

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
