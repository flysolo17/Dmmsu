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
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class StudentLessonAdapter extends RecyclerView.Adapter<StudentLessonAdapter.LessonViewHolder> {
    Context context;
    List<Lesson> lessons;
    StudentLessonClickListener lessonClickListener;

    public interface StudentLessonClickListener {
        void onViewLesson(Lesson lesson,List<Quiz> quizzes);
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
        holder.cardLesson.setOnClickListener(view -> {
            if (holder.quizArray != null) {
                lessonClickListener.onViewLesson(lesson,holder.quizArray);
            }

        });
        holder.getActivities(lesson.getId());
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle,textActivities;
        MaterialCardView cardLesson;
        FirebaseFirestore firestore;
        List<Quiz> quizArray;
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textLessonTitle);
            cardLesson = itemView.findViewById(R.id.cardLesson);
            textActivities = itemView.findViewById(R.id.textActivities);
            firestore = FirebaseFirestore.getInstance();

        }
        void getActivities(String lessonID) {
            firestore.collection(Constants.ACTIVITIES_TABLE)
                    .whereEqualTo("lessonID",lessonID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<Quiz> quizzes = task.getResult().toObjects(Quiz.class);
                            quizArray = quizzes;
                            String count = quizzes.size() > 1 ? "Activities" : "Activity";
                            textActivities.setText(quizzes.size() + " " + count);
                        }
                    });
        }
    }
}
