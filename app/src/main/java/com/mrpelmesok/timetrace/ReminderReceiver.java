package com.mrpelmesok.timetrace;
import android.app.*;
import android.content.*;
import android.os.Build;
public class ReminderReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c,Intent i){
  if(Build.VERSION.SDK_INT>=26){NotificationChannel ch=new NotificationChannel("daily","Ежедневная статистика",NotificationManager.IMPORTANCE_DEFAULT);((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(ch);}
  Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(c,"daily"):new Notification.Builder(c);
  b.setSmallIcon(android.R.drawable.ic_menu_recent_history).setContentTitle("TimeTrace").setContentText("Загляни в статистику использования телефона за сегодня.").setAutoCancel(true);
  ((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).notify(99,b.build());
 }
}
