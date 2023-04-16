package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.google.android.material.card.MaterialCardView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityAdapter extends FirestoreRecyclerAdapter<Quiz, ActivityAdapter.ActivityViewHolder> {
    Context context;
    FirestoreRecyclerOptions<Quiz>  options;
    ActivityClickListener activityClickListener;
    public ActivityAdapter(@NonNull FirestoreRecyclerOptions<Quiz> options,Context context,ActivityClickListener activityClickListener) {
        super(options);
        this.context = context;
        this.options = options;
        this.activityClickListener = activityClickListener;
    }

    public interface ActivityClickListener{
        void onActivityClicked(Quiz quiz);
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_activities,parent,false);
        return new ActivityViewHolder(view);
    }
    @Override
    protected void onBindViewHolder(@NonNull ActivityViewHolder holder, int position, @NonNull Quiz model) {
        holder.textTitle.setText(model.getName());
        String data = model.getQuestions().size() > 1 ? "Questions" : "Question";
        holder.textQuestions.setText(model.getQuestions().size() + " " + data);
        holder.textPoints.setText("+" + Constants.getMaxScore(model.getQuestions()) + " Points");
        holder.cardActivity.setOnClickListener(view -> activityClickListener.onActivityClicked(model));
        holder.textDesc.setText(model.getDescription());
        holder.textTime.setText(model.getTimer() + " min");
    }



    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle,textDesc,textQuestions,textPoints,textTime;
        MaterialCardView cardActivity;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDesc = itemView.findViewById(R.id.textDesc);
            textTime = itemView.findViewById(R.id.textTime);
            cardActivity = itemView.findViewById(R.id.cardActivity);
            textQuestions = itemView.findViewById(R.id.textQuestion);
            textPoints = itemView.findViewById(R.id.textPoints);
        }
    }
}
