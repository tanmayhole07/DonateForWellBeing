package com.example.donatefoewellbeing.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donatefoewellbeing.Activities.AddEventActivity;
import com.example.donatefoewellbeing.Activities.EventDescriptionActivity;
import com.example.donatefoewellbeing.Activities.OngoingEventsActivity;
import com.example.donatefoewellbeing.Models.ModelOngoingEvent;
import com.example.donatefoewellbeing.R;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterOngoingEvent extends RecyclerView.Adapter<AdapterOngoingEvent.HolderOngoingEvent> {


    private Context context;
    public ArrayList<ModelOngoingEvent> ongoingEventList;

    public AdapterOngoingEvent(Context context, ArrayList<ModelOngoingEvent> ongoingEventList) {
        this.context = context;
        this.ongoingEventList = ongoingEventList;
    }

    @NonNull
    @Override
    public HolderOngoingEvent onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_ongoing_events, parent, false);

        return new HolderOngoingEvent(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOngoingEvent holder, int position) {

        ModelOngoingEvent modelOngoingEvent = ongoingEventList.get(position);
        String eventName = modelOngoingEvent.getEventName();
        String eventDescription = modelOngoingEvent.getEventDescription();
        String eventDate = modelOngoingEvent.getDate();
        String organizationName = modelOngoingEvent.getEventOrganizationName();
        String eventImage = modelOngoingEvent.getEventImage();
        String eventId = modelOngoingEvent.getTimeStamp();

        holder.eventNameTv.setText(eventName);
        holder.eventDescriptionTv.setText(eventDescription);
        holder.eventDateTv.setText(eventDate);
        holder.organizationNameTv.setText(organizationName);


        try {
            Picasso.get().load(eventImage).placeholder(R.drawable.ic_photo_grey).into(holder.eventIv);
        } catch (Exception e) {
            holder.eventIv.setImageResource(R.drawable.ic_photo_grey);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventDescriptionActivity.class);
                intent.putExtra("eventId", eventId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ongoingEventList.size();
    }

    class HolderOngoingEvent extends RecyclerView.ViewHolder {

        private ImageView eventIv, nextIv;
        private TextView eventNameTv, eventDescriptionTv, eventDateTv, organizationNameTv;

        public HolderOngoingEvent(@NonNull View itemView) {
            super(itemView);

            eventIv = itemView.findViewById(R.id.eventIv);
            eventNameTv = itemView.findViewById(R.id.eventNameTv);
            eventDescriptionTv = itemView.findViewById(R.id.eventDescriptionTv);
            eventDateTv = itemView.findViewById(R.id.eventDateTv);
            organizationNameTv = itemView.findViewById(R.id.organizationNameTv);
            nextIv = itemView.findViewById(R.id.nextIv);
        }
    }
}
