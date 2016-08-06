package com.rahulyesantharao.www.moveoverclient;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements Alert.OnFragmentInteractionListener {

    private String TAG_NOTHING = "nothing";
    private String TAG_ALERT = "alert";
    private NothingNearby nFrag = null;
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
            getSupportFragmentManager().beginTransaction().hide(test).commit();
        }

        turnOnAlert(true, true, true);
//        setContentView(R.layout.activity_main);
    }

    // plays alert, vibrates, sends push notification, and adds alert fragment
    public void turnOnAlert(boolean police, boolean ambulance, boolean firetruck) {



        // hide nothing fragment and show alert fragment
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

    }

    @Override
    public void onStopPressed() {
        turnOffAlertNoise();
        Log.d(getClass().getSimpleName(), "Stop Pressed");
    }
}
