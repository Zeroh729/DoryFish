package zeroh720.doryfish.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import zeroh720.doryfish.R;
import zeroh720.doryfish.model.Prediction;
import zeroh720.doryfish.util.DateConverter;
import zeroh720.doryfish.values.SpawnStates;

public class PredictionHistoryRecyclerViewAdapter extends RecyclerView.Adapter<PredictionHistoryRecyclerViewAdapter.PredictionHistoryViewHolder>{
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Prediction> data;

    public PredictionHistoryRecyclerViewAdapter(Context context, ArrayList<Prediction> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public PredictionHistoryRecyclerViewAdapter.PredictionHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_predictionhistory, parent, false);
        PredictionHistoryViewHolder holder = new PredictionHistoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PredictionHistoryRecyclerViewAdapter.PredictionHistoryViewHolder holder, int position) {
        int colorRes = 0;
        String probability = "";

        holder.tv_time.setText(DateConverter.getFormattedTime(data.get(position).getTime()));
        holder.tv_date.setText(DateConverter.getFormattedDate(data.get(position).getTime()));
        switch(data.get(position).getStatus()){
            case SpawnStates.NOT_SUITABLE:
                colorRes = R.color.colorState1;
                probability = "Carp Free";
                break;
            case SpawnStates.MIN_SUITABLE:
                colorRes = R.color.colorState2;
                probability = "Low Probability";
                break;
            case SpawnStates.SUITABLE:
                colorRes = R.color.colorState3;
                probability = "Moderate Probability";
                break;
            case SpawnStates.VERY_SUITABLE:
                colorRes = R.color.colorState4;
                probability = "High Probability";
                break;
            case SpawnStates.HIGHLY_SUITABLE:
                colorRes = R.color.colorState5;
                probability = "Extreme Probability";
                break;
        }
        holder.tv_state.setTextColor(context.getResources().getColor(colorRes));
        holder.tv_state.setText(probability);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PredictionHistoryViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_state;
        public TextView tv_date;
        public TextView tv_time;

        public PredictionHistoryViewHolder(View itemView) {
            super(itemView);
            tv_state = (TextView)itemView.findViewById(R.id.tv_stateName);
            tv_date = (TextView)itemView.findViewById(R.id.tv_date);
            tv_time = (TextView)itemView.findViewById(R.id.tv_time);
        }
    }
}
