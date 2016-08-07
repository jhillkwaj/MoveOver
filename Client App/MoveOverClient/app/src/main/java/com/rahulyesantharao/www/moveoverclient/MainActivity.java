package com.rahulyesantharao.www.moveoverclient;


import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements Alert.OnFragmentInteractionListener {

    private String TAG_NOTHING = "nothing";
    private String TAG_ALERT = "alert";
    private NothingNearby nFrag = null;
    private MediaPlayer mediaPlayer = null;

    public boolean poweredOn = false;
    private boolean alarmOn = false;
    private boolean noiseOn = false;

    private static final String STATE_POWER = "power";
    private static final String STATE_ALARM = "alarm";
    private static final String STATE_NOISE = "noise";

    public static  MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        if(savedInstanceState!=null) {
            Log.d(getClass().getSimpleName(), "saved states");
            poweredOn = savedInstanceState.getBoolean(STATE_POWER);
            alarmOn = savedInstanceState.getBoolean(STATE_ALARM);
            noiseOn = savedInstanceState.getBoolean(STATE_NOISE);
            Log.d(getClass().getSimpleName(), "powered on: " + poweredOn);
            Log.d(getClass().getSimpleName(), "alarm on: " + alarmOn);
            Log.d(getClass().getSimpleName(), "noise on: " + noiseOn);
        }
        else {
            Log.d(getClass().getSimpleName(), "not saved states");
        }

        nFrag = NothingNearby.newInstance();
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, nFrag, TAG_NOTHING).commit();
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, Alert.newInstance(true, true, true), TAG_ALERT).commit();

        getSupportFragmentManager().executePendingTransactions();
        if(!poweredOn) {
            Fragment test = getSupportFragmentManager().findFragmentByTag(TAG_ALERT);
            if (test == null) {
                Log.d(getClass().getSimpleName(), "NULL ERROR");
            } else {
                Log.d(getClass().getSimpleName(), "Not null");
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(test).commit();
            }
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(nFrag).commit();
        }
        else {
            if(alarmOn) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(nFrag).commit();
                turnOnAlert(true, true, true);
                if(!noiseOn) {
                    turnOffAlertNoise();
                }
            }
            else {
                turnOffAlert();
            }
        }
//        setContentView(R.layout.activity_main);

        SpannableString s = new SpannableString("MoveOver");
        s.setSpan(new TypefaceSpan(this, "ProximaNova.otf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);

//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setDataSource(R.raw.uwotm8);
        mediaPlayer = MediaPlayer.create(this, R.raw.moveoveraud);
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared (MediaPlayer player){
//                playerPrep = true;
//            }
//        });
//        mediaPlayer.prepareAsync();
        mediaPlayer.setLooping(true);


        Firebase firebase = new Firebase(this);
        LocationData data = new LocationData(this,firebase);

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
                    turnOffAlert();
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(nFrag).commit();

                    item.setIcon(R.drawable.ic_off);
                    item.setTitle("TURN ON");
                    poweredOn = false;
                }
                else { // turn on
                    //turnOnAlert(true, true, true);
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).show(nFrag).commit();
                    item.setIcon(R.drawable.ic_on);
                    item.setTitle("TURN OFF");
                    poweredOn = true;
                }
                return true;
            case R.id.info:
                Intent i = new Intent(this, ScrollingActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(STATE_POWER, poweredOn);
        savedInstanceState.putBoolean(STATE_ALARM, alarmOn);
        savedInstanceState.putBoolean(STATE_NOISE, noiseOn);
        super.onSaveInstanceState(savedInstanceState);
    }

    // plays alert, vibrates, sends push notification, and adds alert fragment
    public void turnOnAlert(boolean police, boolean ambulance, boolean firetruck) {
        alarmOn = true;
        noiseOn = true;
        // vibrate
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0,500,200};
        v.vibrate(pattern, 0);

        // alarm
        if(!mediaPlayer.isPlaying()) mediaPlayer.start();

        // hide nothing fragment and show alert fragment
        Button b = (Button)findViewById(R.id.stopAlertBtn);
        try {
            b.setClickable(true);
        }
        catch (Exception e)
        {}
        b.setEnabled(true);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(nFrag).commit();
//        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).show(getSupportFragmentManager().findFragmentByTag(TAG_ALERT)).commit();


        Fragment old = getSupportFragmentManager().findFragmentByTag(TAG_ALERT);
        if(old!=null) {
            getSupportFragmentManager().beginTransaction().remove(old).commit();
        }
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, Alert.newInstance(police, ambulance, firetruck), TAG_ALERT).commit();
    }

    public void turnOffAlert() {
        alarmOn=false;
        turnOffAlertNoise();

        // show nothing fragment and hide alert fragment
//        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(getSupportFragmentManager().findFragmentByTag(TAG_ALERT)).commit();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).show(nFrag).commit();

        // delete any previous alert fragments
        Fragment old = getSupportFragmentManager().findFragmentByTag(TAG_ALERT);
        if(old!=null) {
            getSupportFragmentManager().beginTransaction().remove(old).commit();
        }
    }

    public void turnOffAlertNoise() {
        noiseOn = false;
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





}
