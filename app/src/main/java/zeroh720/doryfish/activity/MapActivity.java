package zeroh720.doryfish.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import zeroh720.doryfish.R;
import zeroh720.doryfish.model.Location;
import zeroh720.doryfish.model.Prediction;
import zeroh720.doryfish.values.Constants;
import zeroh720.doryfish.values.SpawnStates;

public class MapActivity extends AppCompatActivity {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ArrayList<Location> locations;
    private ArrayList<Prediction> predictions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setHomeButtonEnabled(true);

        if(savedInstanceState != null){
            locations = savedInstanceState.<Location>getParcelableArrayList(Constants.EXTRA_LOCATION);
            predictions = savedInstanceState.<Prediction>getParcelableArrayList(Constants.EXTRA_LOCATION);

        }else{
            locations = getIntent().<Location>getParcelableArrayListExtra(Constants.EXTRA_LOCATION);
            predictions = getIntent().<Prediction>getParcelableArrayListExtra(Constants.EXTRA_PREDICTION);
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(onMapReadyCallback);

    }

    OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            for(Location location : locations){
                updateView(location);
            }
            LatLng greateLake = new LatLng(43.590384643225434, -79.4191162660718);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(greateLake));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(8.957768f));
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.EXTRA_LOCATION, locations);
        outState.putParcelableArrayList(Constants.EXTRA_PREDICTION, predictions);
    }

    private void updateView(Location location){
        LatLng creek = new LatLng(location.getLongitude(), location.getLatitude());
        MarkerOptions marker = new MarkerOptions().position(creek).title(location.getName());
        int icon = getPinResId(getPredictionFromLocationId(location.getId()));
        if(icon != -1) {
            marker.icon(BitmapDescriptorFactory.fromResource(icon));
        }
        mMap.addMarker(marker);
    }

    private Prediction getPredictionFromLocationId(String locationId){
        for(Prediction prediction : predictions){
            if(prediction.getLocationId().equals(locationId))
                return prediction;
        }
        return null;
    }

    private int getPinResId(Prediction prediction){
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
