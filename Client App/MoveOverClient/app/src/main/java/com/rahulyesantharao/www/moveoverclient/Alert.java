package com.rahulyesantharao.www.moveoverclient;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Alert.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Alert#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Alert extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POLICE = "police";
    private static final String ARG_AMBULANCE = "ambulance";
    private static final String ARG_FIRETRUCK = "firetruck";

    private boolean mPolice;
    private boolean mAmbulance;
    private boolean mFiretruck;

    private OnFragmentInteractionListener mListener;

    public Alert() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param police Is it a police car?
     * @param ambulance Is it an ambulance?
     * @param firetruck Is it a firetruck?
     * @return A new instance of fragment NothingNearby.
     */
    // TODO: Rename and change types and number of parameters
    public static Alert newInstance(boolean police, boolean ambulance, boolean firetruck) {
        Alert fragment = new Alert();
        Bundle args = new Bundle();
        args.putBoolean(ARG_POLICE, police);
        args.putBoolean(ARG_AMBULANCE, ambulance);
        args.putBoolean(ARG_FIRETRUCK, firetruck);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPolice = getArguments().getBoolean(ARG_POLICE);
            mAmbulance = getArguments().getBoolean(ARG_AMBULANCE);
            mFiretruck = getArguments().getBoolean(ARG_FIRETRUCK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_alert, container, false);
        final Button stop = (Button) v.findViewById(R.id.stopAlertBtn);
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vi) {
                onStopPressed();
            }
        });
        TextView textView1 = (TextView) v.findViewById(R.id.alert_textView1);
        TextView textView2 = (TextView) v.findViewById(R.id.alert_textView2);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNova.otf");
        textView1.setTypeface(typeface);
        textView2.setTypeface(typeface);
        stop.setTypeface(typeface);

        ImageView imageView = (ImageView) v.findViewById(R.id.imageView2);
        if(mAmbulance) {
            Log.d(getClass().getSimpleName(), "Ambulance Alert!");
            imageView.setImageResource(R.drawable.hospital);
        }
        else if(mPolice) {
            Log.d(getClass().getSimpleName(), "Police Alert!");
            imageView.setImageResource(R.drawable.police_badge);
        }
        else if(mFiretruck) {
            Log.d(getClass().getSimpleName(), "Firetruck Alert!");
            imageView.setImageResource(R.drawable.firetruck);
        }
        else {
            Log.d(getClass().getSimpleName(), "NO PARAMETER ALERT!");
            imageView.setImageResource(R.drawable.hospital);
        }
        return v;
    }

    @Override
    public void onResume() {
        RelativeLayout rl = (RelativeLayout) getActivity().findViewById(R.id.alert_bg);

        ColorDrawable c1 = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
        ColorDrawable c2 = new ColorDrawable(getResources().getColor(R.color.background));

        AnimationDrawable a = new AnimationDrawable();

        a.addFrame(c1, 720);
        a.addFrame(c2, 300);
        a.setOneShot(false);
        a.setEnterFadeDuration(300);
        a.setExitFadeDuration(300);
        rl.setBackground(a);
        a.start();
        super.onResume();
    }

    // TODO: hook method into UI event
    public void onStopPressed() {
        if (mListener != null) {
            mListener.onStopPressed();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onStopPressed();
    }
}
