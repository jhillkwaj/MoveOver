package com.rahulyesantharao.www.moveoverclient;

import android.app.Activity;
import android.content.Context;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by justi on 8/6/2016.
 */
public class Firebase {

    private static boolean login = false;
    FirebaseAuth mAuth;
    String uid;
    float distance = .2f;
    MainActivity mainActivity = null;
    GeoQuery geoQuery = null;

    int lastService = 0;

    public Firebase(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;

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

                } else {
                    login = false;
                    // User is signed out
                    Log.d("Login", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        mAuth.addAuthStateListener(mAuthListener);
        signInAnonymously();
    }

    private void signInAnonymously() {
        Log.d("Login Anon", "Start Anon Login");
        // [START signin_anonymously]
        mAuth.signInAnonymously().addOnCompleteListener(mainActivity, new OnCompleteListener<AuthResult>() {
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
    }

        //call this method with the latest user location
    public void addLocListener(double lat, double lon){
        if((lat == 0 && lon == 0) || !login)
        { return; }

        mainActivity = MainActivity.mainActivity;

        Log.d("Test",lat + " " + lon);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geo-loc");

        GeoFire geoFire = new GeoFire(ref);




            if (geoQuery == null) {

                Log.d("Test","Make Geo");
                //geoFire.setLocation("test-loc", new GeoLocation(24.7853889, -122.4056973));
                geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lon), distance);
                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));

                    }

                    @Override
                    public void onKeyExited(String key) {
                        System.out.println(String.format("Key %s is no longer in the search area", key));
                        if (mainActivity.poweredOn) mainActivity.turnOffAlert();
                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {
                        System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));

                        if (mainActivity.poweredOn){
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geo-loc/"+key);

                            ref.child("type").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    Log.d("test",""+snapshot.getValue());

                                    //this should not be null but it is sometime. Make it so it is not and remove this
                                    if(snapshot == null || snapshot.getValue() == null)
                                    {
                                        callLast();
                                        return;
                                    }

                                    if(snapshot.getValue().equals("Fire"))
                                    {
                                        Log.d("test","Fire");
                                        mainActivity.turnOnAlert(false,false,true);
                                        lastService = 0;
                                    }
                                    else if(snapshot.getValue().equals("Ambulance"))
                                    {
                                        Log.d("test","Ambulance");
                                        mainActivity.turnOnAlert(false,true,false);
                                        lastService = 1;
                                    }
                                    else if(snapshot.getValue().equals("Police"))
                                    {
                                        Log.d("test","Police");
                                        mainActivity.turnOnAlert(true,false,false);
                                        lastService = 2;
                                    }
                                    else
                                    {
                                        callLast();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("Error",databaseError.toString());
                                    callLast();
                                }


                            });


                        }
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
            } else {
                geoQuery.setCenter(new GeoLocation(lat, lon));


            }

    }


    private void callLast()
    {
        if(lastService == 0)
        {
            mainActivity.turnOnAlert(false,false,true);
        }
        else if(lastService == 1)
        {
            mainActivity.turnOnAlert(false,true,false);
        }
        else
        {
            mainActivity.turnOnAlert(true,false,false);
        }
    }
}
