package zeroh720.doryfish.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import zeroh720.doryfish.values.Constants;

public class GeofenceIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeofenceIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e("TEST", geofencingEvent.getErrorCode() + "");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        switch (geofenceTransition){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
                if(geofences.size() > 0) {
                    Geofence geofence = geofences.get(0);

//                    String geofenceTransitionDetails = getGeofenceTransitionDetails(
//                            this,
//                            geofenceTransition,
//                            triggeringGeofences
//                    );
//
                    Intent intent1 = new Intent(Constants.APP_INTENT);
                    intent1.putExtra(Constants.EXTRA_ACTIONTYPE, Constants.ACTIONTYPE_GEOFENCE_ENTER);
                    intent1.putExtra(Constants.EXTRA_LOCATION_ID, geofence.getRequestId());
                    sendBroadcast(intent1);
                }
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:



                break;
            default:
                Log.e("TEST", "Error");
        }
    }

}
