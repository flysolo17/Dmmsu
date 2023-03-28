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

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {
    Context context;
    List<Content> contents;
    ContentClickListener contentClickListener;
    public interface ContentClickListener{
        void onDelete(String id);
        void onEdit(Content content);
    }
    public ContentAdapter(Context context, List<Content> contents,ContentClickListener contentClickListener) {
        this.context = context;
        this.contents = contents;
        this.contentClickListener = contentClickListener;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_content,parent,false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        Content content = contents.get(position);
        holder.textTitle.setText(content.getTitle());
        holder.textDesc.setText(content.getDescription());
        if (!content.getAttachment().isEmpty()) {
            Glide.with(context).load(content.getAttachment()).into(holder.imageContent);
        } else {
            holder.imageContent.setVisibility(View.GONE);
        }
        holder.buttonEdit.setOnClickListener(view -> contentClickListener.onEdit(content));
        holder.buttonDelete.setOnClickListener(view -> contentClickListener.onDelete(content.getId()));
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle,textDesc;
        ImageView imageContent;
        Button buttonEdit,buttonDelete;
        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDesc = itemView.findViewById(R.id.textDesc);
            imageContent = itemView.findViewById(R.id.imageContent);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
        }
    }
}
