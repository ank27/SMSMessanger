package com.smsapplication.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.smsapplication.FragmentInbox;
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
 * Created by ankurkhandelwal on 02/07/16.
 */
public class SMSSearchAdapter extends RecyclerView.Adapter<SMSSearchAdapter.ViewHolder> implements Filterable {
        List<SMS> smsArrayList;
        Activity activity;
        public ArrayFilter mFilter;
        ArrayList<SMS> newsmsList;
    public SMSSearchAdapter(Activity activity, List<SMS> data){
        this.activity= activity;
        this.smsArrayList = data;
        newsmsList=new ArrayList<SMS>(smsArrayList);
        }
    @Override
    public SMSSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(smsArrayList.size()>0) {
            View itemLayoutView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.message_search_view, parent, false);
            return new ViewHolder(itemLayoutView);
        }else {
            View itemLayoutView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.no_item_view, parent, false);
            return new ViewHolder(itemLayoutView);
//            return null;
        }

    }

    @Override
    public void onBindViewHolder(final SMSSearchAdapter.ViewHolder holder, final int position) {
        if(smsArrayList.size()>0) {
            Log.d("SearchAdapter ", smsArrayList.size() + " ");
//            holder.sms_sender.setText(smsArrayList.get(position).sender);
            holder.sms_content.setText(smsArrayList.get(position).message);

            String name= Common.getContactName(activity,smsArrayList.get(position).sender.replaceAll("\\s+",""));
            if(name!=null){
                holder.sms_sender.setText(name);
            }else {
                holder.sms_sender.setText(smsArrayList.get(position).sender);
            }

            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.valueOf(smsArrayList.get(position).time));
            Date date = cal.getTime();
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(cal.getTime());
            holder.sms_timestamp.setText(month_name + " " + cal.get(Calendar.DATE));
            holder.top_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent conversationIntent = new Intent(activity, ConversationDetailActivity.class);
                    conversationIntent.putExtra("name", holder.sms_sender.getText().toString());
                    conversationIntent.putExtra("mobile", smsArrayList.get(position).sender);
                    activity.startActivity(conversationIntent);
                }
            });
        }
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
        sms_sender = (TextView) itemLayoutView.findViewById(R.id.sender);
        sms_content = (TextView) itemLayoutView.findViewById(R.id.content);
        sms_timestamp=(TextView) itemLayoutView.findViewById(R.id.timestamp);
        top_layout=(RelativeLayout) itemLayoutView.findViewById(R.id.top_layout);
    }
}


    @Override
    public android.widget.Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }


private class ArrayFilter extends android.widget.Filter {
    private final Object lock=new Object();
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        if(newsmsList==null){
            synchronized (lock){
                newsmsList=new ArrayList<SMS>(smsArrayList);
            }
        }
        if(constraint==null || constraint.length()==0){
            synchronized (lock){
                ArrayList<SMS> list=new ArrayList<SMS>(newsmsList);
                results.values=list;
                results.count=list.size();
            }
        }else {
            final String prefixString = constraint.toString().toLowerCase();
            Log.d("filter string ",prefixString);
            ArrayList<SMS> values = newsmsList;
            int count = values.size();
            ArrayList<SMS> newValues = new ArrayList<SMS>(count);
            for (int i = 0; i < count; i++) {
                String sender = values.get(i).sender;
                String content= values.get(i).message;
                if (content.toLowerCase().contains(prefixString) || sender.toLowerCase().contains(prefixString)) {
                    SMS sms=new SMS(values.get(i).id,values.get(i).sender,values.get(i).time,values.get(i).message,values.get(i).type,values.get(i).read,values.get(i).person);
                    newValues.add(sms);
                }
            }
            results.values = newValues;
            results.count = newValues.size();
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        if (results.values != null) {
            smsArrayList = (ArrayList<SMS>) results.values;
            Log.d("Result ",results.values.toString());
            Log.d("Filter1 contactSize",String.valueOf(smsArrayList.size()));
        } else {
            Log.d("Filter0 contactSize",String.valueOf(smsArrayList.size()));
            smsArrayList = new ArrayList<SMS>();
        }
        if (results.count > 0) {
            notifyDataSetChanged();
            FragmentInbox.show_layout(1);
        }
    }
}
}