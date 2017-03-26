package com.example.apple.mymemoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by apple on 2017/03/19.
 */

public class MemoDBHelper extends SQLiteOpenHelper {
    //データベース名(ここに外部へのパスを書けば外部に保存される)
    private static final String DB_NAME = "memo.db";
    //バージョン
    private static final int DB_VERSION = 1;
    //テーブル名
    public static final String TABLE_NAME = "memo";
    //IDカラム
    public static final String _ID = "_id";
    //ファイル名カラム
    public static final String TITLE ="title";
    //ファイルパスカラム
    public static final String DATA = "_data";
    //作成日時
    public static final String DATE_ADDED = "data_added";
    //更新日時
    public static final String DATE_MODIFIED = "data_modified";

    //コンストラクタでデータベース名とバージョンを指定
    public MemoDBHelper(Context context){
        super(context, DB_NAME, null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable =
                "CREATE TABLE " + TABLE_NAME + " ( " +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TITLE + " TEXT, " +
                        DATA + " TEXT, " +
                        DATE_ADDED + " INTEGER NOT NULL, " +
                        DATE_MODIFIED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
                        " ) ";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVesion, int newVersion){
        //バージョン管理をここで行う

    }
}
