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
    String classroomID;
    StudentActivityClickListener activityClickListener;
    public interface StudentActivityClickListener{
        void onActivityClicked(Quiz quiz);
        void viewScore(Quiz quiz,Respond respond);

    }

    public StudentActivityAdapter(Context context, List<Quiz> quizzes,String classroomID, StudentActivityClickListener activityClickListener) {
        this.context = context;
        this.quizzes = quizzes;
        this.classroomID = classroomID;
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        holder.textTitle.setText(quiz.getName());
        holder.textDesc.setText(quiz.getDescription());
        Date date = new Date(quiz.getCreatedAt());
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        holder.textCreatedAt.setText(df.format(date));
        holder.buttonStart.setOnClickListener(view -> activityClickListener.onActivityClicked(quiz));
        if (user != null) {
            holder.getQuestion(user.getUid(),quiz.getId());
        }
        holder.cardActivity.setOnClickListener(view -> {
            if (holder.respond != null) {
                activityClickListener.viewScore(quiz, holder.respond);
            }
        });

    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public class StudentActivityViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle,textDesc,textCreatedAt,textMyScore,textMaxScore,textRespondDate;
        MaterialCardView cardActivity;
        Button  buttonStart;
        FirebaseFirestore firestore;
        LinearLayout layoutAnsweredAt;
        Respond respond;
        public StudentActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textActivityTitle);
            textDesc = itemView.findViewById(R.id.textDesc);
            textCreatedAt = itemView.findViewById(R.id.textCreatedAt);
            cardActivity = itemView.findViewById(R.id.cardActivity);
            buttonStart = itemView.findViewById(R.id.buttonStartActivity);
            textMaxScore = itemView.findViewById(R.id.textMaxScore);
            textMyScore = itemView.findViewById(R.id.textMyScore);
            firestore = FirebaseFirestore.getInstance();
            textRespondDate = itemView.findViewById(R.id.textRespondDate);
            layoutAnsweredAt = itemView.findViewById(R.id.layoutAnsweredAt);
        }
        void getQuestion(String studentID,String activityID) {
            firestore.collection(Constants.CLASSROOM_TABLE)
                    .document(classroomID)
                    .collection(Constants.QUESTIONS_TABLE)
                    .whereEqualTo("activityID",activityID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Question> questions = task.getResult().toObjects(Question.class);
                            textMaxScore.setText(String.valueOf(getMaxScore(questions)));
                            getMyScore(studentID, activityID,questions);
                        }
                    });
        }
        void getMyScore(String studentID, String activityID,List<Question> question) {
            firestore.collection(Constants.CLASSROOM_TABLE)
                    .document(classroomID)
                    .collection(Constants.RESOPONSES_TABLE)
                    .whereEqualTo("studentID",studentID)
                    .whereEqualTo("activityID",activityID)
                    .orderBy("dateAnswered", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            Respond respond = task.getResult().toObjects(Respond.class).get(0);
                            this.respond = respond;
                            int score = 0;
                            score += checkIfAnswerCorrect(question,respond);
                            textMyScore.setText(String.valueOf(score));
                            Date date = new Date(respond.getDateAnswered());
                            SimpleDateFormat inFormat = new SimpleDateFormat("MMM dd yyyy hh:mm aa");
                            textRespondDate.setText(inFormat.format(date));
                            buttonStart.setVisibility(View.GONE);
                        } else {
                            layoutAnsweredAt.setVisibility(View.GONE);
                            buttonStart.setVisibility(View.VISIBLE);
                        }
                    });
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
