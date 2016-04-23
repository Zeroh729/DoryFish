package zeroh720.doryfish.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import zeroh720.doryfish.model.Location;
import zeroh720.doryfish.model.Prediction;
import zeroh720.doryfish.util.ServerUtils;
import zeroh720.doryfish.values.Constants;

public class GetPredictionTask extends AsyncTask<String, Void, String> {
    private Context context;
    private String error = "";

    public GetPredictionTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        ServerUtils serverUtilities = new ServerUtils();
        String reply = null;
        error = "";
          if( !isCancelled() ) {
            reply = serverUtilities.getRequest(Constants.API_URL + "/fish/prediction", new HashMap(), false);
        }

        return reply;
    }

    @Override
    protected void onPostExecute(String data) {
        Intent intent = new Intent(Constants.APP_INTENT);
        try {
            if (data != null) {
                Log.d("TEST", "Get Prediction: " + data);
                ArrayList<Prediction> predictions = new ArrayList<>();
                JSONArray json = new JSONArray(data);

                for(int i = 0; i < json.length(); i++) {
                    String locationName = (String) ((JSONObject) json.get(i)).get("Location");
                    String locationId = ((JSONObject) json.get(i)).get("LocationId") + "";
                    String predictedDate = (String) ((JSONObject) json.get(i)).get("PredictedDate");
                    String state = (String) ((JSONObject) json.get(i)).get("Prediction");

                    Log.d("TEST", "key: " + locationName);

                    Prediction pred = new Prediction("", state, predictedDate, locationId, locationName);
                    predictions.add(pred);
                }

                intent.putExtra(Constants.EXTRA_ACTIONTYPE, Constants.ACTION_FETCH_PREDLIST_SUCCESS);
                intent.putParcelableArrayListExtra(Constants.EXTRA_PREDICTION, predictions);

            } else {
                intent.putExtra(Constants.EXTRA_ACTIONTYPE, Constants.ACTION_FETCH_PREDLIST_FAILURE);
                intent.putExtra(Constants.EXTRA_LOCATION, "No Data Found!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            context.sendBroadcast(intent);
        }
    }
}
