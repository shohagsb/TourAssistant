package com.shohagas.tourmate.event_fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shohagas.tourmate.CreateEventActivity;
import com.shohagas.tourmate.MainActivity;
import com.shohagas.tourmate.R;
import com.shohagas.tourmate.adapter.EventViewAdapter;
import com.shohagas.tourmate.model.Event;

import java.util.ArrayList;

public class EventListFragment extends Fragment implements EventViewAdapter.ClickListener {
    private FloatingActionButton addEvent;
    private ProgressBar progressBar;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference dbRef;

    private ArrayList<Event> events = new ArrayList<>();
    private RecyclerView recyclerView;
    private EventViewAdapter adapter;
    RecyclerView.LayoutManager manager;

    private Context context;

    public EventListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        progressBar = view.findViewById(R.id.eventListFragment_progressBar1);
        progressBar.setVisibility(View.VISIBLE);
        addEvent = view.findViewById(R.id.addEventFAB);
        addEvent.setOnClickListener(floatBtnOnClickListener);

        recyclerView = view.findViewById(R.id.recycler_view_events_view);
        manager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);

        // For Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("Events");

        dbRef.addValueEventListener(valueEventListener);

        return view;
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            progressBar.setVisibility(View.VISIBLE);
            events.clear();
            for (DataSnapshot snapshot : dataSnapshot.child(currentUser.getUid()).getChildren()) {
                Event event = snapshot.getValue(Event.class);
                events.add(event);

            }
            adapter = new EventViewAdapter(context, events, EventListFragment.this);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Getting Post failed, log a message
            //Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("event_item", event);
        EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
        eventDetailsFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_fragment_host, eventDetailsFragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    /* Floating Button OnClick Listener*/
    private View.OnClickListener floatBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(getActivity(), CreateEventActivity.class));
        }
    };
}
