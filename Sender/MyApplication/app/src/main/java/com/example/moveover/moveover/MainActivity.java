package com.example.moveover.moveover;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity  {

    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private AdapterView.OnItemSelectedListener selectedListener = null;
    private static final String TAG = "Debug";
    private ToggleButton toggleButton1;
    private SpinnerClass spin;
    private boolean active = false;
    private TextView inactiveText = null;
    private TextView broadcastText = null;
    private int numberSelected = 0; //0 = police, 1 = fire, 2 = ambulance
    FirebaseAuth mAuth;
    String uid;
    static boolean signin = false;
    static String type = "Police";
    private ImageView imageView = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SpannableString s = new SpannableString("MoveOver Sender");
        s.setSpan(new TypefaceSpan(this, "ProximaNova.otf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);

        final Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova.otf");

        inactiveText = (TextView) findViewById(R.id.inactiveText);
        broadcastText = (TextView) findViewById(R.id.broadcastText);
        broadcastText.setVisibility(View.INVISIBLE);
        inactiveText.setTypeface(tf);
        broadcastText.setTypeface(tf);

        toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
        toggleButton1.setTypeface(tf);
        addListenerOnButton();

        imageView = (ImageView) findViewById(R.id.imageView);

        Spinner spinner = (Spinner) findViewById(R.id.options_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.options_array)) {
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(tf);//Typeface for normal view
                ((TextView) v).setTextColor(Color.parseColor("#FFFFFF"));
                ((TextView) v).setTextSize(getResources().getDimension(R.dimen.spinnersize));
                return v;
            }
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(tf);//Typeface for dropdown view
//                ((TextView) v).setBackgroundColor(Color.parseColor("#BBfef3da"));
                ((TextView) v).setTextColor(Color.parseColor("#FFFFFF"));
                ((TextView) v).setTextSize(getResources().getDimension(R.dimen.spinnersize));
                return v;
            }
        };

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spin = new SpinnerClass();
        spinner.setOnItemSelectedListener(spin);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        locationListener = new MyLocationListener();
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Login", "onAuthStateChanged:signed_in:" + user.getUid());


                    uid = user.getUid();


                } else {
                    // User is signed out
                    Log.d("Login", "onAuthStateChanged:signed_out");
                    signin = false;
                }
                // ...
            }
        };


        mAuth.addAuthStateListener(mAuthListener);

        if(!signin)
            signInAnonymously();

    }

    public void addListenerOnButton() {
        toggleButton1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //do something. result is "gettext"
                //if "ON" then turn on location signal
                //if "off" then turn off location signal
                active=!active;
                if(active) {
                    broadcastText.setVisibility(View.VISIBLE);
                    inactiveText.setVisibility(View.INVISIBLE);
                    imageView.setImageResource(R.drawable.sirengreen);
                }
                else{
                    broadcastText.setVisibility(View.INVISIBLE);
                    inactiveText.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.sirenred);
                }
                numberSelected = spin.getNumSelected();
                if(numberSelected == 0) {
                    type = "Police";
                }
                else if(numberSelected == 1) {
                    type = "Fire Truck";
                }
                else if(numberSelected == 2) {
                    type = "Ambulance";
                }
                Log.v("numberSelected",numberSelected+"");
                Log.d("test","click");
                permission();

            }

        });

    }

    public void permission()
    {

        Log.d("test","permission start");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.v("test", "permission fail");
        }
        else{
            Log.v("test","gps start");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, locationListener);

        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            //lattitude = loc.getLatitude()
            //longitude = loc.getLongitude()
            updateLocations(loc.getLatitude(),loc.getLongitude());
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }



    private void signInAnonymously() {
        if(!signin) {
            // [START signin_anonymously]
            mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    Log.d("Login Anon", "signInAnonymously:onComplete:" + task.isSuccessful());
                    signin = task.isSuccessful();

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w("Login Anon", "signInAnonymously", task.getException());

                    } else {
                        Log.w("Login Fail", "Failed to sign it");
                    }
                }


            });
        }


        // [END signin_anonymously]
    }


    //Call this to update the gps location
    public void updateLocations(double lat, double lon)
    {
        Log.v("test","location send");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geo-loc");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(uid, new GeoLocation(lat, lon));
        DatabaseReference typeRef = FirebaseDatabase.getInstance().getReference("geo-loc/" + uid + "/type");
        typeRef.setValue(type);
    }

}
