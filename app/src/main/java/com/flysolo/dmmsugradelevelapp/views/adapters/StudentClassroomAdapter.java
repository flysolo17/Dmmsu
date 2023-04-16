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


            if (classroom.getStatus()) {
                int active = classroom.getActiveStudents().size();
                int students = classroom.getStudents().size();
                holder.textActive.setText(active+ "/" + students + " active students");
                if (classroom.getActiveStudents().contains(studentID)) {
                    holder.buttonJoinClass.setVisibility(View.GONE);
                } else {
                    holder.buttonJoinClass.setVisibility(View.VISIBLE);
                }

            } else {
                holder.textActive.setText("Class is closed");
                holder.buttonJoinClass.setVisibility(View.GONE);
            }

        holder.getTeacherProfile(classroom.getTeacherID());
        holder.buttonJoinClass.setOnClickListener(view -> listener.onJoin(classroom));
        holder.materialCardView.setOnClickListener(view -> listener.onJoin(classroom));
    }

    @Override
    public int getItemCount() {
        return classroomList.size();
    }

    public class StudentClassroomViewHolder  extends RecyclerView.ViewHolder {
        TextView textClassroomName, textSched, textTime;
        ImageView background;
        MaterialCardView materialCardView;
        LinearLayout layoutStudents;
        FirebaseFirestore firestore;
        TextView  textActive, textTeacher;
        MaterialButton buttonJoinClass;
        public StudentClassroomViewHolder(@NonNull View itemView) {
            super(itemView);
            textClassroomName = itemView.findViewById(R.id.textClassroomName);
            background = itemView.findViewById(R.id.classroomBackground);
            materialCardView = itemView.findViewById(R.id.card);
            textSched = itemView.findViewById(R.id.textSched);
            textTime = itemView.findViewById(R.id.textTime);
            firestore = FirebaseFirestore.getInstance();
            textActive = itemView.findViewById(R.id.textActive);
            buttonJoinClass = itemView.findViewById(R.id.buttonJoinClass);
            textTeacher = itemView.findViewById(R.id.textTeacher);
        }


        void getTeacherProfile(String teacherID) {
            firestore.collection(Constants.ACCOUNTS_TABLE)
                    .document(teacherID)
                    .get()
                    .addOnSuccessListener(task -> {
                        if (task.exists()) {
                            Accounts accounts = task.toObject(Accounts.class);
                            if (accounts != null) textTeacher.setText(accounts.getName());
                            else textTeacher.setText("No name");
                        } else {
                            textTeacher.setText("No name");
                        }
                    });
        }
    }
}
