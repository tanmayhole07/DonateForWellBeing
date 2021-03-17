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

import com.example.donatefoewellbeing.Admin.EventDescriptionActivity;
import com.example.donatefoewellbeing.Models.ModelOngoingEvent;
import com.example.donatefoewellbeing.R;
import com.example.donatefoewellbeing.User.EventDescriptionActivityUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterEventUser extends RecyclerView.Adapter<AdapterEventUser.HolderEventUser> {

    private Context context;
    public ArrayList<ModelOngoingEvent> eventList;
    private String eventSection;

    public AdapterEventUser(Context context, ArrayList<ModelOngoingEvent> eventList, String eventSection) {
        this.context = context;
        this.eventList = eventList;
        this.eventSection = eventSection;
    }

    @NonNull
    @Override
    public HolderEventUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_ongoing_events, parent, false);

        return new HolderEventUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderEventUser holder, int position) {

        ModelOngoingEvent modelOngoingEvent = eventList.get(position);
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
                Intent intent = new Intent(context, EventDescriptionActivityUser.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("eventSection",eventSection);
                context.startActivity(intent);
            }
        });
        
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class HolderEventUser extends RecyclerView.ViewHolder {

        private ImageView eventIv, nextIv;
        private TextView eventNameTv, eventDescriptionTv, eventDateTv, organizationNameTv;

        public HolderEventUser(@NonNull View itemView) {
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
