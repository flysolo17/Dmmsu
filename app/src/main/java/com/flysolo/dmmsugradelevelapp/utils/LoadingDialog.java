package com.flysolo.dmmsugradelevelapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.flysolo.dmmsugradelevelapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LoadingDialog {
    Context context;
    AlertDialog dialog;

    public LoadingDialog(Context context) {
        this.context = context;

    }
    public void  showLoadingDialog(String title) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this.context);
        View view = LayoutInflater.from(context).inflate(com.flysolo.dmmsugradelevelapp.R.layout.loading_dialog,null);
        TextView titles = view.findViewById(R.id.textTitle);
        titles.setText(title);
        builder.setView(view);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }
    public void  stopLoading() {
        dialog.dismiss();
    }
}
