package com.smsapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class MarshMallowPermission extends Activity {
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    public static final int READ_SMS_PERMISSION_REQUEST_CODE=3;
    public static final int READ_PHONE_STATE_PERMISSION_REQUEST_CODE=4;
    public static final int READ_CONTACT_PERMISSION_REQUEST_CODE=5;
    Activity activity;

    public MarshMallowPermission(Activity activity) {
        this.activity = activity;
    }

    /**
     * chek permission for marshmallow devices to Access External storage
     * @return
     */
    public boolean checkPermissionForExternalStorage(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    /**
     * chek permission for marshmallow devices to Access Camera
     * @return
     */
    public boolean checkPermissionForCamera(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    /**
     * chek permission for marshmallow devices to Read SMS
     * @return
     */
    public boolean checkPermissionForReadSMS(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }


    /**
     * chek permission for marshmallow devices to Read Phone State
     * @return
     */
    public boolean checkPermissionForReadPhoneState(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    /**
     * chek permission for marshmallow devices to Read Phone State
     * @return
     */
    public boolean checkPermissionForReadContact(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    /**
     * request permission for access external storage
     */
    public void requestPermissionForExternalStorage(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }
    /**
     * request permission for camera access
     */
    public void requestPermissionForCamera(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)){
//            Toast.makeText(activity, "Camera permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * request permission for read sms
     */
    public void requestPermissionForReadSMS(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_SMS)){
//            Toast.makeText(activity, "Read SMS permission allow us to read OTP send to you.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_SMS},READ_SMS_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * request permission for read phone state
     */
    public void requestPermissionForReadPhoneState(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)){
//            Toast.makeText(activity, "Read Phone state permission allow us to verify unique user.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_PHONE_STATE},READ_PHONE_STATE_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * request permission for read contact
     */
    public void requestPermissionForReadContact(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS)){
//            Toast.makeText(activity, "Read Contact permission allow us to verify unique user.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_CONTACTS},READ_CONTACT_PERMISSION_REQUEST_CODE);
        }
    }


    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     *
     * callback from request premission dialog
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("RsgApplication", "External Storage Permission granted");
                } else {
                    Log.d("RsgApplication","External Storage Permission  Permission Denied");
                }
                break;
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("RsgApplication", "Camera Permission granted");
                } else {
                    Log.d("RsgApplication","Camera Permission Denied");
                }
                break;
            case READ_SMS_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("RsgApplication", "Read SmsPermission granted");
                } else {
                    Log.d("RsgApplication","Read Sms Permission Denied");
                }
                break;
            case READ_CONTACT_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("RsgApplication", "Read Contact Permission granted");
                } else {
                    Log.d("RsgApplication","Read Contact Permission Denied");
                }
                break;
            case READ_PHONE_STATE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("RsgApplication", "Phone State Permission granted");
                } else {
                    Log.d("RsgApplication","Phone State Permission Denied");
                }
                break;
        }
    }
}
