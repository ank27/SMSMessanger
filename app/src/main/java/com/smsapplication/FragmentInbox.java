package com.smsapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.smsapplication.Adapter.InboxAdapter;
import com.smsapplication.Adapter.SMSSearchAdapter;
import com.smsapplication.Models.SMS;
import com.smsapplication.Receiver.SmsBroadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class FragmentInbox extends Fragment {
    private static FragmentInbox instance;
    Activity activity;
    View rootView;
    FloatingActionButton fab_sms;
    static RecyclerView inbox_container;
    String TAG="FragmentInbox";
    InboxAdapter adapter;
    SMSSearchAdapter searchAdapter;
    SmsBroadcastReceiver smsBroadcastReceiver=null;
    boolean mIsReceiverRegistered = false;
    ProgressBar progress;
    boolean is_search_visible=false;
    static FrameLayout frame_layout;
    public static RelativeLayout topView,no_item_layout;
    public static FragmentInbox instance() {
        Log.d("fragement inbox","instance");
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        fab_sms=(FloatingActionButton) rootView.findViewById(R.id.fab_sms);
        inbox_container=(RecyclerView) rootView.findViewById(R.id.inbox_container);
        LinearLayoutManager layoutManager=new LinearLayoutManager(activity);
        inbox_container.setLayoutManager(layoutManager);
        inbox_container.setHasFixedSize(true);

        frame_layout=(FrameLayout) rootView.findViewById(R.id.frame_layout);
        no_item_layout=(RelativeLayout) rootView.findViewById(R.id.no_item_layout);
        topView=(RelativeLayout) rootView.findViewById(R.id.topView);
        progress=(ProgressBar) rootView.findViewById(R.id.progress);

        frame_layout.getBackground().setAlpha(0);
        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) rootView.findViewById(R.id.fab_menu);
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frame_layout.getBackground().setAlpha(240);
                frame_layout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frame_layout.getBackground().setAlpha(0);
                frame_layout.setOnTouchListener(null);
            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton fab_upload=(com.getbase.floatingactionbutton.FloatingActionButton) rootView.findViewById(R.id.fab_upload_to_google);
        fab_upload.setClickable(true);
        fab_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.collapse();
                Intent backupIntent=new Intent(activity,BackUpActivity.class);
                startActivity(backupIntent);
            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton fab_contact=(com.getbase.floatingactionbutton.FloatingActionButton) rootView.findViewById(R.id.fab_new_contact);
        fab_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.collapse();
                Intent contactIntent=new Intent(activity,ContactDetailActivity.class);
                startActivity(contactIntent);
            }
        });


        setHasOptionsMenu(true);
        readSMS();
        return rootView;
    }

    private void readSMS() {
        Uri smsUri=Uri.parse("content://sms/");
        String[] reqCols = new String[] {"DISTINCT address", "_id" , "date", "body" , "type" , "read","person" };
        Cursor cur =activity.getContentResolver().query(smsUri, reqCols,"address IS NOT NULL) GROUP BY (address",null,null);
        while (cur.moveToNext()) {
            SMS smsObject=new SMS(cur.getString(1),cur.getString(0),cur.getString(2),cur.getString(3),cur.getString(4),cur.getString(5),cur.getString(6));
            Common.smsArrayList.add(smsObject);
        }
        adapter = new InboxAdapter(activity, Common.smsArrayList);
        inbox_container.setAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (is_search_visible){
                        adapter = new InboxAdapter(activity, Common.smsArrayList);
                        inbox_container.setAdapter(adapter);
                        return true;
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        instance = this;
    }

    public void updateList(final String sender,final String msg) {
        SMS sms=new SMS(sender,String.valueOf(System.currentTimeMillis() / 1000L),msg,String.valueOf(1),String.valueOf(0));
        Common.smsArrayList.add(0,sms);
        Common.smsArrayListFull.add(0,sms);
        adapter = new InboxAdapter(activity, Common.smsArrayList);
        inbox_container.setAdapter(adapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchView.SearchAutoComplete textArea = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        textArea.setTextColor(ContextCompat.getColor(activity, R.color.white));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        Log.d(TAG,"OptionMenu");
//        adapter = new InboxAdapter(activity, Common.smsArrayList);
//        inbox_container.setAdapter(adapter);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchAdapter=new SMSSearchAdapter(activity,Common.smsArrayListFull);
                inbox_container.setAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                adapter = new InboxAdapter(activity, Common.smsArrayList);
                inbox_container.setAdapter(adapter);
                return true;
            }
        });

//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                searchItem.collapseActionView();
//                searchView.setQuery("", false);
//                adapter = new InboxAdapter(activity, Common.smsArrayList);
//                inbox_container.setAdapter(adapter);
//            }
//        });

        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String searchText) {
//                searchAdapter.getFilter().filter(searchText);
                searchAdapter.getFilter().filter(searchText, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        if(count==0){
                            Log.d(TAG,"Count 0");
                            no_item_layout.setVisibility(View.VISIBLE);
                            inbox_container.setVisibility(View.GONE);
                            topView.setVisibility(View.GONE);
                        }
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }
        };
        if (searchView != null)
            searchView.setOnQueryTextListener(queryTextListener);
        return;
    }


    public static void show_layout(int layout_type){
        if(layout_type==1){
            no_item_layout.setVisibility(View.GONE);
            inbox_container.setVisibility(View.VISIBLE);
            topView.setVisibility(View.VISIBLE);
            frame_layout.setVisibility(View.VISIBLE);
//            progressSend.setVisibility(View.GONE);
        }else if(layout_type==0){
            no_item_layout.setVisibility(View.VISIBLE);
            inbox_container.setVisibility(View.GONE);
            topView.setVisibility(View.GONE);
            frame_layout.setVisibility(View.GONE);
//            progressSend.setVisibility(View.GONE);
        }
    }

}
