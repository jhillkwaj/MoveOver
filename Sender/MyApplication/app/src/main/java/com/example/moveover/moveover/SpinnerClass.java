package com.example.moveover.moveover;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class SpinnerClass implements OnItemSelectedListener {

    private int numSelected = 0;

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        numSelected = pos;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        numSelected = 0;

    }
    public int getNumSelected()
    {
        return numSelected;
    }

}
