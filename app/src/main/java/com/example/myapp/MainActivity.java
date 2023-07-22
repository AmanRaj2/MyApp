package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.myapp.otherActivity.SHARED_PREFS;
import static com.example.myapp.otherActivity.TEXT;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myref10 = database.getReference("worldChat/message10");
    DatabaseReference versionRef = database.getReference("version");

    TextView  mdateTime, mnotify, mVersion, cityName, temperature, weatherText, amanraj;
    EditText enterCity;
    Button mExplore, mupdate, findWeather;
    ImageView weatherImg;
    String currentDate, currentTime, str1, currentDay, text, latestVersion, lastCity;
    public final String version = "1.7";
    public static final String CITY = "city";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("tag", "oncreate");

        amanraj = (TextView) findViewById(R.id.textView5);
        findWeather = (Button) findViewById(R.id.findWeather);
        cityName = (TextView) findViewById(R.id.cityName);
        temperature = (TextView) findViewById(R.id.temperature);
        weatherText = (TextView) findViewById(R.id.weatherText);
        enterCity =(EditText) findViewById(R.id.enterCity);
        weatherImg = (ImageView) findViewById(R.id.weatherImg);
        mExplore = (Button) findViewById(R.id.explore);
        mdateTime = (TextView) findViewById(R.id.dateTime);
        mnotify = (TextView) findViewById(R.id.notify);
        mupdate = findViewById(R.id.update_btn);
        mVersion = findViewById(R.id.versionId);
        setBtncolor();
        mExplore.setVisibility(View.INVISIBLE);

        findWeather.setOnClickListener(this);
        mExplore.setOnClickListener(this);
        mupdate.setOnClickListener(this);
        amanraj.setOnClickListener(this);

        //date
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        //time
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        //day
        currentDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
        str1 = currentDate + " " + currentDay;
        mdateTime.setText(str1);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("tag", "mainActivity on start");
        loadData();
        updateViews();
        checkVersion();
        setWeather();
    }

    private void checkVersion() {
        versionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                latestVersion = dataSnapshot.getValue(String.class);
                if (!latestVersion.equals(version)) {
                    mVersion.setText("new update");
                    mExplore.setVisibility(View.INVISIBLE);
                } else {
                    mVersion.setText("");
                    mExplore.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT, "default");
        lastCity = sharedPreferences.getString(CITY, "default");
        Log.d("tag", "data loaded " + text);
    }

    public void saveData(String s) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CITY, s);
        editor.apply();
    }

    public void updateViews() {
        myref10.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value10 = dataSnapshot.getValue(String.class);
                Log.d("tag", "txt " + text + " value10 " + value10);
                if (!text.equals(value10)) {
                    mnotify.setText("New messages on World chat..");
                } else if (text.equals(value10)) {
                    mnotify.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tag", "Failed to read value");
            }
        });

        Log.d("tag", "data put on view");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.findWeather:
                String city ="";
                city = enterCity.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
                setWeather();
                break;

            case R.id.textView5:
                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/amanrazbittu"));
                startActivity(intent2);
                break;
            case R.id.explore:
                if (!latestVersion.equals(version)) {
                    Toast.makeText(this,"Please Update", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent3 = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent3);
                }
                break;
            case R.id.update_btn:
                Intent update = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/open?id=1z0cgf-wldCfSgr3E-8kxGV_mUvpHQ-gS"));
                startActivity(update);
                break;
        }
    }

    private void setWeather() {
        String city ="";
        city = enterCity.getText().toString();
        if(city.isEmpty()){
            city = lastCity;
        }
        if(city.isEmpty() && lastCity.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a city name", Toast.LENGTH_SHORT).show();
        }
        else{
            String Url = "http://api.weatherapi.com/v1/current.json?key=4ff46062ad654c2e84992307220204&q="+city+"&aqi=no";
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String city_inner = response.getJSONObject("location").getString("name");
                        saveData(city_inner);
                        cityName.setText(city_inner);
                        String temperatureValue = response.getJSONObject("current").getString("temp_c");
                        temperature.setText(temperatureValue+"°c");
                        String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                        weatherText.setText(condition);
                        String condition_icon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                        Picasso.with(MainActivity.this).load("http:".concat(condition_icon)).into(weatherImg);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid city name", Toast.LENGTH_SHORT).show();
                    Log.d("invalid_city", error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
    }

    private void setBtncolor() {
        findWeather.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btnBackgroundColor), PorterDuff.Mode.MULTIPLY);
        //openWebPage.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btnBackgroundColor), PorterDuff.Mode.MULTIPLY);
        mExplore.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btnBackgroundColor), PorterDuff.Mode.MULTIPLY);
        mupdate.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btnBackgroundColor), PorterDuff.Mode.MULTIPLY);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
