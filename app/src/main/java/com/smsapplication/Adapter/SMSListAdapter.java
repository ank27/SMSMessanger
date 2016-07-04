package com.smsapplication.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smsapplication.Models.SMS;
import com.smsapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SMSListAdapter extends BaseAdapter {
    private ArrayList<SMS> smsArrayList;
    private Context context;

    public SMSListAdapter(ArrayList<SMS> smses, Context context) {
        this.smsArrayList = smses;
        this.context = context;

    }


    @Override
    public int getCount() {
        return smsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return smsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        SMS message = smsArrayList.get(position);
        ViewHolder1 holder1;
        ViewHolder2 holder2;
        //if sent sms
        if (message.type.equals("2")) {
            v = LayoutInflater.from(context).inflate(R.layout.layout_outgoing_sms, parent, false);
            holder1 = new ViewHolder1();
            holder1.message_text_outgoing = (TextView) v.findViewById(R.id.message_text_outgoing);
            holder1.time_text_outgoing = (TextView) v.findViewById(R.id.time_text_outgoing);
            holder1.message_text_outgoing.setText(message.message);
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.valueOf(message.time));
            Date date = cal.getTime();
            Log.d("SMSAdapter",String.valueOf(date));
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(cal.getTime());

            if(message.status==0){
                holder1.time_text_outgoing.setText("Sending...");
            }else if(message.status==1){
                holder1.time_text_outgoing.setText(month_name+" "+cal.get(Calendar.DATE));
            }

        } else if (message.type.equals("1")) {  //if inbox sms
            v = LayoutInflater.from(context).inflate(R.layout.layout_incoming_sms, parent, false);
            holder2 = new ViewHolder2();
            holder2.message_text_incoming = (TextView) v.findViewById(R.id.message_text_incoming);
            holder2.time_text_incoming = (TextView) v.findViewById(R.id.time_text_incoming);
            holder2.message_text_incoming.setText(message.message);
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.valueOf(message.time));
            Date date = cal.getTime();
            Log.d("InboxAdapter",String.valueOf(date));
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(cal.getTime());
            holder2.time_text_incoming.setText(month_name+" "+cal.get(Calendar.DATE));
        }else if (message.type.equals("5")) {
            v = LayoutInflater.from(context).inflate(R.layout.layout_outgoing_sms, parent, false);
            holder1 = new ViewHolder1();
            holder1.message_text_outgoing = (TextView) v.findViewById(R.id.message_text_outgoing);
            holder1.time_text_outgoing = (TextView) v.findViewById(R.id.time_text_outgoing);
            holder1.message_text_outgoing.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder1.message_text_outgoing.setText(message.message);
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.valueOf(message.time));
            Date date = cal.getTime();
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(cal.getTime());
            holder1.time_text_outgoing.setText(month_name+" "+cal.get(Calendar.DATE));
        }else {
            v = LayoutInflater.from(context).inflate(R.layout.layout_outgoing_sms, parent, false);
            v.setVisibility(View.GONE);
        }
        return v;
    }

    private class ViewHolder1 {
        public TextView message_text_outgoing;
        public TextView time_text_outgoing;
    }

    private class ViewHolder2 {
        public TextView message_text_incoming;
        public TextView time_text_incoming;

    }
}
