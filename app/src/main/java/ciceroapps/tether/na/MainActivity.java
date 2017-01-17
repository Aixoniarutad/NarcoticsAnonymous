package ciceroapps.tether.na;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;

import java.sql.Array;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private Firebase mFirebaseRef = new Firebase("https://narcotics-anonymous.firebaseio.com/master/state");
    private ValueEventListener mConnectedListener;
    private ProgressBar progressBar;
    private Button search_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setSubtitle("Find Meeting");
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        search_button = (Button) findViewById(R.id.search_button);

        // State
        final Spinner spinner_state = (Spinner) findViewById(R.id.spinner_state);
        ArrayAdapter<CharSequence> state_adapter = ArrayAdapter.createFromResource(this, R.array.state_array, android.R.layout.simple_spinner_item);
        state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_state.setAdapter(state_adapter);
        // Day
        final Spinner spinner_day = (Spinner) findViewById(R.id.spinner_day);
        ArrayAdapter<CharSequence> day_adapter = ArrayAdapter.createFromResource(this, R.array.day_array, android.R.layout.simple_spinner_item);
        day_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_day.setAdapter(day_adapter);
        // Distance
        final Spinner spinner_distance = (Spinner) findViewById(R.id.spinner_distance);
        ArrayAdapter<CharSequence> distance_adapter = ArrayAdapter.createFromResource(this, R.array.distance_array, android.R.layout.simple_spinner_item);
        distance_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_distance.setAdapter(distance_adapter);
        spinner_distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final ListView listView = (ListView) findViewById(R.id.meeting_list);
                final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                final Location userLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                int[] distanceArray = new int[] {5,10,25,50,100};
                final String qState = spinner_state.getSelectedItem().toString();
                final String qDay = spinner_day.getSelectedItem().toString();
                final int qDistance = distanceArray[spinner_distance.getSelectedItemPosition()];
                final Firebase query = mFirebaseRef.child(qState+"/"+qDay);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange (DataSnapshot snapshot){
                        progressBar.setVisibility(View.INVISIBLE);
                        int count = 0;

                        ArrayList<MeetingItem> results = new ArrayList<>();

                        // Iterate through meeting list
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            MeetingItem meeting = postSnapshot.getValue(MeetingItem.class);

                            if(userLocation==null){
                                results.add(meeting);
                                count++;
                            } else {
                                Location destLocation = new Location("");
                                destLocation.setLatitude(meeting.LATITUDE);
                                destLocation.setLongitude(meeting.LONGITUDE);
                                meeting.DISTANCE = Math.round((userLocation.distanceTo(destLocation)*0.000621371192237334));
                                results.add(meeting);
                                count++;
                            }
                        }
                        // Apply Adapter to view
                        listView.setAdapter(new CustomListAdapter(getApplicationContext(), results));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        });
                        // Notify Changes
                        if(userLocation==null) {
                            Toast.makeText(MainActivity.this, "Location not found.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, count+" meetings", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(FirebaseError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        // Search Meetings
//        search_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//                final Location userLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                final String qState = spinner_state.getSelectedItem().toString();
//                final String qDay = spinner_day.getSelectedItem().toString();
//                final String qDistance = spinner_distance.getSelectedItem().toString();
//                final Firebase query = mFirebaseRef.child(qState+"/"+qDay);
//                FirebaseListAdapter meetingAdapter = new FirebaseListAdapter<MeetingItem>(MainActivity.this, MeetingItem.class, R.layout.meeting_item, query) {
//                    @Override
//                    protected void populateView(View view, final MeetingItem meeting, int position) {
//                        final Location destLocation = new Location("");
//                        destLocation.setLatitude(meeting.LATITUDE);
//                        destLocation.setLongitude(meeting.LONGITUDE);
//                        final long totalDistance = Math.round((userLocation.distanceTo(destLocation)*0.000621371192237334));
//
//                        ((TextView)view.findViewById(R.id.meeting_time)).setText(meeting.TIME);
//                        ((TextView)view.findViewById(R.id.meeting_distance)).setText(""+totalDistance+"mi");
//
//                        view.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                userDirections(userLocation, destLocation);
//                            }
//                        });
//                    }
//                };
//                ListView meetingList = (ListView) findViewById(R.id.meeting_list);
//                meetingList.setAdapter(meetingAdapter);
//            }
//        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rate:
                userReview();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getBaseContext(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
    }

    public void userReview() {
        Uri marketUri = Uri.parse("market://details?id=ciceroapps.tether.na");
        Intent intent = new Intent(Intent.ACTION_VIEW, marketUri);
        startActivity(intent);
    }
    public void userDirections(Location user, Location meeting) {
        if(user!=null){
            Uri destination = Uri.parse("http://maps.google.com/maps?saddr="+user.getLatitude()+","+user.getLongitude()+"&daddr="+meeting.getLatitude()+","+meeting.getLongitude());
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, destination);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        } else {
            Toast.makeText(this, "Directions not available.", Toast.LENGTH_SHORT).show();
        }
    }
}
