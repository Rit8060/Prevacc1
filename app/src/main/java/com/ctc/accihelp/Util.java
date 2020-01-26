package com.ctc.accihelp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.os.Build;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class Util {
    private MainActivity context2;
    Context context;
    public boolean isSwitchedOn = false;

    public Util(Context context) {
        this.context = context;
    }


    public boolean torchToggle(String str) throws CameraAccessException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null;


            if (cameraManager != null) {
                cameraId = cameraManager.getCameraIdList()[0];
            }

            if (cameraManager != null) {
                if (str.equals("on")) {

                    cameraManager.setTorchMode(cameraId, true);
                    isSwitchedOn = true;
//                    sendSMS("8130823076", "Emergency");
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
//
//    void sendSmsMsgFnc(String mblNumVar, String smsMsgVar)
//    {
//        if (ActivityCompat.checkSelfPermission(Util.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
//        {
//            try
//            {
//                SmsManager smsMgrVar = SmsManager.getDefault();
//                smsMgrVar.sendTextMessage(mblNumVar, null, smsMsgVar, null, null);
//
//            }
//            catch (Exception ErrVar)
//            {
//
//                ErrVar.printStackTrace();
//            }
//        }
//        else
//        {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            {
//                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 10);
//            }
//        }
//
//    }
//    public void sendSMS(String phoneNo, String msg) {
//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
}
