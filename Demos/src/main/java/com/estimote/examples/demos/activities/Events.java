package com.estimote.examples.demos.activities;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.estimote.examples.demos.R;
import com.estimote.examples.demos.adapters.BeaconListAdapter;
import com.estimote.examples.demos.adapters.EddystonesListAdapter;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.eddystone.Eddystone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Created by User on 20.10.2015.
 */
public class Events extends Activity{
    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
    static  public Events e;
    private LinearLayout framesContainer;
    public BeaconManager beaconManager;
    public EddystonesListAdapter adapter;
    public BeaconListAdapter adapterbeacon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events);
        e=this;
        adapter = new EddystonesListAdapter(this);
        beaconManager = new BeaconManager(this);
    }
    @Override protected void onDestroy() {
        beaconManager.disconnect();
        super.onDestroy();
    }

    @Override protected void onStart() {
        super.onStart();

        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToService();
        }
    }

    @Override protected void onStop() {
        beaconManager.disconnect();
        super.onStop();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                connectToService();
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void connectToService() {
        adapter.replaceWith(Collections.<Eddystone>emptyList());
       beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {//слушатель подписанный на обнаружение Eddystone иаячков
            @Override
            public void onEddystonesFound(List<Eddystone> eddystones) {//что делать при нахождении маячка
                adapter.replaceWith(eddystones);
                 String strBeacons = "";
                strBeacons = strBeacons + eddystones.get(0).namespace + eddystones.get(0).instance;
                for (int i = 1; i < eddystones.size(); i++) {
                    strBeacons = strBeacons + "," + eddystones.get(i).namespace + eddystones.get(i).instance;
                }
                if(strBeacons!="") {
                    Json j = new Json();
                    j.execute(strBeacons);
                }
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.setForegroundScanPeriod(1860,1860); //поэкспереминтировать
                beaconManager.startEddystoneScanning();//начинает сканирование
            }
        });
        /* пробывал сделать распознавание не только Eddystone
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Note that beacons reported here are already sorted by estimated
                        // distance between device and beacon.
                        adapterbeacon.replaceWith(beacons);
                       if(strBeacons=="")
                        strBeacons = strBeacons + beacons.get(0).getProximityUUID();
                        else strBeacons = strBeacons + ","  + beacons.get(0).getProximityUUID();
                        for (int i = 1; i < beacons.size(); i++) {
                            strBeacons = strBeacons + "," + beacons.get(i).getProximityUUID() ;
                        }
                        Json j = new Json();
                        j.execute(strBeacons);
                    }
                });
            }
        });
        adapterbeacon.replaceWith(Collections.<Beacon>emptyList());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
            }
        });*/
    }
    public void Create_list(Field []f )
    {
        framesContainer = (LinearLayout) findViewById(R.id.frames_container);
        framesContainer.removeAllViews();
        for (int i = 0; i < f.length; i++) {
            Events_layout frame = new Events_layout(getApplicationContext());
            frame.set_Field(f[i]);
            framesContainer.addView(frame);
        }
        showToast("Перегрузка экрана");
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
    class Field{
        public String end_date;
        public String icon;
        public Integer id;
        public String description;
        public String title;
        public String start_date;


    }

    private class Json extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(String... params) {//по сути сам запрос на сервер
            // получаем данные с внешнего ресурса
            try {
                String strURL = "http://tagbeacon.ru/api/events?email=test1@devslef.com&password=123456&beacons=";
                //String strURL ="";
                strURL = strURL.concat(params[0]);

                URL url = new URL(strURL);
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
        protected void onPostExecute(String strJson) {//обработка ответа
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку


            JSONObject dataJsonObj = null;
            String secondName = "";

            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray data = dataJsonObj.getJSONArray("data");
                Field[] f = new Field[data.length()];
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    f[i] = new Field();
                    f[i].end_date = obj.getString("end_date");
                    f[i].icon = obj.getString("icon");
                    f[i].id = obj.getInt("id");
                    f[i].description = obj.getString("description");
                    f[i].title = obj.getString("title");
                    f[i].start_date = obj.getString("start_date");
                }
                Events.e.Create_list(f);//собственно перересовка экрана

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
