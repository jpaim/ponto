package com.jp.ponto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public final class MarcacaoDbAdapter {

    public static final String KEY_ROWID = "_id";


    /*public static final String KEY_HORA_BR = "CASE " +
            "WHEN CAST (StrFTime('%H', hora) AS INTEGER) = 12 THEN '12:' || StrFTime('%M', hora) || ' PM' " +
            "WHEN CAST (StrFTime('%H', hora) AS INTEGER) > 21 THEN (StrFTime('%H', hora) - 12) || ':' || StrFTime('%M', hora) || ' PM'" +
            "WHEN CAST (StrFTime('%H', hora) AS INTEGER) > 12 THEN SUBSTR('0' || (StrFTime('%H', hora) - 12) || ':' || StrFTime('%M', hora) || ' PM', 1, 8 ) " +
            "WHEN CAST (StrFTime('%H', hora) AS INTEGER) = 0  THEN '12:' || StrFTime('%M', hora) || ' AM' " +
            "ELSE StrFTime('%H:%M', hora) || ' AM' " +
            "END AS fhora "; */
    public static final String KEY_HORA_BR = "StrFTime('%H:%M', hora) AS fhora ";
    public static final String KEY_HORA = "hora";
    public static final String KEY_FHORA = "fhora";
    public static final String KEY_ORDER = "hora";
    public static final String KEY_DATA_BR = "(strftime('%d/%m/%Y ', hora)) as data";
    public static final String KEY_DATA = "data";
    //public static final String KEY_GAP = "'' as GAP";
    private static final String TAG = "MarcacaoDbAdapter";
    private static final String DATABASE_NAME = "Ponto";
    private static final String SQLITE_TABLE = "Marcacao";
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_HORA + ");";
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public MarcacaoDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public MarcacaoDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public long createMarcacao(String hora) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_HORA, hora);
        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }
//
//    public int getTotalDia() {
//        Cursor mCursor;
//
//        Marcacoes m = new Marcacoes();
//        String hoje = m.getTimeString().substring(0, 10);
//        mCursor = mDb.rawQuery("SELECT COUNT(*) AS C FROM " + SQLITE_TABLE + " WHERE date(hora) = '" + hoje + "'", null);
//        mCursor.moveToFirst();
//        int i = mCursor.getInt(0);
//        mCursor.close();
//        return i;
//    }

    public ArrayList<Marcacoes> listaMarcacoes(String dt) {
        ArrayList<Marcacoes> m = new ArrayList<>();
        Cursor cursor_marcacao = fetchAllMarcacoes(dt);
        cursor_marcacao.moveToFirst();
        while (!cursor_marcacao.isAfterLast()) {
            int tId = cursor_marcacao.getInt(0);
            String tData = cursor_marcacao.getString(1);
            String tHora = cursor_marcacao.getString(2);
            m.add(new Marcacoes(tData, tHora, tId));
            cursor_marcacao.moveToNext();
        }
        cursor_marcacao.close();

        return m;
    }


    public boolean deleteAllMarcacoes() {
        int doneDelete;
        doneDelete = mDb.delete(SQLITE_TABLE, null, null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }

    public boolean deleteMarcacao(String numID) {
        int doneDelete;
        doneDelete = mDb.delete(SQLITE_TABLE, KEY_ROWID + "=" + numID, null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }

    public Cursor fetchAllMarcacoes(String dt) {
        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{KEY_ROWID, KEY_DATA_BR, KEY_HORA_BR}, "date(hora) = ?", new String[]{dt}, null, null, KEY_ORDER);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }
    }


}


