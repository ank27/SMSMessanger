package com.smsapplication;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.smsapplication.Adapter.SMSListAdapter;
import com.smsapplication.Models.SMS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ankurkhandelwal on 01/07/16.
 */
public class ConversationDetailActivity extends AppCompatActivity {
    Toolbar toolbarConversation;
    Activity activity;
    public String TAG="ConversationActivity";
    ListView conversation_container;
    SMSListAdapter adapter;
    EditText sms_edit_text;
    FloatingActionButton fab_send;
    ProgressBar progressConversation;
    ArrayList<SMS> filteredList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        toolbarConversation=(Toolbar)findViewById(R.id.toolbarConversation);
        toolbarConversation.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbarConversation.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        activity=this;
        progressConversation=(ProgressBar) findViewById(R.id.progressConversation);
        conversation_container=(ListView) findViewById(R.id.conversation_container);
        sms_edit_text=(EditText) findViewById(R.id.sms_edit_text);
        fab_send=(FloatingActionButton) findViewById(R.id.fab_send);

        String name=getIntent().getExtras().getString("name");
        final String mobile=getIntent().getExtras().getString("mobile");
        toolbarConversation.setTitle(name);
        check_conversation(mobile);


        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sms_edit_text.getText().toString().equals("")){
                    String contact_number = mobile;
                    Log.d(TAG,contact_number);
                    try {
                        String SENT = "sent";
                        String DELIVERED = "delivered";
                        Intent sentIntent = new Intent(SENT);
                        /*Create Pending Intents*/
                        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        Intent deliveryIntent = new Intent(DELIVERED);
                        PendingIntent deliverPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, deliveryIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        /* Register for SMS send action */
                        registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                String result = "";
                                switch (getResultCode()) {
                                    case Activity.RESULT_OK:
                                        //SMS "date", "body" , "type" , "read"
                                        result = "SMS sent successfully";
                                        break;
                                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                        result = "Transmission failed";
                                        break;
                                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                                        result = "Radio off";
                                        break;
                                    case SmsManager.RESULT_ERROR_NULL_PDU:
                                        result = "No PDU defined";
                                        break;
                                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                                        result = "No service";
                                        break;
                                }
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                            }
                        }, new IntentFilter(SENT));
                        /* Register for Delivery event */
                        registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                Toast.makeText(getApplicationContext(), "SMS Deliverd", Toast.LENGTH_LONG).show();
                            }
                        }, new IntentFilter(DELIVERED));


                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(contact_number, null, sms_edit_text.getText().toString(), sentPendingIntent, deliverPendingIntent);
                        //SMS "date","msg","type","read"
                        SMS smsObject=new SMS(String.valueOf(System.currentTimeMillis()),sms_edit_text.getText().toString(),String.valueOf(2),String.valueOf(1));
                        filteredList.add(smsObject);
                        adapter.notifyDataSetChanged();
                        sms_edit_text.getText().clear();
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Failed to send sms",Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
//        String name=getIntent().getExtras().getString("name");
//        String mobile=getIntent().getExtras().getString("mobile");
//        Log.d(TAG,"mobile "+mobile);
    }

    private void check_conversation(String mobile) {
        progressConversation.setVisibility(View.VISIBLE);
        mobile=mobile.replaceAll("\\s+","");
        Log.d("Check conv","of "+mobile);
        filteredList=new ArrayList<>();
        Uri smsUri=Uri.parse("content://sms/");
        String[] reqCols = new String[] {"date", "body" , "type" , "read"};
        String selection="address='"+mobile+"'";
        Cursor cur = getContentResolver().query(smsUri, reqCols, selection, null, "date desc");
        String sms = "";
        Log.d(TAG,"Msgs  "+cur.getCount());
        while (cur.moveToNext()) {
            sms +=" date: " +cur.getString(0) +" body : "+cur.getString(1)+" type : "+cur.getString(2)+" read : "+cur.getString(3)+"\n\n\n";
            Log.d(TAG,"SMS "+sms);
            SMS smsObject=new SMS(cur.getString(0),cur.getString(1),cur.getString(2),cur.getString(3));
            filteredList.add(smsObject);
        }
        if(filteredList.size()>0) {
//            if(filteredList.size()>1) {
                Collections.reverse(filteredList);
//            }
            adapter = new SMSListAdapter(filteredList, activity);
            conversation_container.setAdapter(adapter);
            conversation_container.setSelection(adapter.getCount()-1);
        }
        progressConversation.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
