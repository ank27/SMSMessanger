package com.smsapplication;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import com.smsapplication.Models.Contact;
import com.smsapplication.Models.SMS;

import java.util.ArrayList;

/**
 * Created by ankurkhandelwal on 01/07/16.
 */
public class Common extends Application {
    public static Contact selected_contact=null;
    public static ArrayList<SMS> smsArrayList=new ArrayList<>();
    public static ArrayList<SMS> smsArrayListFull=new ArrayList<>();
    public static ArrayList<Contact> contactList=new ArrayList<>();
    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    @Override
    public void onCreate(){
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
    }

    public static String getContactName(Context context, String phoneNumber) {
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
