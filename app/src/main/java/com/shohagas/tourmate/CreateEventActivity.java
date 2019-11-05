package com.shohagas.tourmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shohagas.tourmate.model.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {
    private EditText mEventNameEt, mEventStartEt, mEventDestinationEt, mBudgetEt;
    private Button mDepartureDateBt, mCreateEventBt;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference dbRef;

    private Event event = new Event();

    private Date currentTime;
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    private int year, month, day;
    private String departureDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        setTitle("Create Event");
        mEventNameEt =  findViewById(R.id.editTextEventName);
        mEventStartEt =  findViewById(R.id.editTextEventStartLocation);
        mEventDestinationEt =  findViewById(R.id.editTextEventDestinationLocation);
        mBudgetEt =  findViewById(R.id.editTextEventBudget);
        mDepartureDateBt = findViewById(R.id.buttonDatePicker);
        mCreateEventBt = findViewById(R.id.buttonCreateEvent);

        //Initializing for getting current time
        currentTime = Calendar.getInstance().getTime();
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog.OnDateSetListener dateLisenter = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                SimpleDateFormat sdf = dateFormat;
                departureDate = sdf.format(calendar.getTime());
                mDepartureDateBt.setText(departureDate);
            }
        };

        mDepartureDateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CreateEventActivity.this, dateLisenter , year, month,day
                );
                datePickerDialog.show();
            }
        });


        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("Events");

        mCreateEventBt.setOnClickListener(onClickListener);


    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Getting Current Date
            String eventCreatedDate = dateFormat.format(currentTime);
            Double Budget = Double.parseDouble(mBudgetEt.getText().toString());
            String  eventId = dbRef.push().getKey();
            event = new Event(
                    eventId,
                    mEventNameEt.getText().toString(),
                    mEventStartEt.getText().toString(),
                    mEventDestinationEt.getText().toString(),
                    Budget,
                    departureDate,
                    eventCreatedDate);

            if(!currentUser.getUid().isEmpty() || currentUser.getUid() != null){

                dbRef.child(currentUser.getUid()).child(eventId).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(CreateEventActivity.this, "Successful to Create Event", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(CreateEventActivity.this, "Failed to Create Event", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }else{
                Toast.makeText(CreateEventActivity.this, "You have to login first!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateEventActivity.this,LoginActivity.class));
            }
        }
    };

}
