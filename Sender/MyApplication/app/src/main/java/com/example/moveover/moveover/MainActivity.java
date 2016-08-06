package com.example.moveover.moveover;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
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
    private static final String TAG = "Debug";
    private ToggleButton toggleButton1;

    FirebaseAuth mAuth;
    String uid;
    static boolean signin = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnButton();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        else
        {
            final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(db));
            startActivity(intent);
        }

    }

    public void addListenerOnButton() {

        toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);

        toggleButton1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //do something. result is "gettext"
                //if "ON" then turn on location signal
                //if "off" then turn off location signal

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


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
    }
    String db = "https://www.youtube.com/watch?v=oHg5SJYRHA0";
}
5