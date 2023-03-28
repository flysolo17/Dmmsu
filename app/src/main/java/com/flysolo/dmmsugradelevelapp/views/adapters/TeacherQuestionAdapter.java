package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Question;

import java.util.List;

public class TeacherQuestionAdapter extends RecyclerView.Adapter<TeacherQuestionAdapter.TeacherQuestionViewHolder> {
    Context context;
    List<Question> questions;
    QuestionClickListener questionClickListener;
    public interface QuestionClickListener{
        void onDelete(String questionID);
        void onEdit(Question question);
    }
    public TeacherQuestionAdapter(Context context, List<Question> questions,QuestionClickListener questionClickListener) {
        this.context = context;
        this.questions = questions;
        this.questionClickListener = questionClickListener;
    }

    @NonNull
    @Override
    public TeacherQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_teacher_questions,parent,false);

        return new TeacherQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherQuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        if (!question.getImage().isEmpty()) {
            Glide.with(context).load(question.getImage()).into(holder.imageAttachment);
        } else {
            holder.imageAttachment.setVisibility(View.GONE);
        }
        if (!question.getDescription().isEmpty()) {
            holder.textDesc.setText(question.getDescription());
        } else {
            holder.textDesc.setVisibility(View.GONE);
        }
        holder.textAnswer.setText(question.getAnswer());
        holder.textTitle.setText(question.getQuestion());
        holder.buttonDelete.setOnClickListener(view -> questionClickListener.onDelete(question.getId()));
        holder.buttonEdit.setOnClickListener(view -> questionClickListener.onEdit(question));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class TeacherQuestionViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle,textDesc,textAnswer;
        ImageView imageAttachment;
        Button buttonDelete,buttonEdit;

        public TeacherQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDesc=  itemView.findViewById(R.id.textDesc);
            textAnswer = itemView.findViewById(R.id.textAnswer);
            imageAttachment = itemView.findViewById(R.id.imageAttachment);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
        }
    }
}