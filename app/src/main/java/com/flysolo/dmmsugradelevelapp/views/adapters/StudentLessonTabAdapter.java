package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.views.student.tabs.StudentActivities;
import com.flysolo.dmmsugradelevelapp.views.student.tabs.StudentLessonContent;

public class StudentLessonTabAdapter extends FragmentStateAdapter {
    FragmentManager fragment;
    Lifecycle lifecycle;
    Classroom classroom;
    Lesson lesson;

    public StudentLessonTabAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Classroom classroom,Lesson lesson) {
        super(fragmentManager, lifecycle);
        this.classroom = classroom;
        this.lesson = lesson;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 1:
                fragment = new StudentActivities();
                break;
            default:
                fragment = new StudentLessonContent();
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable("classroom",classroom);
        bundle.putParcelable("lesson",lesson);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
