package com.smsapplication;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;
import com.smsapplication.Adapter.InboxAdapter;
import com.smsapplication.Adapter.SMSSearchAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class InboxActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public String TAG="Inbox";
    CoordinatorLayout coordinator_inbox;
    DrawerLayout drawer_layout;
    Toolbar toolbar_inbox;
    public static NavigationView navigationView;
    FrameLayout frame_container;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    public int open_fragment=0;
    String removeFragment = "";
    Activity activity;
    GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
//        if(savedInstanceState==null) {
//            transaction = fragmentManager.beginTransaction();
//        }
        coordinator_inbox = (CoordinatorLayout) findViewById(R.id.coordinator_inbox);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar_inbox = (Toolbar) findViewById(R.id.toolbar_inbox);
        setSupportActionBar(toolbar_inbox);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        toolbar_inbox.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar_inbox.setTitle("Inbox");
        toolbar_inbox.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(GravityCompat.START);
            }
        });
        invalidateOptionsMenu();
        navigationView.setNavigationItemSelectedListener(this);
        activity=this;
        frame_container = (FrameLayout) findViewById(R.id.frame_container);
        fragmentManager = getSupportFragmentManager();


    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.home);
        drawer_layout.closeDrawers();
    }

    @Override
    public void onResumeFragments(){
        super.onResumeFragments();
        set_fragment(0);
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed(){
        View focus = getCurrentFocus();
        if (focus != null) {
            hiddenKeyboard(focus);
        }
        if(open_fragment==0){
            Log.d(TAG,"onBackPressed");
            super.onBackPressed();
            InboxActivity.this.finish();
        }else {
            InboxActivity.this.finish();
        }
    }

    private void hiddenKeyboard(View v) {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void set_fragment(int open_fragment) {
        String tag = "";
        Fragment fragment = null;
        if (open_fragment==0) {
            tag = "Inbox";
            if (fragmentManager.findFragmentByTag(tag) == null) {
                transaction=fragmentManager.beginTransaction();
                fragment = new FragmentInbox();
                fragmentManager.popBackStack(removeFragment,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                removeFragment=tag;
                transaction.addToBackStack(tag);
                transaction.replace(R.id.frame_container, fragment, tag).commit();
            }
            else {
                fragmentManager.popBackStack(tag, 0);
            }
        }
        else if (open_fragment==1) {
            tag = "About";
            if (fragmentManager.findFragmentByTag(tag) == null) {
                fragment = new FragmentAbout();
                fragmentManager.popBackStack(removeFragment,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction=fragmentManager.beginTransaction();
                transaction.addToBackStack(tag);
                removeFragment=tag;
                transaction.replace(R.id.frame_container, fragment, tag).commit();
            } else {
                Log.i("else", tag);
                fragmentManager.popBackStack(tag, 0);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            item.setCheckable(true);
            drawer_layout.closeDrawers();
            open_fragment=0;
            set_fragment(0);
        }
        if (id == R.id.about_us) {
            drawer_layout.closeDrawers();
            item.setChecked(true);
            open_fragment=1;
            set_fragment(1);
        }
        if(id==R.id.backup_to_drive){
            Log.d(TAG,"item selected");
            Intent backupIntent=new Intent(InboxActivity.this,BackUpActivity.class);
            startActivity(backupIntent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

