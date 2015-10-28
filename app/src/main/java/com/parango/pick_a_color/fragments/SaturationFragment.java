package com.parango.pick_a_color.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.parango.pick_a_color.R;
import com.parango.pick_a_color.util.ColorAdapter;
import com.parango.pick_a_color.util.HSVColor;

import java.util.ArrayList;


public class SaturationFragment
        extends Fragment {

    public final static String ARG_SWATCH_NUM = "satSwatchNum";
    public final static String ARG_HUE_START = "hueStart";
    public final static String ARG_HUE_END = "hueEnd";

    private int mSwatchNum;
    private float mHueStart;
    private float mHueEnd;
    private ListView mColorList;
    private OnSaturationSelectedListener mCallback;


    public interface OnSaturationSelectedListener {
        public void onSaturationSelected(float hueStart, float hueEnd, float saturation);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mSwatchNum = getActivity().getPreferences(Activity.MODE_PRIVATE).getInt(ARG_SWATCH_NUM, 10);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = ( OnSaturationSelectedListener ) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.color_main, container, false);
        // Inflate the layout for this fragment
        mColorList = (ListView) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            // Set color_row based on argument passed in
            mSwatchNum = args.getInt(ARG_SWATCH_NUM, mSwatchNum);
            mHueStart = args.getFloat(ARG_HUE_START, 0);
            mHueEnd = args.getFloat(ARG_HUE_END, 360);
        }


        mColorList.setDividerHeight(2);
        fillData( mSwatchNum, mHueStart, mHueEnd );
        setListListener( mColorList );

        Button configButton = (Button) getActivity().findViewById(R.id.button);
        configButton.setText(R.string.sat_preferences);
        setButtonListener( configButton );

    }

    private void fillData( int swatchNum,
                           float hueStart,
                           float hueEnd) {

        ArrayList<HSVColor> colorList = new ArrayList<>();
        for (int i = swatchNum; i > 0; i-=1) {
            colorList.add(new HSVColor(hueStart, hueEnd , (float)i/swatchNum , 1f));
        }
        mColorList.setAdapter(new ColorAdapter(getActivity(), colorList));
    }

    private void setListListener(ListView lv ){
        lv.setOnItemClickListener( new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Notify the parent activity of selected item
                HSVColor color = (HSVColor) parent.getItemAtPosition( position );
                float hueStart =  color.getHueStart();
                float hueEnd =  color.getHueEnd();
                float saturation =  color.getSaturation();
                mCallback.onSaturationSelected(hueStart, hueEnd, saturation);

            }
        });
    }

    private void setButtonListener( Button button){

        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater =
                        (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.swatch_num_dialog,
                        (ViewGroup) getActivity().findViewById(R.id.swatch_num_dialog));

                final TextView progressView = (TextView) layout.findViewById(R.id.dialog_seekBar_progress);
                progressView.setText(String.valueOf(mSwatchNum));

                SeekBar seekBar = (SeekBar) layout.findViewById(R.id.dialog_seekBar);
                seekBar.setMax(255);
                seekBar.setProgress(mSwatchNum);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        mSwatchNum = progressValue+1;
                        progressView.setText(String.valueOf(mSwatchNum));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setView(layout);
                dialog.setTitle(R.string.sat_preferences);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fillData( mSwatchNum, mHueStart, mHueEnd);
                    }
                });
                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();



            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_SWATCH_NUM, mSwatchNum);
        outState.putFloat(ARG_HUE_START, mHueStart);
        outState.putFloat(ARG_HUE_END, mHueEnd);
    }

    @Override
    public void onPause(){
        super.onPause();

        SharedPreferences.Editor editor = getActivity().getPreferences(Activity.MODE_PRIVATE).edit();
        editor.putInt(ARG_SWATCH_NUM, mSwatchNum);
        editor.apply();
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        // Notify the parent activity of selected item
//        HSVColor color = (HSVColor) getListAdapter().getItem(position);
//        float hueStart =  color.getHueStart();
//        float hueEnd =  color.getHueEnd();
//        float saturation =  color.getSaturation();
//        mCallback.onSaturationSelected(hueStart, hueEnd, saturation);
//
//    }
//
//
//    private void updateCurrentList( int swatchNum, float hueStart, float hueEnd ){
//        mColorList = new ArrayList<>();
//        for (int i = swatchNum; i > 0; i-=1) {
//            mColorList.add(new HSVColor(hueStart, hueEnd , (float)i/swatchNum , 1f));
//        }
//        setListAdapter(new ColorAdapter(getActivity(), mColorList));
//    }


}
