package com.example.moneyexchange_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ListView lv;

    private static String JSON_URL = "https://www.cbr-xml-daily.ru/daily_json.js";

    ArrayList<HashMap<String, String>> valuteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        valuteList = new ArrayList<>();
        lv = findViewById(R.id.listView);

        GetData getData = new GetData();
        getData.execute();

    }

    public class GetData extends AsyncTask<String, String, String>{



        @Override
        protected String doInBackground(String... strings) {
           String current = "";

           try {
               URL url;
               HttpURLConnection urlConnection = null;

               try {
                   url = new URL(JSON_URL);
                   urlConnection = (HttpURLConnection) url.openConnection();

                   InputStream in = urlConnection.getInputStream();
                   InputStreamReader isr = new InputStreamReader(in);

                   int data = isr.read();
                   while (data != -1) {

                       current += (char) data;
                       data = isr.read();
                   }
                   return current;


               } catch (MalformedURLException e) {
                   e.printStackTrace();
               } catch (IOException e) {
                   e.printStackTrace();
               } finally {
                   if (urlConnection != null) {
                       urlConnection.disconnect();
                   }
               }
           } catch (Exception e){
               e.printStackTrace();
           }
            return current;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject valuteObject = jsonObject.getJSONObject("Valute");

                Iterator<String> keys = valuteObject.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    JSONObject currentValute = valuteObject.getJSONObject(key);
                    HashMap<String, String> valuteInformation = new HashMap<>();
                    valuteInformation.put("ID", currentValute.getString("ID"));
                    valuteInformation.put("NumCode", currentValute.getString("NumCode"));
                    valuteInformation.put("CharCode", currentValute.getString("CharCode"));
                    valuteInformation.put("Value", currentValute.getString("Value"));
                    valuteInformation.put("Previous", currentValute.getString("Previous"));
                    valuteInformation.put("Name", currentValute.getString("Name"));
                    valuteInformation.put("Nominal", currentValute.getString("Nominal"));
                    valuteList.add(valuteInformation);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Displaying the results
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this,
                    valuteList,
                    R.layout.row_layout,
                    new String[] {"CharCode", "Name", "ID", "NumCode", "Nominal", "Value", "Previous"},
                    new int[]{R.id.textView, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7});
            lv.setAdapter(adapter);

        }
    }




}