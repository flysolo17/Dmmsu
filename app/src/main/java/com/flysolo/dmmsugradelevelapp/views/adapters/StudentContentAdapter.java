package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Content;

import java.util.List;

public class StudentContentAdapter extends RecyclerView.Adapter<StudentContentAdapter.StudentContentViewHolder> {
    Context context;
    List<Content> contents;

    public StudentContentAdapter(Context context, List<Content> contents) {
        this.context = context;
        this.contents = contents;
    }

    @NonNull
    @Override
    public StudentContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_student_classroom_content,parent,false);
        return new StudentContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentContentViewHolder holder, int position) {
        Content content = contents.get(position);
        holder.textTitle.setText(content.getTitle());
        holder.textDesc.setText(content.getDescription());
        if (!content.getAttachment().isEmpty()) {
            Glide.with(context).load(content.getAttachment()).into(holder.imageContent);
        } else {
            holder.imageContent.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    public class StudentContentViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle,textDesc;
        ImageView imageContent;
        public StudentContentViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDesc = itemView.findViewById(R.id.textDesc);
            imageContent = itemView.findViewById(R.id.imageContent);
        }
    }
}
