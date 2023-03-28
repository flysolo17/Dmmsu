package com.flysolo.dmmsugradelevelapp.views.student.components;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flysolo.dmmsugradelevelapp.databinding.FragmentStudentViewLessonBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentLessonTabAdapter;
import com.google.android.material.tabs.TabLayout;


public class StudentViewLessonFragment extends Fragment {


    private static final String CLASSROOM = "classroom";
    private static final String LESSON = "lesson";

    private Classroom classroom;
    private Lesson lesson;
    private FragmentStudentViewLessonBinding binding;
    public StudentViewLessonFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroom = getArguments().getParcelable(CLASSROOM);
            lesson = getArguments().getParcelable(LESSON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStudentViewLessonBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StudentLessonTabAdapter adapter = new StudentLessonTabAdapter(requireActivity().getSupportFragmentManager(),getLifecycle(),classroom,lesson);
        binding.viewpager2.setAdapter(adapter);
        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewpager2.setCurrentItem(tab.getPosition());
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