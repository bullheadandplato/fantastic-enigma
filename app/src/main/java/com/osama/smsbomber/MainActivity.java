package com.osama.smsbomber;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecentListView;
    private RecentAdapter mRecentAdapter;
    private RecentDataSource mDataSource;
    private boolean isServiceStarted=false;
    private SendMessageService messageService;
    Intent serviceIntent;

    private static final String TAG=MainActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        serviceIntent=new Intent(this,SendMessageService.class);
        if(!isServiceStarted){
            startService(serviceIntent);
            isServiceStarted=true;
        }
        registerReceiver(rec,new IntentFilter(CommonConstants.SERVICE_CONTEXT_BROAD));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        setUpRecentList();
    }
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void setUpRecentList() {
        mRecentListView=(RecyclerView)findViewById(R.id.recent_list);
        mDataSource=new RecentDataSource(this);
        mDataSource.open();
        mDataSource.fillData();
        mRecentAdapter=new RecentAdapter(this,mDataSource.getModels());
        mRecentListView.setLayoutManager(new LinearLayoutManager(this));
        mRecentListView.setAdapter(mRecentAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void sendButtonClick(View view){

        try {

            String phone=((EditText)findViewById(R.id.number_edit_text)).getText().toString();
            int count = Integer.valueOf(((EditText) findViewById(R.id.count_edit_text)).getText().toString());
            String message = ((EditText) findViewById(R.id.message_edit_text)).getText().toString();
            if (isCorrectPhoneAndCount(phone, count)) {
                messageService.startSendingMessages(phone, count, message);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Snackbar.make(view,"There is an error. "+ex.getMessage(),Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean isCorrectPhoneAndCount(String phone, int count) {
        if(count>500){
            ((EditText)findViewById(R.id.count_edit_text)).setError("Count must be less than 500");
            return false;
        }
        if(phone.length()<8){
            ((EditText)findViewById(R.id.number_edit_text)).setError("Count must be less than 500");
            return false;
        }
        return true;
    }
    protected BroadcastReceiver rec=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Broadcast received");
            MainActivity.this.messageService= SendMessageService.getInstance();
        }
    };

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: binding service");
        bindService(serviceIntent, connection,BIND_ABOVE_CLIENT);
    }
}
