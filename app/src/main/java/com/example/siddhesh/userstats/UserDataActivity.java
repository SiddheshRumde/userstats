package com.example.siddhesh.userstats;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        final Button runningBtn = (Button)findViewById(R.id.running);
        final Button installedBtn = (Button)findViewById(R.id.installed);
        final Button timeBtn = (Button)findViewById(R.id.timestats);
        final Button networkStatsBtn = (Button)findViewById(R.id.network);

        final EditText result = (EditText)findViewById(R.id.output);
        result.setScroller(new Scroller(getApplicationContext()));
        result.setVerticalScrollBarEnabled(true);
        result.setHorizontalScrollBarEnabled(true);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        final UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        final List<UsageStats> queryUsageStat = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, calendar.getTimeInMillis(), System.currentTimeMillis());

        final PackageManager packageManager = getPackageManager();

        runningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = activityManager.getRunningAppProcesses();
                StringBuffer stringBuffer = new StringBuffer("\nRunning Apps: ");
                if(runningAppProcessInfos != null){
                    for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos){
                        try {
                            stringBuffer.append("\n" + packageManager.getApplicationLabel(packageManager.getApplicationInfo(runningAppProcessInfo.processName, PackageManager.GET_META_DATA)));
                        }catch (PackageManager.NameNotFoundException e){
                            Log.e("USERDATA", e.toString());
                        }
                    }
                }
                if(result != null) {
                    result.append(stringBuffer.toString());
                }
            }
        });

        installedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer stringBuffer = new StringBuffer("\nInstalled Apps: ");
                final List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
                for(ApplicationInfo applicationInfo : installedApps){
                    try {
                        stringBuffer.append("\n" + packageManager.getApplicationLabel(packageManager.getApplicationInfo(applicationInfo.processName, PackageManager.GET_META_DATA)));
                    }catch (PackageManager.NameNotFoundException e){
                        Log.e("UserData", e.toString());
                    }
                }
                if(result != null) {
                    result.append(stringBuffer.toString());
                }
            }
        });

        networkStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer stringBuffer = new StringBuffer("\nNetwork Statistics:");
                stringBuffer.append("\nTotal bytes sent since boot: " + TrafficStats.getTotalTxBytes());
                stringBuffer.append("\nTotal bytes received since boot: " + TrafficStats.getTotalRxBytes());
                stringBuffer.append("\nTotal bytes sent across mobile networks since boot: " + TrafficStats.getMobileTxBytes());
                stringBuffer.append("\nTotal bytes received across mobile networks since boot: " + TrafficStats.getMobileRxBytes());
                result.append(stringBuffer);
            }
        });

        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.facebook.katana", 0);
                    String appSource = applicationInfo.sourceDir;
                    Date date = new Date(new File(appSource).lastModified());
                    result.append("\n Last Time Facebook was used: " + date.toString());
                }catch (PackageManager.NameNotFoundException e){
                    Log.e("TimeStats", e.toString());
                }
            }
        });
    }
}
