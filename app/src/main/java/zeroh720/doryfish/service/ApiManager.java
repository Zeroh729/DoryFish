package zeroh720.doryfish.service;

import android.content.Context;

import zeroh720.doryfish.application.DoryFish;
import zeroh720.doryfish.task.GetLocationTask;
import zeroh720.doryfish.task.GetPredictionTask;

public class ApiManager {
    private static Context context;
    private static ApiManager instance;

    public static ApiManager getInstance() {
        if(instance == null){
            instance = new ApiManager();
        }
        if(context == null){
            context = DoryFish.getContext();
        }
        return instance;
    }

    public void refreshPredictionList(){
        GetPredictionTask predictionTask = new GetPredictionTask(context);
        predictionTask.execute();
    }

    public void refreshPredictionList(String locationId){
        GetPredictionTask predictionTask = new GetPredictionTask(context);
        predictionTask.execute();
    }

    public void refreshAllPredictionsFromLocation(String locationId){
//      TODO:  GetPredictionTask predictionTask = new GetPredictionTask(context);
//        predictionTask.execute();
    }

    public void refreshLocation(String locationId){
        GetLocationTask locationTask = new GetLocationTask(context, locationId);
        locationTask.execute();
    }
}
