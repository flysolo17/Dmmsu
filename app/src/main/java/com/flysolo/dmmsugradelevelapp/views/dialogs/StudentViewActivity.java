package com.flysolo.dmmsugradelevelapp.views.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentStudentViewActivityBinding;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.views.student.components.StudentViewLessonFragmentDirections;


public class StudentViewActivity extends Fragment {


    private static final String ARG_PARAM1 = "quiz";
    private Quiz quiz;
    private FragmentStudentViewActivityBinding binding;
    public static StudentViewActivity newInstance(Quiz quiz) {
        StudentViewActivity fragment = new StudentViewActivity();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, quiz);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quiz = StudentViewActivityArgs.fromBundle(getArguments()).getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStudentViewActivityBinding.inflate(inflater,container,false);
        display(quiz);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonPlay.setOnClickListener(view1 -> {
            if (quiz.getQuestions().size() > 0) {
                NavDirections directions = StudentViewActivityDirections.actionStudentViewActivityToStartActivity(quiz);
                Navigation.findNavController(view).navigate(directions);
            } else {
                Toast.makeText(view.getContext(), "No questions yet!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void display(Quiz quiz) {
        binding.textType.setText(quiz.getQuizType().toString().replace("_"," "));
        binding.textTitle.setText(quiz.getName());
        binding.textDesc.setText(quiz.getDescription());
        int count = quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;
        binding.textQuestion.setText(count + " Question");
        binding.textPoints.setText("+" + Constants.getMaxScore(quiz.getQuestions()) + " Points");
    }
}