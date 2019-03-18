package com.sailors.messenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class WorkItemList extends AppCompatActivity {

    ListView simpleList;
   // Intent mIntent = getIntent();
    //int value = mIntent.getIntExtra("teamValue",0);
    //ArrayList items;// = new ArrayList<String>();
    String workItemList[]={"IDIA","LANKA"};
    @Override   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_item_list);
        simpleList = (ListView)findViewById(R.id.workitemlist);
        workItemList = new String[100];
        Intent mIntent = getIntent();
        String teamSelected = mIntent.getStringExtra("TeamSelected");
        if(teamSelected == "101"){
            String LocalList [] = {"nerdyfib_pat1","nerdyfib_pat3"};
            //workItemList = LocalList;
        }
        else if(teamSelected == "102"){
            String LocalList [] = {"nerdyfib_patient2","venti_pat1"};
            //workItemList = LocalList;
        }
        else{}
        ListAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,workItemList);
        simpleList.setAdapter(arrayAdapter);
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                goToNextActivity.putExtra("patientSelected",workItemList[position]);
                startActivity(goToNextActivity);            }
        });
    }
    }

