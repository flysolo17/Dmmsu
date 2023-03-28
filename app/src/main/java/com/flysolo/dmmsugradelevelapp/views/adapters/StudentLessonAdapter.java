package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class StudentLessonAdapter extends RecyclerView.Adapter<StudentLessonAdapter.LessonViewHolder> {
    Context context;
    List<Lesson> lessons;
    StudentLessonClickListener lessonClickListener;
    public interface StudentLessonClickListener {
        void onViewLesson(Lesson lesson);
    }
    public StudentLessonAdapter(Context context, List<Lesson> lessons, StudentLessonClickListener lessonClickListener) {
        this.context = context;
        this.lessons = lessons;
        this.lessonClickListener = lessonClickListener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_student_lessons,parent,false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.textTitle.setText(lesson.getTitle());
        holder.textDesc.setText(lesson.getDescription());
        holder.cardLesson.setOnClickListener(view -> {
            lessonClickListener.onViewLesson(lesson);
        });
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle,textDesc;
        MaterialCardView cardLesson;
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textLessonTitle);
            textDesc = itemView.findViewById(R.id.textLessonDesc);
            cardLesson = itemView.findViewById(R.id.cardLesson);


        }
    }
}
