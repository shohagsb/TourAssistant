package com.shohagas.tourmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private  FirebaseUser currentUser;
    private Button loginButton;
    private EditText mEmailET, mPasswordET;
    private ProgressBar progressBar;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();

        loginButton = findViewById(R.id.buttonLogin);
        mEmailET = findViewById(R.id.editTextEmail);
        mPasswordET = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.loginProgressBar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if(checkFields()){
                    mFirebaseAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        currentUser = mFirebaseAuth.getCurrentUser();
                                        if(currentUser.getDisplayName() != null){
                                            Toast.makeText(LoginActivity.this, "Welcome "+currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                        }
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Email and Password not matched", Toast.LENGTH_SHORT).show();
                                        mEmailET.setText("");
                                        mPasswordET.setText("");
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mFirebaseAuth.getCurrentUser();
        if( currentUser!= null){
            Toast.makeText(this, "Welcome "+currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private boolean checkFields() {
        email = mEmailET.getText().toString();
        password = mPasswordET.getText().toString();
        if(email.isEmpty() || password.isEmpty()){
            mEmailET.setError("Field is Required");
            mPasswordET.setError("Field is Required");
            progressBar.setVisibility(View.GONE);
            return false;
        }
        return true;
    }

    public void signUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

}
