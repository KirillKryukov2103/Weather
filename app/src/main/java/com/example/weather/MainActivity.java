package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button main_button;
    private TextView res_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        main_button = findViewById(R.id.main_button);
        res_label = findViewById(R.id.res_label);

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.null_input, Toast.LENGTH_SHORT).show();
                else {
                    String city = editText.getText().toString();
                    String token = "d2da75289e794d72b23203001222112";
                    String url = "https://api.weatherapi.com/v1/current.json?key=" + token + "&q=" + city + "&aqi=no";

                    new GetURLData().execute(url);
                }
            }
        });
    }

    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            res_label.setText("Загрузка...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();

                try {
                    if(reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject obj = new JSONObject(result);
                res_label.setText("Температура в C: " + obj.getJSONObject("current").getDouble("temp_c"));
                res_label.setText("Температура в F: " + obj.getJSONObject("current").getDouble("temp_f"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}