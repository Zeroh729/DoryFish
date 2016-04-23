package zeroh720.doryfish.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

import zeroh720.doryfish.R;
import zeroh720.doryfish.adapter.PredictionHistoryRecyclerViewAdapter;
import zeroh720.doryfish.model.Location;
import zeroh720.doryfish.model.Prediction;
import zeroh720.doryfish.service.ApiManager;
import zeroh720.doryfish.values.Constants;

public class DetailsActivity extends AppCompatActivity {
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

        if(savedInstanceState != null){
            location = savedInstanceState.getParcelable(Constants.EXTRA_LOCATION);
            predictions = savedInstanceState.getParcelableArrayList(Constants.EXTRA_PREDICTION);
        }else{
            String locationId = getIntent().getStringExtra(Constants.EXTRA_LOCATION);
            ApiManager.getInstance().refreshLocation(locationId);
//            ApiManager.getInstance().refreshPredictionList(locationId);
            predictions = new ArrayList<>();
        }

        adapter = new PredictionHistoryRecyclerViewAdapter(this, predictions);
        recyclerView.setAdapter(adapter);

        registerReceiver(detailsReceiver, new IntentFilter(Constants.APP_INTENT));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.EXTRA_LOCATION, location);
        outState.putParcelableArrayList(Constants.EXTRA_PREDICTION, predictions);
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
                    location = intent.getParcelableExtra(Constants.EXTRA_LOCATION);
                    updateView();
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    };

    private void updateView(){
        tv_name.setText(location.getName());
    }
}
