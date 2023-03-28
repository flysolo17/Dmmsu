package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.ActivityTab;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.ContentTab;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.QuestionsTab;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.RespondentsTab;

public class QuestionTabAdapter extends FragmentStateAdapter {
    FragmentManager fragment;
    Lifecycle lifecycle;
    String activityID;
    String classroomID;
    public QuestionTabAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String classroomID, String activityID) {
        super(fragmentManager, lifecycle);
        this.classroomID = classroomID;
        this.activityID = activityID;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 1) {
            fragment = new RespondentsTab();
        } else {
            fragment = new QuestionsTab();
        }
        Bundle bundle = new Bundle();
        bundle.putString("activityID",activityID);
        bundle.putString("classroomID",classroomID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
