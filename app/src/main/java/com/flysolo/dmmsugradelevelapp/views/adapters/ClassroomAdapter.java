package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.ClassrooomViewHolder> {
    Context context;
    ArrayList<Classroom> classroomList;
    ClassroomClickListener classroomClickListener;
    public interface ClassroomClickListener {
        void onClassroomClick(int position);
        void onStartClassroom(int position);
    }
    public ClassroomAdapter(Context context, ArrayList<Classroom> classroomList,ClassroomClickListener classroomClickListener) {
        this.context = context;
        this.classroomList = classroomList;
        this.classroomClickListener = classroomClickListener;
    }


    @NonNull
    @Override
    public ClassrooomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_classroom,parent,false);
        return new ClassrooomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassrooomViewHolder holder, int position) {
        Classroom classroom = classroomList.get(position);
        if (!classroom.getBackground().isEmpty() || classroom.getBackground() != null) {
           Glide.with(context).load(classroom.getBackground()).into(holder.background);
        }
        holder.textClassroomName.setText(classroom.getName());
        holder.materialCardView.setOnClickListener(view -> classroomClickListener.onClassroomClick(position));

    }

    @Override
    public int getItemCount() {
        return classroomList.size();
    }

    public  class ClassrooomViewHolder extends RecyclerView.ViewHolder {
        TextView textClassroomName;
        ImageView background;
        MaterialCardView materialCardView;
        public ClassrooomViewHolder(@NonNull View itemView) {
            super(itemView);
             textClassroomName= itemView.findViewById(R.id.textClassroomName);
             background = itemView.findViewById(R.id.classroomBackground);
             materialCardView = itemView.findViewById(R.id.card);
        }
    }
}
