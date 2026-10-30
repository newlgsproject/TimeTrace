package com.mrpelmesok.timetrace;

import android.app.*;
import android.app.usage.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import java.util.*;

public class UsageTracker {
    private final Context context;
    private final SessionStore store;
    public UsageTracker(Context c){context=c.getApplicationContext();store=new SessionStore(context);}
    public boolean hasAccess(){try{AppOpsManager a=(AppOpsManager)context.getSystemService(Context.APP_OPS_SERVICE);int m=a.checkOpNoThrow("android:get_usage_stats",android.os.Process.myUid(),context.getPackageName());return m==AppOpsManager.MODE_ALLOWED;}catch(Exception e){return false;}}
    public void sync() {
        if(!hasAccess()) return;
        UsageStatsManager us=(UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        long now=System.currentTimeMillis();
        // Rebuild a rolling window. The app's own package is excluded.
        long from=now-7L*24*60*60*1000;
        UsageEvents events=us.queryEvents(from,now);
        if(events==null) return;
        UsageEvents.Event ev=new UsageEvents.Event();
        String active=null; long activeStart=0;
        HashMap<String,Long> starts=new HashMap<>();
        while(events.hasNextEvent()){
            events.getNextEvent(ev);
            String pkg=ev.getPackageName(); if(pkg==null || pkg.equals(context.getPackageName())) continue;
            int type=ev.getEventType(); long t=ev.getTimeStamp();
            boolean resumed=(Build.VERSION.SDK_INT>=29 && type==UsageEvents.Event.ACTIVITY_RESUMED) || type==UsageEvents.Event.MOVE_TO_FOREGROUND;
            boolean paused=(Build.VERSION.SDK_INT>=29 && type==UsageEvents.Event.ACTIVITY_PAUSED) || type==UsageEvents.Event.MOVE_TO_BACKGROUND;
            if(resumed){
                if(active!=null && activeStart>0 && t>activeStart) store.put(active,activeStart,t);
                active=pkg; activeStart=t; starts.put(pkg,t);
            } else if(paused && pkg.equals(active)) {
                if(activeStart>0 && t>activeStart) store.put(pkg,activeStart,t);
                active=null; activeStart=0;
            }
        }
        // Do not persist an unfinished interval as a completed session. It is closed by the next sync.
        context.getSharedPreferences("tracker",0).edit().putLong("last_sync",now).apply();
    }
    public Map<String,Long> aggregate(long from,long to){
        HashMap<String,Long> map=new HashMap<>();
        for(SessionStore.Session s:store.list(from,to)){
            long a=Math.max(from,s.start), b=Math.min(to,s.end); if(b>a) map.put(s.pkg,map.getOrDefault(s.pkg,0L)+(b-a));
        }
        return map;
    }
    public List<SessionStore.Session> sessions(String pkg,long from,long to){ArrayList<SessionStore.Session> a=new ArrayList<>();for(SessionStore.Session s:store.list(from,to))if(pkg==null||pkg.equals(s.pkg))a.add(s);return a;}
    public String label(String pkg){try{PackageManager pm=context.getPackageManager();return pm.getApplicationLabel(pm.getApplicationInfo(pkg,0)).toString();}catch(Exception e){return pkg;}}
    public android.graphics.drawable.Drawable icon(String pkg){try{return context.getPackageManager().getApplicationIcon(pkg);}catch(Exception e){return null;}}
    public List<String> installedApps(){ArrayList<String> out=new ArrayList<>();PackageManager pm=context.getPackageManager();Intent i=new Intent(Intent.ACTION_MAIN,null);i.addCategory(Intent.CATEGORY_LAUNCHER);for(ResolveInfo r:pm.queryIntentActivities(i,0)){String p=r.activityInfo.packageName;if(!p.equals(context.getPackageName())&&!out.contains(p))out.add(p);}Collections.sort(out,(a,b)->label(a).compareToIgnoreCase(label(b)));return out;}
}
