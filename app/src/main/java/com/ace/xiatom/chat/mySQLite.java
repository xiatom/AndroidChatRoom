package com.ace.xiatom.chat; /**
 * Created by xiatom on 2019/4/24.
 */


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by xiatom on 2019/3/29.
 */

public class mySQLite extends SQLiteOpenHelper{
    private final String CREATE_MESSAGE = "create table messageHistory(id integer primary key autoincrement,message text,fromOther integer)";
    private Context myContext;

    public mySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        myContext = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_MESSAGE);
        Log.i("msg","create SUCCESSFULLY");
    }
}
