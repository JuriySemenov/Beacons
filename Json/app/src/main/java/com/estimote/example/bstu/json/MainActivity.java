package com.estimote.example.bstu.json;


import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends ActionBarActivity {
    class Field{
        public String end_date;
        public String icon;
        public Integer id;
        public String description;
        public String title;
        public String start_date;
    }
    public static String LOG_TAG = "my_log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ParseTask().execute();
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("http://tagbeacon.ru/api/events?email=test1@devslef.com&password=123456&beacons=edd1ebeac04e5defa017d17a85026fb4");

                 // URL url = new URL("http://tagbeacon.ru/api/events?email=admin@tagbeacon.ru&password=123456&beacons=edd1ebeac04e5defa017d17a85026fb4");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            Log.d(LOG_TAG, strJson);

            JSONObject dataJsonObj = null;
            String secondName = "";

            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray data = dataJsonObj.getJSONArray("data");

                // 1. достаем инфо о втором друге - индекс 1

                Field[] f = new Field[data.length()];


                // 2. перебираем и выводим контакты каждого друга
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    f[i]=new Field();
                    f[i].end_date = obj.getString("end_date");
                    f[i].icon = obj.getString("icon");
                    f[i].id =  obj.getInt("id");
                    f[i].description = obj.getString("description");
                    f[i].title = obj.getString("title");
                    f[i].start_date = obj.getString("start_date");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}