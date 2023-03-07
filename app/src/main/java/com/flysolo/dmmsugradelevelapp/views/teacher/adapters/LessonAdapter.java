package com.flysolo.dmmsugradelevelapp.views.teacher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Content;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {
    Context context;
    ArrayList<Lesson> lessons;
    LessonClickListener lessonClickListener;
    public interface LessonClickListener {
        void onLessonClick(Lesson lesson);
    }
    public LessonAdapter(Context context, ArrayList<Lesson> lessons,LessonClickListener lessonClickListener) {
        this.context = context;
        this.lessons = lessons;
        this.lessonClickListener = lessonClickListener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_lessons,parent,false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.textTitle.setText(lesson.getTitle());
        holder.textDesc.setText(lesson.getDescription());
        holder.itemView.setOnClickListener(view -> lessonClickListener.onLessonClick(lesson));
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle,textDesc;
        MaterialCardView cardLesson;
        LinearLayout layoutActions;
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDesc = itemView.findViewById(R.id.textDesc);
            cardLesson = itemView.findViewById(R.id.cardLesson);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            cardLesson.setOnClickListener(view -> {
                if (layoutActions.getVisibility() == View.VISIBLE) {
                    layoutActions.setVisibility(View.GONE);
                } else {
                    layoutActions.setVisibility(View.VISIBLE);
                }
            });

        }
    }
}