package zeroh720.doryfish.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import zeroh720.doryfish.R;
import zeroh720.doryfish.service.GeofenceService;
import zeroh720.doryfish.values.Constants;


public class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient mGoogleApiClient;
//    private Location mLastLocation;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if(savedInstanceState != null){
//            mLastLocation = savedInstanceState.getParcelable(Constants.EXTRA_CURR_LOCATION);
//        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        locationRequest = new LocationRequest();
        locationRequest.setInterval(15 * 60 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(locationResultCallback);
    }

    ResultCallback locationResultCallback = new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(LocationSettingsResult locationSettingsResult) {
            final Status status = locationSettingsResult.getStatus();
            final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    try {
                        GeofenceService.getInstance().getGeofencePendingIntent(BaseActivity.this).send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//       TODO: Popup Location Required
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        }
    };


//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putParcelable(Constants.EXTRA_CURR_LOCATION, mLastLocation);
//    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener);
        }
    }

    @Override
    protected void onPause() {
        if(mGoogleApiClient.isConnected()) {
            GeofenceService.getInstance().removePendingIntent(this, mGoogleApiClient);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//
//        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener);
    }

    LocationListener locationListener = new LocationListener(){
        @Override
        public void onLocationChanged(Location location) {
//            if(location != null)
//                mLastLocation = location;

        }
    };

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("TEST", "onConnectionFailed");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("TEST", "onConnectionFailed");
    }
}
