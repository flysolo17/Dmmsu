package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentClassroomAdapter extends RecyclerView.Adapter<StudentClassroomAdapter.StudentClassroomViewHolder> {
    Context context;
    List<Classroom> classroomList;
    String studentID;
    StudentClassroomClickListener listener;
    public interface StudentClassroomClickListener {
        void onJoin(Classroom classroom);
    }

    public StudentClassroomAdapter(Context context, List<Classroom> classroomList,String studentID,StudentClassroomClickListener listener) {
        this.context = context;
        this.classroomList = classroomList;
        this.listener = listener;
        this.studentID = studentID;
    }

    @NonNull
    @Override
    public StudentClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_student_classroom_2,parent,false);
        return new StudentClassroomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentClassroomViewHolder holder, int position) {
        Classroom classroom = classroomList.get(position);
        holder.textTime.setText(classroom.getStartTime());
        holder.textClassroomName.setText(classroom.getName());
        holder.textSched.setText(String.join(", " ,classroom.getSchedule()));
        holder.getStudents(classroom.getStudents());
        if (classroom.getStatus()) {
            holder.textActive.setText(classroom.getActiveStudents().size() + "/" + classroom.getStudents().size() + " active students");
            holder.buttonJoinClass.setVisibility(View.VISIBLE);
        } else {
            holder.textActive.setText("Class is closed");
            holder.buttonJoinClass.setVisibility(View.GONE);
        }
        holder.getTeacherProfile(classroom.getTeacherID());
    }

    @Override
    public int getItemCount() {
        return classroomList.size();
    }

    public class StudentClassroomViewHolder  extends RecyclerView.ViewHolder{
        TextView textClassroomName,textSched,textTime;
        ImageView background;
        MaterialCardView materialCardView;
        LinearLayout layoutStudents,layout3OrMoreStudent,layoutMoreStudents;
        FirebaseFirestore firestore;
        View view;
        CircleImageView profile1 ,profile2,profile3;
        TextView textNoStudents,textMoreStudentCount,textActive,textTeacher;
        MaterialButton buttonJoinClass;
        public StudentClassroomViewHolder(@NonNull View itemView) {
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
            textActive = itemView.findViewById(R.id.textActive);
            buttonJoinClass = itemView.findViewById(R.id.buttonJoinClass);
            textTeacher = itemView.findViewById(R.id.textTeacher);
        }

        void getStudents(List<String> students){
            bindStudentLayout(students.size());
            for (int i = 0; i < students.size(); i++) {
                if (i <= 3){
                    getStudentProfile(students.get(i),i);
                }
            }
        }
        void getTeacherProfile(String teacherID) {
            firestore.collection(Constants.ACCOUNTS_TABLE)
                    .document(teacherID)
                    .get()
                    .addOnSuccessListener(task -> {
                        if (task.exists()){
                            Accounts accounts = task.toObject(Accounts.class);
                            if (accounts != null) textTeacher.setText(accounts.getName());
                            else textTeacher.setText("No name");
                        } else  {
                            textTeacher.setText("No name");
                        }
                    });
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
            switch (count){
                case 1:
                    textNoStudents.setVisibility(View.GONE);
                    profile2.setVisibility(View.GONE);
                    profile3.setVisibility(View.GONE);
                    layoutMoreStudents.setVisibility(View.GONE);
                    break;
                case 2:
                    textNoStudents.setVisibility(View.GONE);
                    profile3.setVisibility(View.GONE);
                    layoutMoreStudents.setVisibility(View.GONE);
                    break;
                case 3:
                    textNoStudents.setVisibility(View.GONE);
                    layoutMoreStudents.setVisibility(View.GONE);
                    break;
                case 4:
                    textNoStudents.setVisibility(View.GONE);
                    textMoreStudentCount.setText((count - 3)+"");
                    break;
                default:
                    textNoStudents.setVisibility(View.VISIBLE);
                    layout3OrMoreStudent.setVisibility(View.GONE);
            }
        }

    }


}
