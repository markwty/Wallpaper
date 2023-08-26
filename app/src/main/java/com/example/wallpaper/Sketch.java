package com.example.wallpaper;

import processing.core.PApplet;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;


public class Sketch extends PApplet {
    /*
    stay connect via USB
    connect to your WIFI network (computer and mobile device both)
    ping DeviceIP (must be have ping to your device)
    adb kill-server
    adb -d usb
    adb -d tcpip 5555
    unplug usb cable
    adb connect yourDeviceIP
    adb devices (must be see two device names , one of them is by deviceIP)
     */
    public void start(){
        super.start();
        loadPrefs();
    }

    public void resume(){
        super.resume();
        loadPrefs();
    }

    public void loadPrefs(){}

    public void settings()
    {
        fullScreen();
    }
}

class Sketch_SensorEnabled extends Sketch
{
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private SensorListener listener;

    float azimuth, pitch, tilt;
    private boolean sensor_enabled, started = false;

    Sketch_SensorEnabled(boolean sensor_enabled)
    {
        super();
        this.sensor_enabled = sensor_enabled;
    }

    public void start()
    {
        super.start();
        if (sensor_enabled)
        {
            listener = new SensorListener();
            mSensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            started = true;
        }
    }

    public void resume()
    {
        super.resume();
        if(sensor_enabled)
        {
            mSensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            started = true;
        }
    }

    public void onPause()
    {
        super.onPause();
        if(sensor_enabled && started)
        {
            mSensorManager.unregisterListener(listener);
            started = false;
        }
    }

    public void onStop()
    {
        super.onStop();
        if(sensor_enabled && started)
        {
            mSensorManager.unregisterListener(listener);
            started = false;
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        if(sensor_enabled && started)
        {
            mSensorManager.unregisterListener(listener);
            started = false;
        }
    }

    class SensorListener implements SensorEventListener
    {
        float[] R = new float[16];
        float[] orientation = new float[3];

        SensorListener()
        {
            super();
            mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            try
            {
                if(mSensorManager != null){
                    accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                }
            }
            catch (NullPointerException e)
            {
                Log.d("Error", "Cannot get sensors info");
            }
        }

        public void onSensorChanged(SensorEvent event)
        {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
            {
                SensorManager.getRotationMatrixFromVector(R, event.values);
            }
            SensorManager.getOrientation(R, orientation);
            azimuth = orientation[0];
            pitch = orientation[1];
            tilt = orientation[2];
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    }
}