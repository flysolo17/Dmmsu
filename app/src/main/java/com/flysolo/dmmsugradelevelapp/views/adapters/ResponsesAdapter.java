package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
public class ResponsesAdapter extends RecyclerView.Adapter<ResponsesAdapter.ResponsesViewHolder> {
    Context context;
    List<Respond> responds;
    ResponsesClickListener listener;

    public interface ResponsesClickListener {
        void onResponseClicked(Respond respond,Quiz quiz);
    }
    public ResponsesAdapter(Context context, List<Respond> responds,ResponsesClickListener responsesClickListener) {
        this.context = context;
        this.responds = responds;
        this.listener = responsesClickListener;
    }

    @NonNull
    @Override
    public ResponsesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_responses,parent,false);
        return new ResponsesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponsesViewHolder holder, int position) {
        Respond respond = responds.get(position);
        holder.displayActivityInfo(respond.getClassroomID(),respond.getActivityID(),respond);
        holder.cardActivity.setOnClickListener(view -> {
            listener.onResponseClicked(responds.get(position),holder.quiz);
        });
    }

    @Override
    public int getItemCount() {
        return responds.size();
    }

    public class ResponsesViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle,textDesc,textCreatedAt,textMyScore,textMaxScore,textRespondDate;
        MaterialCardView cardActivity;
        FirebaseFirestore firestore;
        Quiz quiz;
        public ResponsesViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textActivityTitle);
            textDesc = itemView.findViewById(R.id.textDesc);
            textCreatedAt = itemView.findViewById(R.id.textCreatedAt);
            cardActivity = itemView.findViewById(R.id.cardActivity);
            textRespondDate = itemView.findViewById(R.id.textRespondDate);
            textMaxScore = itemView.findViewById(R.id.textMaxScore);
            textMyScore = itemView.findViewById(R.id.textMyScore);
            firestore = FirebaseFirestore.getInstance();

        }
        void displayActivityInfo(String classroomID,String activityID,Respond respond) {
            firestore.collection(Constants.CLASSROOM_TABLE)
                    .document(classroomID)
                    .collection(Constants.ACTIVITIES_TABLE)
                    .document(activityID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()) {
                            quiz = documentSnapshot.toObject(Quiz.class);

                            textTitle.setText(quiz.getName() + "");
                            textDesc.setText(quiz.getDescription()+ "");
                            textCreatedAt.setText(Constants.formatDate(quiz.getCreatedAt()));
                            getQuestion(activityID,respond);
                        }
                    });
        }
        void getQuestion(String activityID,Respond respond) {
            firestore.collection(Constants.CLASSROOM_TABLE)
                    .document(respond.getClassroomID())
                    .collection(Constants.QUESTIONS_TABLE)
                    .whereEqualTo("activityID",activityID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Question> questions = task.getResult().toObjects(Question.class);
                            textMaxScore.setText(String.valueOf(getMaxScore(questions)));
                            textMyScore.setText(String.valueOf(checkIfAnswerCorrect(questions,respond)));
                        }
                    });
        }
        int getMaxScore(List<Question> questions){
            int count = 0;
            for (Question question : questions) {
                count += question.getPoints();
            }
            return count;
        }
         int checkIfAnswerCorrect(List<Question> questions,Respond respond) {
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
}
