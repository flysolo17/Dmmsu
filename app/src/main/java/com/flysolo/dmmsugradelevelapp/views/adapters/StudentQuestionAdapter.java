package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class StudentQuestionAdapter extends RecyclerView.Adapter<StudentQuestionAdapter.StudentQuestionViewHolder> {
    Context context;
    List<Question> questions;
    List<Answer> answers = new ArrayList<>();
    public StudentQuestionAdapter(Context context, List<Question> questions) {
        this.context = context;
        this.questions = questions;
        for (Question question: this.questions) {
            answers.add(new Answer(question.getId(),""));
        }
    }

    @NonNull
    @Override
    public StudentQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_student_questions,parent,false);
        return new StudentQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentQuestionViewHolder holder,int position) {
        Question question = questions.get(position);
        holder.textQuestion.setText(question.getQuestion());
        if(!question.getImage().isEmpty()) {
            Glide.with(context).load(question.getImage()).into(holder.imageQuestion);
        } else {
            holder.imageQuestion.setVisibility(View.GONE);
        }
        holder.textPosition.setText(String.valueOf(position + 1));
        holder.edtAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    answers.get(position).setAnswer(editable.toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class StudentQuestionViewHolder extends RecyclerView.ViewHolder{
        TextView textQuestion,textPosition;
        ImageView imageQuestion;
        EditText edtAnswer;

        public StudentQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            textQuestion = itemView.findViewById(R.id.textQuestion);
            imageQuestion = itemView.findViewById(R.id.imageQuestion);
            edtAnswer = itemView.findViewById(R.id.edtAnswer);
            textPosition = itemView.findViewById(R.id.textPosition);
        }


    }
    public List<Answer> getAnswer() {
        return answers;
    }
}
