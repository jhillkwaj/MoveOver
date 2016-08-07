package com.rahulyesantharao.www.moveoverclient;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements Alert.OnFragmentInteractionListener {

    private String TAG_NOTHING = "nothing";
    private String TAG_ALERT = "alert";
    private NothingNearby nFrag = null;
    private boolean poweredOn = false;
    private MediaPlayer mediaPlayer = null;

    private static boolean login = false;
    FirebaseAuth mAuth;
    String uid;
    float distance = .6f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nFrag = NothingNearby.newInstance();
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, nFrag, TAG_NOTHING).commit();
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, Alert.newInstance(true, true, true), TAG_ALERT).commit();

        getSupportFragmentManager().executePendingTransactions();
        Fragment test = getSupportFragmentManager().findFragmentByTag(TAG_ALERT);
        if(test==null) {
            Log.d(getClass().getSimpleName(), "NULL ERROR");
        }
        else {
            Log.d(getClass().getSimpleName(), "Not null");
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(test).commit();
        }
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(nFrag).commit();

//        setContentView(R.layout.activity_main);

        SpannableString s = new SpannableString("MoveOver");
        s.setSpan(new TypefaceSpan(this, "ProximaNova.otf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        // Firebase code
        mAuth = FirebaseAuth.getInstance();


        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    login = true;
                    // User is signed in
                    Log.d("Login", "onAuthStateChanged:signed_in:" + user.getUid());

                    uid = user.getUid();
                    addLocListener(0,.0001f);
                } else {
                    login = false;
                    // User is signed out
                    Log.d("Login", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        mAuth.addAuthStateListener(mAuthListener);

        if(!login)
            signInAnonymously();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return(super.onCreateOptionsMenu(menu));
    }

    //call this method with the latest user location
    private void addLocListener(float lat, float lon){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geo-loc");

        GeoFire geoFire = new GeoFire(ref);

        //geoFire.setLocation("test-loc", new GeoLocation(24.7853889, -122.4056973));
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lon), distance);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                if(poweredOn) turnOnAlert(true, true, true);
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
                if(poweredOn) turnOffAlert();
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.power:
                if(poweredOn) { // turn off
                    turnOffAlert();
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(nFrag).commit();

                    item.setIcon(R.drawable.ic_off);
                    item.setTitle("TURN ON");
                    poweredOn = false;
                }
                else { // turn on
                    turnOnAlert(true, true, true);
//                    getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).show(nFrag).commit();
                    item.setIcon(R.drawable.ic_on);
                    item.setTitle("TURN OFF");
                    poweredOn = true;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // plays alert, vibrates, sends push notification, and adds alert fragment
    public void turnOnAlert(boolean police, boolean ambulance, boolean firetruck) {

        // vibrate
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0,500,200};
        v.vibrate(pattern, 0);

        // alarm
        mediaPlayer = MediaPlayer.create(this, R.raw.uwotm8);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // hide nothing fragment and show alert fragment
        findViewById(R.id.stopAlertBtn).setClickable(true);
        findViewById(R.id.stopAlertBtn).setEnabled(true);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(nFrag).commit();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).show(getSupportFragmentManager().findFragmentByTag(TAG_ALERT)).commit();


        // detach nothing fragment
//        if(!nFrag.isDetached()) {
//            getSupportFragmentManager().beginTransaction().detach(nFrag).commit();
//        }
        // delete any previous alert fragments
//        Fragment old = getSupportFragmentManager().findFragmentByTag(TAG_ALERT);
//        if(old!=null) {
//            getSupportFragmentManager().beginTransaction().remove(old).commit();
//        }
        // add new alert fragment
//        getSupportFragmentManager().beginTransaction().add(android.R.id.content, Alert.newInstance(police, ambulance, firetruck), TAG_ALERT).commit();
    }

    public void turnOffAlert() {
        turnOffAlertNoise();

        // show nothing fragment and hide alert fragment
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(getSupportFragmentManager().findFragmentByTag(TAG_ALERT)).commit();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).show(nFrag).commit();

        // delete any previous alert fragments
//        Fragment old = getSupportFragmentManager().findFragmentByTag(TAG_ALERT);
//        if(old!=null) {
//            getSupportFragmentManager().beginTransaction().remove(old).commit();
//        }
        // attach nothing fragment
//        getSupportFragmentManager().beginTransaction().attach(nFrag).commit();
    }

    public void turnOffAlertNoise() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.cancel();
        if(mediaPlayer!=null) mediaPlayer.pause();
    }

    @Override
    public void onStopPressed() {
        turnOffAlertNoise();
        findViewById(R.id.stopAlertBtn).setClickable(false);
        findViewById(R.id.stopAlertBtn).setEnabled(false);
        Log.d(getClass().getSimpleName(), "Stop Pressed");
    }


    private void signInAnonymously() {
        Log.d("Login Anon", "Start Anon Login");
        // [START signin_anonymously]
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                Log.d("Login Anon", "signInAnonymously:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w("Login Anon", "signInAnonymously", task.getException());

                }
            }


        });


        // [END signin_anonymously]
    }
}
