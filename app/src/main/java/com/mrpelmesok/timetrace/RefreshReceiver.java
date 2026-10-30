package com.mrpelmesok.timetrace;
import android.content.*;
public class RefreshReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c,Intent i){ final PendingResult pending=goAsync(); new Thread(()->{try{new UsageTracker(c).sync();}finally{pending.finish();}}).start();}
}
