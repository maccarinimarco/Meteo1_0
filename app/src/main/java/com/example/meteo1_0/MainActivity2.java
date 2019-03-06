package com.example.meteo1_0;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.InputStream;
import java.net.URL;

public class MainActivity2 extends AppCompatActivity {
    private static final String EXTRA_KEY = "test_data";
    //private Button mButtonBack;
    private TextView tCitta;
    private TextView tTemp, tMin, tMax, desc, tT1, tT2, tT3;
    private Switch switchButton;

    public static Intent newIntent(Context packageContext, int extraParam) {
        Intent intent = new Intent(packageContext, MainActivity2.class);
        intent.putExtra(EXTRA_KEY, extraParam);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermission();
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main2);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        //overridePendingTransition(R.anim.slide_out, R.anim.slide_out);
        //overridePendingTransition( R.anim.slide_out,R.anim.slide_in);
        tTemp = findViewById(R.id.textView6);
        tMin = findViewById(R.id.textView7);
        tMax = findViewById(R.id.textView8);
        desc = findViewById(R.id.textView5);
        tT1 = findViewById(R.id.tTemp1);
        tT2 = findViewById(R.id.tTemp2);
        tT3 = findViewById(R.id.tTemp3);
        switchButton = findViewById(R.id.temperatureSwitch);
        switchButton.setChecked(true);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                Double tAt, tMi, tMa;
                tAt = Double.parseDouble(tTemp.getText().toString());
                tMi = Double.parseDouble(tMin.getText().toString());
                tMa = Double.parseDouble(tMax.getText().toString());
                if (bChecked) {
                    tTemp.setText(Double.toString(fromCtoK(tAt)));
                    tMin.setText(Double.toString(fromCtoK(tMi)));
                    tMax.setText(Double.toString(fromCtoK(tMa)));
                    tT1.setText("° K");
                    tT2.setText("° K");
                    tT3.setText("° K");
                } else {
                    tTemp.setText(Double.toString(fromKtoC(tAt)));
                    tMin.setText(Double.toString(fromKtoC(tMi)));
                    tMax.setText(Double.toString(fromKtoC(tMa)));
                    tT1.setText("° C");
                    tT2.setText("° C");
                    tT3.setText("° C");
                }
            }
        });
        ImageView immagine = findViewById(R.id.imageView);
        City d = null;
        int i = getIntent().getIntExtra(EXTRA_KEY, 0);
        tCitta = findViewById(R.id.tCitta);
        String nomeCitta = MainActivity.myDB.getRow(i + 1).getString(1);
        tCitta.setText(nomeCitta);
        RequestWeather app = new RequestWeather(nomeCitta);
        try {
            if (i == 0) {
                d = app.call_me_lat_lon(MainActivity.lat, MainActivity.lon);
            } else
                d = app.call_me();
            if (d.getName().equals("")) {
                tCitta.setText("Lat" + d.getLat() + " , Lon " + d.getLon());
            } else {
                if (d.getName().contains(","))
                    tCitta.setText(d.getName());
                else
                    tCitta.setText(d.getName() + ", " + d.getCapol());
            }
            tTemp.setText(Double.toString(d.getTemp()));
            tMin.setText(Double.toString(d.getMin()));
            tMax.setText(Double.toString(d.getMax()));
            desc.setText(d.getDescrizione());
            immagine.setImageDrawable(LoadImageFromWebOperations("http://openweathermap.org/img/w/" + d.getIcon() + ".png"));
            System.out.println(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_t2, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back: {
                backToParent();
                return true;
            }
            default:
                return super.onContextItemSelected(item);
        }
    }


    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);
            }
        } else {
            // Permission has already been granted
        }
    }


    private double fromFtoC(double f) {
        double d = (f - 32) * ((double) 5 / 9);
        return Math.floor(d * 100) / 100;
    }

    private double fromCtoF(double f) {
        //todo formula sbagliata, controllare
        return Math.floor((f + 273.15) * 100) / 100;
    }

    private double fromKtoC(double f) {

        return Math.floor((f - 273.15) * 100) / 100;
    }

    private double fromCtoK(double f) {
        return Math.floor((f + 273.15) * 100) / 100;
    }


    private void backToParent() {
        Intent data = new Intent();
        data.putExtra(EXTRA_KEY, 99);
        setResult(RESULT_OK, data);

        finish();
    }

    float x1,x2,y1,y2;

    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1= touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2= touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1 <x2){
                    //Todo se non va cambio qui
                    backToParent();
                }
                break;
        }
        return false;
    }
}

