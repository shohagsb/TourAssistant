package com.shohagas.tourmate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shohagas.tourmate.R;
import com.shohagas.tourmate.model.Event;

import java.util.ArrayList;

public class EventViewAdapter extends RecyclerView.Adapter<EventViewAdapter.EventViewHolder> {
    private Context context;
    private ArrayList<Event> events;
    private ClickListener listener;

    public EventViewAdapter(Context context, ArrayList<Event> events,ClickListener listener ) {
        this.context = context;
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_layout_event_view, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.mEventNameTv.setText(event.getEventName());
        holder.mEventDestinationTv.setText(event.getEventDestinationLocation());
        holder.mEventDate.setText(event.getEventDepartureDate());
        holder.mEventBudget.setText("BDT "+String.valueOf(event.getEventBudget()));

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        TextView mEventNameTv;
        TextView mEventDestinationTv;
        TextView mEventDate;
        TextView mEventBudget;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            mEventNameTv = itemView.findViewById(R.id.row_event_name);
            mEventDestinationTv = itemView.findViewById(R.id.row_event_destination);
            mEventDate = itemView.findViewById(R.id.row_event_date);
            mEventBudget = itemView.findViewById(R.id.row_event_budget);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(events.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface ClickListener{
        public void onClick(Event event);
    }
}
