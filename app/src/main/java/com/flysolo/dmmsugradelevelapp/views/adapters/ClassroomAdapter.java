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
        holder.textClassroomName.setText(classroom.getName());
        holder.materialCardView.setOnClickListener(view -> classroomClickListener.onClassroomClick(position));

    }

    @Override
    public int getItemCount() {
        return classroomList.size();
    }

    public static class ClassroomViewHolder extends RecyclerView.ViewHolder {
        TextView textClassroomName;

        MaterialCardView materialCardView;


        public ClassroomViewHolder(@NonNull View itemView) {
            super(itemView);
             textClassroomName= itemView.findViewById(R.id.textClassroomName);
             materialCardView = itemView.findViewById(R.id.card);

        }

    }



}
