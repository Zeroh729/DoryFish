package zeroh720.doryfish.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zeroh720.doryfish.R;
import zeroh720.doryfish.model.Prediction;

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
        holder.tv_date.setText(data.get(position).getTime());
        holder.tv_state.setText(data.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PredictionHistoryViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_state;
        public TextView tv_date;

        public PredictionHistoryViewHolder(View itemView) {
            super(itemView);
            tv_state = (TextView)itemView.findViewById(R.id.tv_stateName);
            tv_date = (TextView)itemView.findViewById(R.id.tv_date);
        }
    }
}
