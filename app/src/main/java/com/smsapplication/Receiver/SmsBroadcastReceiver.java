package com.smsapplication.Receiver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.smsapplication.FragmentInbox;
import com.smsapplication.InboxActivity;
import com.smsapplication.R;

/**
 * Created by ankurkhandelwal on 01/07/16.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {
    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            String userdata="";
            String address="";
            String smsBody="";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                smsBody = smsMessage.getMessageBody();
                address = smsMessage.getOriginatingAddress();
                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
                byte[] data = smsMessage.getUserData();
                for (int index=0; index < data.length; index++) {
                    userdata += Byte.toString(data[index]);
                }
                Log.e("USerdata",userdata);
            }
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
            FragmentInbox fragmentInbox= FragmentInbox.instance();
            fragmentInbox.updateList(address,smsBody);

            Intent notificationIntent = new Intent(context, InboxActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_sms);
            Notification noti = new Notification.Builder(context).setContentTitle("New SMS from "+address).setContentText(smsMessageStr)
                    .setSmallIcon(R.drawable.ic_sms_small_icon).setContentIntent(pendingIntent).setLargeIcon(icon).build();
            noti.flags |= Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1,noti);
        }
    }
}