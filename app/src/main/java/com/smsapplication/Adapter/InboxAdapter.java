package com.smsapplication.Adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smsapplication.Common;
import com.smsapplication.ContactDetailActivity;
import com.smsapplication.ConversationDetailActivity;
import com.smsapplication.Models.Contact;
import com.smsapplication.Models.SMS;
import com.smsapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ankurkhandelwal on 01/07/16.
 */
public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder>{
    List<SMS> smsArrayList;
    Activity activity;
    public InboxAdapter(Activity activity, List<SMS> data){
        this.activity= activity;
        this.smsArrayList = data;
    }
    @Override
    public InboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.single_sms_layout, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final InboxAdapter.ViewHolder holder, final int position) {
        holder.sms_content.setText(smsArrayList.get(position).message);

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.valueOf(smsArrayList.get(position).time));
        Date date = cal.getTime();
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(cal.getTime());
        holder.sms_timestamp.setText(month_name+" "+cal.get(Calendar.DATE));

        String name= getContactName(activity,smsArrayList.get(position).sender.replaceAll("\\s+",""));
        if(name!=null){
            holder.sms_sender.setText(name);
        }else {
            holder.sms_sender.setText(smsArrayList.get(position).sender);
        }


        holder.top_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent conversationIntent=new Intent(activity, ConversationDetailActivity.class);
                conversationIntent.putExtra("name", holder.sms_sender.getText().toString());
                conversationIntent.putExtra("mobile",smsArrayList.get(position).sender);
                activity.startActivity(conversationIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return smsArrayList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView sms_sender;
        public TextView sms_content;
        public TextView sms_timestamp;
        public RelativeLayout top_layout;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            sms_sender = (TextView) itemLayoutView.findViewById(R.id.sms_sender);
            sms_content = (TextView) itemLayoutView.findViewById(R.id.sms_content);
            sms_timestamp=(TextView) itemLayoutView.findViewById(R.id.sms_timestamp);
            top_layout=(RelativeLayout) itemLayoutView.findViewById(R.id.top_layout);
        }
    }

    public String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }
}