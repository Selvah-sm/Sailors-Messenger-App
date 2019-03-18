package com.sailors.messenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TeamActivity extends AppCompatActivity {
    public String serverURL = "http://ec2-35-171-185-240.compute-1.amazonaws.com";
    public static int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        Button btnJoin = (Button) findViewById(R.id.btnjoin);
        final EditText teamSelected = (EditText) findViewById(R.id.teamid);
        final Button btnTeamA = (Button) findViewById(R.id.teamA);
        final Button btnTeamB = (Button) findViewById(R.id.teamB);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* String TeamSelected = teamSelected.getText().toString();
                if(TeamSelected == "101"){
                    btnTeamA.setVisibility(View.VISIBLE);
                }
                else if (TeamSelected == "102"){
                    btnTeamB.setVisibility(View.VISIBLE);
                }*/

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
                }.execute(serverURL+"/jointeam","text="+teamSelected.getText().toString()+"&uid="+Utils.Device_UID);

            }
        });



        btnTeamA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=1;
                System.out.println("FLAG = "+flag);
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
                }.execute(serverURL+"/getmemberslist","text=teamA");

                Intent goToNextActivity = new Intent(getApplicationContext(), WorkItemList.class);
                goToNextActivity.putExtra("TeamSelected","101");
                startActivity(goToNextActivity);

            }
        });
        btnTeamB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=1;
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
                }.execute(serverURL+"/getmemberslist","text=teamB");

                Intent goToNextActivity = new Intent(getApplicationContext(), WorkItemList.class);
                goToNextActivity.putExtra("TeamSelected","102");
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
    public  void createSqs(String pumper,String receiver)
    {

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
            }.execute(serverURL+"/createsqs","text1="+pumper+"&text2="+receiver);


    }


    private String readStream(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        System.out.println("FLAG: "+ flag);
        if(flag == 1) {
            flag=0;
            System.out.println(sb.toString());
            String UIDArray[] = new String[100];
            String array[] = sb.toString().split("\"");
            int i=1;
            while(i<array.length) {
                UIDArray[i / 2] = array[i];
                System.out.println(UIDArray[i / 2]);
                i += 2;
            }
            i=0;
            while(i<UIDArray.length) {
                createSqs(Utils.Device_UID.replace(":","_"),UIDArray[i++].replace(":","_"));
            }
        }
        else
            System.out.println("TEAM NAME: "+sb.toString());
        br.close();
        return sb.toString();
    }
}
