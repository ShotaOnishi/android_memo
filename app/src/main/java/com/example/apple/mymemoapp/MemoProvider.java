package com.example.apple.mymemoapp;

import android.content.ContentProvider;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by apple on 2017/03/24.
 */

public class MemoProvider extends ContentProvider {
    //<authority>
    private static final String AUTHORITY = "com.example.android.sample.mymemoapp.memo";

    //<path>
    private static final String CONTENT_PATH = "files";

    //MIMEタイプの接頭語
    //複数要素にはvnd.android.cursor.dir
    //単一要素にはvnd.android.cursor.itemを使用するとされている
    public static final String MIME_DIR_PREFIX = "vnd.android.cursor.dir/";
    public static final String MIME_ITEM_PREFIX = "vnd.android.cursor.item/";

    //独自のMIMEタイプを設定する
    public static final String MIME_ITEM ="vnd.memoapp.memo";
    public static final String MIME_TYPE_MULTIPLE = MIME_DIR_PREFIX + MIME_ITEM;
    public static final String MIME_TYPE_SINGLE = MIME_ITEM_PREFIX + MIME_ITEM;

    //このContentProviderがハンドルするURI
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);

    //メモリストのリクエスト
    private static final int URI_MATCH_MEMO_LIST = 1;
    //単一のメモのリクエスト
    private static final int URI_MATCH_MEMO_ITEM = 2;

    //URIとの一致をチェックするUriMatcher
    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        //idが指定されていない場合
        sMatcher.addURI(AUTHORITY, CONTENT_PATH, URI_MATCH_MEMO_LIST);
        //idが指定されている場合
        sMatcher.addURI(AUTHORITY, CONTENT_PATH + "/#", URI_MATCH_MEMO_ITEM);
    }

    //データの保存に関するデータベース
    private SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate(){
        //SQLiteDatabseオブジェクトを取得する
        MemoDBHelper helper = new MemoDBHelper(getContext());
        mDatabase = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        //URIが正しいことをチェックしておく
        int match = sMatcher.match(uri);

        Cursor cursor;
        switch(match){
            case URI_MATCH_MEMO_LIST:
                cursor = mDatabase.query(MemoDBHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case URI_MATCH_MEMO_ITEM:
                String id = uri.getLastPathSegment();

                cursor = mDatabase.query(MemoDBHelper.TABLE_NAME,
                        //条件にIDを追加する
                        projection, MemoDBHelper._ID + "=" + id
                        + (TextUtils.isEmpty(selection) ?
                        "" : "AND (" + selection + ")"),
                        selectionArgs, null, null, sortOrder);

                break;
            default:
                throw new IllegalArgumentException("invalid uri: " + uri);

        }

        //指定したURIへの通知イベントを受診するようにする
        Context context = getContext();
        if(context != null){
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }
    }
}
