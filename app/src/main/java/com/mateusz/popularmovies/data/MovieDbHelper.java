package com.mateusz.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.mateusz.popularmovies.data.MovieContract.MoviesEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moviesDb.db";
    private static final int DATABASE_VERSION = 2;

    MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "+MoviesEntry.TABLE_NAME+" ("+
                                    MoviesEntry._ID                       +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                                    MoviesEntry.COLUMN_TITLE              +" TEXT NOT NULL, "+
                                    MoviesEntry.COLUMN_POSTER             +" BLOB, "+
                                    MoviesEntry.COLUMN_RELEASE_DATE       +" TEXT NOT NULL, "+
                                    MoviesEntry.COLUMN_RATING             +" TEXT NOT NULL, "+
                                    MoviesEntry.COLUMN_PLOT               +" TEXT NOT NULL);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
