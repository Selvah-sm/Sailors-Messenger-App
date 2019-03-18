package com.sailors.messenger;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity {
    public static String UID = "";
    public static String UIDArray[];
    public String serverURL = "http://ec2-35-171-185-240.compute-1.amazonaws.com";
    TextView tvUID;
    EditText Username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        final Button btnUID = (Button) findViewById(R.id.btnuid);
        UIDArray = new String[100];
        tvUID = (TextView) findViewById(R.id.tvuid);
        Username = (EditText) findViewById(R.id.username);
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String uuid = wm.getConnectionInfo().getMacAddress();
        if(uuid !=null){
            tvUID.setText(uuid);
            Utils.Device_UID = uuid.toString();
            System.out.println("UID: "+Utils.Device_UID);

        }
        else{
            tvUID.setText("No uid found!");
        }
        btnUID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Utils.User_Name = Username.getText().toString();

                    new AsyncTask<String, Void, String>() {

                        @Override
                        protected void onPreExecute() {
                        }

                        @SuppressLint("WrongThread")
                        @Override
                        protected String doInBackground(String... params) {
                            return doPost(params[0], params[1]);
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                        }
                    }.execute(serverURL+"/registration","text="+Utils.Device_UID+"&username="+Utils.User_Name);

                Intent goToNextActivity = new Intent(getApplicationContext(), TeamActivity.class);
                startActivity(goToNextActivity);

            }
        });
    }

    public String doPost(String urlString, String params) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(5000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();
                os.write(params.getBytes());
                os.flush();
                os.close();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == 200) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    return readStream(in);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String readStream(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }

        br.close();
        return sb.toString();
    }
}
