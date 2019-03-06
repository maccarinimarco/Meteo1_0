package com.example.meteo1_0;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestWeather {
    public static String citta;
    public RequestWeather(String citta) {
        this.citta = citta;//.replaceAll("\\s+", "");
    }

    public static void main(String[] args) {
        try {
            RequestWeather.call_me();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static City call_me() throws Exception {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + citta.replaceAll("\\s+", "") + "&APPID=c200173e4aeed3198803206f96382afe\n";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());
        JSONObject myResponse = new JSONObject(response.toString());
        System.out.println("result after Reading JSON Response");
        JSONArray array = new JSONArray(myResponse.getString("weather"));
        JSONObject ob = array.getJSONObject(0);
        String description = ob.getString("description");
        String icon = ob.getString("icon");
        JSONObject mai = new JSONObject(myResponse.getString("main"));
        double temp = Double.parseDouble(mai.getString("temp"));
        double min = Double.parseDouble(mai.getString("temp_min"));
        double max = Double.parseDouble(mai.getString("temp_max"));
        JSONObject sys = new JSONObject(myResponse.getString("sys"));
        String d = sys.getString("country");
        City appo = new City(citta, d, description, temp, min, max, icon);
        Log.e("oo", appo.toString());
        return (appo);
    }


    public static City call_me_lat_lon(double lat, double lon) throws IOException, JSONException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&APPID=c200173e4aeed3198803206f96382afe\n";
        URL obj = null;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());
        JSONObject myResponse = new JSONObject(response.toString());
        System.out.println("result after Reading JSON Response");
        JSONArray array = new JSONArray(myResponse.getString("weather"));
        JSONObject ob = array.getJSONObject(0);
        String description = ob.getString("description");
        JSONObject mai = new JSONObject(myResponse.getString("main"));
        double temp = Double.parseDouble(mai.getString("temp"));
        double min = Double.parseDouble(mai.getString("temp_min"));
        double max = Double.parseDouble(mai.getString("temp_max"));
        JSONObject sys = new JSONObject(myResponse.getString("sys"));
        String d = null;
        try {
            d = sys.getString("country");
        } catch (JSONException e) {
            e.printStackTrace();
            d = "";
        }
        citta = myResponse.getString("name");
        Log.e("Weather", "Città corrente: " + citta);
        String icon = ob.getString("icon");
        City appo = new City(citta, d, description, temp, min, max, icon);
        String nomeCitta = myResponse.getString("name");
        appo.setLat(lat);
        appo.setLon(lon);
        appo.setName(nomeCitta);
        return appo;
    }
}