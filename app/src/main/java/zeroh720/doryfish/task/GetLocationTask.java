package zeroh720.doryfish.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import zeroh720.doryfish.model.Location;
import zeroh720.doryfish.util.ServerUtils;
import zeroh720.doryfish.values.Constants;

public class GetLocationTask extends AsyncTask<String, Void, String> {
    private Context context;
    private String error = "";
    private String id;

    public GetLocationTask(Context context, String locationId) {
        this.context = context;
        id = locationId;
    }


    @Override
    protected String doInBackground(String... params) {
        ServerUtils serverUtilities = new ServerUtils();
        String reply = null;
        error = "";
        if( !isCancelled() ) {
            reply = serverUtilities.getRequest(Constants.API_URL + "fish/location/" + id, new HashMap(), false);
        }

        return reply;
    }

    @Override
    protected void onPostExecute(String data) {
        Intent intent = new Intent(Constants.APP_INTENT);
        try {
            if (data != null) {
                Log.d("TEST", "Get Location: " + data);

                JSONObject json = new JSONObject(data);
                Double latitude = (Double)json.get("Latitude");
                Double longitude = (Double)json.get("Longitude");
                String id = json.get("LocationId") + "";
                String name = (String)json.get("LocationName");

                Log.d("TEST", "key: " + id);

                Location location = new Location(id, name, latitude, longitude);

                intent.putExtra(Constants.EXTRA_ACTIONTYPE, Constants.ACTION_FETCH_LOCATION_SUCCESS);
                intent.putExtra(Constants.EXTRA_LOCATION, location);

            } else {
                intent.putExtra(Constants.EXTRA_ACTIONTYPE, Constants.ACTION_FETCH_LOCATION_FAILURE);
                intent.putExtra(Constants.EXTRA_LOCATION, "No Data Found!");
            }
        }catch (Exception e){

            e.printStackTrace();
        }
        context.getApplicationContext().sendBroadcast(intent);
    }
}
