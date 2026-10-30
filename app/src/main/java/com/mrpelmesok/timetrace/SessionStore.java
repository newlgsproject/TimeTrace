package com.mrpelmesok.timetrace;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.*;

public class SessionStore extends SQLiteOpenHelper {
    public SessionStore(Context c) { super(c, "timetrace.db", null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE sessions(id INTEGER PRIMARY KEY AUTOINCREMENT, pkg TEXT NOT NULL, started INTEGER NOT NULL, ended INTEGER NOT NULL, duration INTEGER NOT NULL, UNIQUE(pkg,started))");
        db.execSQL("CREATE INDEX idx_sessions_started ON sessions(started)");
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldV,int newV) {}
    public synchronized void put(String pkg,long start,long end) {
        if(pkg==null || pkg.isEmpty() || end<=start) return;
        ContentValues v=new ContentValues(); v.put("pkg",pkg); v.put("started",start); v.put("ended",end); v.put("duration",end-start);
        getWritableDatabase().insertWithOnConflict("sessions",null,v,SQLiteDatabase.CONFLICT_REPLACE);
    }
    public synchronized void updateEnd(String pkg,long start,long end) {
        if(end<=start) return;
        ContentValues v=new ContentValues(); v.put("ended",end); v.put("duration",end-start);
        getWritableDatabase().update("sessions",v,"pkg=? AND started=?",new String[]{pkg,String.valueOf(start)});
    }
    public synchronized ArrayList<Session> list(long from,long to) {
        ArrayList<Session> out=new ArrayList<>();
        try(Cursor c=getReadableDatabase().rawQuery("SELECT pkg,started,ended,duration FROM sessions WHERE started < ? AND ended > ? ORDER BY started DESC",new String[]{String.valueOf(to),String.valueOf(from)})) {
            while(c.moveToNext()) out.add(new Session(c.getString(0),c.getLong(1),c.getLong(2),c.getLong(3)));
        }
        return out;
    }
    public synchronized void clear(){getWritableDatabase().delete("sessions",null,null);}
    public static class Session { public final String pkg; public final long start,end,duration; Session(String p,long s,long e,long d){pkg=p;start=s;end=e;duration=d;} }
}
