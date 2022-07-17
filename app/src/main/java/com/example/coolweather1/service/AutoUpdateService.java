package com.example.coolweather1.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.coolweather1.gson.Weather;
import com.example.coolweather1.util.HttpUtil;
import com.example.coolweather1.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int StartId){
        updateWeather();
        updateBingPic();
        AlarmManager manager=(AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour=5*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        /*NotificationChannel mChannel = new NotificationChannel("1", "2", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager1=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager1.createNotificationChannel(mChannel);
        Notification notification = new Notification.Builder(this)
                .setChannelId("1")
                .setContentIntent(pi)
                .setContentTitle("通知")
                .setContentText("定时任务正在后台")
                .build();
        startForeground(1,notification);*/
        return super.onStartCommand(intent,flags,StartId);
    }
    public void updateWeather(){
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=pref.getString("weather",null);
        if (weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            String weatherId=weather.basic.weatherId;
            String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=e3f8d76ee06c4222bdbc4601c73fd58a";
            HttpUtil.sendOKHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseText=response.body().string();
                    Weather weather=Utility.handleWeatherResponse(responseText);
                    if (weather!=null&"ok".equals(weather.status)) {
                        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this)
                                .edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    public void updateBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOKHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }
}