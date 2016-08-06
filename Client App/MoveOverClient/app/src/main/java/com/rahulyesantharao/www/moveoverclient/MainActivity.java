package com.rahulyesantharao.www.moveoverclient;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements Alert.OnFragmentInteractionListener {

    private String TAG_NOTHING = "nothing";
    private String TAG_ALERT = "alert";
    private NothingNearby nFrag = null;
    private boolean poweredOn = false;
    private MediaPlayer mediaPlayer;
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

        turnOnAlert(true, true, true);
//        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.power:
                if(poweredOn) { // turn off

//                    item.setIcon(R.drawable.off);
                    item.setTitle("OFF");
                    poweredOn = false;
                }
                else { // turn on
//                    item.setIcon(R.drawable.on);
                    item.setTitle("ON");
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
        mediaPlayer.pause();
    }

    @Override
    public void onStopPressed() {
        turnOffAlertNoise();
        findViewById(R.id.stopAlertBtn).setClickable(false);
        findViewById(R.id.stopAlertBtn).setEnabled(false);
        Log.d(getClass().getSimpleName(), "Stop Pressed");
    }
}
