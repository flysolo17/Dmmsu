package com.flysolo.dmmsugradelevelapp.views.teacher.nav;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flysolo.dmmsugradelevelapp.databinding.FragmentTeacherClassroomBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.transitions.DepthPageTransformer;
import com.flysolo.dmmsugradelevelapp.views.adapters.ClassroomTabAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TeacherClassroomFragment extends Fragment {
    private FragmentTeacherClassroomBinding binding;
    private String[] tabs = {"Lessons", "Activities", "Students", "Settings"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTeacherClassroomBinding.inflate(inflater,container,false);
        binding.viewpager2.setOffscreenPageLimit(3);
        binding.viewpager2.setPageTransformer(
                new DepthPageTransformer());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Classroom classroom = TeacherClassroomFragmentArgs.fromBundle(getArguments()).getClassroom();
        ClassroomTabAdapter adapter = new ClassroomTabAdapter(getChildFragmentManager(),getLifecycle(),classroom);
        binding.viewpager2.setAdapter(adapter);
        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewpager2.setCurrentItem(tab.getPosition());
                int position = tab.getPosition();
                binding.textPageName.setText(position <= 4 ? tabs[position] : tabs[0]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        binding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tablayout.getTabAt(position).select();
            }
        });
    }

}