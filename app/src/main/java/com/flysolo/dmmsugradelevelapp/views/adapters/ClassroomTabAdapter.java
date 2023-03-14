package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.ActivityTab;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.LessonTab;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.SettingsTab;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.StudentsTab;

public class ClassroomTabAdapter extends FragmentStateAdapter {
    FragmentManager fragment;
    Lifecycle lifecycle;
    Classroom classroom;

    public ClassroomTabAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,Classroom classroom) {
        super(fragmentManager, lifecycle);
        this.classroom = classroom;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 1:
                fragment = new ActivityTab();
                break;
            case 2:
                fragment = new StudentsTab();
                break;
            case 3:
                fragment = new SettingsTab();
                break;
            default:
                fragment = new LessonTab();
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable("classroom",classroom);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
