package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RespondentsAdapter extends RecyclerView.Adapter<RespondentsAdapter.RespondentsViewHolder> {
    Context context;
    List<Respond> responses;
    List<Question> questions;
    public RespondentsAdapter(Context context, List<Respond> responds,List<Question> questions) {
        this.context = context;
        this.responses = responds;
        this.questions = questions;
    }

    @NonNull
    @Override
    public RespondentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_respondent,parent,false);
        return new RespondentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RespondentsViewHolder holder, int position) {
        Respond respond = responses.get(position);
        holder.textMaxScore.setText(String.valueOf(Constants.getMaxScore(questions)));
        holder.textAnswered.setText(Constants.formatDate(respond.getDateAnswered()));
        holder.textScore.setText(String.valueOf(Constants.getMyScore(questions,respond)));
        holder.displayStudentInfo(respond.getStudentID());
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }

    public class RespondentsViewHolder  extends RecyclerView.ViewHolder{
        TextView textStudentName;
        CircleImageView studentImage;
        TextView textAnswered;
        TextView textScore,textMaxScore;
        FirebaseFirestore firestore;
        public RespondentsViewHolder(@NonNull View itemView) {
            super(itemView);
            textScore = itemView.findViewById(R.id.textScore);
            textMaxScore = itemView.findViewById(R.id.textMaxScore);
            textAnswered = itemView.findViewById(R.id.textDateAnswered);
            textStudentName = itemView.findViewById(R.id.textStudentName);
            studentImage = itemView.findViewById(R.id.imageStudent);
            firestore = FirebaseFirestore.getInstance();
        }
        void displayStudentInfo(String studentID) {
            firestore.collection(Constants.ACCOUNTS_TABLE)
                    .document(studentID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Accounts accounts = documentSnapshot.toObject(Accounts.class);
                            if (accounts != null) {
                                if (!accounts.getProfile().isEmpty()) {
                                    Glide.with(itemView.getContext()).load(accounts.getProfile()).into(studentImage);
                                }
                                textStudentName.setText(accounts.getName());
                            } else  {
                                textStudentName.setText("Not found");
                            }
                        }
                    });
        }
    }
}
