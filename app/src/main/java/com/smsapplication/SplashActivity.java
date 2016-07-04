package com.smsapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smsapplication.Models.Contact;
import com.smsapplication.Models.SMS;

import java.util.logging.Logger;


public class SplashActivity extends Activity {
    Activity activity;
    CountDownTimer countDownTimer;
    boolean sms_granted=false;
    boolean contact_granted=false;
    MarshMallowPermission marshMallowPermission=null;
    ProgressBar progress_splash;
    TextView progress_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        progress_splash=(ProgressBar) findViewById(R.id.progress_splash);
        progress_text=(TextView) findViewById(R.id.progress_text);

        marshMallowPermission = new MarshMallowPermission(this);
        if (!marshMallowPermission.checkPermissionForReadSMS()) {
            marshMallowPermission.requestPermissionForReadSMS();
        } else {
            Log.d("Permission", "Already granted for Read Phone SMS");
        }
        if(!marshMallowPermission.checkPermissionForReadContact()){
            marshMallowPermission.requestPermissionForReadContact();
        } else {
            Log.d("Permission", "Already granted for Read Phone Contact");
        }


        if(Common.prefs.getString("first_time","").equals("")){
            progress_splash.setVisibility(View.VISIBLE);
            progress_text.setVisibility(View.VISIBLE);
        }

        if (marshMallowPermission.checkPermissionForReadSMS()) {
            if (Common.smsArrayListFull.size() == 0) {
                Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Uri smsUri = Uri.parse("content://sms/");
                        String[] reqCols = new String[]{"address", "_id", "date", "body", "type", "read", "person"};
                        Cursor cur = activity.getContentResolver().query(smsUri, reqCols, "address IS NOT NULL", null, null);
                        while (cur.moveToNext()) {
                            SMS smsObject = new SMS(cur.getString(1), cur.getString(0), cur.getString(2), cur.getString(3), cur.getString(4), cur.getString(5), cur.getString(6));
                            Common.smsArrayListFull.add(smsObject);
                        }
                    }
                };
                handler.post(runnable);
            }
        }
        if(marshMallowPermission.checkPermissionForReadContact()) {
            if (Common.contactList.size() == 0) {
                getContacts();
            }
        }

        if (marshMallowPermission.checkPermissionForReadSMS() && marshMallowPermission.checkPermissionForReadContact()) {
            countDownTimer = new CountDownTimer(2500, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    Intent inboxIntent = new Intent(SplashActivity.this, InboxActivity.class);
                    progress_splash.setVisibility(View.GONE);
                    Common.editor.putString("first_time","false").apply();
                    SplashActivity.this.startActivity(inboxIntent);
                    SplashActivity.this.finish();
                    SplashActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

            };
            countDownTimer.start();
        }else{
            if(!marshMallowPermission.checkPermissionForReadContact()){
                marshMallowPermission.requestPermissionForReadContact();
            }
            if(!marshMallowPermission.checkPermissionForReadSMS()){
                marshMallowPermission.requestPermissionForReadSMS();
            }
        }
    }

    private void getContacts() {
        Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String[] reqCols = new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, reqCols, null, null, null);
                // use the cursor to access the contacts
                while (phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                    String contact_id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    Contact contact = new Contact(id, name, phoneNumber, contact_id);
                    Common.contactList.add(contact);
                }

            }

        };
        handler.post(runnable);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("SMSApplication", "Read SmsPermission granted");
                    Handler handler = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Uri smsUri = Uri.parse("content://sms/");
                            String[] reqCols = new String[]{"address", "_id", "date", "body", "type", "read", "person"};
                            Cursor cur = activity.getContentResolver().query(smsUri, reqCols, "address IS NOT NULL", null, null);
                            while (cur.moveToNext()) {
                                SMS smsObject = new SMS(cur.getString(1), cur.getString(0), cur.getString(2), cur.getString(3), cur.getString(4), cur.getString(5), cur.getString(6));
                                Common.smsArrayListFull.add(smsObject);
                            }
                        }
                    };
                    handler.post(runnable);
                } else {
                    Log.d("RsgApplication","Read Sms Permission Denied");
                }
                break;
            case 5:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("RsgApplication", "Read Contact Permission granted");
                    if (Common.contactList.size() == 0) {
                        getContacts();
                    }
                } else {
                    Log.d("RsgApplication","Read Contact Permission Denied");
                }
                break;
        }
    }
}
