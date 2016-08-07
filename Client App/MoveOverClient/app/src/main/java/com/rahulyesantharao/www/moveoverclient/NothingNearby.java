package com.rahulyesantharao.www.moveoverclient;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NothingNearby#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NothingNearby extends Fragment {
    public NothingNearby() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NothingNearby.
     */
    // TODO: Rename and change types and number of parameters
    public static NothingNearby newInstance() {
        NothingNearby fragment = new NothingNearby();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_nothing_nearby, container, false);
        TextView textView = (TextView) v.findViewById(R.id.textview_nothing);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNova.otf");
        textView.setTypeface(typeface);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
