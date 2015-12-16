package ciceroapps.tether.na;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.androidchat.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends ListActivity {

    private static final String FIREBASE_URL = "https://narcotics-anonymous.firebaseio.com";
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;

    public static final String PREFS_NAME = "USER_PREFS";
    SharedPreferences settings;
    SharedPreferences.Editor storage;

    ProgressBar progressBar;
    Toast toast_meeting;
    Spinner spinner_state;
    Spinner spinner_day;
    Spinner spinner_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        storage = settings.edit();

        // Action Bar
        ActionBar action_bar = getActionBar();
        action_bar.setSubtitle("Find Meeting");
        toast_meeting = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        // ListView to render list of NA Meetings
        final ListView listView = (ListView) findViewById(R.id.custom_list);
        mFirebaseRef = new Firebase(FIREBASE_URL);

        // State Select
        ArrayAdapter<CharSequence> state_adapter = ArrayAdapter.createFromResource(this, R.array.state_array, android.R.layout.simple_spinner_item);
        state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_state = getSpinner("spinner_state");
        spinner_state.setAdapter(state_adapter);

        // City Select
        String[] items = new String[] {"- Cities -"};
        ArrayAdapter<String> place = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        final List<String> cityArray =  new ArrayList<>();
        final ArrayAdapter<String> city_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityArray);
        city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_city = getSpinner("spinner_city");
        spinner_city.setAdapter(place);

        // Day Select
        ArrayAdapter<CharSequence> day_adapter = ArrayAdapter.createFromResource(this, R.array.day_array, android.R.layout.simple_spinner_item);
        day_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        spinner_day = getSpinner("spinner_day");
        spinner_day.setAdapter(day_adapter);
        spinner_day.setSelection(day);


        // If state was previously selected
        // load that state on start
        int saved = settings.getInt("SavedState", 0);
        if(saved==0) {
            spinner_state.setSelection(saved);
        } else if(saved!=0){
            spinner_state.setSelection(saved);
        }
        // On State change
        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                storage.putInt("SavedState", position);
                storage.commit();
                populateCities(spinner_city, city_adapter, cityArray, parent.getSelectedItem().toString());
            }
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("No state selected!");
            }
        });

        // Submit Search
        findViewById(R.id.perform_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Spinner spin_state = getSpinner("spinner_state");
                Spinner spin_city = getSpinner("spinner_city");
                Spinner spin_day = getSpinner("spinner_day");

                if(spin_state.getVisibility()==View.VISIBLE && spin_city.getVisibility()==View.VISIBLE && spin_day.getVisibility()==View.VISIBLE){
                    String q_state = spinnerValue(getSpinner("spinner_state"));
                    final String q_city = spinnerValue(getSpinner("spinner_city"));
                    String q_day = spinnerValue(getSpinner("spinner_day"));

                    mFirebaseRef.child("States/"+q_state+"/meetings/"+q_day).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            int count = 0;
                            progressBar.setVisibility(View.INVISIBLE);

                            ArrayList<MeetingItem> results = new ArrayList<>();

                            // Iterate through meeting list
                            for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                MeetingItem listData = new MeetingItem();
                                MeetingItem meeting = postSnapshot.getValue(MeetingItem.class);

                                listData.setName(meeting.getName());
                                listData.setDay(meeting.getDay());
                                listData.setTime(meeting.getTime());
                                listData.setState(meeting.getState());
                                listData.setLocation_street(meeting.getLocation_street());
                                listData.setLocation_city(meeting.getLocation_city());

                                if (q_city.equals(meeting.getLocation_city())){
                                    results.add(listData);
                                    count++;
                                }
                            }

                            // Apply Adapter to view
                            listView.setAdapter(new CustomListAdapter(getApplicationContext(), results));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                    if(location!=null){
                                        double longitude = location.getLongitude();
                                        double latitude = location.getLatitude();

                                        TextView address = (TextView) view.findViewById(R.id.meeting_address);
                                        TextView city = (TextView) view.findViewById(R.id.meeting_city);
                                        String dest_address = address.getText().toString();
                                        String dest_city = city.getText().toString();
                                        // Google Maps Directions
                                        userDirections(latitude, longitude, dest_address,dest_city);
                                    } else {
                                        toast_meeting.setText("Sorry, directions aren't available.");
                                        toast_meeting.show();
                                    }
                                }
                            });

                            // Notify meeting count
                            if(count==0){
                                toast_meeting.setText("No meetings found.");
                            } else {
                                toast_meeting.setText(count+" meetings found.");
                            }
                                toast_meeting.show();
                        }
                        @Override
                        public void onCancelled(FirebaseError error) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    toast_meeting.setText("You must select a state, city, and day.");
                    toast_meeting.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                userReview();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void userReview() {
        try {
            Uri marketUri = Uri.parse("market://details?id=ciceroapps.tether.na");
            Intent intent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(intent);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            toast_meeting.setText("Sorry. Not available!");
            toast_meeting.show();
        }
    }
    public void userDirections(double src_lat, double src_ltg, String des_lat, String des_ltg) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr="+src_lat+","+src_ltg+"&daddr="+des_lat+","+des_ltg));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    public Spinner getSpinner(String spin) {
        return (Spinner)findViewById(getResources().getIdentifier(spin, "id", getPackageName()));
    }
    public String spinnerValue(Spinner selected) {
        return selected.getSelectedItem().toString();
    }

    public void populateCities(final Spinner spinner_city, final ArrayAdapter city_adapter, final List cityArray, final String the_state) {
        // Get Cities from state and create spinner
        mFirebaseRef.child("States/"+the_state+"/cities").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                cityArray.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    cityArray.add(postSnapshot.getValue());
                    Collections.sort(cityArray);
                }
                spinner_city.setAdapter(city_adapter);
            }
            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Finally, a little indication of connection status
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
            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
    }
}
