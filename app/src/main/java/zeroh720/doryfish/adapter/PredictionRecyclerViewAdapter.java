package zeroh720.doryfish.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zeroh720.doryfish.R;
import zeroh720.doryfish.model.Location;
import zeroh720.doryfish.model.Prediction;
import zeroh720.doryfish.values.SpawnStates;

public class PredictionRecyclerViewAdapter extends RecyclerView.Adapter<PredictionRecyclerViewAdapter.PredictionViewHolder> {
    private Context context;
    private PredictionViewHolder.ClickListener clickListener;
    private LayoutInflater inflater;
    private ArrayList<Prediction> predictions;

    public PredictionRecyclerViewAdapter(Context context, ArrayList<Prediction> predictions) {
        this.context = context;
        this.predictions = predictions;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public PredictionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_locationstate, parent, false);
        PredictionViewHolder holder = new PredictionViewHolder(view, clickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(PredictionViewHolder holder, int position) {
        String locationId = predictions.get(position).getLocationId();
        int imageRes = 0;
        int colorRes = 0;
        String probability = "";

        switch(predictions.get(position).getStatus()){
            case SpawnStates.NOT_SUITABLE:
                imageRes = R.drawable.ic_fish_green_trans;
                colorRes = R.color.colorState1;
                probability = "Carp Free";
                break;
            case SpawnStates.MIN_SUITABLE:
                imageRes = R.drawable.ic_fish_yellow_trans;
                colorRes = R.color.colorState2;
                probability = "Low Probability";
                break;
            case SpawnStates.SUITABLE:
                imageRes = R.drawable.ic_fish_orange_trans;
                colorRes = R.color.colorState3;
                probability = "Moderate Probability";
                break;
            case SpawnStates.VERY_SUITABLE:
                imageRes = R.drawable.ic_fish_red_trans;
                colorRes = R.color.colorState4;
                probability = "High Probability";
                break;
            case SpawnStates.HIGHLY_SUITABLE:
                imageRes = R.drawable.ic_fish_black_trans;
                colorRes = R.color.colorState5;
                probability = "Extreme Probability";
                break;
        }
        holder.iv_state.setImageResource(imageRes);
        holder.tv_state.setTextColor(context.getResources().getColor(colorRes));

        holder.locationId = locationId;
        holder.tv_location.setText(predictions.get(position).getLocationName());
        holder.tv_state.setText(probability);
        holder.tv_verified.setText("");
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    public void setClickListener(PredictionViewHolder.ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public static class PredictionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private String locationId;
        public ClickListener listener;
        public CardView parent_view;
        public ImageView iv_state;
        public TextView tv_location;
        public TextView tv_state;
        public TextView tv_verified;

        public PredictionViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            parent_view = (CardView)itemView.findViewById(R.id.parent_view);
            iv_state = (ImageView)itemView.findViewById(R.id.iv_stateIcon);
            tv_location = (TextView)itemView.findViewById(R.id.tv_locationName);
            tv_state = (TextView)itemView.findViewById(R.id.tv_stateName);
            tv_verified = (TextView)itemView.findViewById(R.id.tv_verfiedBy);
            listener = clickListener;
            parent_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listener != null){
                listener.onClick(locationId);
            }
        }

        public interface ClickListener{
            void onClick(String locationId);
        }
    }
}
