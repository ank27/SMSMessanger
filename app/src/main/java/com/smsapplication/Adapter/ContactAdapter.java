package com.smsapplication.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.smsapplication.Common;
import com.smsapplication.ContactDetailActivity;
import com.smsapplication.ConversationDetailActivity;
import com.smsapplication.Models.Contact;
import com.smsapplication.R;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Filterable {
    ArrayList<Contact> contactList;
    static Activity activity;
    public ArrayFilter mFilter;
    ArrayList<Contact> newcontactList;
    public ContactAdapter(Activity activity, ArrayList<Contact> data){
        this.activity= activity;
        this.contactList = data;
        newcontactList=new ArrayList<Contact>(contactList);
    }
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.single_contact_layout, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ViewHolder holder, final int position) {
        holder.username.setText(contactList.get(position).name);
        holder.phone.setText(contactList.get(position).mobile);
        holder.top_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactDetailActivity.editPerson.setText("");
                ContactDetailActivity.editPerson.setText(contactList.get(position).mobile);
                ContactDetailActivity.editPerson.setSelection(ContactDetailActivity.editPerson.length());
                Common.selected_contact=contactList.get(position);
                ContactDetailActivity.fab_send_sms.performClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public TextView phone;
        public ImageView sms_sender_image;
        public RelativeLayout top_layout;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            username = (TextView) itemLayoutView.findViewById(R.id.username);
            phone = (TextView) itemLayoutView.findViewById(R.id.phone);
            sms_sender_image=(ImageView) itemLayoutView.findViewById(R.id.user_image);
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
            if(newcontactList==null){
                synchronized (lock){
                    newcontactList=new ArrayList<Contact>(contactList);
                }
            }
            if(constraint==null || constraint.length()==0){
                synchronized (lock){
                    ArrayList<Contact> list=new ArrayList<Contact>(newcontactList);
                    results.values=list;
                    results.count=list.size();
                }
            }else {
                final String prefixString = constraint.toString().toLowerCase();
                ArrayList<Contact> values = newcontactList;
                int count = values.size();
                ArrayList<Contact> newValues = new ArrayList<Contact>(count);
                for (int i = 0; i < count; i++) {
                    String phone = values.get(i).mobile;
                    String name= values.get(i).name;
                    if (phone.toLowerCase().contains(prefixString) || name.toLowerCase().contains(prefixString)) {
                        Contact contact=new Contact(values.get(i).id,values.get(i).name,values.get(i).mobile,values.get(i).contact_id);
                        newValues.add(contact);
//                        HashMap<String, String> hashMap = new HashMap<String, String>();
//                        hashMap.put("name", name);
//                        hashMap.put("email", email);
//                        newValues.add(hashMap);
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
                contactList = (ArrayList<Contact>) results.values;
            } else {
                contactList = new ArrayList<Contact>();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
                ContactDetailActivity.show_layout(1);
            } else {
                notifyDataSetChanged();
                ContactDetailActivity.show_layout(0);
            }
        }
    }
}
