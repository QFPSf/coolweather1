package com.example.coolweather1.gson;

import com.google.gson.annotations.SerializedName;

public class AQI {
    public AQIcity city;
    public static class AQIcity{
        public String aqi;
        public String pm25;
    }
}
