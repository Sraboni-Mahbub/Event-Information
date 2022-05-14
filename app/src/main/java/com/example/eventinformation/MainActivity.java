package com.example.eventinformation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnCreateNew, btnExit;
    private ListView lvEvents;
    private ArrayList<Event> events;
    private CustomEventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCreateNew = findViewById(R.id.btnCreateNew);
        btnCreateNew.setOnClickListener(v->createNew());

        Button history = findViewById(R.id.btnHistory);
        history.setOnClickListener(v->history());

        Button load = findViewById(R.id.loadData);
        load.setOnClickListener(v->loadAttendenceList());

        findViewById(R.id.btnExit).setOnClickListener(v->finish());



        String value = Util.getInstance().getValueByKey(this, "Sraboni-::-06/08/1998");
        if(value != null){
            String[] columValues = value.split("-::-");
            for(int i=0; i<9;i++){
                System.out.println(columValues[i]);
            }
        }


        // initialize list-reference by ListView object defined in XML
        lvEvents = findViewById(R.id.lvEvents);
        // load events from database if there is any
        loadData();

    }

    private void loadData(){
        events = new ArrayList<>();
        KeyValueDB db = new KeyValueDB(this);
        Cursor rows = db.execute("SELECT * FROM key_value_pairs");
        if (rows.getCount() == 0) {
            return;
        }
        //events = new Event[rows.getCount()];
        while (rows.moveToNext()) {
            String key = rows.getString(0);
            String eventData = rows.getString(1);
            String[] fieldValues = eventData.split("-::-");

            String name = fieldValues[0];
            String dateTime = fieldValues[1];
            String eventType = fieldValues[2];
            Event e;
            e = new Event(key, name, "", "","", "", "", "", "", "");
            String place = fieldValues[3];
            String capacity = fieldValues[4];
            String budget = fieldValues[5];
            String email = fieldValues[6];
            String phone = fieldValues[7];
            String description = fieldValues[8];


            e = new Event(key, name, place, dateTime, capacity, budget, email, phone, description, eventType);
            events.add(e);
        }
        db.close();

        adapter = new CustomEventAdapter(this, events);
        lvEvents.setAdapter(adapter);

        // handle the click on an event-list item
        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // String item = (String) parent.getItemAtPosition(position);
                Event e= events.get(position);

                System.out.println(position);
                Intent i = new Intent(MainActivity.this, EventInformationActivity.class);
                i.putExtra("EventKey", e.key);
                startActivity(i);
            }
        });
        // handle the long-click on an event-list item
        lvEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //String message = "Do you want to delete event - "+events[position].name +" ?";
                String message = "Do you want to delete event - "+events.get(position).name +" ?";
                System.out.println(message);
                //showDialog(message, "Delete Event", events.get(position).key);
                return true;
            }
        });

        lvEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //String message = "Do you want to delete event - "+events[position].name +" ?";
                String message = "Do you want to delete event - "+events.get(position).name +" ?";
                showDialog(message, "Delete Event", events.get(position).key);
                return true;

            }
        });
    }

    public void showDialog(String message, String title, String key){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);

        builder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Util.getInstance().deleteByKey(MainActivity.this, key);
                dialog.cancel();
                loadData();
                adapter.notifyDataSetChanged();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    void createNew(){
        Intent i = new Intent(this, EventInformationActivity.class);
        startActivity(i);
    }
    void history(){
        System.out.println("Invoke History Activity here");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("@mainActivity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("@mainActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("@mainActivity onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("@mainActivity onRestart");
        // re-load events from database after coming back from the next page
        loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("@mainActivity onStop");
        events.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("@mainActivity onDestroy");
    }

    void loadAttendenceList(){
        Intent intent = new Intent(this, MyAttendanceActivity.class);
        startActivity(intent);
    }
}