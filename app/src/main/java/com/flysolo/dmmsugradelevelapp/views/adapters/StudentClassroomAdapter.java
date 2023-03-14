package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        View view= LayoutInflater.from(context).inflate(R.layout.row_student_classroom,parent,false);
        return new StudentClassroomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentClassroomViewHolder holder, int position) {
        Classroom classroom = classroomList.get(position);
        if (!classroom.getBackground().isEmpty() || classroom.getBackground() != null) {
            Glide.with(context).load(classroom.getBackground()).into(holder.background);
        }
        holder.textClassroomName.setText(classroom.getName());
        holder.getTeacher(classroom.getTeacherID());
        if (classroom.getStudents().contains(studentID)) {
            holder.buttonJoin.setVisibility(View.GONE);
        } else {
            holder.buttonJoin.setVisibility(View.VISIBLE);
        }
        holder.buttonJoin.setOnClickListener(view -> listener.onJoin(classroom));
    }

    @Override
    public int getItemCount() {
        return classroomList.size();
    }

    public class StudentClassroomViewHolder  extends RecyclerView.ViewHolder{
        TextView textClassroomName,textTeacherName;
        ImageView background;
        MaterialCardView materialCardView;
        MaterialButton buttonJoin;
        CircleImageView techerProfile;
        FirebaseFirestore firestore;
        public StudentClassroomViewHolder(@NonNull View itemView) {
            super(itemView);
            textClassroomName= itemView.findViewById(R.id.textClassroomName);
            background = itemView.findViewById(R.id.classroomBackground);
            materialCardView = itemView.findViewById(R.id.card);
            techerProfile = itemView.findViewById(R.id.teacherProfile);
            textTeacherName = itemView.findViewById(R.id.textTeacher);
            buttonJoin = itemView.findViewById(R.id.buttonJoin);
            firestore = FirebaseFirestore.getInstance();
            techerProfile.setImageResource(R.drawable.profile_placeholder);

        }
        void getTeacher(String teacherID){
            firestore.collection(Constants.ACCOUNTS_TABLE)
                    .document(teacherID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            Accounts accounts = task.getResult().toObject(Accounts.class);
                            if (accounts != null) {
                                if (!accounts.getProfile().isEmpty()) {
                                    Glide.with(context).load(accounts.getProfile()).into(techerProfile);
                                }
                                textTeacherName.setText(accounts.getName());
                            }
                        }
                    });
        }
    }
}
