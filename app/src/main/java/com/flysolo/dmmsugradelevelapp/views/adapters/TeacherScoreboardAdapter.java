package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherScoreboardAdapter extends RecyclerView.Adapter<TeacherScoreboardAdapter.TeacherScoreboardViewHolder> {
    private Context context;
    private List<String> studentList;
    private List<Classroom> classroomList;
    private List<Respond> respondList;


    public TeacherScoreboardAdapter(Context context, List<String> studentList, List<Classroom> classroomList, List<Respond> respondList) {
        this.context = context;
        this.studentList = studentList;
        this.classroomList = classroomList;
        this.respondList = respondList;

    }

    @NonNull
    @Override
    public TeacherScoreboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_teacher_scoreboard,parent,false);
        return new TeacherScoreboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherScoreboardViewHolder holder, int position) {
        String studentID = studentList.get(position);
        holder.getStudentInfo(studentID);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class TeacherScoreboardViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imageStudent;
        TextView textFullname,textActivities,textClasses,textCompleted,textActivityCount;
        FirebaseFirestore firestore;
        CircularProgressIndicator progressBar;

        public TeacherScoreboardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageStudent = itemView.findViewById(R.id.imageStudent);
            textFullname = itemView.findViewById(R.id.textStudentName);
            textActivities = itemView.findViewById(R.id.textActivities);
            textClasses = itemView.findViewById(R.id.textClasses);
            textCompleted = itemView.findViewById(R.id.textCompleted);
            textActivityCount = itemView.findViewById(R.id.textActivityCount);
            firestore = FirebaseFirestore.getInstance();
            progressBar = itemView.findViewById(R.id.progress);

        }
        void getStudentInfo(String uid) {
            firestore.collection(Constants.ACCOUNTS_TABLE)
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Accounts accounts = documentSnapshot.toObject(Accounts.class);
                            if (accounts != null) {
                                if (!accounts.getProfile().isEmpty()) {
                                    Glide.with(itemView.getContext()).load(accounts.getProfile()).into(imageStudent);
                                }
                                textFullname.setText(accounts.getName());
                                textClasses.setText(String.valueOf(getClassroomList(uid).size()));
                                getAllActivities(getClassroomList(uid),uid);
                            }
                        }
                    });
        }
        void getAllActivities(List<Classroom> classroomList,String uid) {
            List<Quiz> activities = new ArrayList<>();
            for (Classroom classroom: classroomList) {
                firestore.collection(Constants.CLASSROOM_TABLE)
                        .document(classroom.getId())
                        .collection(Constants.ACTIVITIES_TABLE)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot snap : task.getResult()) {
                                    Quiz respond = snap.toObject(Quiz.class);
                                    activities.add(respond);
                                }

                                textActivityCount.setText(String.valueOf(activities.size()));
                                if (activities.size() > 0) {
                                    double progress = getCompleted(getYourResponses(uid,respondList).size(),activities.size());
                                    progressBar.setProgress((int) progress);
                                    textCompleted.setText(progress + "%");
                                    textActivities.setText(getYourResponses(uid,respondList).size() + "");
                                }

                            } else {
                                Toast.makeText(context, "Failed getting activities", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }
    }
    private List<Respond> getYourResponses(String studentID,List<Respond> respondList) {
        List<Respond> arrayList = new ArrayList<>();
        for (Respond respond: respondList) {
            if (respond.getStudentID().equals(studentID)) {
                arrayList.add(respond);
            }
        }

        return arrayList;
    }
    private List<Classroom> getClassroomList(String uid) {
        List<Classroom> classrooms = new ArrayList<>();
        for (Classroom classroom: classroomList) {
            if (classroom.getStudents().contains(uid)){
                classrooms.add(classroom);
            }
        }
        return classrooms;
    }
    private double getCompleted(int completed,int activities) {
        return completed * 100 / activities;
    }
}
