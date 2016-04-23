package zeroh720.doryfish.application;

import android.app.Application;
import android.content.Context;

public class DoryFish extends Application{
    private static DoryFish app;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        DoryFish.app = this;
        if(context == null){
            context = getApplicationContext();
        }
    }

    public static Context getContext() {
        return context;
    }
}
