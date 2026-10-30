package com.mrpelmesok.timetrace;
import android.content.*;
import android.app.*;
public class BootReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c,Intent i){
  Intent refresh=new Intent(c,RefreshReceiver.class); PendingIntent pi=PendingIntent.getBroadcast(c,41,refresh,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
  am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis()+60000, 15*60*1000, pi);
 }
}
