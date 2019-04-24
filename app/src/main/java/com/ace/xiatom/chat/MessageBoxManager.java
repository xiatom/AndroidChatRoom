package com.ace.xiatom.chat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiatom on 2019/4/24.
 */

public class MessageBoxManager {

    SQLiteDatabase db;
    public MessageBoxManager(SQLiteDatabase db){
        this.db = db;
    }
    public List<chat_content> getMessages(){
        List<chat_content> messages = new ArrayList<>();
        Cursor cursor = db.query("messageHistory",null,null,null,null,null,null);
        if(cursor.moveToFirst())
            do{
                String message = cursor.getString(cursor.getColumnIndex("message"));
                int fromOther = cursor.getInt(cursor.getColumnIndex("fromOther"));
                chat_content m = new chat_content(fromOther==-1?false:true,message);
                messages.add(m);
            }while(cursor.moveToNext());
        return messages;
    }

    public void insertMeg(chat_content m){
        ContentValues values = new ContentValues();
        values.put("fromOther",m.getFromOther()?1:-1);
        values.put("message",m.getContent());
        db.insert("messageHistory",null,values);
        Log.i("db","insert suc");
    }
}
