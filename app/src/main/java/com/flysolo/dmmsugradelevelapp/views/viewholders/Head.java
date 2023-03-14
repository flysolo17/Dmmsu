package com.flysolo.dmmsugradelevelapp.views.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flysolo.dmmsugradelevelapp.R;

import org.w3c.dom.Text;

public class Head extends RecyclerView.ViewHolder {
    public TextView textType,textCount;
    public Head(@NonNull View itemView) {
        super(itemView);
        textType = itemView.findViewById(R.id.textType);
        textCount= itemView.findViewById(R.id.textCount);
    }
}
