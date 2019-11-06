package com.shohagas.tourmate.fragments_view;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shohagas.tourmate.R;
import com.shohagas.tourmate.model.Event;

public class EventDetailsFragment extends Fragment {
    private Context context;
    private TextView mEventNameTv;
    private TextView mEventDestinationTv;
    private SeekBar seekBar;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        mEventNameTv = view.findViewById(R.id.detailFragment_event_name);
        mEventDestinationTv = view.findViewById(R.id.detailFragment_event_budget_status);
        seekBar = view.findViewById(R.id.detainlFragment_seekBar);
        seekBar.setProgress(50);

        Event event = (Event) getArguments().getSerializable("event_item");
        mEventNameTv.setText(event.getEventName());
        mEventDestinationTv.setText(String.valueOf(event.getEventBudget()));




        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
