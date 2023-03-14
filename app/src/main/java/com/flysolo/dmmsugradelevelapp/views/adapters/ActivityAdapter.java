package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Quiz;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    Context context;
    ArrayList<Quiz> quizzes;

    public ActivityAdapter(Context context, ArrayList<Quiz> quizzes) {
        this.context = context;
        this.quizzes = quizzes;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_activities,parent,false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {

        Quiz quiz = quizzes.get(position);
        Date date = new Date(quiz.getCreatedAt());
        holder.textTitle.setText(quiz.getName());
        holder.textDesc.setText(quiz.getDescription());
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        holder.textCreatedAt.setText(df.format(date));
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle,textDesc,textCreatedAt;
        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDesc = itemView.findViewById(R.id.textDesc);
            textCreatedAt = itemView.findViewById(R.id.textCreatedAt);
        }
    }
}
