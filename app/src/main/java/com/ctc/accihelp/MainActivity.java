package com.ctc.accihelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String LATITUDE = "Latitude";

    private static final String LONGITUDE = "Longitude";

    public int seconds = 20;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    TextView latTextView, lonTextView;
    FirebaseFirestore db;
    private Util handler;
    public boolean isSwitchedOn = false;
    public final int MIN_TIME_BETWEEN_SHAKES = 500;
    SensorManager sensorManager = null;
    Vibrator vibrator = null;
    String loca;
    private long Time = 0;
    private boolean flashLightOn = false;
    private Float threshold = 40.0f;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    int pStatus = 0;
    private Handler handler2 = new Handler();
    public MainActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        handler = new Util(MainActivity.this);
        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.lonTextView);
        final TextView textTimer = (TextView)findViewById(R.id.timer);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();
        checkForSmsPermission();
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        if(sensorManager!=null)
        {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(MainActivity.this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        }

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.progressbar);
        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);
    }
    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
        }
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latTextView.setText(location.getLatitude()+"");
                                    lonTextView.setText(location.getLongitude()+"");

                                }
                            }

                        }
                );
                loca=latTextView.toString()+" "+lonTextView.toString();
            } else {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latTextView.setText(mLastLocation.getLatitude()+"");
            lonTextView.setText(mLastLocation.getLongitude()+"");
            loca=latTextView.toString()+" "+lonTextView.toString();
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

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
                            flashLightOn = torchToggle("on");

                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }else
                    {
                        try {
                            flashLightOn = torchToggle("off");
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public boolean torchToggle(String str) throws CameraAccessException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final CameraManager cameraManager = (CameraManager) MainActivity.this.getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null;
            final String cmid2;


            if (cameraManager != null) {
                cameraId = cameraManager.getCameraIdList()[0];

            }

            if (cameraManager != null) {
                if (str.equals("on")) {
                    final TextView tv = (TextView) findViewById(R.id.timer);
                    cameraManager.setTorchMode(cameraId, true);
                    isSwitchedOn = true;
                    cmid2=cameraId;
                    final Button tap = findViewById(R.id.Tap);
                    Resources res = getResources();
                    Drawable drawable = res.getDrawable(R.drawable.progressbar);
                    final ProgressBar mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
                    final ProgressBar mProgress2 = (ProgressBar) findViewById(R.id.circularProgressbar2);
                    mProgress.setProgress(0);   // Main Progress
                    mProgress.setSecondaryProgress(100); // Secondary Progress
                    mProgress.setMax(100); // Maximum Progress
                    mProgress.setProgressDrawable(drawable);
                    mProgress2.setProgress(0);
                    Timer t = new Timer();
                    final String loco = "28.3662629 77.5425133";
                    t.scheduleAtFixedRate(new TimerTask() {

                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    if(seconds != 0)
                                    {
                                        tv.setText("00"+":"+String.valueOf(seconds));
                                        seconds -= 1;
                                    }
                                    if(seconds == 0)
                                    {
                                        tv.setText("00:00");
                                    }

                                }

                            });
                        }

                    }, 0, 1000);
                    final MediaPlayer alarm = MediaPlayer.create(MainActivity.this, R.raw.alarm);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            while (pStatus != 100) {
                                pStatus += 1;
                                tap.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mProgress2.setVisibility(View.VISIBLE);
                                        mProgress.setVisibility(View.GONE);
                                        Thread.currentThread().interrupt();
                                        alarm.stop();
                                        seconds=0;
                                        tv.setText("00:00");
                                        try {
                                            cameraManager.setTorchMode(cmid2, false);
                                            isSwitchedOn = false;
                                            Thread.sleep(0);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (CameraAccessException e) {
                                            e.printStackTrace();

                                        }
                                    }
                                });

                                handler2.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        mProgress.setProgress(pStatus);
                                        alarm.start();

                                    }
                                });
                                try {

                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            sendSMS("8130823076", "I need your help!!!");
                            sendSMS("8130823076", loco);
                            sendSMS("7838717601", "I need your help!!!");
                            sendSMS("7838717601", loco);
                            sendSMS("7065587091", "I need your help!!!");
                            sendSMS("7065587091", loco);
                        }
                    }).start();

//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage("+918130823076", null, "Your Friend Ritvik need help", null, null);

                } else {
                    cameraManager.setTorchMode(cameraId, false);
                    isSwitchedOn = false;
                }
            }
        }
        return isSwitchedOn;
    }
    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
