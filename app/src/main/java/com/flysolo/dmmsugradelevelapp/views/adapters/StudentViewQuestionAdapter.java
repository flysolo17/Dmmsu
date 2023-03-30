package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Answer;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Respond;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StudentViewQuestionAdapter extends RecyclerView.Adapter<StudentViewQuestionAdapter.StudentViewQuestionViewHolder> {
    Context context;
    List<Question> questions;
    Respond respond;

    public StudentViewQuestionAdapter(Context context, List<Question> questions,Respond respond) {
        this.context = context;
        this.questions = questions;
        this.respond = respond;

    }

    @NonNull
    @Override
    public StudentViewQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_view_question,parent,false);
        return new StudentViewQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewQuestionViewHolder holder,int position) {
        Question question = questions.get(position);
        holder.edtAnswer.setText(question.getAnswer());

        holder.textQuestion.setText(question.getQuestion());
        holder.textDescription.setText(question.getDescription());
        if (question.getDescription().isEmpty()) {
            holder.textDescription.setVisibility(View.GONE);
        }
        if(!question.getImage().isEmpty()) {
            Glide.with(context).load(question.getImage()).into(holder.imageQuestion);
        } else {
            holder.imageQuestion.setVisibility(View.GONE);
        }
        if (!question.getChoices().isEmpty()) {
            holder.edtAnswer.setVisibility(View.GONE);
        } else {
            holder.edtAnswer.setVisibility(View.VISIBLE);
            holder.radioGroup.setVisibility(View.GONE);
        }
        holder.attachRadioButtons(question,respond);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class StudentViewQuestionViewHolder extends RecyclerView.ViewHolder{
        TextView textQuestion,textDescription,textMyScore,textYourAnswer;
        ImageView imageQuestion;
        RadioGroup radioGroup;
        EditText edtAnswer;
        RadioButton[] rb;
        public StudentViewQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            textQuestion = itemView.findViewById(R.id.textQuestion);
            textDescription = itemView.findViewById(R.id.textDesc);
            textMyScore = itemView.findViewById(R.id.textMyScore);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            imageQuestion = itemView.findViewById(R.id.imageQuestion);
            edtAnswer = itemView.findViewById(R.id.edtAnswer);
            textYourAnswer = itemView.findViewById(R.id.textYourAnswer);
        }
        void attachRadioButtons(Question question,Respond respond) {
            rb = new RadioButton[question.getChoices().size()];
            for (int i = 0; i < question.getChoices().size(); i++) {
                rb[i]  = new RadioButton(context);
                rb[i].setText(" " + question.getChoices().get(i));
                rb[i].setId(i);
                radioGroup.addView(rb[i]);
                if (question.getChoices().get(i).equals(question.getAnswer())){
                    radioGroup.check(i);
                    rb[i].setTextColor(Color.GREEN);
                }
            }
            checkAnswer(respond,question);
        }
         void checkAnswer(Respond respond,Question question) {
            String ans = "";
            String correct = question.getAnswer();
            int color = Color.RED;
            for (Answer answer : respond.getAnswers()) {
                 if (answer.getQuestionID().equals(question.getId())) {
                     ans = answer.getAnswer();
                     if (correct.equals(ans)) {
                         color = Color.GREEN;
                     } else  {
                         color = Color.RED;
                     }
                 }
             }
             textYourAnswer.setTextColor(color);
             textYourAnswer.setText(ans);
             textMyScore.setText(String.valueOf(checkIfAnswerCorrect(respond,question)));
        }

    }
    private int checkIfAnswerCorrect(Respond respond,Question question) {
        int score = 0;
        for (Answer ans: respond.getAnswers()) {
            if (ans.getQuestionID().equals(question.getId()) && question.getAnswer().equals(ans.getAnswer())) {
                score += question.getPoints();
            }
        }
        return score;
    }
}
