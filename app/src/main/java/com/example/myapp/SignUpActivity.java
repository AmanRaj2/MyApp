package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText memail, mpassword, mname, mpassword1, mPhone;
    Button mSignUp;
    TextView mlogin, mLoading;
    private ProgressBar mProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        memail = findViewById(R.id.emailid);
        mname = findViewById(R.id.nameid);
        mPhone = findViewById(R.id.phone);
        mpassword = findViewById(R.id.passwordid);
        mSignUp = findViewById(R.id.signUpid);
        mlogin = findViewById(R.id.newUserid);
        mpassword1 = findViewById(R.id.cnfrmpwd);
        mLoading = findViewById(R.id.signUp_loading_textView);
        mProgress = findViewById(R.id.signUp_progressBar);
        mSignUp.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btnBackgroundColor), PorterDuff.Mode.MULTIPLY);


        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoading.setVisibility(View.INVISIBLE);
                mProgress.setVisibility(View.INVISIBLE);

                String email = memail.getText().toString().trim();
                final String profilePic = "";
                final String password = mpassword.getText().toString().trim();
                final String name = mname.getText().toString().trim();
                final String cnfrmpass = mpassword1.getText().toString().trim();
                final String phoneNo = mPhone.getText().toString().trim();
                final String fb = "", whatsApp = "", insta = "", other = "";
                if (email.isEmpty()) {
                    memail.setError("Please enter email id");
                    memail.requestFocus();
                } else if (password.isEmpty()) {
                    mpassword.setError("Please enter your password");
                    mpassword.requestFocus();
                } else if (cnfrmpass.isEmpty()) {
                    mpassword1.setError("Please confirm your password");
                    mpassword1.requestFocus();
                } else if (name.isEmpty()) {
                    mname.setError("Please enter your name");
                    mname.requestFocus();
                } else if (!(password.equals(cnfrmpass))) {
                    mpassword1.setError("Password does not matched");
                    mpassword1.requestFocus();
                } else if (!(email.isEmpty() && password.isEmpty() && name.isEmpty())) {
                    mLoading.setVisibility(View.VISIBLE);
                    mProgress.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String Uid = user.getUid();
                                String email = user.getEmail().toString();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("users/" + Uid);
                                userDetails userObj = new userDetails(Uid, name, profilePic, phoneNo, email, fb, insta, whatsApp);
                                myRef.child("info").setValue(userObj);
                                myRef.child("***").setValue(password);
                                /*myRef.child("Email").setValue(user.getEmail().toString());
                                myRef.child("Name").setValue(name);
                                myRef.child("uid").setValue(Uid);
                                myRef.child("phone").setValue(phoneNo);
                                myRef.child("password").setValue(password);
                                myRef.child("socialMedia").child("fb").setValue(fb);
                                myRef.child("socialMedia").child("whatsApp").setValue(whatsApp);
                                myRef.child("socialMedia").child("insta").setValue(insta);
                                myRef.child("socialMedia").child("other").setValue(other);*/
                                mLoading.setVisibility(View.INVISIBLE);
                                mProgress.setVisibility(View.INVISIBLE);
                                Intent i = new Intent(SignUpActivity.this, PrivateActivity.class);
                                startActivity(i);
                                finish();
                            } else if (password.length() < 6) {
                                mLoading.setVisibility(View.INVISIBLE);
                                mProgress.setVisibility(View.INVISIBLE);
                                mpassword.setError("Minimum 6 character required");
                                mpassword.requestFocus();
                            } else {
                                mLoading.setVisibility(View.INVISIBLE);
                                mProgress.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "SignUp unsuccessful! This email is already taken or not valid. Else check internet connection", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    mLoading.setVisibility(View.INVISIBLE);
                    mProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
