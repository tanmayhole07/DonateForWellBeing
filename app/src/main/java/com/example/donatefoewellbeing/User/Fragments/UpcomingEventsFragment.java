package com.example.donatefoewellbeing.User.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.donatefoewellbeing.Adapters.AdapterEvent;
import com.example.donatefoewellbeing.Adapters.AdapterEventUser;
import com.example.donatefoewellbeing.Admin.Main.UpcommingEventsActivity;
import com.example.donatefoewellbeing.Models.ModelOngoingEvent;
import com.example.donatefoewellbeing.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpcomingEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpcomingEventsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UpcomingEventsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RandSEvents.
     */
    // TODO: Rename and change types and number of parameters
    public static UpcomingEventsFragment newInstance(String param1, String param2) {
        UpcomingEventsFragment fragment = new UpcomingEventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private RecyclerView eventsRv;

    private ArrayList<ModelOngoingEvent> ongoingEventList;
    private AdapterEventUser adapterEvent;

    String eventSection = "UpComingEvents";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);

        eventsRv = view.findViewById(R.id.eventsRv);

        loadUpComingEvents();


        return view;
    }

    private void loadUpComingEvents() {

        ongoingEventList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        ref.child(eventSection)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ongoingEventList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ModelOngoingEvent modelOngoingEvent = ds.getValue(ModelOngoingEvent.class);
                            ongoingEventList.add(modelOngoingEvent);
                        }
                        adapterEvent = new AdapterEventUser(getActivity(), ongoingEventList, eventSection);
                        eventsRv.setAdapter(adapterEvent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}