package com.example.meteo1_0;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class MainActivity extends AppCompatActivity {
    private static final String EXTRA_KEY = "test_data";
    private static final int SECOND_ACTIVITY_CODE = 2;
    public static DBAdapter myDB;
    //Button aggiungi;
    ListView mylist;

    City c = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate() called");
        System.out.println("onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int req = 0;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, req);
        } else {
            /*aggiungi = findViewById(R.id.button2);
            aggiungi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick_add();
                }
            });
            */
            openDB();
            populateListView();
            mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                    startSecond(position);
                }
            });
        } // fine dell'else dei permessi

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission not granted");
            requestPermissions();
        } else {
            Log.i(TAG, "Permission granted");
            startLocationListener();
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_t, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add: {
                onClick_add();
                return true;
            }
            default:
                return super.onContextItemSelected(item);
        }
    }



    public static double lat=0.0,lon=0.0;
    private void startLocationListener() {
        long mLocTrackingInterval = 1000 * 5;  // 5 secondi
        float trackingDistance = 100;          // 100 non so unità di misura
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;
        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(mLocTrackingInterval);
        SmartLocation.with(this)
                .location()
                .continuous()
                .config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        RequestWeather r = new RequestWeather("Nullo");
                        try {
                            c = r.call_me_lat_lon(location.getLatitude(), location.getLongitude());
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (mylist.getCount() == 0) {
                            if (c.getCapol().equals("Unknow name")){
                                myDB.insertRow( c.getCapol(), " ");
                            }else
                                myDB.insertRow(c.getName() + "," + c.getCapol(), " ");
                        }
                        else {
                            if (c.getCapol().equals("Unknow name")){
                                myDB.updateRow(1, c.getCapol(), "Ciao");
                            }else
                            myDB.updateRow(1, c.getName() + ", " + c.getCapol(), "Ciao");
                        }
                        populateListView();
                    }
                });
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            startLocationListener();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startLocationListener();
                return;
            }
        }
    }

    private void onClick_add() {
        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add a new task")
                .setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        try {
                            myDB.insertRow(taskEditText.getText().toString() + "," + (new RequestWeather(taskEditText.getText().toString())).call_me().getCapol(), " ");
                           // myDB.insertRow(taskEditText.getText().toString(), " ");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        populateListView();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void openDB() {
        myDB = new DBAdapter(this);
        myDB.open();
    }

    public void populateListView() {
        Cursor cursor = myDB.getAllRows();
        String[] fromFieldNames = new String[]{DBAdapter.KEY_NAME, DBAdapter.KEY_CAPITAL};
        int[] toViewsIDs = new int[]{R.id.textView2}; //, R.id.textView3};
        SimpleCursorAdapter simple = new SimpleCursorAdapter(getBaseContext(), R.layout.customlayout, cursor, fromFieldNames, toViewsIDs, 0);
        mylist = findViewById(R.id.listView);
        mylist.setAdapter(simple);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startSecond(int nCitta) {
        Intent intent = MainActivity2.newIntent(MainActivity.this, nCitta);
        startActivityForResult(intent, SECOND_ACTIVITY_CODE);

/**
        Intent myIntent = MainActivity2.newIntent(MainActivity.this, nCitta);
        ActivityOptions options =ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.pull_in_from_left, R.anim.pull_out_to_left);
        MainActivity.this.startActivity(myIntent, options.toBundle());
**/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == SECOND_ACTIVITY_CODE) {
            if (data != null) {
                int i = data.getIntExtra(EXTRA_KEY, 0);
            }
        }
    }
    private static final String TAG = "LifecycleExample";



    @Override
    public void onStart() {
        super.onStart();


    }
    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in2, R.anim.slide_out2);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }





}


