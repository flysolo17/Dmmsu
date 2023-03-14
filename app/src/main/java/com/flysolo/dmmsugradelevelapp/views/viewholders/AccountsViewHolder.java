package com.flysolo.dmmsugradelevelapp.views.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flysolo.dmmsugradelevelapp.R;
import com.google.android.material.button.MaterialButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountsViewHolder extends RecyclerView.ViewHolder {
    public TextView textFullname;
    public MaterialButton buttonAdd,buttonRemove;
    public CircleImageView imageProfile;
    public AccountsViewHolder(@NonNull View itemView) {
        super(itemView);
        imageProfile = itemView.findViewById(R.id.imageProfile);
        textFullname = itemView.findViewById(R.id.textFullname);
        buttonAdd = itemView.findViewById(R.id.buttonAdd);
        buttonRemove = itemView.findViewById(R.id.buttonRemove);
    }
}
