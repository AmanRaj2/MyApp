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

public class LoginActivity extends AppCompatActivity {
    EditText memail, mpassword;
    Button mlogin;
    TextView mnewuser,mLoading;
    private ProgressBar mProgress;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(LoginActivity.this, PrivateActivity.class));
            finish();
        }
        memail = findViewById(R.id.emailid);
        mpassword = findViewById(R.id.passwordid);
        mlogin = findViewById(R.id.signUpid);
        mnewuser = findViewById(R.id.newUserid);
        mLoading = findViewById(R.id.login_loading_textView);
        mProgress = findViewById(R.id.login_progressBar);
        mlogin.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btnBackgroundColor), PorterDuff.Mode.MULTIPLY);

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoading.setVisibility(View.INVISIBLE);
                mProgress.setVisibility(View.INVISIBLE);

                String email = memail.getText().toString();
                String password = mpassword.getText().toString();
                if (email.isEmpty()) {
                    memail.setError("Please enter email id");
                    memail.requestFocus();
                } else if (password.isEmpty()) {
                    mpassword.setError("Please enter your password");
                    mpassword.requestFocus();
                } else if (!(email.isEmpty() && password.isEmpty())) {
                    mLoading.setVisibility(View.VISIBLE);
                    mProgress.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mLoading.setVisibility(View.INVISIBLE);
                                mProgress.setVisibility(View.INVISIBLE);
                                Intent i = new Intent(LoginActivity.this, PrivateActivity.class);
                                startActivity(i);
                                LoginActivity.this.finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "SignIn unsuccessful", Toast.LENGTH_SHORT).show();
                                mLoading.setVisibility(View.INVISIBLE);
                                mProgress.setVisibility(View.INVISIBLE);
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

        mnewuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                LoginActivity.this.finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
