package com.smsapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.smsapplication.Adapter.ContactAdapter;
import com.smsapplication.Adapter.InboxAdapter;
import com.smsapplication.Models.Contact;
import com.smsapplication.Models.SMS;

import java.util.ArrayList;

/**
 * Created by ankurkhandelwal on 01/07/16.
 */
public class ContactDetailActivity extends AppCompatActivity {
    Toolbar toolbarSendSms;
    RecyclerView contact_container;
    public static EditText editPerson;
    public static FloatingActionButton fab_send_sms;
    public String TAG="ContactDetailActivity";
    ArrayList<Contact> contactList=new ArrayList<>();
    ContactAdapter adapter;
    Activity activity;
    public static RelativeLayout no_item_layout,contactListLayout;
    public static ProgressBar progressSend;
    Button done_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        toolbarSendSms=(Toolbar) findViewById(R.id.toolbarSendSms);
        toolbarSendSms.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        editPerson=(EditText) findViewById(R.id.editPerson);
        editPerson.addTextChangedListener(filterTextWatcher);
        toolbarSendSms.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        done_btn=(Button) findViewById(R.id.done_btn);
        activity=this;
        progressSend=(ProgressBar) findViewById(R.id.progressSend);
        contactListLayout=(RelativeLayout) findViewById(R.id.contactListLayout);
        no_item_layout=(RelativeLayout) findViewById(R.id.no_item_layout);
        fab_send_sms=(FloatingActionButton) findViewById(R.id.fab_send_sms);
        fab_send_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editPerson.getText().toString().equals("")){
                    Intent conversationIntent=new Intent(ContactDetailActivity.this,ConversationDetailActivity.class);
                    if(Common.selected_contact!=null) {
                        conversationIntent.putExtra("name", Common.selected_contact.name);
                        conversationIntent.putExtra("mobile", Common.selected_contact.mobile);
                    }else {
                        conversationIntent.putExtra("name", editPerson.getText().toString());
                        conversationIntent.putExtra("mobile", editPerson.getText().toString());
                    }
                    startActivity(conversationIntent);
                }else {
                    Toast.makeText(getApplicationContext(),"Please select a contact person",Toast.LENGTH_SHORT).show();
                }
            }
        });

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_send_sms.performClick();
            }
        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab_send_sms.getLayoutParams();
            p.setMargins(0, 0, 0, 0); // get rid of margins since shadow area is now the margin
            fab_send_sms.setLayoutParams(p);
        }
        contact_container=(RecyclerView) findViewById(R.id.contact_container);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        contact_container.setLayoutManager(layoutManager);
        contact_container.setHasFixedSize(true);

        getContacts();
    }

    private void getContacts() {

//        String[] reqCols = new String[] {ContactsContract.CommonDataKinds.Phone._ID,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
//        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, reqCols, null, null, null);
//        // use the cursor to access the contacts
//        while (phones.moveToNext()) {
//            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//            String id=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
//            String contact_id=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//            Contact contact=new Contact(id,name,phoneNumber,contact_id);
//            contactList.add(contact);
//        }
        adapter = new ContactAdapter(activity, Common.contactList);
        contact_container.setAdapter(adapter);

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence entered_string, int start, int before, int count) {
            adapter.getFilter().filter(entered_string);
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editPerson.removeTextChangedListener(filterTextWatcher);
    }

    public static void show_layout(int layout_type){
        if(layout_type==1){
            no_item_layout.setVisibility(View.GONE);
            contactListLayout.setVisibility(View.VISIBLE);
            progressSend.setVisibility(View.GONE);
        }else if(layout_type==0){
            no_item_layout.setVisibility(View.VISIBLE);
            contactListLayout.setVisibility(View.GONE);
            progressSend.setVisibility(View.GONE);
        }
    }
}
