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
import com.flysolo.dmmsugradelevelapp.model.Scores;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.color.utilities.Score;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RespondentsAdapter extends RecyclerView.Adapter<RespondentsAdapter.RespondentsViewHolder> {
    Context context;
    List<String> students;
    List<Respond> respondList;
    public RespondentsAdapter(Context context, List<String> students,List<Respond> respondList) {
        this.context = context;
        this.students = students;
        Collections.sort(respondList, new Comparator<Respond>() {
            @Override
            public int compare(Respond respond, Respond t1) {
                return Integer.compare(respond.getTotal(),t1.getTotal());
            }

            @Override
            public Comparator<Respond> reversed() {
                return Comparator.super.reversed();
            }
        });
        this.respondList = respondList;
    }

    @NonNull
    @Override
    public RespondentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_respondent,parent,false);
        return new RespondentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RespondentsViewHolder holder, int position) {
        holder.displayStudentInfo(students.get(position));
        holder.getHighest(getMyResponses(respondList,students.get(position)));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class RespondentsViewHolder  extends RecyclerView.ViewHolder{
        TextView textStudentName,textAttemps;
        CircleImageView studentImage;
        TextView textScore;
        FirebaseFirestore firestore;
        public RespondentsViewHolder(@NonNull View itemView) {
            super(itemView);
            textAttemps = itemView.findViewById(R.id.textAttemps);
            textScore = itemView.findViewById(R.id.textPoints);
            textStudentName = itemView.findViewById(R.id.textFullname);
            studentImage = itemView.findViewById(R.id.imageProfile);
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
        void getHighest(List<Respond> respondList) {
            int highest = respondList.size() != 0 ? respondList.get(0).getTotal() : 0;
            textAttemps.setText(String.format("Matches : %s", respondList.size()));
            textScore.setText(String.format("Highest Score : %s", highest));
        }
    }

    List<Respond> getMyResponses(List<Respond> respondList, String studentID) {
        List<Respond> responds = new ArrayList<>();
        for (Respond respond: respondList) {
            if (respond.getStudentID().equals(studentID)) {
                responds.add(respond);
            }
        }
        return responds;
    }
}
