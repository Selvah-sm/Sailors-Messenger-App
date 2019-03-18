package com.sailors.messenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    /*TEMP VARIABLES*/

    public String workItemSelected = "";//mIntent.getStringExtra("patientSelected");

    public String userName = "Doctor :  "+Utils.User_Name;
    String pumpSqSid = "A2S";
    String drainSqSid = "S2A";
    public String otherGuyName = "John";

    //public String userName = "Doctor : John  ";
    //String drainSqSid = "A2S";
    //String pumpSqSid = "S2A";
    //public String otherGuyName = "Alex";

    public String serverURL = "http://ec2-35-171-185-240.compute-1.amazonaws.com";
    public String teamToken = "teamA";


    public int seconds = 60;
    public String minutes = "00";

    private String verifyingHash = "";
    public SailorsLogChain sailorsLogChain = null;
    TextView loggingScrollView, statusText,conflictStatusTV;
    TextView teamNameTV, userNameTV;
    TextView workItem;//,activityTV;
    Button button,btnGet, chargeBTN, dischageBTN;


    //SailorsLogChain logChain = new SailorsLogChain();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent mIntent = getIntent();
        workItemSelected = mIntent.getStringExtra("patientSelected");

        getAllUIComponents();
        setOnCreateUIComponents();
        initiateBeatTimer();
        conflictStatusTV.setVisibility(View.INVISIBLE);


        chargeBTN.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            public void onClick(View v) {
                sailorsLogChain = Utils.getSailorsHashMapWithTeamTokenID(teamToken + workItem.getText().toString());
                String inputText;
                inputText = workItem.getText().toString()  + "|" + "Charging";
                onBefibAction(inputText);
            }
        });

        dischageBTN.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            public void onClick(View v) {
                sailorsLogChain = Utils.getSailorsHashMapWithTeamTokenID(teamToken + workItem.getText().toString());
                String inputText;
                inputText = workItem.getText().toString()  + "|" + "disCharging";
                onBefibAction(inputText);

            }
        });

    }

   public void onBefibAction (String inputText) {
       loggingScrollView.append("\n You - " +inputText );
       verifyingHash = sailorsLogChain.insert(sailorsLogChain,inputText,"","time",workItemSelected);
       System.out.println("vefirying HASH  :"+verifyingHash);
       inputText = inputText + "|" +verifyingHash;

       String urlPath = serverURL + "/pumpLog";
       String inputParamsString= "text=" + inputText + "&token=arvind&sqsid="+pumpSqSid+"&verifyingHash="+verifyingHash;
       asyncTasktoPump(urlPath, inputParamsString);
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
        System.out.println(sb.toString());
        br.close();
        return sb.toString();
    }

    private void getAllUIComponents () {
        statusText = (TextView) findViewById(R.id.status_text);
        loggingScrollView = (TextView) findViewById(R.id.loggingScrollView);
        workItem = (TextView) findViewById(R.id.workitem);
        //activityTV = (TextView) findViewById(R.id.activityTV);
        //button = (Button) findViewById(R.id.btnlog);
        //btnGet = (Button) findViewById(R.id.btnget);

        chargeBTN = (Button) findViewById(R.id.defib_charge);
        dischageBTN = (Button) findViewById(R.id.defib_discharge);

        teamNameTV = (TextView) findViewById(R.id.teamName);
        userNameTV = (TextView) findViewById(R.id.userName);
        conflictStatusTV = (TextView)findViewById(R.id.conflict_status);

    }

    private void setOnCreateUIComponents() {
        teamNameTV.setText(workItemSelected);
        userNameTV.setText(userName);

    }

    private void initiateBeatTimer() {
        //Declare the timer
        Timer t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.beatTimer);
                        tv.setText(String.valueOf(minutes)+":"+String.valueOf(seconds));
                        seconds -= 1;
                        if(seconds == 0)
                        {
                            tv.setText(String.valueOf(minutes)+":"+String.valueOf(seconds));
                            seconds=60;
                        }
                        //EVERY 5 SECONDS CALL DRAIN
                        if(seconds%5==0) {
                            String urlPath = serverURL + "/drainLog";
                            String inputParamsString= "&token=arvind &sqsid="+drainSqSid;
                            asyncTasktoDrain(urlPath, inputParamsString);
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    public void asyncTasktoPump(String urlPath, String inputParamsString) {
       System.out.println("Calling the request "+urlPath+ " " +inputParamsString);
        new AsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                statusText.setText("Logging in Progress");
            }

            @Override
            protected String doInBackground(String... params) {
                return doPost(params[0], params[1]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //if (s != null && s.length() != 0)
                    //loggingScrollView.append("\n" + s);
                statusText.setText("");
            }
        }.execute(urlPath, inputParamsString);

    }

    public void asyncTasktoDrain(String urlPath, String inputParamsString) {
        System.out.println("Calling the request "+urlPath+ " " +inputParamsString);
        new AsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                //Bacground FETCH PROCESS NO NEED STATUS FOR NOW
                //statusText.setText("Logging in Progress");
            }

            @Override
            protected String doInBackground(String... params) {
                return doPost(params[0], params[1]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null && s.length() != 0) {
                    String[] data = s.split("\\|", 4);
                    s = otherGuyName + " - "+ data[0]+"|"+data[1];
                    String verifyingHashfromDrain = data[2];
                    loggingScrollView.append("\n" + s);
                    conflictStatusTV.setVisibility(View.VISIBLE);

                }
                //statusText.setText(s);
            }
        }.execute(urlPath, inputParamsString);

    }


}




