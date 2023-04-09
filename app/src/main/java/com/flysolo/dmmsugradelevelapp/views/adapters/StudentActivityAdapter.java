package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Answer;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StudentActivityAdapter extends RecyclerView.Adapter<StudentActivityAdapter.StudentActivityViewHolder> {
    Context context;
    List<Quiz> quizzes;
    StudentActivityClickListener activityClickListener;
    public interface StudentActivityClickListener{
        void onActivityClicked(Quiz quiz);
    }

    public StudentActivityAdapter(Context context, List<Quiz> quizzes, StudentActivityClickListener activityClickListener) {
        this.context = context;
        this.quizzes = quizzes;
        this.activityClickListener = activityClickListener;
    }

    @NonNull
    @Override
    public StudentActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_student_activities,parent,false);
        return new StudentActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentActivityViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.textTitle.setText(quiz.getName());
        holder.textCreatedAt.setText(Constants.formatDate(quiz.getCreatedAt()));
        String data = quiz.getQuestions().size() > 1 ? "Questions" : "Question";
        holder.textQuestions.setText(quiz.getQuestions().size() + " " + data);
        holder.textPoints.setText("+" + Constants.getMaxScore(quiz.getQuestions()) + " Points");
        holder.cardActivity.setOnClickListener(view -> activityClickListener.onActivityClicked(quiz));
        holder.textDesc.setText(quiz.getDescription());
        holder.textTime.setText(quiz.getTimer() + " min");
        holder.textType.setText(quiz.getQuizType().toString().replace("_" ," "));
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public class StudentActivityViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle,textDesc,textCreatedAt,textQuestions,textPoints,textTime,textType;
        MaterialCardView cardActivity;
        public StudentActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDesc = itemView.findViewById(R.id.textDesc);
            textTime = itemView.findViewById(R.id.textTime);
            textCreatedAt = itemView.findViewById(R.id.textCreatedAt);
            cardActivity = itemView.findViewById(R.id.cardActivity);
            textQuestions = itemView.findViewById(R.id.textQuestion);
            textPoints = itemView.findViewById(R.id.textPoints);
            textType = itemView.findViewById(R.id.textType);
        }

    }
    private int getMaxScore(List<Question> questions){
        int count = 0;
        for (Question question : questions) {
            count += question.getPoints();
        }
        return count;
    }
    private int checkIfAnswerCorrect(List<Question> questions,Respond respond) {
        int score = 0;
        for (Question question: questions) {
            for (Answer answer: respond.getAnswers()) {
                if (question.getId().equals(answer.getQuestionID()) && question.getAnswer().equals(answer.getAnswer())) {
                    score+= question.getPoints();
                }
            }
        }
        return score;
    }
}
