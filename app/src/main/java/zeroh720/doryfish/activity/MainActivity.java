package zeroh720.doryfish.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import zeroh720.doryfish.R;
import zeroh720.doryfish.adapter.PredictionRecyclerViewAdapter;
import zeroh720.doryfish.fragment.ValidationDialogFragment;
import zeroh720.doryfish.model.Location;
import zeroh720.doryfish.model.Prediction;
import zeroh720.doryfish.service.ApiManager;
import zeroh720.doryfish.service.GeofenceService;
import zeroh720.doryfish.task.GetLocationTask;
import zeroh720.doryfish.util.DateConverter;
import zeroh720.doryfish.values.Constants;
import zeroh720.doryfish.values.SpawnStates;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ValidationDialogFragment validationPopup;
    private RecyclerView recyclerView;
    private TextView tv_lastsynced;
    private ProgressBar progressBar;
    private PredictionRecyclerViewAdapter adapter;
    private ArrayList<Prediction> predictionList;
    private ArrayList<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        tv_lastsynced = (TextView)findViewById(R.id.tv_lastsynced);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar = (Toolbar)findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Where are the Carps?");

        if(savedInstanceState != null){
            predictionList = savedInstanceState.getParcelableArrayList(Constants.EXTRA_PREDICTION);
            locations = savedInstanceState.getParcelableArrayList(Constants.EXTRA_LOCATION);
        }else{
            predictionList = new ArrayList<>();
            locations = new ArrayList<>();
        }

        adapter = new PredictionRecyclerViewAdapter(this, predictionList);
        adapter.setClickListener(locationClickListener);
        recyclerView.setAdapter(adapter);
        tv_lastsynced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshHomeContent();
            }
        });

        registerReceiver(mainReciever, new IntentFilter(Constants.APP_INTENT));
        ApiManager.getInstance().refreshPredictionList();
    }

    private void refreshHomeContent(){
        ApiManager.getInstance().refreshPredictionList();
        progressBar.setVisibility(View.VISIBLE);
    }

    PredictionRecyclerViewAdapter.PredictionViewHolder.ClickListener locationClickListener = new PredictionRecyclerViewAdapter.PredictionViewHolder.ClickListener() {
        @Override
        public void onClick(String locationId) {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(Constants.EXTRA_LOCATION, locationId);
            intent.putExtra(Constants.EXTRA_PREDICTION, getPredictionFromLocationId(locationId));
            startActivity(intent);
        }
    };

    private BroadcastReceiver mainReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionType = intent.getStringExtra(Constants.EXTRA_ACTIONTYPE);
            switch (actionType){
                case Constants.ACTION_FETCH_PREDLIST_SUCCESS:
                    predictionList.clear();
                    predictionList.addAll(intent.<Prediction>getParcelableArrayListExtra(Constants.EXTRA_PREDICTION));
                    for(Prediction prediction : predictionList){
                        GetLocationTask getLocationTask = new GetLocationTask(MainActivity.this, prediction.getLocationId());
                        getLocationTask.execute();
                    }
                    sortData();
                    tv_lastsynced.setText("Last Synced: " + DateConverter.getFormattedTime(predictionList.get(0).getTime()));
                    break;
                case Constants.ACTION_FETCH_LOCATION_SUCCESS:
                    Location location = intent.getParcelableExtra(Constants.EXTRA_LOCATION);
                    locations.add(location);
                    GeofenceService.getInstance().addGeofence(location.getId(), location.getLatitude(), location.getLongitude());
                    break;
                case Constants.ACTIONTYPE_GEOFENCE_ENTER:
                    String id = intent.getStringExtra(Constants.EXTRA_LOCATION_ID);
                    Location location1 = getLocationFromId(id);
                    if(location1 != null){
                        showNotification(MainActivity.this, location1.getName(), 0, getIntent());
                    }
                    break;
                case Constants.ACTION_POST_VALIDATION_SUCCESS:
                    dismissValidationPopup();
                    break;
            }
            progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    };

    private Prediction getPredictionFromLocationId(String locationId){
        for(Prediction prediction : predictionList){
            if(prediction.getLocationId().equals(locationId)){
                return prediction;
            }
        }
        return null;
    }

    private Location getLocationFromId(String id){
        for(Location location : locations){
          if(location.getId().equals(id)){
              return location;
          }
        }
        return null;
    }

    private void showNotification(Context context, String content, int id, Intent intent) {
        PendingIntent pi = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.bigText(content)
                .setBigContentTitle("You've entered " + content + "!");

        Notification.Builder mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(bigTextStyle)
                .setContentText(content)
                .setContentTitle(context.getResources().getString(R.string.app_name));
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());
    }

    private void showValidationPopup(){
        validationPopup = new ValidationDialogFragment();
        validationPopup.show(getSupportFragmentManager(), "");
        validationPopup.onCreateViewListener = new ValidationDialogFragment.OnCreateViewListener() {
            @Override
            public void doneLoading() {
                validationPopup.tv_popupContent.setText("You are in Krosno Creeks\nAre there carps there?");
                validationPopup.btn_approve.setOnClickListener(approveListener);
                validationPopup.btn_disapprove.setOnClickListener(disapproveListener);
                validationPopup.btn_notnow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissValidationPopup();
                    }
                });
            }
        };
    }

    private void dismissValidationPopup(){
        if(validationPopup!=null)
            validationPopup.dismiss();
    }

    private View.OnClickListener disapproveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "Feedback sent!", Toast.LENGTH_SHORT).show();
            dismissValidationPopup();
        }
    };

    private View.OnClickListener approveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "Feedback sent!", Toast.LENGTH_SHORT).show();
            dismissValidationPopup();
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.EXTRA_PREDICTION, predictionList);
        outState.putParcelableArrayList(Constants.EXTRA_LOCATION, locations);
    }

    private void sortData(){
        Collections.sort(predictionList, predictionComparator);
        adapter.notifyDataSetChanged();
    }

    private Comparator<Prediction> predictionComparator = new Comparator<Prediction>() {
        @Override
        public int compare(Prediction lhs, Prediction rhs) {
            int lhsP = getPriority(lhs);
            int rhsP = getPriority(rhs);

            if(lhsP < rhsP)
                return 1;
            if(lhsP == rhsP)
                return 0;
            return -1;
        }
    };

    private int getPriority(Prediction prediction){
        switch(prediction.getStatus()){
            case SpawnStates.NOT_SUITABLE:
                return 0;
            case SpawnStates.MIN_SUITABLE:
                return 1;
            case SpawnStates.SUITABLE:
                return 2;
            case SpawnStates.VERY_SUITABLE:
                return 3;
            case SpawnStates.HIGHLY_SUITABLE:
                return 4;
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mainReciever);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_refresh:
                refreshHomeContent();
                break;
            case R.id.item_mapView:
                Intent intent = new Intent(this, MapActivity.class);
                intent.putParcelableArrayListExtra(Constants.EXTRA_PREDICTION, predictionList);
                intent.putParcelableArrayListExtra(Constants.EXTRA_LOCATION, locations);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setValidationSchedule(){
        Date date =  new Date();
        date.setTime(new Date().getTime() + (1000 * 35));
        new Timer().schedule(task,date);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            showValidationPopup();
        }
    };
}
