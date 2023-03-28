package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.ClassroomViewHolder> {
    Context context;
    List<Classroom> classroomList;
    ClassroomClickListener classroomClickListener;
    public interface ClassroomClickListener {
        void onClassroomClick(int position);
        void onStartClassroom(int position);
        void startClass(int position);
        void endClass(int position);
        void shareCode(int position);
    }
    public ClassroomAdapter(Context context,List<Classroom> classroomList,ClassroomClickListener classroomClickListener) {
        this.context = context;
        this.classroomList = classroomList;
        this.classroomClickListener = classroomClickListener;
    }


    @NonNull
    @Override
    public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_classroom,parent,false);
        return new ClassroomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomViewHolder holder, int position) {
        Classroom classroom = classroomList.get(position);
        holder.textTime.setText(classroom.getStartTime());
        holder.textClassroomName.setText(classroom.getName());
        holder.textSched.setText(String.join(", " ,classroom.getSchedule()));
        holder.materialCardView.setOnClickListener(view -> classroomClickListener.onClassroomClick(position));
        holder.buttonShareCode.setOnClickListener(view -> classroomClickListener.shareCode(position));
        holder.getStudents(classroom.getStudents());
        if (classroom.getStatus()){
            holder.buttonShareCode.setVisibility(View.VISIBLE);
            holder.buttonEnd.setVisibility(View.VISIBLE);
            holder.buttonStart.setVisibility(View.GONE);
        } else {
            holder.buttonShareCode.setVisibility(View.GONE);
            holder.buttonEnd.setVisibility(View.GONE);
            holder.buttonStart.setVisibility(View.VISIBLE);
        }
        holder.buttonStart.setOnClickListener(view -> classroomClickListener.startClass(position));
        holder.buttonEnd.setOnClickListener(view -> classroomClickListener.endClass(position));
        if (classroom.getStatus()) {
            holder.textActive.setText(classroom.getActiveStudents().size() + "/" + classroom.getStudents().size() + " active students");
        } else {
            holder.textActive.setText("Class is closed");

        }
    }

    @Override
    public int getItemCount() {
        return classroomList.size();
    }

    public static class ClassroomViewHolder extends RecyclerView.ViewHolder {
        TextView textClassroomName,textSched,textTime;
        ImageView background;
        MaterialCardView materialCardView;
        LinearLayout layoutStudents,layout3OrMoreStudent,layoutMoreStudents;
        FirebaseFirestore firestore;
        View view;
        CircleImageView profile1 ,profile2,profile3;
        TextView textNoStudents,textMoreStudentCount,textActive;
        MaterialButton buttonShareCode,buttonEnd,buttonStart;

        public ClassroomViewHolder(@NonNull View itemView) {
            super(itemView);
             textClassroomName= itemView.findViewById(R.id.textClassroomName);
             background = itemView.findViewById(R.id.classroomBackground);
             materialCardView = itemView.findViewById(R.id.card);
             textSched = itemView.findViewById(R.id.textSched);
             textTime = itemView.findViewById(R.id.textTime);
             layoutStudents = itemView.findViewById(R.id.layoutStudents);
             firestore = FirebaseFirestore.getInstance();
             view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_stud,layoutStudents,false);
             layout3OrMoreStudent = view.findViewById(R.id.layout3OrMoreStudent);
             textNoStudents = view.findViewById(R.id.textNoStudents);
             textMoreStudentCount = view.findViewById(R.id.textMoreStudentCount);
             profile1 = view.findViewById(R.id.profile1);
             profile3= view.findViewById(R.id.profile3);
             profile2 = view.findViewById(R.id.profile2);
             layoutMoreStudents = view.findViewById(R.id.layoutMoreStudents);
             layoutStudents.addView(view);
             buttonStart = itemView.findViewById(R.id.buttonStart);
             buttonEnd = itemView.findViewById(R.id.buttonEnd);
             buttonShareCode = itemView.findViewById(R.id.buttonShare);
             textActive = itemView.findViewById(R.id.textActive);

        }
        void getStudents(List<String> students){
            bindStudentLayout(students.size());
            for (int i = 0; i < students.size(); i++) {
                if (i <= 3){
                    getStudentProfile(students.get(i),i);
                }
            }
        }
        void getStudentProfile(String id,int count) {
            firestore.collection(Constants.ACCOUNTS_TABLE)
                    .document(id)
                    .get()
                    .addOnSuccessListener(task -> {
                        if (task.exists()){
                            Accounts accounts = task.toObject(Accounts.class);
                            if (accounts != null && !accounts.getProfile().isEmpty()) {
                                bindProfile(accounts.getProfile(), count);
                            }
                        }
                    });
        }
        void bindProfile(String url ,int count) {
            switch (count) {
                case 1:
                    Glide.with(itemView.getContext()).load(url).into(profile2);
                    break;
                case 2:
                    Glide.with(itemView.getContext()).load(url).into(profile3);
                    break;
                default:
                    Glide.with(itemView.getContext()).load(url).into(profile1);
            }
        }
        void bindStudentLayout(int count){
            if (count == 1) {
                textNoStudents.setVisibility(View.GONE);
                profile2.setVisibility(View.GONE);
                profile3.setVisibility(View.GONE);
                layoutMoreStudents.setVisibility(View.GONE);
            } else if (count == 2) {
                textNoStudents.setVisibility(View.GONE);
                profile3.setVisibility(View.GONE);
                layoutMoreStudents.setVisibility(View.GONE);
            }
             else if (count == 3) {

                textNoStudents.setVisibility(View.GONE);
                layoutMoreStudents.setVisibility(View.GONE);
            }
            else if (count >= 4) {

                textNoStudents.setVisibility(View.GONE);
                textMoreStudentCount.setText((count - 3) + "");
            }else {
                textNoStudents.setVisibility(View.VISIBLE);
                layout3OrMoreStudent.setVisibility(View.GONE);
            }
        }
    }

    private int getChar(String str)
    {
        StringBuilder ch = new StringBuilder();
        // loop through each element
        for(int i = 0; i<2; i++) {
            ch.append(str.charAt(i));
        }
        return Integer.parseInt(ch.toString());
    }

}
