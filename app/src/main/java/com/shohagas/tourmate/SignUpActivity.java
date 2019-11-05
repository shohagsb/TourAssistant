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

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.shohagas.tourmate.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;


    private ProgressBar mProgressBar;
    private EditText mFullNameET;
    private EditText mPhoneET;
    private EditText mEmailET;
    private EditText mPasswordET;
    private EditText mConfirmPasswordET;
    private Button mRegisterBT;

    private String mFullName;
    private String mPhone;
    private String mEmail;
    private String mPassword;
    private String mConfirmPassowrd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle(getLocalClassName());
        mFirebaseAuth = FirebaseAuth.getInstance();

        mProgressBar = findViewById(R.id.progressBar);
        mFullNameET = findViewById(R.id.editTextFullName);
        mPhoneET = findViewById(R.id.editTextPhoneNumber);
        mEmailET = findViewById(R.id.editTextEmail);
        mPasswordET = findViewById(R.id.editTextPassword);
        mConfirmPasswordET = findViewById(R.id.editTextConfirmPassword);
        mRegisterBT = findViewById(R.id.buttonRegister);
        mRegisterBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                if (checkFields()) {
                    mFirebaseAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        User user = new User(mFullName, mPhone, mEmail);
                                        currentUser = mFirebaseAuth.getCurrentUser();
                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(currentUser.getUid())
                                                .setValue(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            if (currentUser != null) {
                                                                Toast.makeText(SignUpActivity.this, "Welcome " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                            }
                                                        } else {
                                                            Toast.makeText(SignUpActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                        mProgressBar.setVisibility(View.GONE);
                                    } else {
                                        mProgressBar.setVisibility(View.GONE);
                                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }


    private boolean checkFields() {
        mFullName = mFullNameET.getText().toString();
        mPhone = mPhoneET.getText().toString();
        mEmail = mEmailET.getText().toString();
        mPassword = mPasswordET.getText().toString();
        mConfirmPassowrd = mConfirmPasswordET.getText().toString();

        if (mFullName.isEmpty() || mPhone.isEmpty() || mEmail.isEmpty() || mPassword.isEmpty() || mConfirmPassowrd.isEmpty()) {
            mFullNameET.setError("Required Field");
            mPhoneET.setError("Required Field");
            mEmailET.setError("Required Field");
            mPasswordET.setError("Required Field");
            mConfirmPasswordET.setError("Required Field");
            return false;
        }

        if (!mPassword.equals(mConfirmPassowrd)) {
            mPasswordET.setError("Password Not Matched");
            mConfirmPasswordET.setError("Password Not Matched");
            Toast.makeText(this, "Password Not Matched ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
