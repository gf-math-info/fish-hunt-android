package fortin.leblanc.fishhuntandroid.controleur;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GestionnaireRecordBDD extends SQLiteOpenHelper {

    public static final String NOM_TABLE = "Record";
    public static final String CLE = "id";
    public static final String NOM = "nom";
    public static final String SCORE = "score";

    public GestionnaireRecordBDD(Context context, String nom,
                                  SQLiteDatabase.CursorFactory cursorFactory, int version) {
        super(context, nom, cursorFactory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NOM_TABLE + " (" +
                CLE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOM + " TEXT NOT NULL, " +
                SCORE + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOM_TABLE + ";");
        onCreate(db);
    }
}
