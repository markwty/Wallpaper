package com.example.wallpaper;

import processing.android.PWallpaper;
import processing.core.PApplet;

import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class MainService extends PWallpaper{
    @Override
    public PApplet createSketch()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        if (this.getEngine().isPreview())
        {
            editor.putBoolean("Previewed", true);
        }
        else
        {
            editor.putBoolean("Previewed", false);
        }
        editor.apply();
        return SketchDispatch(prefs.getInt("Wallpaper number", 0), prefs.getBoolean("Sensor enabled", true));
    }

    public PApplet SketchDispatch(int wallpaper_number, boolean sensor_enabled)
    {
        Sketches sketches = new Sketches();
        return sketches.GetSketch(wallpaper_number, sensor_enabled);
    }
}