package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Answer;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.model.Scores;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ResponsesAdapter extends RecyclerView.Adapter<ResponsesAdapter.ResponsesViewHolder> {
    Context context;
    List<Scores> scores;
    public ResponsesAdapter(Context context,List<Scores> scores) {
        this.context = context;
        this.scores = scores;
    }

    @NonNull
    @Override
    public ResponsesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_responses,parent,false);
        return new ResponsesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponsesViewHolder holder, int position) {
        Scores score = scores.get(position);
        holder.textFullname.setText(score.getStudentName() != null ? score.getStudentName() : "User not found!");
        holder.textScore.setText(score.getStudentScore()+ " Points");
        if (!score.getStudentProfile().isEmpty()) {
            Glide.with(context).load(score.getStudentProfile()).into(holder.imageProfile);
        }
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public class ResponsesViewHolder extends RecyclerView.ViewHolder {
        TextView textFullname,textScore;
        CircleImageView imageProfile;
        public ResponsesViewHolder(@NonNull View itemView) {
            super(itemView);
            textFullname = itemView.findViewById(R.id.textFullname);
            textScore = itemView.findViewById(R.id.textPoints);
            imageProfile = itemView.findViewById(R.id.imageProfile);

        }

    }
}
