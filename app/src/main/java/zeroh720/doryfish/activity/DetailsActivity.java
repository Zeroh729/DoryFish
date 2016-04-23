package zeroh720.doryfish.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import zeroh720.doryfish.R;
import zeroh720.doryfish.adapter.PredictionHistoryRecyclerViewAdapter;
import zeroh720.doryfish.model.Location;
import zeroh720.doryfish.model.Prediction;
import zeroh720.doryfish.service.ApiManager;
import zeroh720.doryfish.ui.SimpleDividerItemDecoration;
import zeroh720.doryfish.values.Constants;
import zeroh720.doryfish.values.SpawnStates;

public class DetailsActivity extends BaseActivity {
    private GoogleMap mMap;
    private Prediction prediction;
    private SupportMapFragment mapFragment;
    private PredictionHistoryRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tv_name;
    private Location location;
    private ArrayList<Prediction> predictions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        tv_name = (TextView)findViewById(R.id.tv_locationName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(onMapReadyCallback);

        if(savedInstanceState != null){
            location = savedInstanceState.getParcelable(Constants.EXTRA_LOCATION);
            predictions = savedInstanceState.getParcelableArrayList(Constants.EXTRA_PREDICTION);
            prediction = savedInstanceState.getParcelable(Constants.EXTRA_PREDICTION);
        }else{
            String locationId = getIntent().getStringExtra(Constants.EXTRA_LOCATION);
            prediction = getIntent().getParcelableExtra(Constants.EXTRA_PREDICTION);
            ApiManager.getInstance().refreshLocation(locationId);
            ApiManager.getInstance().refreshPredictionList(locationId);
            predictions = new ArrayList<>();
        }

        adapter = new PredictionHistoryRecyclerViewAdapter(this, predictions);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        registerReceiver(detailsReceiver, new IntentFilter(Constants.APP_INTENT));
    }

    OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.EXTRA_LOCATION, location);
        outState.putParcelable(Constants.EXTRA_PREDICTION, prediction);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(detailsReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver detailsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionType = intent.getStringExtra(Constants.EXTRA_ACTIONTYPE);
            switch (actionType){
                case Constants.ACTION_FETCH_PREDLIST_SUCCESS:
                    predictions.clear();
                    predictions.addAll(intent.<Prediction>getParcelableArrayListExtra(Constants.EXTRA_PREDICTION));
                    break;
                case Constants.ACTION_FETCH_LOCATION_SUCCESS:
                    if(location == null) {
                        location = intent.getParcelableExtra(Constants.EXTRA_LOCATION);
                        updateView();
                    }
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    };

    private void updateView(){
        tv_name.setText(location.getName());

        LatLng creek = new LatLng(location.getLongitude(), location.getLatitude());
        MarkerOptions marker = new MarkerOptions().position(creek).title(location.getName());
        int icon = getPinResId();
        if(icon != -1) {
            marker.icon(BitmapDescriptorFactory.fromResource(icon));
        }
        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(creek));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
    }

    private int getPinResId(){
        switch (prediction.getStatus()){
            case SpawnStates.NOT_SUITABLE:
                return R.drawable.ic_fish_green_pin;
            case SpawnStates.MIN_SUITABLE:
                return R.drawable.ic_fish_yellow_pin;
            case SpawnStates.SUITABLE:
                return R.drawable.ic_fish_orange_pin;
            case SpawnStates.VERY_SUITABLE:
                return R.drawable.ic_fish_red_pin;
            case SpawnStates.HIGHLY_SUITABLE:
                return R.drawable.ic_fish_black_pin;
        }
        return -1;
    }
}
