package com.smsapplication;

import android.app.Application;

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
    @Override
    public void onCreate(){
        super.onCreate();
    }
}
