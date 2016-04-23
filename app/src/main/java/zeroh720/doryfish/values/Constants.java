package zeroh720.doryfish.values;

public class Constants {
    public final static String API_URL = "http://findingdory.azurewebsites.net/";

    public final static String APP_INTENT = "com.zeroh729.doryfish";

    public final static String ACTION_FETCH_PREDLIST_SUCCESS = "fetchPredListSuccess";
    public final static String ACTION_FETCH_PREDLIST_FAILURE = "fetchPredListFailure";

    public final static String ACTION_FETCH_LOCATION_SUCCESS = "fetchLocationSuccess";
    public final static String ACTION_FETCH_LOCATION_FAILURE = "fetchLocationFailure";

    public final static String ACTION_POST_VALIDATION_SUCCESS = "postValidationSuccess";
    public final static String ACTION_POST_VALIDATION_FAILURE = "postValidationFailure";

    public final static String EXTRA_ACTIONTYPE = "actionType";
    public final static String EXTRA_LOCATION = "location";
    public final static String EXTRA_LOCATION_ID = "locationId";
    public final static String EXTRA_PREDICTION = "prediction";
    public final static String EXTRA_PREDICTION_ID = "prediction";
    public final static String ACTIONTYPE_GEOFENCE_ENTER = "geofenceEnter";
    public final static String EXTRA_CURR_LOCATION = "currentLocation";
}
