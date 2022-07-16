package com.example.coolweather1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
        String weather=pref.getString("weather",null);
        if (weather!=null){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
        }
    }
}