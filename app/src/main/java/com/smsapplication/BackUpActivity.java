package com.smsapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.smsapplication.Models.SMS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BackUpActivity extends BaseActivity{
    public String TAG="BackupActivity";
    private Bitmap mBitmapToSave;
    Toolbar toolbarBackup;
    Button backup_btn;
    ProgressBar progress_drive;
    TextView progress_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        backup_btn=(Button) findViewById(R.id.backup_btn);
        toolbarBackup=(Toolbar) findViewById(R.id.toolbarBackup);
        toolbarBackup.setTitle("BackUp to Drive");
        toolbarBackup.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        progress_drive=(ProgressBar)findViewById(R.id.progress_drive);
        progress_text=(TextView) findViewById(R.id.progress_text);
        toolbarBackup.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        backup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backup_btn.setVisibility(View.GONE);
                progress_drive.setVisibility(View.VISIBLE);
                progress_text.setVisibility(View.VISIBLE);
                if(getGoogleApiClient().isConnected()) {
                    Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(driveContentsCallback);
                }else {
                    getGoogleApiClient().connect();
                    backup_btn.setVisibility(View.VISIBLE);
                    progress_drive.setVisibility(View.GONE);
                    progress_text.setVisibility(View.GONE);
                    return;
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        Log.d(TAG,"Connected");
        // create new contents resource
//        Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(driveContentsCallback);
    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        backup_btn.setVisibility(View.VISIBLE);
                        progress_drive.setVisibility(View.GONE);
                        progress_text.setVisibility(View.GONE);
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();
                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            String sms_string="";
                            final Calendar cal = Calendar.getInstance();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd-yyyy");
                            if(Common.smsArrayList.size()>0){
                                for(int i=0;i<Common.smsArrayListFull.size();i++) {
                                    SMS sms=Common.smsArrayListFull.get(i);
                                    cal.setTimeInMillis(Long.valueOf(sms.time));
                                    Date date = cal.getTime();
                                    sms_string +=String.valueOf(dateFormat.format(date)) +" - "+sms.sender+" - "+sms.message+"\n\n";
                                }
                            }
                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                writer.write(sms_string);
                                writer.close();
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }
                            Calendar calendar = Calendar.getInstance();
                            int year=calendar.get(Calendar.YEAR);
                            int month=calendar.get(Calendar.MONTH);
                            int date=calendar.get(Calendar.DATE);
                            String current_time=  String.valueOf(date)+"_"+String.valueOf(month+1)+"_"+String.valueOf(year)+"_"+String.valueOf(System.currentTimeMillis());

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("SMS_BACKUP_"+current_time).setMimeType("text/plain")
                                    .setStarred(true).build();

                            // create a file on root folder
                            Drive.DriveApi.getRootFolder(getGoogleApiClient())
                                    .createFile(getGoogleApiClient(), changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }else {
                        showMessage("File Uploaded Successfully");
                    }
                    backup_btn.setVisibility(View.VISIBLE);
                    progress_drive.setVisibility(View.GONE);
                    progress_text.setVisibility(View.GONE);
                }
            };

}


