package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class otherActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference nameRef;
    private ScrollView sv;

    DatabaseReference myref1 = database.getReference("worldChat/message1");
    DatabaseReference myref2 = database.getReference("worldChat/message2");
    DatabaseReference myref3 = database.getReference("worldChat/message3");
    DatabaseReference myref4 = database.getReference("worldChat/message4");
    DatabaseReference myref5 = database.getReference("worldChat/message5");
    DatabaseReference myref6 = database.getReference("worldChat/message6");
    DatabaseReference myref7 = database.getReference("worldChat/message7");
    DatabaseReference myref8 = database.getReference("worldChat/message8");
    DatabaseReference myref9 = database.getReference("worldChat/message9");
    DatabaseReference myref10 = database.getReference("worldChat/message10");
    DatabaseReference myref90 = database.getReference("worldChat/message90");


    TextView mmsg1, mmsg2, mmsg3, mmsg4, mmsg5, mmsg6, mmsg7, mmsg8, mmsg9, mmsg10, mwrite;
    Button msend;
    String userUid, currentDate, currentTime, str, value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, fullmsg,name;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        setName();
        //to read data sent from other intent
        Bundle bundle = getIntent().getExtras();
        //String str = bundle.getString("KEY");
        //Toast.makeText(this,str,Toast.LENGTH_SHORT).show();

        mmsg1 = (TextView) findViewById(R.id.msg1);
        mmsg2 = (TextView) findViewById(R.id.msg2);
        mmsg3 = (TextView) findViewById(R.id.msg3);
        mmsg4 = (TextView) findViewById(R.id.msg4);
        mmsg5 = (TextView) findViewById(R.id.msg5);
        mmsg6 = (TextView) findViewById(R.id.msg6);
        mmsg7 = (TextView) findViewById(R.id.msg7);
        mmsg8 = (TextView) findViewById(R.id.msg8);
        mmsg9 = (TextView) findViewById(R.id.msg9);
        mmsg10 = (TextView) findViewById(R.id.msg10);
        mwrite = (TextView) findViewById(R.id.write);
        msend = (Button) findViewById(R.id.send);
        sv = findViewById(R.id.scrollView_otherActivity);
        msend.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btnBackgroundColor), PorterDuff.Mode.MULTIPLY);
        scrollBottom();

        mwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollBottom();
            }
        });
        myref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value1 = dataSnapshot.getValue(String.class);
                mmsg1.setText(value1);
                //Log.d("tag","Value is " + value1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tag", "Failed to read value");
            }
        });

        myref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value2 = dataSnapshot.getValue(String.class);
                mmsg2.setText(value2);
                //Log.d("tag","Value is " + value2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tag", "Failed to read value");
            }
        });

        myref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value3 = dataSnapshot.getValue(String.class);
                mmsg3.setText(value3);
                //Log.d("tag","Value is " + value3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tag", "Failed to read value");
            }
        });

        myref4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value4 = dataSnapshot.getValue(String.class);
                mmsg4.setText(value4);
                //Log.d("tag","Value is " + value4);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tag", "Failed to read value");
            }
        });

        myref5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value5 = dataSnapshot.getValue(String.class);
                mmsg5.setText(value5);
                // Log.d("tag","Value is " + value5);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tag", "Failed to read value");
            }
        });

        myref6.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value6 = dataSnapshot.getValue(String.class);
                mmsg6.setText(value6);
                //Log.d("tag","Value is " + value6);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tag", "Failed to read value");
            }
        });

        myref7.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value7 = dataSnapshot.getValue(String.class);
                mmsg7.setText(value7);
                //Log.d("tag","Value is " + value7);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tag", "Failed to read value");
            }
        });

        myref8.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value8 = dataSnapshot.getValue(String.class);
                mmsg8.setText(value8);
                //Log.d("tag","Value is " + value8);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tag", "Failed to read value");
            }
        });

        myref9.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value9 = dataSnapshot.getValue(String.class);
                mmsg9.setText(value9);
                //Log.d("tag","Value is " + value9);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tag", "Failed to read value");
            }
        });

        myref10.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value10 = dataSnapshot.getValue(String.class);
                mmsg10.setText(value10);
                //Log.d("tag","Value is " + value10);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tag", "Failed to read value");
            }
        });
        /*myref90.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fullmsg = dataSnapshot.getValue(String.class);
                Log.d("test2",fullmsg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        msend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = mwrite.getText().toString();
                str.trim();
                //to send data to database
                if (str.equals("")) {
                    Toast.makeText(otherActivity.this, "please write something..", Toast.LENGTH_SHORT).show();
                } else {

                    currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                    currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                    String str1 = currentDate + ", " + currentTime;

                    str = "(" + str1 + ") "+ name  + "\n ->  " + str;
                    myref10.setValue(str);
                    myref9.setValue(value10);
                    myref8.setValue(value9);
                    myref7.setValue(value8);
                    myref6.setValue(value7);
                    myref5.setValue(value6);
                    myref4.setValue(value5);
                    myref3.setValue(value4);
                    myref2.setValue(value3);
                    myref1.setValue(value2);
                    mwrite.setText("");
                    scrollBottom();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("tag", "otherActivity on pause");
        saveData();
        //takeData();
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, value10);
        editor.apply();
        Log.d("tag", "data saved " + value10);
    }
    /*public void takeData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String text = sharedPreferences.getString(TEXT,"default");
        //Log.d("tag",text);
    }*/
    private void setName() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userUid = user.getUid();
        nameRef = database.getReference("users/" + userUid + "/info/name");
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void scrollBottom() {
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.scrollTo(0, mmsg10.getBottom());
            }
        });
        //mmsg10.getParent().requestChildFocus(mmsg10,mmsg10);
    }

}
