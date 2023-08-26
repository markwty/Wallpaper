package com.example.wallpaper;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class ColorPreference extends Preference {

    private int red, green, blue;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        setWidgetLayoutResource(R.layout.color_preference);
    }
    public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWidgetLayoutResource(R.layout.color_preference);
    }
    @Override
    public void onBindViewHolder(PreferenceViewHolder view)
    {
        super.onBindViewHolder(view);
        view.itemView.setClickable(false); // disable parent click
        final Button colorBtn = (Button)view.findViewById(R.id.colorBtn);
        colorBtn.setClickable(false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SeekBar redSeekBar = (SeekBar)view.findViewById(R.id.redSeekBar);
        red = prefs.getInt("red", 0);
        green = prefs.getInt("green", 0);
        blue = prefs.getInt("blue", 0);
        redSeekBar.setProgress(red);
        redSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){ }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
                editor = prefs.edit();
                editor.putInt("red", red);
                editor.apply();
            }
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int intColor = ((ColorDrawable)colorBtn.getBackground()).getColor();
                colorBtn.setBackgroundColor(Color.argb(255, progress, Color.green(intColor), Color.blue(intColor)));
                red = progress;
            }
        });
        final SeekBar greenSeekBar = (SeekBar)view.findViewById(R.id.greenSeekBar);
        greenSeekBar.setProgress(green);
        greenSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){ }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
                editor = prefs.edit();
                editor.putInt("green", green);
                editor.apply();
            }
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int intColor = ((ColorDrawable)colorBtn.getBackground()).getColor();
                colorBtn.setBackgroundColor(Color.argb(255, Color.red(intColor), progress, Color.blue(intColor)));
                green = progress;
            }
        });
        final SeekBar blueSeekBar = (SeekBar)view.findViewById(R.id.blueSeekBar);
        blueSeekBar.setProgress(blue);
        blueSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){ }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
                editor = prefs.edit();
                editor.putInt("blue", blue);
                editor.apply();
            }
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int intColor = ((ColorDrawable)colorBtn.getBackground()).getColor();
                colorBtn.setBackgroundColor(Color.argb(255, Color.red(intColor), Color.green(intColor), progress));
                blue = progress;
            }
        });
        colorBtn.setBackgroundColor(Color.argb(255, red, green, blue));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue)
    {
        if(restorePersistedValue)
        {
            red = this.getPersistedInt(defaultValue == null ? 0 : Color.red((int)defaultValue));
            green = this.getPersistedInt(defaultValue == null ? 0 : Color.green((int)defaultValue));
            blue = this.getPersistedInt(defaultValue == null ? 0 : Color.blue((int)defaultValue));
        }
        else
        {
            red = Color.red((int)defaultValue);
            green = Color.green((int)defaultValue);
            blue = Color.blue((int)defaultValue);
            if (shouldPersist())
                persistInt(Color.argb(255, red, green, blue));
        }
    }
}
