package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.ActivityTab;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.ContentTab;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.LessonTab;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.SettingsTab;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.StudentsTab;

public class LessonTabAdapter extends FragmentStateAdapter {
    FragmentManager fragment;
    Lifecycle lifecycle;
    String lessonID;
    String classroomID;
    public LessonTabAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,String classroomID, String lessonID) {
        super(fragmentManager, lifecycle);
        this.classroomID = classroomID;
        this.lessonID = lessonID;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 1) {
            fragment = new ActivityTab();
        } else {
            fragment = new ContentTab();
        }
        Bundle bundle = new Bundle();
        bundle.putString("lessonID",lessonID);
        bundle.putString("classroomID",classroomID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
