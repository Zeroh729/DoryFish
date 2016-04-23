package zeroh720.doryfish.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import zeroh720.doryfish.R;
import zeroh720.doryfish.adapter.PredictionRecyclerViewAdapter;
import zeroh720.doryfish.fragment.ValidationDialogFragment;
import zeroh720.doryfish.model.Location;
import zeroh720.doryfish.model.Prediction;
import zeroh720.doryfish.service.ApiManager;
import zeroh720.doryfish.service.GeofenceService;
import zeroh720.doryfish.task.GetLocationTask;
import zeroh720.doryfish.values.Constants;

public class MainActivity extends BaseActivity {
    private ValidationDialogFragment validationPopup;
    private RecyclerView recyclerView;
    private PredictionRecyclerViewAdapter adapter;
    private ArrayList<Prediction> predictionList;
    private ArrayList<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


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

        registerReceiver(mainReciever, new IntentFilter(Constants.APP_INTENT));
        ApiManager.getInstance().refreshPredictionList();

        showValidationPopup();
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
                validationPopup.tv_popupContent.setText("You are in Creek #2 with state: HEALTHY");
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
            //TODO: PostValidationTask
        }
    };

    private View.OnClickListener approveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: PostValidationTask
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.EXTRA_PREDICTION, predictionList);
        outState.putParcelableArrayList(Constants.EXTRA_LOCATION, locations);
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
                ApiManager.getInstance().refreshPredictionList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
