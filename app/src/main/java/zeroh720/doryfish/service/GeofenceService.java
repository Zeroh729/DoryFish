package zeroh720.doryfish.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import zeroh720.doryfish.R;
import zeroh720.doryfish.model.Location;

public class GeofenceService {
    private static GeofenceService instance;
    private static ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    public static GeofenceService getInstance() {
        if(instance == null){
            init();
        }
        return instance;
    }

    private static void init(){
        instance = new GeofenceService();
    }

    public void addGeofence(String locationId, Double latitude, Double longitude){
        if(mGeofenceList == null)
            mGeofenceList = new ArrayList<>();

        if(isLocationUnique(locationId))
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(locationId)
                .setCircularRegion(latitude, longitude, 300)
                .setExpirationDuration(6 * 60 * 60 * 1000)
                .setLoiteringDelay(60 * 1000)
//                .setLoiteringDelay(30 * 60 * 1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL)
                .build());
    }

    private boolean isLocationUnique(String locationId){
        for(int i = 0; i < mGeofenceList.size(); i++){
            if(mGeofenceList.get(i).getRequestId().equals(locationId))
                return false;
        }
        return true;
    }

    public PendingIntent getGeofencePendingIntent(Context context) {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceIntentService.class);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void removePendingIntent(Context context, GoogleApiClient client){
        LocationServices.GeofencingApi.removeGeofences(client,
                GeofenceService.getInstance().getGeofencePendingIntent(context)
        );
    }

}
