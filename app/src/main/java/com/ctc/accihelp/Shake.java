package com.ctc.accihelp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.os.IBinder;
import android.os.Vibrator;

public class Shake extends Service implements SensorEventListener {

    public final int MIN_TIME_BETWEEN_SHAKES = 500;
    SensorManager sensorManager = null;
    Vibrator vibrator = null;
    private long Time = 0;
    private boolean flashLightOn = false;
    private Float threshold = 40.0f;
    public Shake(){}
    Util util;
    MainActivity mainActivity;

    @Override
    public void onCreate()
    {
        super.onCreate();
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        if(sensorManager!=null)
        {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        }
        util = new Util(this);
        mainActivity = new MainActivity();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            long currentTimeMillis = System.currentTimeMillis();

            if((currentTimeMillis - Time)>MIN_TIME_BETWEEN_SHAKES)
            {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                double acc = Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2))-SensorManager.GRAVITY_EARTH;
                if (acc > threshold) {
                    Time = currentTimeMillis;
                    if (!flashLightOn)
                    {
                        try {
                            flashLightOn = util.torchToggle("on");

                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }else
                        {
                            try {
                                flashLightOn = util.torchToggle("off");
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
