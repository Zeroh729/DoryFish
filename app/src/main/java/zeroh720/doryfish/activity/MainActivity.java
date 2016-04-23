package zeroh720.doryfish.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;

import zeroh720.doryfish.R;
import zeroh720.doryfish.adapter.PredictionRecyclerViewAdapter;
import zeroh720.doryfish.model.Location;
import zeroh720.doryfish.model.Prediction;
import zeroh720.doryfish.service.ApiManager;
import zeroh720.doryfish.task.GetPredictionTask;
import zeroh720.doryfish.values.Constants;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PredictionRecyclerViewAdapter adapter;
    private ArrayList<Prediction> predictionList;
    private ArrayList<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        if(savedInstanceState != null){
            predictionList = savedInstanceState.getParcelableArrayList(Constants.EXTRA_PREDICTION);
            locations = savedInstanceState.getParcelableArrayList(Constants.EXTRA_LOCATION);
        }else{
            predictionList = new ArrayList<>();
            locations = new ArrayList<>();
        }

        adapter = new PredictionRecyclerViewAdapter(this, predictionList);
        adapter.setClickListener(locationClickListener);
        recyclerView.setAdapter(adapter);

        registerReceiver(mainReciever, new IntentFilter(Constants.APP_INTENT));
        ApiManager.getInstance().refreshPredictionList();
    }

    PredictionRecyclerViewAdapter.PredictionViewHolder.ClickListener locationClickListener = new PredictionRecyclerViewAdapter.PredictionViewHolder.ClickListener() {
        @Override
        public void onClick(String locationId) {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(Constants.EXTRA_LOCATION, locationId);
            startActivity(intent);
        }
    };

    private BroadcastReceiver mainReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionType = intent.getStringExtra(Constants.EXTRA_ACTIONTYPE);
            switch (actionType){
                case Constants.ACTION_FETCH_PREDLIST_SUCCESS:
                    predictionList.clear();
                    predictionList.addAll(intent.<Prediction>getParcelableArrayListExtra(Constants.EXTRA_PREDICTION));
                    break;
                case Constants.ACTION_FETCH_LOCATION_SUCCESS:
                    locations = intent.getParcelableArrayListExtra(Constants.EXTRA_LOCATION);
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.EXTRA_PREDICTION, predictionList);
        outState.putParcelableArrayList(Constants.EXTRA_LOCATION, locations);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mainReciever);
        super.onPause();
    }
//
//    @Override
//    protected void onDestroy() {
//        unregisterReceiver(mainReciever);
//        super.onDestroy();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_refresh:
                ApiManager.getInstance().refreshPredictionList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
